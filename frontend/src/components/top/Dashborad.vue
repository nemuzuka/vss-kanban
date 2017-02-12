<template>
  <div>
    <div id="kanban-dashboard-area" class="container">
      <div class="content">
        <nav class="level">
          <!-- Left side -->
          <div class="level-left">
            <div class="level-item">
              <h1 class="title">Dashboard</h1>
            </div>
          </div>

          <!-- Right side -->
          <div class="level-right">

            <p class="level-item">

              <label class="checkbox" style="margin-right: .75rem;" v-if="authority === '1'">
                <input type="checkbox" v-model="viewAllKanban" @change="refresh">
                登録済みのかんばんも見る
              </label>
              <label class="checkbox" style="margin-right: .75rem;">
                <input type="checkbox" v-model="viewArchiveKanban" @change="refresh">
                アーカイブ済みのかんばんも見る
              </label>

              <a class="button is-success" @click="refresh">
                <span class="icon">
                  <i class="fa fa-refresh"></i>
                </span>
              </a>

              <a class="button is-success" @click="openEditDialog">
                <span class="icon">
                  <i class="fa fa-plus"></i>
                </span>
              </a>

            </p>

          </div>

        </nav>
      </div>
    </div>

    <kanban-edit-dialog ref="editDialog" @Refresh="refresh"></kanban-edit-dialog>
  </div>
</template>

<script>

  import KanbanEditDialog from '../kanban/EditDialog'

  export default {
    name: 'dashboard',
    components: {
      'kanban-edit-dialog': KanbanEditDialog,
    },
    data() {
      return {
        viewArchiveKanban:false,
        viewAllKanban:false,
        authority:''
      }
    },
    methods: {
      openEditDialog() {
        const self = this;
        self.$refs.editDialog.openEditDialog();
      },
      refresh() {
        alert("りふれっしょ");
      }
    },
    created() {
      const userInfo = JSON.parse(localStorage.getItem("kanbanUserInfo"));
      const self = this;
      self.authority = userInfo.authority;
    }
  }
</script>
