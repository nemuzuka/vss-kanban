<template>
  <div v-if="viewAllKanban" style="padding-top:1.5rem">

    <nav class="level">
      <!-- Left side -->
      <div class="level-left">
        <div class="level-item">
          <h1 class="title">参加していないかんばん</h1>
        </div>
      </div>
    </nav>

    <div v-if="listData.otherKanbans.length > 0">
      <div class="columns kanban">
        <kanban-item v-for="item in listData.otherKanbans" :item="item" @ViewKanbanMain="viewKanbanMain"></kanban-item>
      </div>
    </div>

    <div v-if="listData.otherKanbans.length <= 0 && listData.otherKanbanMsg !== ''">
      <article class="message is-danger">
        <div class="message-body">{{listData.otherKanbanMsg}}</div>
      </article>
    </div>
  </div>
</template>

<script>

  import Row from './KanbanItem'

  export default {
    name: 'other-kanban-list',
    components: {
      'kanban-item' : Row
    },
    props:["listData", "viewAllKanban"],
    methods : {
      viewKanbanMain(e, id) {
        const self = this;
        self.$emit("ViewKanbanMain", e, id);
      }
    }
  }
</script>
