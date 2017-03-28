<template>
  <dashboard :targetKanbanId="targetKanbanId" :targetNoteId="targetNoteId"></dashboard>
</template>

<script>
  import Dashboard from '../top/Dashboard'
  export default {
    name: 'notification-dashboard',
    components: {
      'dashboard' : Dashboard
    },
    data() {
      return {
        targetKanbanId:null,
        targetNoteId:null
      }
    },
    created() {
      const self = this;
      self.targetKanbanId = $("#notification-target-kanban-id").val();
      self.targetNoteId = $("#notification-target-note-id").val();
    }
  }
</script>
