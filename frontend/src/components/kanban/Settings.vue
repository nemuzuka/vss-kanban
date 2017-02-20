<template>

  <div style="height: 100%" id="kanban-settings-area" class="hide">
    <p class="back"><a @click="hideContext"><i class="fa fa-chevron-left"></i></a></p>
    <div class="container">

      <div class="content-view">
        <div class="columns">
          <div class="column is-2">
            <aside class="menu">
              <ul class="menu-list">
                <li><a :class="selectedBaseMenu" @click="changeMenu('1')">基本情報</a></li>
                <li><a :class="selectedLaneMenu" @click="changeMenu('2')">レーン</a></li>
                <li><a :class="selectedJoinMenu" @click="changeMenu('3')">担当者</a></li>
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
                    <input class="input" type="text" v-model="form.kanbanTitle" placeholder="かんばんのタイトルを入力してください">
                    <span class="help is-danger" v-html="msg.kanbanTitleErrMsg"></span>
                  </p>
                </div>

                <div>
                  <label class="label">かんばん説明</label>
                  <p class="control">
                    <textarea v-model="form.kanbanDescription" placeholder="かんばんの説明を入力してください" class="textarea"></textarea>
                    <span class="help is-danger" v-html="msg.kanbanDescriptionErrMsg"></span>
                  </p>
                </div>

                <div>
                  <label class="label">Appendix</label>
                  <p class="control">
                    <label class="checkbox">
                      <input type="checkbox" v-model="form.archiveStatus" :true-value="1" :false-value="0">
                      このかんばんをアーカイブする
                    </label>
                  </p>
                </div>
              </div>
            </div>

            <div class="has-text-right">
              <a class="button is-info">変更</a>
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
              </div>
            </div>

            <div class="has-text-right">
              <a class="button is-info">変更</a>
            </div>

          </div>

          <!-- 担当者 -->
          <div class="column is-9 margin" v-if="menuType === '3'">
            <nav class="level">
              <div class="level-left">
                <div class="level-item">
                  <h1 class="title">担当者変更</h1>
                </div>
              </div>
            </nav>

            <div class="message is-dark">
              <div class="message-body">

                <article class="message is-danger" v-if="msg.globalErrMsg !== ''">
                  <div class="message-body" v-html="msg.globalErrMsg"></div>
                </article>
              </div>
            </div>

            <div class="has-text-right">
              <a class="button is-info">変更</a>
            </div>

          </div>

        </div>
      </div>

    </div>
  </div>

</template>

<script>

  import Utils from '../../utils'

  export default {
    name: 'kanban-settings',
    props:["kanbanId"],
    data() {
      return {
        menuType:"1",
        form:{
          kanbanTitle:"",
          archiveStatus:"0",
          kanbanDescription:""
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
      refresh() {
        const self = this;
        self.changeMenu("1");
      },
      hideContext(e) {
        const self = this;
        $('#body').addClass("kanban-detail");
        $("#kanban-settings-area").addClass("hide");
        $('#kanban-main-area').removeClass("hide");
        Utils.moveTop();
        self.$emit("Refresh", e);
      },
      changeMenu(menuType) {
        const self = this;
        self.menuType = menuType;
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
    }
  }

</script>

<style scoped>
.menu {
  background: #fff;
  border: 1px solid #363636;
}
</style>
