<template>

  <div class="card" :class="{ 'archived': noteItem.archiveStatus === '1' }" @click="openDetailDialog">
    <header class="card-header">
      <p class="card-header-title">
        {{noteItem.noteTitle}}
      </p>
      <a class="card-header-icon drag-item">
        <span class="icon">
          <i class="fa fa-arrows"></i>
        </span>
      </a>
    </header>
    <div class="card-content">
      <div class="content">

        <div v-html="escapeDescription"></div>

        <div class="charge-users">
          <template v-for="userId in noteItem.chargedUsers">
            <span v-if="typeof joinedUserMap[userId] !== 'undefined'" class="tag is-dark" style="margin-right: 5px;" :title="joinedUserMap[userId].name">{{takeStringTop(joinedUserMap[userId].name)}}</span>
          </template>
        </div>

        <div class="fix-date">
          <span v-if="typeof noteItem.fixDate !== 'undefined' && noteItem.fixDate !== ''" class="tag" :class="fixDateClass">{{toDateString(noteItem.fixDate)}}</span>
          <span v-if="noteItem.hasAttachmentFile" class="tag is-info">添付有</span>
        </div>

      </div>
    </div>
  </div>

</template>

<script>
  import Utils from '../../utils'

  export default {
    name: 'note-item',
    props: ['noteItem', 'joinedUserMap', 'completeLane'],
    methods:{
      takeStringTop(target) {
        if(typeof target === 'undefined' || target === '') {
          return "未";
        } else {
          return target.substr(0, 1);
        }
      },
      toDateString(target) {
        return Utils.toDateString(target, "YY/MM/DD");
      },
      openDetailDialog(e) {
        if (e && (e.target.tagName.toLocaleLowerCase() === 'a' || e.target.tagName.toLocaleLowerCase() === 'span' || e.target.tagName.toLocaleLowerCase() === 'i')) {
          //aタグの場合、処理終了
          return;
        }
        const self = this;
        self.$emit("OpenDetailDialog", e, self.noteItem.noteId);
      }
    },
    computed:{
      escapeDescription() {
        const self = this;
        return Utils.escapeTextArea(self.noteItem.noteDescription);
      },
      fixDateClass() {
        const self = this;
        return Utils.fixDateClass(self.noteItem.fixDate, self.completeLane);
      }
    }
  }
</script>

<style scoped>
  .card {
    margin-bottom:1rem;
  }
  .card.archived {
    background-color:#aaa;
  }
  .card-content {
    padding: 0.75rem;
  }
  .charge-users, .fix-date {
    margin-top: 0.5rem;
  }
</style>
