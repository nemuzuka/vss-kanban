<template>
  <div class="column is-3" :class="{ 'archived': item.archiveStatus === '1' }">

    <div class="card" @click="viewKanbanMain" style="cursor: pointer;">
      <header class="card-header">
        <p class="card-header-title">
          {{item.title}}
        </p>
      </header>
      <div class="card-content">
        <div class="content" v-html="escapeDescription"></div>
      </div>
    </div>

  </div>
</template>

<script>
  import Utils from '../../utils'

  export default {
    name: 'kanban-item',
    props: ["item"],
    methods : {
      viewKanbanMain(e) {
        if (e && (e.target.tagName.toLocaleLowerCase() === 'a' || e.target.tagName.toLocaleLowerCase() === 'span' || e.target.tagName.toLocaleLowerCase() === 'i')) {
          //aタグの場合、処理終了
          return;
        }
        
        const self = this;
        self.$emit("ViewKanbanMain", e, self.item.id);
      }
    },
    computed:{
      escapeDescription() {
        const self = this;
        return Utils.escapeTextArea(self.item.description);
      }
    }
  }
</script>

<style scoped>

  .archived > .card {
    background-color:#eee;
  }

</style>
