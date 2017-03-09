<template>
  <div class="column is-3" :class="{ 'archived': item.archiveStatus === '1' }">

    <div class="card" @click.stop="viewKanbanMain" style="cursor: pointer;">
      <header class="card-header">
        <p class="card-header-title">
          {{item.title}}
        </p>
      </header>
      <div class="card-content">
        <div class="content description" v-html="escapeDescription"></div>
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
  .card-content .content.description {
    word-wrap: break-word;
    overflow-wrap: break-word;
  }

</style>
