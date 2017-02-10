<template>
  <div class="container">
    <user-list :listData="listData" @Refresh="refresh" @OpenEditDialog="openEditDialog"></user-list>
    <user-edit-dialog ref="editDialog" @Refresh="refresh"></user-edit-dialog>
  </div>
</template>

<script>
  import Utils from '../../utils'
  import UserList from './List'
  import UserEditDialog from './EditDialog'

  export default {
    components: {
      'user-list': UserList,
      'user-edit-dialog': UserEditDialog,
    },
    methods: {
      refresh : function(e) {
        const self = this;
        Utils.setAjaxDefault();
        $.ajax({
          method: 'GET',
          url: "/user/all"
        }).then(
          function (data) {
            const rows = data.result;
            self.listData.rows.splice(0,self.listData.rows.length);
            self.listData.rows.push(...rows);

            if(rows.length === 0) {
              self.listData.msg = '登録されているデータはありません';
            }
          }
        );
      },
      openEditDialog:function(e, id) {
        const self = this;
        self.$refs.editDialog.openEditDialog(id);
      }
    },
    data() {
      return {
        listData:{
          rows:[],
          msg:""
        }
      }
    },
    created: function() {
      const self = this;
      self.refresh();
    }
  }
</script>
