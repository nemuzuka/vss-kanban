<template>

  <tr :class="{archived : isArchived}">
    <td style="cursor: move;" class="drag-item">
      <span class="icon">
        <i class="fa fa-arrows"></i>
      </span>
    </td>
    <td @click="openEditDialog" style="cursor: pointer;">
      <p>{{item.laneName}} <span class="tag is-danger" v-if="item.completeLane">完了</span></p>
    </td>
    <td>
      <a class="button is-danger is-small" @click="deleteItem">
        <span class="icon">
          <i class="fa fa-times"></i>
        </span>
      </a>
    </td>
  </tr>

</template>

<script>
  export default {
    name: 'settings-lane-item',
    props: ["item", "index"],
    methods: {
      deleteItem(e) {
        if(window.confirm("このレーンを削除すると紐づく付箋も削除しますが本当によろしいですか？(「アーカイブ」することで見えなくすることもできます)") == false) {
          return;
        }

        const self = this;
        self.$emit("DeleteItem", e, self.index);
      },
      openEditDialog(e) {
        if (e && (e.target.tagName.toLocaleLowerCase() === 'a' || e.target.tagName.toLocaleLowerCase() === 'span' || e.target.tagName.toLocaleLowerCase() === 'i')) {
          //aタグの場合、処理終了
          return;
        }
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
