<template>

  <div style="height: 100%">
    <div id="kanban-main-area" class="hide content-view" style="height: 100%;margin:1rem;">

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
                アーカイブ済みのレーンやふせんも見る
              </label>

              <a v-if="kanbanAttachmentFiles.length > 0" @click="openAttachmentDialog" class="button is-outlined is-danger attachment-file">
                添付有
              </a>

              <a class="button is-success" @click="openAttachmentUploadDialog">
              <span class="icon">
                <i class="fa fa-paperclip"></i>
              </span>
              </a>

              <a class="button is-dark" v-if="kanban.authority === '1'" @click="viewSettings">
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
        <lane-list v-for="laneItem in lanes" :laneItem="laneItem" :noteMap="noteMap" :joinedUserMap="joinedUserMap" :key="laneItem.laneId" @OpenEditDialog="openNoteEditDialog"></lane-list>
      </div>

      <kanban-attachment-upload-dialog ref="kanbanAttachmentUploadDialog" :kanbanId="kanbanId" @Refresh="refresh"></kanban-attachment-upload-dialog>
      <kanban-attachment-dialog ref="kanbanAttachmentDialog" :kanbanId="kanbanId"></kanban-attachment-dialog>
      <note-edit-dialog ref="noteEditDialog" :kanbanId="kanbanId" @Refresh="refresh"></note-edit-dialog>

    </div>

    <kanban-settings ref="kanbanSettings" :kanbanId="kanbanId" @Refresh="refresh" @Back="hideContext"></kanban-settings>
  </div>

</template>

<script>

  import Utils from '../../utils'
  import KanbanAttachmentUploadDialog from './KanbanAttachmentUploadDialog'
  import KanbanAttachmentDialog from './KanbanAttachmentDialog'
  import NoteEditDialog from './NoteEditDialog'
  import LaneList from './LaneList'
  import KanbanSettings from './Settings'

  export default {
    name: 'kanban-main',
    components: {
      'kanban-attachment-upload-dialog': KanbanAttachmentUploadDialog,
      'kanban-attachment-dialog':KanbanAttachmentDialog,
      'lane-list':LaneList,
      'kanban-settings':KanbanSettings,
      'note-edit-dialog': NoteEditDialog
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
        kanbanAttachmentFiles:[],
        joinedUserMap:{}
      }
    },
    methods: {
      viewContext(e, kanbanId, hideAreaId) {
        const self = this;
        self.kanbanId = kanbanId;
        self.hideAreaId = hideAreaId;
        self.includeArchive = false;

        const callBack = () => {
          $('#body').addClass("kanban-detail");
          $("#" + hideAreaId).addClass("hide");
          $('#kanban-main-area').removeClass("hide");
          Utils.moveTop();
        };

        self.refresh(e, callBack);
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
      openNoteEditDialog(e, laneId, noteId) {
        const self = this;
        self.$refs.noteEditDialog.openDialog(e, laneId, noteId);
      },
      viewSettings(e) {
        const self = this;

        const callBack = () => {
          $('#body').removeClass("kanban-detail");
          $('#kanban-main-context').scrollLeft(0);
          $('#kanban-main-area').addClass("hide");
          $('#kanban-settings-area').removeClass("hide");
          Utils.moveTop();
        };
        self.$refs.kanbanSettings.refresh(callBack);
      },
      refresh(e, callBack) {
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
        self.joinedUserMap = kanbanDetail.joinedUserMap;

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
    padding: 0rem 3rem 1rem 3rem;
  }

  .kanban-main-container {
    overflow-x:auto;
    height:100%;
  }

  .button.is-danger.is-outlined.attachment-file {
    background-color: #fff;
  }
  .button.is-danger.is-outlined.attachment-file:hover {
    background-color: #ff3860;
  }
</style>
