<template>
  <div style="height: 100%">
    <div id="kanban-dashboard-area" class="container">
      <div class="content-view">
        <nav class="level">
          <div class="level-left">
            <div class="level-item">
              <h1 class="title">参加中のかんばん</h1>
            </div>
          </div>

          <div class="level-right">

            <p class="level-item">

              <label class="checkbox" style="margin-right: .75rem;" v-if="authority === '1'">
                <input type="checkbox" v-model="form.viewAllKanban" @change="refresh">
                参加していないかんばんも見る
              </label>
              <label class="checkbox" style="margin-right: .75rem;">
                <input type="checkbox" v-model="form.viewArchiveKanban" @change="refresh">
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

        <joined-kanban-list :listData="listData" @ViewKanbanMain="viewKanbanMain"></joined-kanban-list>
        <other-kanban-list :listData="listData" :viewAllKanban="form.viewAllKanban" @ViewKanbanMain="viewKanbanMain"></other-kanban-list>

      </div>
    </div>

    <kanban-main ref="kanbanMain" @Refresh="refresh"></kanban-main>

    <kanban-edit-dialog ref="editDialog" @Refresh="refresh" @ViewKanbanMain="viewKanbanMain"></kanban-edit-dialog>
  </div>
</template>

<script>

  import Utils from '../../utils'
  import KanbanEditDialog from '../kanban/EditDialog'
  import JoinedKanbanList from './JoinedKanbanList'
  import OtherKanbanList from './OtherKanbanList'
  import KanbanMain from '../kanban/Main'

  export default {
    name: 'dashboard',
    components: {
      'kanban-edit-dialog': KanbanEditDialog,
      'joined-kanban-list': JoinedKanbanList,
      'other-kanban-list': OtherKanbanList,
      'kanban-main' : KanbanMain
    },
    data() {
      return {
        form:{
          viewArchiveKanban:false,
          viewAllKanban:false,
        },
        listData:{
          joinedKanbans:[],
          joinedKanbanMsg:"",
          otherKanbans:[],
          otherKanbanMsg:""
        },
        authority:''
      }
    },
    methods: {
      openEditDialog() {
        const self = this;
        self.$refs.editDialog.openEditDialog();
      },
      refresh() {
        const self = this;
        self.listData.joinedKanbanMsg = "";
        self.listData.otherKanbanMsg = "";
        Utils.setAjaxDefault();
        $.ajax({
          data: self.form,
          method: 'GET',
          url: "/kanban/list"
        }).then(
          function (data) {
            const msg = "この条件に合致するかんばんは登録されていません";
            const result = data.result;

            self.listData.joinedKanbans.splice(0,self.listData.joinedKanbans.length);
            self.listData.joinedKanbans.push(...result.joinedKanbans);
            if(self.listData.joinedKanbans.length <= 0) {
              self.listData.joinedKanbanMsg = msg;
            }

            self.listData.otherKanbans.splice(0,self.listData.otherKanbans.length);
            self.listData.otherKanbans.push(...result.otherKanbans);
            if(self.listData.otherKanbans.length <= 0) {
              self.listData.otherKanbanMsg = msg;
            }
          }
        );
      },
      viewKanbanMain(e, id) {
        const self = this;
        self.$refs.kanbanMain.viewContext(e, id, "kanban-dashboard-area");
      }
    },
    created() {
      const userInfo = JSON.parse(localStorage.getItem("kanbanUserInfo"));
      const self = this;
      self.authority = userInfo.authority;
      self.refresh();
    }
  }
</script>

<style>
  .columns.kanban {
    flex-wrap: wrap;
  }
</style>
