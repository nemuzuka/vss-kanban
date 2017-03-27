<template>
  <div class="container">
    <notification-list :listData="listData" @Refresh="refresh" @RemoveNotification="removeNotification"></notification-list>
  </div>
</template>

<script>
  import Utils from '../../utils'
  import NotificationList from './List'

  export default {
    components: {
      'notification-list': NotificationList
    },
    methods: {
      refresh(e) {
        const self = this;
        Utils.setAjaxDefault();
        $.ajax({
          method: 'GET',
          url: "/notification/list"
        }).then(
          function (data) {
            const rows = data.result;
            self.listData.rows.splice(0,self.listData.rows.length);
            self.listData.rows.push(...rows);

            if(rows.length === 0) {
              self.listData.msg = '未読の通知はありません';
            } else {
              self.listData.msg = '';
            }
          }
        );
      },
      removeNotification(e, targetId){
        //対象の行を削除する
        const self = this;
        const targetIndex = self.listData.rows.findIndex(function(element, index, array){
          return element.id === targetId;
        });
        if(targetIndex !== -1) {
          self.listData.rows.splice(targetIndex, 1);
          if(self.listData.rows.length === 0) {
            self.listData.msg = '未読の通知はありません';
          }
        }
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
    created() {
      const self = this;
      self.refresh();
    }
  }
</script>
