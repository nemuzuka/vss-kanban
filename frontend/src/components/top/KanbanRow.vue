<template>
  <div class="column is-3">

    <div class="card" @click="viewKanbanMain">
      <header class="card-header">
        <p class="card-header-title">
          {{item.title}}
        </p>
      </header>
      <div class="card-content">
        <div class="content">
          {{item.description}}
        </div>
      </div>
    </div>

  </div>
</template>

<script>
  export default {
    name: 'kanban-item',
    props: ["item"],
    methods : {
      viewKanbanMain(e) {
        const self = this;
        self.$emit("ViewKanbanMain", e, self.item.id);
      }
    }
  }
</script>
