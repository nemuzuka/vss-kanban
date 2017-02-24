<template>
  <div class="modal" id="note-detail-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">{{detail.noteTitle}}</p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body detail">

        <article class="message is-danger" v-if="msg.globalErrMsg !== ''">
          <div class="message-body" v-html="msg.globalErrMsg"></div>
        </article>

        <div class="box" v-if="detail.noteDescription !== ''">
          <article class="media">
            <div class="media-content">
              <div class="content is-large" v-html="escapeDescription"></div>
            </div>
          </article>
        </div>

        <div class="box" v-if="chargedUserNames.length > 0 || detail.fixDate !== ''">
          <article class="media">
            <div class="media-content">

              <nav class="level" v-if="chargedUserNames.length > 0">
                <div class="level-left">
                  <div class="level-item">[担当者]</div>
                  <template v-for="name in chargedUserNames" :name="name">
                    <div class="level-item"><span class="tag is-light">{{name}}</span></div>
                  </template>
                </div>
              </nav>

              <nav class="level" v-if="detail.fixDate !== ''">
                <div class="level-left">
                  <div class="level-item">[期日]</div>
                  <div class="level-item"><span class="tag" :class="fixDateClass">{{toDateString}}</span></div>
                </div>
              </nav>

            </div>
          </article>
        </div>

        <saved-file-list :fileList="files" type="detail"></saved-file-list>

      </section>
      <footer class="modal-card-foot">
        <a class="button is-primary" @click="openEditDialog">このふせんを変更する</a>
        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>

</template>

<script>
  import Utils from '../../utils'
  import SavedFileList from '../attachment/List'

  export default {
    name: 'note-detail-dialog',
    props: ['kanbanId'],
    components: {
      'saved-file-list':SavedFileList
    },
    data() {
      return {
        detail: {
          id:"",
          laneId:"",
          kanbanId:"",
          noteTitle: "",
          noteDescription: "",
          archiveStatus: "0",
          fixDate: ""
        },
        msg: {
          globalErrMsg: ""
        },
        files:[],
        isCharged: true,
        chargedUserNames:[],
        clearMsg(){
          this.msg.globalErrMsg = "";
        }
      };
    },
    methods: {
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
          url: "/kanban/note/detail"
        }).then(
          function (data) {
            if(Utils.alertErrorMsg(data)) {
              return;
            }
            self.setFormData(data.result);
            self.clearMsg();
            Utils.openDialog('note-detail-dialog');
          }
        );
      },
      openEditDialog(e) {
        const self = this;
        self.closeDialog();
        self.$emit("OpenEditDialog", e, self.detail.laneId, self.detail.id);
      },
      closeDialog() {
        Utils.closeDialog('note-detail-dialog');
      },
      setFormData(result) {
        const self = this;
        const form = result.form;
        self.detail.id = form.id;
        self.detail.kanbanId = form.kanbanId;
        self.detail.laneId = form.laneId;
        self.detail.noteTitle = form.noteTitle;
        self.detail.noteDescription = form.noteDescription;
        self.detail.archiveStatus = form.archiveStatus;
        self.detail.fixDate = form.fixDate;

        self.isCharged = result.isCharged;
        self.chargedUserNames = result.chargedUserNames;

        self.files.splice(0, self.files.length);
        self.files.push(...result.noteAttachmentFiles);

      }
    },
    computed: {
      escapeDescription() {
        const self = this;
        return Utils.escapeTextArea(self.detail.noteDescription);
      },
      fixDateClass() {
        const self = this;
        return Utils.fixDateClass(self.detail.fixDate);
      },
      toDateString() {
        const self = this;
        return Utils.toDateString(self.detail.fixDate, "YY/MM/DD");
      }
    }
  }
</script>
