<template>
  <article class="media">
    <div class="media-content">
      <div class="content">
        <div style="margin-bottom: 0.5rem;">
          <small style="margin-right: 0.5rem;">{{createDateStr}}</small><span class="tag is-light" v-if="commentItem.createUserName !== ''">{{commentItem.createUserName}}</span>
        </div>
        <div class="description" v-html="escapeComment"></div>

        <saved-file-list :fileList="commentItem.attachmentFiles" type="detail"></saved-file-list>

      </div>
    </div>
  </article>
</template>

<script>
  import Utils from '../../utils'
  import SavedFileList from '../attachment/List'

  export default {
    name: 'note-comment',
    props: ['commentItem'],
    components: {
      'saved-file-list':SavedFileList
    },
    computed: {
      createDateStr(){
        const self = this;
        return Utils.toDateTimeString(self.commentItem.createAt, "YY/MM/DD HH:mm");
      },
      escapeComment() {
        const self = this;
        return Utils.escapeTextArea(self.commentItem.comment);
      }
    }
  }
</script>

<style scoped>
  .media-content {
    max-width: 100%;
  }
  .media-content .content .description {
    word-wrap: break-word;
    overflow-wrap: break-word;
  }
</style>
