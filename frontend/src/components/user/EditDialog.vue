<template>
  <div class="modal" id="user-edit-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">ユーザ{{buttonLabel}}</p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body">

        <article class="message is-danger" v-if="msg.globalErrMsg !== ''">
          <div class="message-body" v-html="msg.globalErrMsg"></div>
        </article>

        <div>
          <label class="label">ログインID <span class="tag is-danger" v-if="form.id === ''">必須</span></label>
          <p class="control">
            <input class="input" type="text" v-model="form.loginId" :disabled="loginIdDisabledCondition" placeholder="ログインの際に使用するIDです">
            <span class="help is-danger" v-html="msg.loginIdErrMsg"></span>
          </p>
        </div>

        <div>
          <label class="label">ユーザ名 <span class="tag is-danger">必須</span></label>
          <p class="control">
            <input class="input" type="text" v-model="form.userName" placeholder="その人だとわかりやすい名前を入力してください">
            <span class="help is-danger" v-html="msg.userNameErrMsg"></span>
          </p>
        </div>

        <div>
          <label class="label">パスワード <span class="tag is-danger" v-if="form.id === ''">必須</span></label>
          <p class="control">
            <input class="input" type="password" v-model="form.password" placeholder="ログインの際に使用するパスワードです">
            <span class="help is-danger" v-html="msg.passwordErrMsg"></span>
          </p>
        </div>

        <div>
          <label class="label">パスワード(確認用) <span class="tag is-danger" v-if="form.id === ''">必須</span></label>
          <p class="control">
            <input class="input" type="password" v-model="form.passwordConfirm">
            <span class="help is-danger" v-html="msg.passwordConfirmErrMsg"></span>
          </p>
        </div>

        <div>
          <label class="label">Appendix</label>
          <p class="control">
            <label class="checkbox">
              <input type="checkbox" v-model="form.authority" :true-value="1" :false-value="0">
              このユーザはアプリケーション管理者
            </label>
          </p>
        </div>

      </section>
      <footer class="modal-card-foot">
        <a class="button is-info" @click="saveDialog">{{buttonLabel}}</a>
        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>
</template>

<script>

  import Utils from '../../utils'

  export default {
    name: 'user-edit-modal',
    data() {
      return {
        form:{
          loginId:"",
          userName:"",
          password:"",
          passwordConfirm:"",
          authority:"0",
          id:"",
          lockVersion:""
        },
        msg:{
          globalErrMsg:"",
          loginIdErrMsg:"",
          userNameErrMsg:"",
          passwordErrMsg:"",
          passwordConfirmErrMsg:""
        },
        clearMsg(){
          this.msg.globalErrMsg = "";
          this.msg.loginIdErrMsg = "";
          this.msg.userNameErrMsg = "";
          this.msg.passwordErrMsg = "";
          this.msg.passwordConfirmErrMsg = "";
        }
      }
    },
    methods: {
      openEditDialog(id) {
        const param = {
          userId: id === null ? '' : id
        };
        const self = this;
        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'GET',
          url: "/user/detail"
        }).then(
          function (data) {
            if(Utils.alertErrorMsg(data)) {
                return;
            }

            self.setFormData(data.result);
            self.clearMsg();
            Utils.openDialog('user-edit-dialog');
          }
        );
      },
      setFormData(form) {
        const self = this;
        self.form.loginId = form.loginId;
        self.form.userName = form.userName;
        self.form.password = "";
        self.form.passwordConfirm = "";
        self.form.authority = form.authority;
        self.form.id = form.id;
        self.form.lockVersion = form.lockVersion;
      },
      closeDialog() {
        Utils.closeDialog('user-edit-dialog');
      },
      saveDialog(e) {
        const self = this;
        self.clearMsg();

        Utils.setAjaxDefault();
        $.ajax({
          data: self.form,
          method: 'POST',
          url: "/user/store"
        }).then(
          function (data) {
            //エラーが存在する場合、その旨記述
            if(Utils.writeErrorMsg(self, data)) {
              return;
            }

            Utils.viewInfoMsg(data);
            setTimeout(function(){
              Utils.closeDialog('user-edit-dialog');
              self.$emit("Refresh", e);
            },1500);
          }
        );
      }
    },
    computed: {
      buttonLabel() {
        const self = this;
        return self.form.id === "" ? '登録' : '変更';
      },
      loginIdDisabledCondition() {
        const self = this;
        return self.form.id !== "";
      }
    }
  }
</script>
