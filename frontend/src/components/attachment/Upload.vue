<template>

  <div>
    <div @drop="fileUpload4Drop" @dragover="dragover" @dragleave="dragleave" draggable="true" class="drop margin" :class="{'drag-over': isOver}">
      <p>ここにファイルをドロップ</p>
    </div>

    <p class="margin">または</p>

    <input type="file" ref="file" value="ファイルを選択" multiple="multiple" @change="fileUpload4Input">
  </div>

</template>

<script>

  import Utils from '../../utils'
  export default {
    name: 'file-upload',
    data(){
      return {
        isOver : false
      }
    },
    methods:{
      fileUpload4Drop(e) {
        const self = this;
        e.preventDefault();
        const files = e.dataTransfer.files;
        self.isOver = false;
        self.$emit("FileUpload", e, files);
      },
      dragover(e) {
        const self = this;
        e.preventDefault();
        self.isOver = true;
      },
      dragleave(e) {
        const self = this;
        e.preventDefault();
        self.isOver = false;
      },
      fileUpload4Input(e) {
        const self = this;
        const files = self.$refs.file.files;
        self.$emit("FileUpload", e, files);
      }
    }
  }

</script>

<style scoped>
  .drop {
    padding: 15px;
    border: 2px dashed;
  }

  .margin {
    margin-bottom: 1rem;
  }

  .drop.drag-over {
    background: #00d1b2;
    color: #fff;
  }

</style>
