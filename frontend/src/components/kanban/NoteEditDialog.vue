<template>
  <div class="modal" id="note-edit-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">ふせん{{buttonLabel}}</p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body">

        <article class="message is-danger" v-if="msg.globalErrMsg !== ''">
          <div class="message-body" v-html="msg.globalErrMsg"></div>
        </article>

        <note-edit-area :form="form" :msg="msg" :joinedUsers="joinedUsers" :files="files" @ClearFixDate="clearFixDate"></note-edit-area>

      </section>
      <footer class="modal-card-foot">
        <a class="button is-info" @click="saveDialog">
          <span class="icon is-small">
            <i class="fa fa-floppy-o"></i>
          </span>
          <span>{{buttonLabel}}</span>
        </a>
        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>
</template>

<script>

  import autosize from 'autosize/dist/autosize'
  import Utils from '../../utils'
  import NoteEditArea from './NoteEditArea'

  export default {
    name: 'note-edit-dialog',
    components: {
      'note-edit-area':NoteEditArea
    },
    props:['kanbanId'],
    data() {
      return {
        form:{
          id: "",
          stageId: "",
          kanbanId:"",
          lockVersion: "0",
          noteTitle: "",
          noteDescription: "",
          fixDate: "",
          archiveStatus: "0",
          chargedUserIds: [],
          attachmentFileIds: []
        },
        joinedUsers:[],
        files:[],
        fixDate:null,
        msg:{
          globalErrMsg:"",
          noteTitleErrMsg:"",
          noteDescriptionErrMsg:"",
          fixDateErrMsg: ""
        },
        clearMsg(){
          this.msg.globalErrMsg = "";
          this.msg.noteTitleErrMsg = "";
          this.msg.noteDescriptionErrMsg = "";
          this.msg.fixDateErrMsg = "";
        }
      }
    },
    methods: {
      clearFixDate(e){
        const self = this;
        self.form.fixDate = "";
        self.fixDate.clear();
      },
      openDialog(e, stageId, noteId) {
        const self = this;
        const param = {
          noteId: noteId,
          kanbanId: self.kanbanId,
          stageId: stageId
        };

        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'GET',
          url: "/kanban/note/form"
        }).then(
          function (data) {
            if(Utils.alertErrorMsg(data)) {
              return;
            }
            self.setFormData(data.result);
            self.clearMsg();
            Utils.openDialog('note-edit-dialog');

            setTimeout(function(){
              $('#note-edit-dialog .note-title').focus();

              const ta = document.querySelector('#note-edit-dialog textarea');
              autosize(ta);
              autosize.update(ta);

              self.fixDate = Utils.datepicker('#note-edit-dialog .flatpickr', self.form.fixDate);

            }, 100);
          }
        );
      },
      setFormData(result) {
        const self = this;
        const form = result.form;
        self.form.id = form.id;
        self.form.stageId = form.stageId;
        self.form.kanbanId = form.kanbanId;
        self.form.lockVersion = form.lockVersion;
        self.form.noteTitle = form.noteTitle;
        self.form.noteDescription = form.noteDescription;
        self.form.archiveStatus = form.archiveStatus;
        self.form.fixDate = Utils.toDateString(form.fixDate,'YYYY/MM/DD');
        self.form.chargedUserIds = form.chargedUserIds;
        self.form.attachmentFileIds = form.attachmentFileIds;

        self.joinedUsers.splice(0, self.joinedUsers.length);
        self.joinedUsers.push(...result.joinedUsers);

        self.files.splice(0, self.files.length);
        self.files.push(...result.noteAttachmentFiles);
      },
      closeDialog() {
        Utils.closeDialog('note-edit-dialog');
      },
      saveDialog(e){
        const self = this;
        self.clearMsg();
        self.form.attachmentFileIds = self.files.map(function(element, index, array) {
          return element.attachmentFileId;
        });
        Utils.setAjaxDefault();

        $.ajax({
          data: self.form,
          method: 'POST',
          url: "/kanban/note/store"
        }).then(
          function (data) {
            //エラーが存在する場合、その旨記述
            if(Utils.writeErrorMsg(self, data)) {
              return;
            }
            Utils.viewInfoMsg(data);
            setTimeout(function(){
              self.closeDialog();
              self.$emit("Refresh", e);
            },1500);
          }
        );
      }
    },
    computed: {
      buttonLabel() {
        const self = this;
        return self.form.id === "" ? '登録' : '変更';
      }
    }
  }
</script>
