<template>
  <div>

    <nav class="level">
      <div class="level-left">
      </div>
      <div class="level-right">
        <p class="level-item">
          <a class="button is-success" @click="openEditDialog">
            <span class="icon">
              <i class="fa fa-plus"></i>
            </span>
          </a>
        </p>
      </div>
    </nav>

    <div v-if="stage.stages.length > 0">

      <table class="table is-bordered is-striped is-narrow">
        <thead><tr><th style="width:20px"></th><th>ステージ名</th><th style="width:20px"></th></tr></thead>
        <draggable :list="stage.stages" :options="{handle:'.drag-item'}" element="tbody" @start="drag=true" @end="drag=false">
          <settings-stage-item v-for="(item, index) in stage.stages" :item="item" :index="index" :key="item.stageId" @OpenEditDialog="openEditDialog" @DeleteItem="deleteItem"></settings-stage-item>
        </draggable>
      </table>

    </div>

    <div v-if="stage.stages.length <= 0 && stage.stagesMsg !== ''">
      <article class="message is-danger">
        <div class="message-body">{{stage.stagesMsg}}</div>
      </article>
    </div>

    <stage-edit-dialog ref="stageEditDialog" :stage="stage"></stage-edit-dialog>

  </div>
</template>

<script>

  import SettingsStageItem from './SettingsStageItem'
  import SettingsStageEditDialog from './SettingsStageEditDialog'
  import Draggable from 'vuedraggable'

  export default {
    name: 'settings-stage',
    props: ["stage"],
    components:{
      'settings-stage-item':SettingsStageItem,
      'stage-edit-dialog':SettingsStageEditDialog,
      'draggable':Draggable
    },
    methods:{
      openEditDialog(e, item, index) {
        const self = this;
        self.$refs.stageEditDialog.openEditDialog(item, index);
      },
      deleteItem(e,index) {
        const self = this;
        self.stage.stages.splice(index, 1);
        if(self.stage.stages.length <= 0) {
          self.stage.stagesMsg = "ステージは登録されていません";
        }
      }
    }
  }
</script>
