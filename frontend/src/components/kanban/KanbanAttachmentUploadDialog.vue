<template>
  <div class="modal" id="kanban-attachment-upload-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">添付ファイル</p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body">

        <article class="message is-danger" v-if="msg.globalErrMsg !== ''">
          <div class="message-body" v-html="msg.globalErrMsg"></div>
        </article>

        <file-upload @FileUpload="fileUpload"></file-upload>
        <saved-file-list :fileList="files" type="edit" @DeleteItem="deleteItem"></saved-file-list>

      </section>
      <footer class="modal-card-foot">
        <a class="button is-info" @click="saveDialog">保存</a>
        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>

</template>

<script>
  import Utils from '../../utils'
  import Upload from '../attachment/Upload'
  import SavedFileList from '../attachment/List'

  export default {
    name: 'kanban-attachment-upload-dialog',
    components: {
      'file-upload': Upload,
      'saved-file-list':SavedFileList
    },
    props:["kanbanId"],
    data() {
      return {
        files:[],
        msg:{
          globalErrMsg:"",
        },
        clearMsg(){
          this.msg.globalErrMsg = "";
        }
      }
    },
    methods: {
      openDialog(savedFiles) {
        const self = this;
        self.files.splice(0,self.files.length);
        self.files.push(...savedFiles);
        self.clearMsg();
        Utils.openDialog('kanban-attachment-upload-dialog');
      },
      closeDialog() {
        Utils.closeDialog('kanban-attachment-upload-dialog');
      },
      saveDialog(e) {
        const self = this;
        Utils.setAjaxDefault();
        $.ajax({
          data: {
            kanbanId: self.kanbanId,
            attachmentFileIds: self.files.map(function(element, index, array) {
              return element.attachmentFileId;
            })
          },
          method: 'POST',
          url: "/kanban/attachmentFile"
        }).then(
          function (data) {
            Utils.viewInfoMsg(data);
            setTimeout(function(){
              self.closeDialog();
              self.$emit("Refresh", e);
            },1500);
          }
        );
      },
      deleteItem(e, index) {
        const self = this;
        self.files.splice(index, 1);
      },
      fileUpload(e, targetFiles) {
        const self = this;
        Utils.uploadFile(targetFiles, self.files);
      }
    }
  }

</script>
