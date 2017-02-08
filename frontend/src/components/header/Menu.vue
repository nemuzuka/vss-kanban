<template>
  <nav class="nav" style="margin-bottom:1em;">
    <div class="nav-left">
      <a class="nav-item" @click="moveTop">
        VSS Kanban
      </a>
    </div>

    <span class="nav-toggle" @click="toggleNavMenu">
      <span></span>
      <span></span>
      <span></span>
    </span>

    <div class="nav-right nav-menu"
         :class="{'is-active': isMenuShow}">
      <a class="nav-item" @click="moveAdmin" v-if="authority === '1'">
        管理者機能へ
      </a>

      <span class="nav-item">
        <a class="button" @click="logout">
          <span class="icon">
            <i class="fa fa-sign-out"></i>
          </span>
          <span>ログアウト</span>
        </a>
      </span>
    </div>

  </nav>
</template>

<script>

  import Utils from '../../utils'

  export default {
    name: 'app-header',
    methods: {
      moveAdmin:function(e) {
        Utils.moveUrl("/user");
      },
      moveTop:function(e) {
        Utils.moveUrl("/top");
      },
      toggleNavMenu:function(e){
        const self = this;
        self.isMenuShow = !self.isMenuShow
      },
      logout:function(e) {
        if(!window.confirm("ログアウトしてもよろしいですか？")) {
            return;
        }

        Utils.setAjaxDefault();
        $.ajax({
          method: 'GET',
          url: "/logout"
        }).then(
          function (data) {
            Utils.moveUrl("/");
          }
        );
      }
    },
    data() {
      return {
        authority:'',
        isMenuShow:false
      }
    },
    created: function() {
      const userInfo = JSON.parse(localStorage.getItem("kanbanUserInfo"));
      const self = this;
      self.authority = userInfo.authority;
    }
  }
</script>
