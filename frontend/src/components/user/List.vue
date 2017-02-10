<template>
  <div class="content">

    <nav class="level">
      <!-- Left side -->
      <div class="level-left">
        <div class="level-item">
          <h1 class="title">ユーザ管理</h1>
        </div>
      </div>

      <!-- Right side -->
      <div class="level-right">

        <p class="level-item">
          <a class="button is-success" @click="refresh">
            <span class="icon">
            <i class="fa fa-refresh"></i>
            </span>
          </a>

          <a class="button is-success" @click="openEditDialog(e,null)">
            <span class="icon">
            <i class="fa fa-plus"></i>
            </span>
          </a>
        </p>
      </div>
    </nav>


    <div v-if="listData.rows.length > 0">
      <table class="table is-bordered is-striped is-narrow">
        <thead><tr><th>名前</th><th>ログインID</th><th>アプリケーション管理者</th><th style="width:20px"></th></tr></thead>
        <tbody>
        <user-list-row v-for="row in listData.rows" :row="row" @OpenEditDialog="openEditDialog"></user-list-row>
        </tbody>
      </table>
    </div>

    <div v-if="listData.rows.length <= 0 && listData.msg !== ''">
      <span>{{listData.msg}}</span>
    </div>

  </div>
</template>

<script>

  import Row from './Row'

  export default {
    name: 'user-list',
    components: {
      'user-list-row' : Row
    },
    props:["listData", "refresh"],
    methods:{
      openEditDialog:function(e, id) {
        const self = this;
        self.$emit("OpenEditDialog", e, id);
      },
      refresh: function(e) {
        const self = this;
        self.$emit("Refresh", e);
      }
    }
  }
</script>
