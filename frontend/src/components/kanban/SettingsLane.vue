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

    <div v-if="lane.lanes.length > 0">

      <table class="table is-bordered is-striped is-narrow">
        <thead><tr><th style="width:20px"></th><th>レーン名</th><th style="width:20px"></th></tr></thead>
        <draggable :list="lane.lanes" :options="{handle:'.drag-item'}" element="tbody" @start="drag=true" @end="drag=false">
          <settings-lane-item v-for="(item, index) in lane.lanes" :item="item" :index="index" :key="item.laneId" @OpenEditDialog="openEditDialog" @DeleteItem="deleteItem"></settings-lane-item>
        </draggable>
      </table>

    </div>

    <div v-if="lane.lanes.length <= 0 && lane.lanesMsg !== ''">
      <article class="message is-danger">
        <div class="message-body">{{lane.lanesMsg}}</div>
      </article>
    </div>

    <lane-edit-dialog ref="laneEditDialog" :lane="lane"></lane-edit-dialog>

  </div>
</template>

<script>

  import SettingsLaneItem from './SettingsLaneItem'
  import SettingsLaneEditDialog from './SettingsLaneEditDialog'
  import Draggable from 'vuedraggable'

  export default {
    name: 'settings-lane',
    props: ["lane"],
    components:{
      'settings-lane-item':SettingsLaneItem,
      'lane-edit-dialog':SettingsLaneEditDialog,
      'draggable':Draggable
    },
    methods:{
      openEditDialog(e, item, index) {
        const self = this;
        self.$refs.laneEditDialog.openEditDialog(item, index);
      },
      deleteItem(e,index) {
        const self = this;
        self.lane.lanes.splice(index, 1);
        if(self.lane.lanes.length <= 0) {
          self.lane.lanesMsg = "レーンは登録されていません";
        }
      }
    }
  }
</script>
