<template>
  <div class="content-view">

    <nav class="level">
      <!-- Left side -->
      <div class="level-left">
        <div class="level-item">
          <h1 class="title">通知一覧</h1>
        </div>
      </div>

      <!-- Right side -->
      <div class="level-right">

        <p class="level-item">

          <a class="button is-info" @click="unreadAll" v-if="listData.rows.length > 0">
            <span class="icon">
              <i class="fa fa-exclamation-triangle"></i>
            </span>
            <span>全てを既読にする</span>
          </a>

          <a class="button is-success" @click="refresh" v-if="listData.rows.length > 0">
            <span class="icon">
            <i class="fa fa-refresh"></i>
            </span>
          </a>
        </p>
      </div>
    </nav>


    <div v-if="listData.rows.length > 0">
      <table class="table is-bordered is-striped is-narrow">
        <thead><tr><th>ふせん</th><th>操作</th><th>操作者</th><th>かんばん</th></tr></thead>
        <tbody>
          <notification-list-row v-for="row in listData.rows" :row="row" :key="row.id" @OpenDetailDialog="openDetailDialog"></notification-list-row>
        </tbody>
      </table>
    </div>

    <div v-if="listData.rows.length <= 0 && listData.msg !== ''">
      <article class="message is-danger">
        <div class="message-body">{{listData.msg}}</div>
      </article>
    </div>

    <note-edit-dialog ref="noteEditDialog" :kanbanId="kanbanId" @Refresh="refresh"></note-edit-dialog>
    <note-detail-dialog ref="noteDetailDialog" :kanbanId="kanbanId" @Refresh="refresh" :stages="stages" @OpenEditDialog="openNoteEditDialog"></note-detail-dialog>

  </div>
</template>

<script>

  import Utils from '../../utils'
  import Row from './Row'
  import NoteEditDialog from '../kanban/NoteEditDialog'
  import NoteDetailDialog from '../kanban/NoteDetailDialog'

  export default {
    name: 'notification-list',
    components: {
      'notification-list-row' : Row,
      'note-edit-dialog': NoteEditDialog,
      'note-detail-dialog': NoteDetailDialog
    },
    props:["listData"],
    data() {
      return {
        kanbanId:null,
        stages:[]
      };
    },
    methods:{
      openDetailDialog(e, kanbanId, noteId) {
        const self = this;
        self.kanbanId = kanbanId;

        //現在ふせんが紐づくステージ情報と、かんばんに紐づくステージ一覧(アーカイブ除)を取得して、呼び出す

        //親に対して、対象idの通知情報を削除させる

        self.$refs.noteDetailDialog.openDialog(e, stageId, noteId);
      },
      refresh(e) {
        const self = this;
        self.$emit("Refresh", e);
      },
      unreadAll(e) {
        if(!window.confirm("未読の通知を既読にします。本当によろしいですか？")) {
          return;
        }
      }
    }
  }
</script>
