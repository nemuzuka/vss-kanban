<template>
  <div class="box">
    <article class="media">
      <div class="media-content">
        <div class="content">
          <div>
            {{item.realFileName}}
            <a class="button is-danger is-small" @click="deleteItem" v-if="type === 'edit'">
              <span class="icon is-small">
                <i class="fa fa-times"></i>
              </span>
            </a>
          </div>
          <div class="image is-128x128" v-if="typeof item.thumbnailWidth !== 'undefined'">
            <img :src="createUrl">
          </div>
        </div>
      </div>
    </article>
  </div>
</template>

<script>
  export default {
    name: 'file-item',
    props:["item", "type", "index"],
    methods:{
      deleteItem(e) {
        const self = this;
        self.$emit("DeleteItem", e, self.index);
      }
    },
    computed:{
      createUrl() {
        const self = this;
        return "/attachment/dl?attachmentFileId=" + self.item.attachmentFileId + "&fileImageType=2"
      }
    }
  }

</script>
