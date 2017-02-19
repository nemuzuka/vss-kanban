<template>
  <div id="kanban-main-area" class="hide content-view" style="height: 100%">

    <p class="back"><a @click="hideContext"><i class="fa fa-chevron-left"></i></a></p>

    <div class="kanban-title-area">
      <nav class="level">
        <!-- Left side -->
        <div class="level-left">
          <div class="level-item">
            <h1 class="title">{{kanban.title}}</h1>
          </div>
        </div>

        <!-- Right side -->
        <div class="level-right">

          <p class="level-item">

            <label class="checkbox" style="margin-right: .75rem;">
              <input type="checkbox" v-model="includeArchive" @change="refresh">
              アーカイブ済みのレーンや付箋も見る
            </label>

            <a v-if="kanbanAttachmentFiles.length > 0" @click="openAttachmentDialog">
              <span class="tag is-black is-large">添付有</span>
            </a>

            <a class="button is-success" @click="openAttachmentUploadDialog">
              <span class="icon">
                <i class="fa fa-paperclip"></i>
              </span>
            </a>

            <a class="button is-success" v-if="kanban.authority === '1'">
              <span class="icon">
                <i class="fa fa-cog"></i>
              </span>
            </a>

            <a class="button is-success" @click="refresh">
              <span class="icon">
                <i class="fa fa-refresh"></i>
              </span>
            </a>
          </p>

        </div>

      </nav>
    </div>

    <div class="columns is-mobile kanban-main-container" id="kanban-main-context">
      <div class="column is-one-quarter-desktop is-one-quarter-tablet is-half-mobile" style="height:100%">
        <div class="lane-container is-info">

          <div class="card-header">
            <p class="card-header-title">未着手1</p>
            <a class="card-header-icon button is-success">
              <span class="icon"><i class="fa fa-plus"></i></span>
            </a>
          </div>

          <div class="lane-items">
            <div class="card" style="margin-bottom:10px;">
              <header class="card-header">
                <p class="card-header-title">
                  じゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむじゅげむ
                </p>
              </header>
              <div class="card-content">
                <div class="content">
                  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus nec iaculis mauris.
                  <a>@bulmaio</a>. <a>#css</a> <a>#responsive</a>
                  <br>
                  <small>11:09 PM - 1 Jan 2016</small>
                </div>
              </div>
            </div>

            <div class="card">
              <header class="card-header">
                <p class="card-header-title">
                  Component
                </p>
                <a class="card-header-icon">
                <span class="icon">
                  <i class="fa fa-angle-down"></i>
                </span>
                </a>
              </header>
              <div class="card-content">
                <div class="content">
                  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus nec iaculis mauris.
                  <a>@bulmaio</a>. <a>#css</a> <a>#responsive</a>
                  <br>
                  <small>11:09 PM - 1 Jan 2016</small>
                </div>
              </div>
            </div>

            <div class="card">
              <header class="card-header">
                <p class="card-header-title">
                  Component
                </p>
                <a class="card-header-icon">
                <span class="icon">
                  <i class="fa fa-angle-down"></i>
                </span>
                </a>
              </header>
              <div class="card-content">
                <div class="content">
                  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus nec iaculis mauris.
                  <a>@bulmaio</a>. <a>#css</a> <a>#responsive</a>
                  <br>
                  <small>11:09 PM - 1 Jan 2016</small>
                </div>
              </div>
            </div>

            <div class="card">
              <header class="card-header">
                <p class="card-header-title">
                  Component
                </p>
                <a class="card-header-icon">
                <span class="icon">
                  <i class="fa fa-angle-down"></i>
                </span>
                </a>
              </header>
              <div class="card-content">
                <div class="content">
                  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus nec iaculis mauris.
                  <a>@bulmaio</a>. <a>#css</a> <a>#responsive</a>
                  <br>
                  <small>11:09 PM - 1 Jan 2016</small>
                </div>
              </div>
            </div>

            <div class="card">
              <header class="card-header">
                <p class="card-header-title">
                  Component
                </p>
                <a class="card-header-icon">
                <span class="icon">
                  <i class="fa fa-angle-down"></i>
                </span>
                </a>
              </header>
              <div class="card-content">
                <div class="content">
                  Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus nec iaculis mauris.
                  <a>@bulmaio</a>. <a>#css</a> <a>#responsive</a>
                  <br>
                  <small>11:09 PM - 1 Jan 2016</small>
                </div>
              </div>
            </div>

          </div>

        </div>
      </div>

    </div>

    <kanban-attachment-upload-dialog ref="kanbanAttachmentUploadDialog" :kanbanId="kanbanId" @Refresh="refresh"></kanban-attachment-upload-dialog>
    <kanban-attachment-dialog ref="kanbanAttachmentDialog" :kanbanId="kanbanId"></kanban-attachment-dialog>

  </div>
</template>

<script>

  import Utils from '../../utils'
  import KanbanAttachmentUploadDialog from './KanbanAttachmentUploadDialog'
  import KanbanAttachmentDialog from './KanbanAttachmentDialog'

  export default {
    name: 'kanban-main',
    components: {
      'kanban-attachment-upload-dialog': KanbanAttachmentUploadDialog,
      'kanban-attachment-dialog':KanbanAttachmentDialog
    },
    data() {
      return {
        kanbanId:null,
        hideAreaId:"",
        includeArchive:false,
        kanban:{
          title:"",
          description:"",
          archiveStatus:"",
          lockVersion:null,
          authority:""
        },
        lanes:[],
        noteMap:{},
        kanbanAttachmentFiles:[]
      }
    },
    methods: {
      viewContext(kanbanId, hideAreaId) {
        const self = this;
        self.kanbanId = kanbanId;
        self.hideAreaId = hideAreaId;
        self.includeArchive = false;

        const callBack = () => {
          $('#body').addClass("kanban-detail");
          $("#" + hideAreaId).addClass("hide");
          $('#kanban-main-area').removeClass("hide");
          Utils.moveTop();
        }

        self.refresh(callBack);
      },
      hideContext(e) {
        const self = this;
        $('#body').removeClass("kanban-detail");
        $("#" + self.hideAreaId).removeClass("hide");
        $('#kanban-main-context').scrollLeft(0);
        $('#kanban-main-area').addClass("hide");
        Utils.moveTop();
        self.$emit("Refresh", e);
      },
      openAttachmentUploadDialog() {
        const self = this;
        self.$refs.kanbanAttachmentUploadDialog.openDialog(self.kanbanAttachmentFiles);
      },
      openAttachmentDialog() {
        const self = this;
        self.$refs.kanbanAttachmentDialog.openDialog(self.kanbanAttachmentFiles);
      },
      refresh(callBack) {
        const self = this;
        const param = {
          kanbanId : self.kanbanId,
          includeArchive: self.includeArchive
        };
        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'GET',
          url: "/kanban/detail"
        }).then(
          function (data) {
            if(Utils.alertErrorMsg(data)) {
              return;
            }
            self.setKanbanData(data.result);

            if(typeof callBack === 'function') {
              callBack();
            }

          }
        );
      },
      setKanbanData(kanbanDetail) {
        const self = this;
        const kanban = kanbanDetail.kanban;
        self.kanban.title = kanban.title;
        self.kanban.description = kanban.description;
        self.kanban.archiveStatus = kanban.archiveStatus;
        self.kanban.lockVersion = kanban.lockVersion;
        self.kanban.authority = kanban.authority;

        self.lanes.splice(0,self.lanes.length);
        self.lanes.push(...kanbanDetail.lanes);

        self.noteMap = kanbanDetail.noteMap;

        self.kanbanAttachmentFiles.splice(0,self.kanbanAttachmentFiles.length);
        self.kanbanAttachmentFiles.push(...kanbanDetail.kanbanAttachmentFiles);
      }
    }
  }
</script>

<style scoped>

  .level-item > a > .tag {
    margin-right: 0.75rem;
  }

  .kanban-title-area, .kanban-main-container {
    padding: 1rem 4rem;
  }

  .kanban-main-container {
    overflow-x:auto;
    height:100%;
  }

  .lane-items {
    height:90%;
    overflow-y:auto
  }
  .lane-container {
    padding: 1rem 1rem;
    height:85%;
  }

  .lane-container > .card-header .card-header-title {
    color: #fff;
  }

  .lane-container.is-info {
    background-color: #3273dc;
  }

</style>
