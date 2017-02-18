<template>
  <div>
    <div v-if="listData.joinedKanbans.length > 0">
      <div class="columns kanban">
        <kanban-item v-for="item in listData.joinedKanbans" :item="item" @ViewKanbanMain="viewKanbanMain"></kanban-item>
      </div>
    </div>

    <div v-if="listData.joinedKanbans.length <= 0 && listData.joinedKanbanMsg !== ''">
      <article class="message is-danger">
        <div class="message-body">{{listData.joinedKanbanMsg}}</div>
      </article>
    </div>
  </div>
</template>

<script>

  import Row from './KanbanItem'

  export default {
    name: 'joined-kanban-list',
    components: {
      'kanban-item' : Row
    },
    props:["listData"],
    methods : {
      viewKanbanMain(e, id) {
        const self = this;
        self.$emit("ViewKanbanMain", e, id);
      }
    }
  }
</script>
