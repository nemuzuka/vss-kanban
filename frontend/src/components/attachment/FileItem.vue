<template>
  <div class="box">
    <article class="media">
      <div class="media-content">
        <div class="content">
          <div>
            <a @click="downloadFile">{{item.realFileName}}</a>
            <a class="button is-danger is-small" @click="deleteItem" v-if="type === 'edit'">
              <span class="icon is-small">
                <i class="fa fa-times"></i>
              </span>
            </a>
          </div>
          <div class="image is-128x128" v-if="typeof item.thumbnailWidth !== 'undefined'">
            <a :href="createOriginalUrl" data-lightbox="sample" :data-title="item.realFileName">
              <img :src="createUrl">
            </a>
          </div>
        </div>
      </div>
    </article>
  </div>
</template>

<script>

  import 'lightbox2/dist/js/lightbox.min.js'
  import 'lightbox2/dist/css/lightbox.css'

  export default {
    name: 'file-item',
    props:["item", "type", "index"],
    methods:{
      deleteItem(e) {
        const self = this;
        self.$emit("DeleteItem", e, self.index);
      },
      downloadFile(e) {
        const self = this;
        const form = $('#downloader-form');

        const csrfToken = $('meta[name=csrf-token]').attr("content") || $(event.target).data('csrf-token');
        form.empty();
        form.append($("<input>").attr({"type":"hidden", "name":"attachmentFileId"}).val(self.item.attachmentFileId));
        form.append($("<input>").attr({"type":"hidden", "name":"fileImageType"}).val("1"));
        form.append($("<input>").attr({"type":"hidden", "name":"csrf-token"}).val(csrfToken));
        form.attr({"action":"/attachment/dl"});
        form[0].submit(function () {
          return false;
        });
      }
    },
    computed:{
      createUrl() {
        const self = this;
        return "/attachment/dl?attachmentFileId=" + self.item.attachmentFileId + "&fileImageType=2"
      },
      createOriginalUrl() {
        const self = this;
        return "/attachment/dl?attachmentFileId=" + self.item.attachmentFileId + "&fileImageType=1"
      }
    }
  }

</script>
