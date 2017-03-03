<template>
  <div class="content-view">

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
        <thead><tr><th style="width:20px"></th><th>ユーザ名</th><th>ログインID</th><th style="width:200px">アプリケーション管理者</th><th style="width:20px"></th></tr></thead>

        <draggable :list="listData.rows" :options="{group:'users',handle:'.drag-item'}" element="tbody" @start="drag=true" @end="drag=false" @change="updateSortNum">
          <user-list-row v-for="row in listData.rows" :row="row" @OpenEditDialog="openEditDialog" @Refresh="refresh"></user-list-row>
        </draggable>

      </table>
    </div>

    <div v-if="listData.rows.length <= 0 && listData.msg !== ''">
      <span>{{listData.msg}}</span>
    </div>

  </div>
</template>

<script>

  import Utils from '../../utils'
  import Row from './Row'
  import Draggable from 'vuedraggable'

  export default {
    name: 'user-list',
    components: {
      'user-list-row' : Row,
      'draggable':Draggable
    },
    props:["listData"],
    methods:{
      openEditDialog(e, id) {
        const self = this;
        self.$emit("OpenEditDialog", e, id);
      },
      refresh(e) {
        const self = this;
        self.$emit("Refresh", e);
      },
      updateSortNum() {
        const self = this;
        const param = {
          userIds: self.listData.rows.map(function(element){
            return element.id
          })
        };
        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'POST',
          url: "/user/update/sort"
        }).then(
          function (data) {
            self.$emit("Refresh", null);
          }
        );
      }
    }
  }
</script>
