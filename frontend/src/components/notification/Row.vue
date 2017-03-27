<template>
  <tr style="cursor: pointer;" @click.stop="openDetailDialog">
    <td class="large">{{row.noteTitle}}</td>
    <td>{{row.actionLabel}}</td>
    <td><small>{{row.createUserName}} ({{viewCreateAt}})</small></td>
    <td>{{row.kanbanTitle}}</td>
  </tr>
</template>

<script>

  import Utils from '../../utils'

  export default {
    name: 'notification-list-row',
    props: ["row"],
    methods: {
      openDetailDialog(e){
        const self = this;
        self.$emit("OpenDetailDialog", e, self.row);
      }
    },
    computed: {
      viewCreateAt() {
        const self = this;
        return Utils.toDateTimeString(self.row.createAt, "YY/MM/DD HH:mm");
      }
    }
  }

</script>
