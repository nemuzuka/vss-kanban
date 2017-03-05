<template>

  <tr :class="{archived : isArchived}">
    <td style="cursor: move;" class="drag-item">
      <span class="icon">
        <i class="fa fa-arrows"></i>
      </span>
    </td>
    <td @click.stop="openEditDialog" style="cursor: pointer;">
      <p>{{item.stageName}} <span class="tag is-danger" v-if="item.completeStage">完了</span></p>
    </td>
    <td>
      <a class="button is-danger is-small" @click.stop="deleteItem">
        <span class="icon">
          <i class="fa fa-times"></i>
        </span>
      </a>
    </td>
  </tr>

</template>

<script>
  export default {
    name: 'settings-stage-item',
    props: ["item", "index"],
    methods: {
      deleteItem(e) {
        if(window.confirm("このステージを削除すると紐づく付箋も削除しますが本当によろしいですか？(「アーカイブ」することで見えなくすることもできます)") == false) {
          return;
        }

        const self = this;
        self.$emit("DeleteItem", e, self.index);
      },
      openEditDialog(e) {
        const self = this;
        self.$emit("OpenEditDialog", e, self.item, self.index);
      }
    },
    computed: {
      isArchived() {
        const self = this;
        return self.item.archiveStatus==='1'
      }
    }
  }
</script>

<style scoped>
  tr.archived {
    background-color: #eee;
  }
</style>
