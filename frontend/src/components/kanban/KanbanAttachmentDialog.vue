<template>
  <div class="modal" id="kanban-attachment-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">添付ファイル</p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body">

        <saved-file-list :fileList="files" type="detail"></saved-file-list>

      </section>
      <footer class="modal-card-foot">
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
    name: 'kanban-attachment-dialog',
    components: {
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
        Utils.openDialog('kanban-attachment-dialog');
      },
      closeDialog() {
        Utils.closeDialog('kanban-attachment-dialog');
      }
    }
  }

</script>
