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

        <div>
          <label class="label">件名 <span class="tag is-danger">必須</span></label>
          <p class="control">
            <input class="input" type="text" v-model="form.noteTitle" placeholder="件名を入力してください" id="note-edit-dialog-note-title">
            <span class="help is-danger" v-html="msg.noteTitleErrMsg"></span>
          </p>
        </div>

        <div>
          <label class="label">説明</label>
          <p class="control">
            <textarea v-model="form.noteDescription" placeholder="説明を入力してください" class="textarea"></textarea>
            <span class="help is-danger" v-html="msg.noteDescriptionErrMsg"></span>
          </p>
        </div>

        <div>
          <label class="label">期限</label>
          <p class="control has-addons">
            <input class="input is-expanded flatpickr" type="text" v-model="form.fixDate" placeholder="期限があれば入力してください" id="note-edit-dialog-fix-date">
            <a class="button" @click="dateClear">
              日付をクリア
            </a>
          </p>
          <span class="help is-danger" v-html="msg.fixDateErrMsg"></span>
        </div>

        <div>
          <p class="control">
            <label class="checkbox">
              <input type="checkbox" v-model="form.archiveStatus" :true-value="1" :false-value="0">
              このふせんをアーカイブする
            </label>
          </p>
        </div>

        <div>
          <label class="label">担当者</label>

          <table class="table is-bordered is-striped is-narrow">

            <tbody>
              <template v-for="user in joinedUsers" :user="user" :key="user.userId">
                <tr>
                  <td>
                    <label class="checkbox">
                      <input type="checkbox" v-model="form.chargedUserIds" :value="user.userId">
                      {{user.name}}
                    </label>
                  </td>
                </tr>
              </template>
            </tbody>

          </table>
        </div>


        <div>
          <label class="label">添付ファイル</label>
          <file-upload @FileUpload="fileUpload"></file-upload>
          <saved-file-list :fileList="files" type="edit" @DeleteItem="deleteItem"></saved-file-list>
        </div>

      </section>
      <footer class="modal-card-foot">
        <a class="button is-info" @click="saveDialog">{{buttonLabel}}</a>
        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>
</template>

<script>

  import autosize from 'autosize/dist/autosize'
  import Utils from '../../utils'
  import Upload from '../attachment/Upload'
  import SavedFileList from '../attachment/List'
  import 'flatpickr/dist/flatpickr.min.css'

  export default {
    name: 'note-edit-dialog',
    components: {
      'file-upload': Upload,
      'saved-file-list':SavedFileList
    },
    props:['kanbanId'],
    data() {
      return {
        form:{
          id: "",
          laneId: "",
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
      dateClear(e){
        $("#note-edit-dialog-fix-date").val("");
      },
      openDialog(e, laneId, noteId) {
        const self = this;
        const param = {
          noteId: noteId,
          kanbanId: self.kanbanId,
          laneId: laneId
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
              $('#note-edit-dialog-note-title').focus();

              const ta = document.querySelector('#note-edit-dialog textarea');
              autosize(ta);
              autosize.update(ta);

              Utils.datepicker('#note-edit-dialog .flatpickr');

            }, 500);
          }
        );
      },
      setFormData(result) {
        const self = this;
        const form = result.form;
        self.form.id = form.id;
        self.form.laneId = form.laneId;
        self.form.kanbanId = form.kanbanId;
        self.form.lockVersion = form.lockVersion;
        self.form.noteTitle = form.noteTitle;
        self.form.noteDescription = form.noteDescription;
        self.form.archiveStatus = form.archiveStatus;
        self.form.fixDate = form.fixDate;
        self.form.chargedUserIds = form.chargedUserIds;
        self.form.attachmentFileIds = form.attachmentFileIds;

        self.joinedUsers.splice(0, self.joinedUsers.length);
        self.joinedUsers.push(...result.joinedUsers);

        self.files.splice(0, self.files.length);
        self.files.push(...result.noteAttachmentFiles)
      },
      closeDialog() {
        Utils.closeDialog('note-edit-dialog');
      },
      fileUpload(e, targetFiles) {
        const self = this;
        Utils.uploadFile(targetFiles, self.files);
      },
      deleteItem(e, index) {
        const self = this;
        self.files.splice(index, 1);
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
