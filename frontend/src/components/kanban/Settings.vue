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
                <li><a :class="selectedLaneMenu" @click="refreshLanes">レーン</a></li>
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
              <a class="button is-info" @click="saveBase">変更</a>
            </div>

          </div>


          <!-- レーン -->
          <div class="column is-9 margin" v-if="menuType === '2'">
            <nav class="level">
              <div class="level-left">
                <div class="level-item">
                  <h1 class="title">レーン変更</h1>
                </div>
              </div>
            </nav>

            <div class="message is-dark">
              <div class="message-body">

                <article class="message is-danger" v-if="msg.globalErrMsg !== ''">
                  <div class="message-body" v-html="msg.globalErrMsg"></div>
                </article>

                <settings-lane :lane="lane"></settings-lane>

              </div>
            </div>

            <div class="has-text-right">
              <a class="button is-info" @click="saveLanes">変更</a>
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
              <a class="button is-info" @click="saveJoinedUsers">変更</a>
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
  import SettingsLane from './SettingsLane'

  export default {
    name: 'kanban-settings',
    props:["kanbanId"],
    components:{
      'settings-lane':SettingsLane
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
        lane:{
          id:null,
          lockVersion:null,
          lanes:[],
          lanesMsg:""
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
        const self = this;
        $('#body').addClass("kanban-detail");
        $("#kanban-settings-area").addClass("hide");
        $('#kanban-main-area').removeClass("hide");
        Utils.moveTop();
        self.$emit("Refresh", e);
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

            autosize.destroy(document.querySelector('#kanban-settings-area textarea'));
            autosize(document.querySelector('#kanban-settings-area textarea'));

            self.menuType = "1";
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
      refreshLanes(){
        const self = this;
        self.clearMsg();
        self.lane.lanesMsg = "";

        const param = {
          kanbanId : self.kanbanId
        };
        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'GET',
          url: "/kanban/admin/lanes"
        }).then(
          function (data) {
            if(Utils.alertErrorMsg(data)) {
              return;
            }

            const msg = "レーンは登録されていません";
            self.lane.id = data.result.id;
            self.lane.lockVersion = data.result.lockVersion;

            self.lane.lanes.splice(0,self.lane.lanes.length);
            self.lane.lanes.push(...data.result.lanes);

            if(self.lane.lanes.length <= 0) {
              self.lane.lanesMsg = msg;
            }

            self.menuType = "2";
          }
        );
      },
      saveLanes() {
        const self = this;
        const param = {
          id: self.lane.id,
          lockVersion: self.lane.lockVersion,
          laneIds: self.lane.lanes.map( function(element) {
            return Utils.isUniqueStr(element.laneId) ? "" : element.laneId;
          }),
          laneNames:self.lane.lanes.map( function(element) {
            return element.laneName;
          }),
          archiveStatuses:self.lane.lanes.map( function(element) {
            return element.archiveStatus;
          }),
          completeLanes:self.lane.lanes.map( function(element) {
            return element.completeLane;
          })
        };
        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'POST',
          url: "/kanban/admin/updateLanes"
        }).then(
          function (data) {
            if(Utils.writeErrorMsg(self, data)) {
              return;
            }
            Utils.viewInfoMsg(data);
            setTimeout(function(){
              self.refreshLanes();
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
      selectedLaneMenu() {
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
    },
    created() {
      autosize(document.querySelector('#kanban-settings-area textarea'));
    }
  }

</script>

<style scoped>
.menu {
  background: #fff;
  border: 1px solid #363636;
}
</style>
