<template>
  <div class="modal" id="note-detail-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">{{detail.noteTitle}}</p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body" id="note-detail-dialog-body">

        <article class="message is-danger" v-if="msg.globalErrMsg !== '' && mode==='detail'">
          <div class="message-body" v-html="msg.globalErrMsg"></div>
        </article>

        <div v-if="mode==='detail' || (mode==='comment' && comment.viewNoteDetail)">
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

          <div class="box" v-if="comments.length > 0" style="background: ghostwhite;">
            <div>
              <label class="label">コメント</label>
              <note-comment v-for="commentItem in comments" :commentItem="commentItem" :key="commentItem.noteCommentId"></note-comment>
            </div>
          </div>

        </div>

        <div v-if="mode==='comment'">

          <div style="margin: 0.5rem 0rem">
            <a class="button" @click="toggleViewNoteDetail">
              <span class="icon is-small">
                <i class="fa" :class="{'fa-plus-circle':!comment.viewNoteDetail, 'fa-minus-circle':comment.viewNoteDetail}"></i>
              </span>
              <span>ふせんの詳細情報を{{viewNoteDetailLabel}}</span>
            </a>
          </div>

          <article class="message is-danger" v-if="msg.globalErrMsg !== '' && mode==='comment'">
            <div class="message-body" v-html="msg.globalErrMsg"></div>
          </article>

          <div>
            <label class="label">コメント</label>
            <p class="control">
              <textarea v-model="comment.comment" placeholder="コメントを入力してください" class="textarea" id="note-detail-dialog-comment"></textarea>
            </p>
          </div>

          <div>
            <label class="label">添付ファイル</label>
            <file-upload @FileUpload="fileUpload"></file-upload>
            <saved-file-list :fileList="comment.files" type="edit" @DeleteItem="deleteItem"></saved-file-list>
          </div>

          <div v-if="isCharged">
            <div style="margin-top: 0.5rem">
              <a class="button is-primary" @click="toggleAppendixChange" >
              <span class="icon is-small">
                <i class="fa" :class="{'fa-plus-circle':!comment.appendixChange, 'fa-ban':comment.appendixChange}"></i>
              </span>
                <span>{{appendixChangeLabel}}</span>
              </a>
            </div>
            <div v-if="comment.appendixChange === true" style="margin-top: 1rem;">
              <note-edit-area :form="form" :msg="msg" :joinedUsers="joinedUsers" :files="formFiles"></note-edit-area>
            </div>
          </div>

        </div>

      </section>
      <footer class="modal-card-foot">

        <a class="button is-danger" @click="deleteNote" v-if="isCharged && mode==='detail'">
          <span class="icon is-small">
            <i class="fa fa-times"></i>
          </span>
          <span>削除</span>
        </a>

        <a class="button is-primary" @click="openEditDialog" v-if="isCharged && mode==='detail'">
          <span class="icon is-small">
            <i class="fa fa-pencil"></i>
          </span>
          <span>このふせんを変更する</span>
        </a>

        <a class="button is-primary" @click="viewCommentEdit" v-if="mode==='detail'">
          <span class="icon is-small">
            <i class="fa fa-comment-o"></i>
          </span>
          <span>このふせんにコメントする</span>
        </a>

        <a class="button is-info" @click="saveComment" v-if="mode==='comment'">
          <span class="icon is-small">
            <i class="fa fa-floppy-o"></i>
          </span>
          <span>登録</span>
        </a>

        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>

</template>

<script>

  import autosize from 'autosize/dist/autosize'
  import Utils from '../../utils'
  import SavedFileList from '../attachment/List'
  import NoteComment from './NoteComment'
  import Upload from '../attachment/Upload'
  import NoteEditArea from './NoteEditArea'

  export default {
    name: 'note-detail-dialog',
    props: ['kanbanId'],
    components: {
      'saved-file-list':SavedFileList,
      'file-upload':Upload,
      'note-comment':NoteComment,
      'note-edit-area':NoteEditArea
    },
    data() {
      return {
        detail: {
          id:"",
          laneId:"",
          kanbanId:"",
          lockVersion:"",
          noteTitle: "",
          noteDescription: "",
          archiveStatus: "0",
          fixDate: ""
        },
        comment: {
          comment: "",
          files:[],
          attachmentFileIds:[],
          appendixChange: false,
          viewNoteDetail:false
        },
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
        formFiles:[],
        msg: {
          globalErrMsg: "",
          noteTitleErrMsg:"",
          noteDescriptionErrMsg:"",
          fixDateErrMsg: ""
        },
        files:[],
        isCharged: true,
        chargedUserNames:[],
        comments:[],
        mode:"",
        clearMsg(){
          this.msg.globalErrMsg = "";
          this.msg.noteTitleErrMsg="";
          this.msg.noteDescriptionErrMsg="";
          this.msg.fixDateErrMsg= "";
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
        self.detail.lockVersion = form.lockVersion;

        self.comments.splice(0, self.comments.length);
        self.comments.push(...result.comments);

        self.isCharged = result.isCharged;
        self.chargedUserNames = result.chargedUserNames;

        self.files.splice(0, self.files.length);
        self.files.push(...result.noteAttachmentFiles);

        //変更用
        self.form.id = form.id;
        self.form.laneId = form.laneId;
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

        self.formFiles.splice(0, self.formFiles.length);
        self.formFiles.push(...result.noteAttachmentFiles);

        $('#note-detail-dialog-body').addClass("detail");
        self.mode = 'detail';
      },
      viewCommentEdit() {
        const self = this;
        self.mode = 'comment';
        self.comment.comment = "";
        self.comment.files = [];
        self.comment.attachmentFileIds = [];
        self.comment.appendixChange = false;
        self.comment.viewNoteDetail = false;
        $('#note-detail-dialog-body').removeClass("detail");
        $('#note-detail-dialog section.modal-card-body').scrollTop(0);

        setTimeout(function(){
          $('#note-detail-dialog-comment').focus();
          const ta = document.querySelector('#note-detail-dialog-comment');
          autosize(ta);
          autosize.update(ta);
        }, 100);
      },
      toggleAppendixChange() {
        const self = this;
        self.comment.appendixChange = !self.comment.appendixChange;

        if(self.comment.appendixChange) {
          setTimeout(function(){
            $('#note-detail-dialog .note-title').focus();

            const ta = document.querySelectorAll('#note-detail-dialog textarea');
            autosize(ta);
            autosize.update(ta);

            Utils.datepicker('#note-detail-dialog .flatpickr', self.form.fixDate);

          }, 100);
        }

      },
      toggleViewNoteDetail() {
        const self = this;
        self.comment.viewNoteDetail = !self.comment.viewNoteDetail;
      },
      deleteNote(e) {

        if(window.confirm("このふせんを削除して本当によろしいですか？(「アーカイブ」することで見えなくすることもできます)") == false) {
          return;
        }

        const self = this;
        self.clearMsg();

        const param = {
          kanbanId: self.kanbanId,
          noteId: self.detail.id,
          lockVersion: self.detail.lockVersion
        };

        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'POST',
          url: "/kanban/note/delete"
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
      },
      fileUpload(e, targetFiles) {
        const self = this;
        Utils.uploadFile(targetFiles, self.comment.files);
      },
      deleteItem(e, index) {
        const self = this;
        self.comment.files.splice(index, 1);
      },
      saveComment(e) {
        const self = this;
        if(!self.comment.appendixChange) {
          //コメントの登録だけ
          self.executeSaveComment(e);
        } else {
          alert("他の情報も更新するよ...");
        }
      },
      executeSaveComment(e) {
        const self = this;
        self.clearMsg();
        self.comment.attachmentFileIds = self.comment.files.map(function(element, index, array) {
          return element.attachmentFileId;
        });

        const param = {
          noteId: self.detail.id,
          attachmentFileIds: self.comment.attachmentFileIds,
          comment: self.comment.comment
        };

        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'POST',
          url: "/kanban/note/comment/store"
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
      },
      appendixChangeLabel() {
        const self = this;
        return self.comment.appendixChange ? "他の情報は変更しない":"他の情報も合わせて変更する";
      },
      viewNoteDetailLabel() {
        const self = this;
        return self.comment.viewNoteDetail ? "隠す":"見る";
      }
    }
  }
</script>
