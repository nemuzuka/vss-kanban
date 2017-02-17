<template>
  <div id="kanban-main-area" class="hide content-view" style="height: 100%">

    <p class="back"><a @click="hideContext"><i class="fa fa-chevron-left"></i></a></p>

    <div class="kanban-title-area">
      <nav class="level">
        <!-- Left side -->
        <div class="level-left">
          <div class="level-item">
            <h1 class="title">かんばんの名前がココに入ります！</h1>
          </div>
        </div>

        <!-- Right side -->
        <div class="level-right">

          <p class="level-item">

            <a class="button is-success" @click="openAttachmentUploadDialog">
              <span class="icon">
                <i class="fa fa-paperclip"></i>
              </span>
            </a>

            <a class="button is-success">
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

    <kanban-attachment-upload-dialog ref="kanbanAttachmentUploadDialog"></kanban-attachment-upload-dialog>

  </div>
</template>

<script>

  import Utils from '../../utils'
  import KanbanAttachmentUploadDialog from './KanbanAttachmentUploadDialog'

  export default {
    name: 'kanban-main',
    components: {
      'kanban-attachment-upload-dialog': KanbanAttachmentUploadDialog,
    },
    data() {
      return {
        kanbanId:null,
        hideAreaId:""
      }
    },
    methods: {
      viewContext(kanbanId, hideAreaId) {
        const self = this;
        self.kanbanId = kanbanId;
        self.hideAreaId = hideAreaId;
        self.refresh();

        $('#body').addClass("kanban-detail");
        $("#" + hideAreaId).addClass("hide");
        $('#kanban-main-area').removeClass("hide");
        Utils.moveTop();
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
        self.$refs.kanbanAttachmentUploadDialog.openDialog();
      },
      refresh() {
        const self = this;
        alert(self.kanbanId + " の詳細情報取得しちゃうもんね！");
      }
    }
  }
</script>

<style scoped>

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
