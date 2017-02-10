<template>
  <tr style="cursor: pointer;" @click="openEditDialog">
    <td class="large">{{row.name}}</td>
    <td>{{row.loginId}}</td>
    <td class="has-text-centered">{{authority}}</td>
    <td>
      <a class="button is-danger is-small" @click="deleteRow">
        <span class="icon is-small">
          <i class="fa fa-times"></i>
        </span>
      </a>
    </td>
  </tr>
</template>

<script>
  export default {
    name: 'user-list-row',
    props: ["row"],
    methods: {
      openEditDialog:function(e){
        if (e && (e.target.tagName.toLocaleLowerCase() === 'a' || e.target.tagName.toLocaleLowerCase() === 'span' || e.target.tagName.toLocaleLowerCase() === 'i')) {
          //aタグの場合、処理終了
          return;
        }
        const self = this;
        self.$emit("OpenEditDialog", e, self.row.id);
      },
      deleteRow:function(e){
          alert("消せって言われたよ");
      }
    },
    computed: {
      authority:function() {
        const self = this;
        return self.row.authority === '1' ? '◯' : '';
      }
    }
  }

</script>
