<template>

  <div style="height: 100%" id="kanban-settings-area" class="hide">
    <p class="back"><a @click="hideContext"><i class="fa fa-chevron-left"></i></a></p>
    <div class="container">

      <div class="content-view">
        <div class="columns">
          <div class="column is-2">
            <aside class="menu">
              <ul class="menu-list">
                <li><a :class="selectedBaseMenu" @click="refreshBase">基本情報</a></li>
                <li><a :class="selectedStageMenu" @click="refreshStages">ステージ</a></li>
                <li><a :class="selectedJoinMenu" @click="refreshJoinedUsers">参加者</a></li>
              </ul>
            </aside>
          </div>

          <div class="column is-1"></div>

          <!-- 基本情報 -->
          <div class="column is-9 margin" v-if="menuType === '1'">

            <nav class="level">
              <div class="level-left">
                <div class="level-item">
                  <h1 class="title">基本情報変更</h1>
                </div>
              </div>
            </nav>

            <div class="message is-dark">
              <div class="message-body">

                <article class="message is-danger" v-if="msg.globalErrMsg !== ''">
                  <div class="message-body" v-html="msg.globalErrMsg"></div>
                </article>

                <div>
                  <label class="label">かんばんタイトル <span class="tag is-danger">必須</span></label>
                  <p class="control">
                    <input class="input" type="text" v-model="baseForm.kanbanTitle" placeholder="かんばんのタイトルを入力してください">
                    <span class="help is-danger" v-html="msg.kanbanTitleErrMsg"></span>
                  </p>
                </div>

                <div>
                  <label class="label">かんばん説明</label>
                  <p class="control">
                    <textarea v-model="baseForm.kanbanDescription" placeholder="かんばんの説明を入力してください" class="textarea"></textarea>
                    <span class="help is-danger" v-html="msg.kanbanDescriptionErrMsg"></span>
                  </p>
                </div>

                <div>
                  <label class="label">Appendix</label>
                  <p class="control">
                    <label class="checkbox">
                      <input type="checkbox" v-model="baseForm.archiveStatus" :true-value="1" :false-value="0">
                      このかんばんをアーカイブする
                    </label>
                  </p>
                </div>
              </div>
            </div>

            <div class="has-text-right">
              <a class="button is-danger" @click="deleteKanban">
                <span class="icon"><i class="fa fa-times"></i></span>
                <span>削除</span>
              </a>
              <a class="button is-info" @click="saveBase">
                <span class="icon is-small">
                  <i class="fa fa-floppy-o"></i>
                </span>
                <span>変更</span>
              </a>
            </div>

          </div>


          <!-- ステージ -->
          <div class="column is-9 margin" v-if="menuType === '2'">
            <nav class="level">
              <div class="level-left">
                <div class="level-item">
                  <h1 class="title">ステージ変更</h1>
                </div>
              </div>
            </nav>

            <div class="message is-dark">
              <div class="message-body">

                <article class="message is-danger" v-if="msg.globalErrMsg !== ''">
                  <div class="message-body" v-html="msg.globalErrMsg"></div>
                </article>

                <settings-stage :stage="stage"></settings-stage>

              </div>
            </div>

            <div class="has-text-right">
              <a class="button is-info" @click="saveStages">
                <span class="icon is-small">
                  <i class="fa fa-floppy-o"></i>
                </span>
                <span>変更</span>
              </a>
            </div>

          </div>

          <!-- 参加者 -->
          <div class="column is-9 margin" v-if="menuType === '3'">
            <nav class="level">
              <div class="level-left">
                <div class="level-item">
                  <h1 class="title">参加者変更</h1>
                </div>
              </div>
            </nav>

            <div class="message is-dark">
              <div class="message-body">

                <article class="message is-danger" v-if="msg.globalErrMsg !== ''">
                  <div class="message-body" v-html="msg.globalErrMsg"></div>
                </article>


                <table class="table is-bordered is-striped is-narrow">
                  <thead><tr><th style="width:170px">このかんばんに参加する</th><th>名前</th><th style="width:140px">かんばんの管理者</th></tr></thead>
                  <tbody>

                    <template v-for="row in joinedUser.allUsers">
                      <tr>
                        <td class="has-text-centered"><input type="checkbox" v-model="joinedUser.joinedUserIds" :value="row.id"></td>
                        <td>{{row.name}}</td>
                        <td class="has-text-centered"><input type="checkbox" v-model="joinedUser.adminUserIds" :value="row.id"></td>
                      </tr>
                    </template>

                  </tbody>
                </table>

              </div>
            </div>

            <div class="has-text-right">
              <a class="button is-info" @click="saveJoinedUsers">
                <span class="icon is-small">
                  <i class="fa fa-floppy-o"></i>
                </span>
                <span>変更</span>
              </a>
            </div>

          </div>

        </div>
      </div>

    </div>
  </div>

</template>

<script>

  import autosize from 'autosize/dist/autosize'
  import Utils from '../../utils'
  import SettingsStage from './SettingsStage'

  export default {
    name: 'kanban-settings',
    props:["kanbanId"],
    components:{
      'settings-stage':SettingsStage
    },
    data() {
      return {
        menuType:"1",
        baseForm:{
          id:"",
          kanbanTitle:"",
          archiveStatus:"0",
          kanbanDescription:"",
          lockVersion:""
        },
        joinedUser: {
          id:null,
          lockVersion:null,
          allUsers:[],
          joinedUserIds:[],
          adminUserIds:[],
        },
        stage:{
          id:null,
          lockVersion:null,
          stages:[],
          stagesMsg:""
        },
        msg:{
          globalErrMsg:"",
          kanbanTitleErrMsg:"",
          kanbanDescriptionErrMsg:"",
        },
        clearMsg(){
          this.msg.globalErrMsg = "";
          this.msg.kanbanTitleErrMsg = "";
          this.msg.kanbanDescriptionErrMsg = "";
        }

      }
    },
    methods: {
      refresh(callBack) {
        const self = this;
        self.refreshBase(callBack);
      },
      hideContext(e) {
        const calBack = () => {
          $('#body').addClass("kanban-detail");
          $("#kanban-settings-area").addClass("hide");
          $('#kanban-main-area').removeClass("hide");
          Utils.moveTop();
        };
        const self = this;
        self.$emit("Refresh", e, calBack);
      },
      refreshBase(callBack) {
        const self = this;
        self.clearMsg();

        const param = {
          kanbanId : self.kanbanId
        };
        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'GET',
          url: "/kanban/admin/base"
        }).then(
          function (data) {
            if(Utils.alertErrorMsg(data)) {
              return;
            }

            const form = data.result;
            self.baseForm.id = form.id;
            self.baseForm.kanbanTitle = form.kanbanTitle;
            self.baseForm.kanbanDescription = form.kanbanDescription;
            self.baseForm.archiveStatus = form.archiveStatus;
            self.baseForm.lockVersion = form.lockVersion;

            self.menuType = "1";

            setTimeout(function(){
              const ta = document.querySelector('#kanban-settings-area textarea');
              autosize(ta);
              autosize.update(ta);
            }, 500);

            if(typeof callBack === 'function') {
              callBack();
            }
          }
        );
      },
      saveBase() {
        const self = this;
        self.clearMsg();
        Utils.setAjaxDefault();
        $.ajax({
          data: self.baseForm,
          method: 'POST',
          url: "/kanban/admin/updateBase"
        }).then(
          function (data) {
            if(Utils.writeErrorMsg(self, data)) {
              return;
            }
            Utils.viewInfoMsg(data);
            setTimeout(function(){
              self.refreshBase();
            },1500);
          }
        );
      },
      deleteKanban(e) {
        if(window.confirm("このかんばんを削除して本当によろしいですか？(「アーカイブ」することで見えなくすることもできます)") == false) {
            return;
        }
        const self = this;
        const param = {
          id: self.baseForm.id,
          lockVersion: self.baseForm.lockVersion
        };
        self.clearMsg();
        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'POST',
          url: "/kanban/admin/delete"
        }).then(
          function (data) {
            if(Utils.writeErrorMsg(self, data)) {
              return;
            }
            Utils.viewInfoMsg(data);
            setTimeout(function(){
              $('#body').addClass("kanban-detail");
              $("#kanban-settings-area").addClass("hide");
              self.$emit("Back", e);
            },1500);
          }
        );
      },
      refreshStages(){
        const self = this;
        self.clearMsg();
        self.stage.stagesMsg = "";

        const param = {
          kanbanId : self.kanbanId
        };
        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'GET',
          url: "/kanban/admin/stages"
        }).then(
          function (data) {
            if(Utils.alertErrorMsg(data)) {
              return;
            }

            const msg = "ステージは登録されていません";
            self.stage.id = data.result.id;
            self.stage.lockVersion = data.result.lockVersion;

            self.stage.stages.splice(0,self.stage.stages.length);
            self.stage.stages.push(...data.result.stages);

            if(self.stage.stages.length <= 0) {
              self.stage.stagesMsg = msg;
            }

            self.menuType = "2";
          }
        );
      },
      saveStages() {
        const self = this;
        const param = {
          id: self.stage.id,
          lockVersion: self.stage.lockVersion,
          stageIds: self.stage.stages.map( function(element) {
            return Utils.isUniqueStr(element.stageId) ? "" : element.stageId;
          }),
          stageNames:self.stage.stages.map( function(element) {
            return element.stageName;
          }),
          archiveStatuses:self.stage.stages.map( function(element) {
            return element.archiveStatus;
          }),
          completeStages:self.stage.stages.map( function(element) {
            return element.completeStage;
          })
        };
        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'POST',
          url: "/kanban/admin/updateStages"
        }).then(
          function (data) {
            if(Utils.writeErrorMsg(self, data)) {
              return;
            }
            Utils.viewInfoMsg(data);
            setTimeout(function(){
              self.refreshStages();
            },1500);
          }
        );
      },
      refreshJoinedUsers() {
        const self = this;
        self.clearMsg();

        const param = {
          kanbanId : self.kanbanId
        };
        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'GET',
          url: "/kanban/admin/joinedUsers"
        }).then(
          function (data) {
            if(Utils.alertErrorMsg(data)) {
              return;
            }

            self.joinedUser.id = data.result.id;
            self.joinedUser.lockVersion = data.result.lockVersion;

            self.joinedUser.allUsers.splice(0,self.joinedUser.allUsers.length);
            self.joinedUser.allUsers.push(...data.result.allUsers);

            self.joinedUser.joinedUserIds.splice(0, self.joinedUser.joinedUserIds.length);
            self.joinedUser.joinedUserIds.push(...data.result.joinedUsers.map(function(element){
                return element.userId;
            }));

            self.joinedUser.adminUserIds.splice(0,self.joinedUser.adminUserIds.length);
            const adminUsers = data.result.joinedUsers.filter(function(element){
              if(element.authority === '1') return true;
            });
            self.joinedUser.adminUserIds.push(...adminUsers.map(function(element){
                return element.userId;
            }));

            self.menuType = "3";

          }
        );
      },
      saveJoinedUsers() {
        const self = this;
        self.clearMsg();

        const param = {
          id:self.joinedUser.id,
          lockVersion:self.joinedUser.lockVersion,
          joinedUserIds:self.joinedUser.joinedUserIds,
          adminUserIds:self.joinedUser.adminUserIds
        };

        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'POST',
          url: "/kanban/admin/updateJoinedUsers"
        }).then(
          function (data) {
            if(Utils.writeErrorMsg(self, data)) {
              return;
            }
            Utils.viewInfoMsg(data);
            setTimeout(function(){
              self.refreshJoinedUsers();
            },1500);
          }
        );
      }
    },
    computed: {
      selectedBaseMenu() {
        const self = this;
        return {
          'is-active': self.menuType === '1'
        }
      },
      selectedStageMenu() {
        const self = this;
        return {
          'is-active': self.menuType === '2'
        }
      },
      selectedJoinMenu() {
        const self = this;
        return {
          'is-active': self.menuType === '3'
        }
      }
    }
  }

</script>

<style scoped>
.menu {
  background: #fff;
  border: 1px solid #363636;
}
</style>
