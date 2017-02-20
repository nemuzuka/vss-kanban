<template>

  <div class="columns is-mobile">
    <div class="column is-half is-offset-one-quarter">

      <div class="message is-dark">
        <div class="message-body">

          <article class="message is-danger" v-if="msg.globalErrMsg !== ''">
            <div class="message-body" v-html="msg.globalErrMsg"></div>
          </article>

          <label class="label">ログインID <span class="tag is-danger">必須</span></label>
          <p class="control">
            <input class="input" type="text" placeholder="ログインIDを入力してください" v-model="form.loginId">
            <span class="help is-danger" v-html="msg.loginIdErrMsg"></span>
          </p>

          <label class="label">パスワード <span class="tag is-danger">必須</span></label>
          <p class="control">
            <input class="input" type="password" placeholder="パスワードを入力してください" v-model="form.password" @keyup.enter="login">
            <span class="help is-danger" v-html="msg.passwordErrMsg"></span>
          </p>

          <p class="control">
            <label class="checkbox">
              <input type="checkbox" v-model="form.rememberMe">
              ログイン情報を記憶する
            </label>
          </p>

          <div class="has-text-centered">
            <a class="button is-info is-large login" @click="login">
              <span class="icon is-medium">
                <i class="fa fa-sign-in"></i>
              </span>
              <span>ログイン</span>
            </a>
          </div>

        </div>
      </div>

    </div>
  </div>

</template>

<script>

  import Utils from './utils'

  export default {
    name: 'login',
    methods: {
      login(e) {
        const self = this;
        if(self.form.rememberMe === true) {
          //localStorageの値を書き換える
          const memoryLoginInfoJson = {
            loginId: self.form.loginId,
            password: self.form.password,
            rememberMe: self.form.rememberMe
          };
          localStorage.setItem("kanbanMemoryLoginInfo", JSON.stringify(memoryLoginInfoJson));
        } else {
          //localstrageの値を削除
          localStorage.removeItem("kanbanMemoryLoginInfo")
        }

        self.clearMsg();

        Utils.setAjaxDefault();
        $.ajax({
          data: self.form,
          method: 'POST',
          url: "/login"
        }).then(
          function (data) {
            //エラーが存在する場合、その旨記述
            if(Utils.writeErrorMsg(self, data, false)) {
                return;
            }
            Utils.moveUrl("/top");
          }
        );
      }
    },
    data() {
      return {
        form:{
          loginId:"",
          password:"",
          rememberMe:false
        },
        msg:{
          globalErrMsg:"",
          loginIdErrMsg:"",
          passwordErrMsg:""
        },
        clearMsg(){
          this.msg.globalErrMsg = "";
          this.msg.loginIdErrMsg = "";
          this.msg.passwordErrMsg = "";
        }
      }
    },
    created() {
      const self = this;
      const memoryLoginInfo = localStorage.getItem("kanbanMemoryLoginInfo");
      if(memoryLoginInfo !== null) {
        const memoryLoginInfoJson = JSON.parse(memoryLoginInfo);
        self.form.loginId = memoryLoginInfoJson.loginId;
        self.form.password = memoryLoginInfoJson.password;
        self.form.rememberMe = memoryLoginInfoJson.rememberMe;
      }
    }
  }
</script>

<style scoped>
  .message-body a {
    text-decoration: none;
  }
</style>
