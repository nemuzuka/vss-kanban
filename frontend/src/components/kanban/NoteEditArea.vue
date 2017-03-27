<template>
  <div>
    <div>
      <label class="label">件名 <span class="tag is-danger">必須</span></label>
      <p class="control">
        <input class="input note-title" type="text" v-model="form.noteTitle" placeholder="件名を入力してください">
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
        <input class="input is-expanded flatpickr" type="text" v-model="form.fixDate" placeholder="期限があれば入力してください">
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

  </div>
</template>

<script>

  import Utils from '../../utils'
  import Upload from '../attachment/Upload'
  import SavedFileList from '../attachment/List'

  export default {
    name: 'note-edit-area',
    components: {
      'file-upload': Upload,
      'saved-file-list':SavedFileList
    },
    props: ['form','msg','joinedUsers', 'files'],
    methods: {
      dateClear(e){
        const self = this;
        self.$emit("ClearFixDate", e);
      },
      fileUpload(e, targetFiles) {
        const self = this;
        Utils.uploadFile(targetFiles, self.files);
      },
      deleteItem(e, index) {
        const self = this;
        self.files.splice(index, 1);
      },
    }
  }
</script>
