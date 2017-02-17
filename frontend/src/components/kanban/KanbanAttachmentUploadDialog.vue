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

  export default {
    name: 'kanban-attachment-upload-dialog',
    components: {
      'file-upload': Upload,
    },
    data() {
      return {
        form:{
          files:[],
        },
        msg:{
          globalErrMsg:"",
        },
        clearMsg(){
          this.msg.globalErrMsg = "";
        }
      }
    },
    methods: {
      openDialog() {
        const self = this;
        self.clearMsg();
        Utils.openDialog('kanban-attachment-upload-dialog');
      },
      closeDialog() {
        Utils.closeDialog('kanban-attachment-upload-dialog');
      },
      saveDialog() {
        alert("ほぞんするよ");
      },
      fileUpload(e, files) {
        if(files.length <= 0) {
          return;
        }
        const fd = new FormData();
        files.forEach(function(file){
          fd.append("files", file);
        });



        alert("まずはアップロードします");
      }
    }
  }



</script>
