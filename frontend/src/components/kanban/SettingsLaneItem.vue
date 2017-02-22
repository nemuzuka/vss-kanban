<template>

  <div class="box" :class="{archived : isArchived}">
    <div class="media">
      <div class="media-left" style="cursor: move;">
      <span class="icon">
        <i class="fa fa-arrows"></i>
      </span>
      </div>

      <div class="media-content" @click="openEditDialog" style="cursor: pointer;">
        <div class="content">
          <p>{{item.laneName}} <span class="tag is-danger" v-if="item.completeLane">完了</span></p>
        </div>
      </div>

      <div class="media-right">
        <a class="button is-danger is-small" @click="deleteItem">
        <span class="icon">
          <i class="fa fa-times"></i>
        </span>
        </a>
      </div>

    </div>
  </div>

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
  .box.archived {
    background-color: #eee;
  }
</style>
