<template>
  <div class="column is-one-quarter-desktop is-one-quarter-tablet is-half-mobile" style="height:100%" :class="{ 'archived': laneItem.archiveStatus === '1' }">

    <div class="lane-container is-info">

      <div class="card-header">
        <p class="card-header-title">{{laneItem.laneName}}</p>
        <a class="card-header-icon button is-success" @click="openEditDialog">
          <span class="icon"><i class="fa fa-plus"></i></span>
        </a>
      </div>

      <draggable :list="getNoteList" :options="{group:'lane',handle:'.drag-item'}" @start="drag=true" @end="drag=false" class="lane-items" @change="noteChange">
        <note-item v-for="noteItem in noteMap[laneItem.laneId]" :noteItem="noteItem" :joinedUserMap="joinedUserMap" :key="noteItem.noteId" :completeLane="laneItem.completeLane" @OpenDetailDialog="openDetailDialog"></note-item>
      </draggable>

    </div>

  </div>
</template>

<script>
  import Utils from '../../utils'
  import NoteItem from './NoteItem'
  import Draggable from 'vuedraggable'

  export default {
    name: 'lane-list',
    props:['laneItem', 'noteMap', 'joinedUserMap'],
    components: {
      'note-item' : NoteItem,
      'draggable':Draggable
    },
    methods: {
      openEditDialog(e) {
        const self = this;
        self.$emit("OpenEditDialog", e, self.laneItem.laneId, "");
      },
      openDetailDialog(e, noteId) {
        const self = this;
        self.$emit("OpenDetailDialog", e, self.laneItem.laneId, noteId);
      },
      noteChange(evt) {
        if(typeof evt.added === 'undefined' && typeof evt.moved === 'undefined') {
            return;
        }

        let noteId = "";
        if(typeof evt.added !== 'undefined') {
          //別のレーンに移動したとみなす
          noteId = evt.added.element.noteId;
        }

        const self = this;
        const param = {
          noteId: noteId,
          laneId: self.laneItem.laneId,
          noteIds: self.noteMap[self.laneItem.laneId].map(function(element){
            return element.noteId
          })
        };
        Utils.setAjaxDefault();
        $.ajax({
          data: param,
          method: 'POST',
          url: "/kanban/note/move"
        }).then(
          function (data) {
            self.$emit("Refresh", null);
          }
        );
      }
    },
    computed:{
      getNoteList() {
        const self = this;
        let list = self.noteMap[self.laneItem.laneId];
        if(typeof list === 'undefined') {
          list = [];
          self.$set(self.noteMap, self.laneItem.laneId, list);
        }
        return list;
      }
    },
  }
</script>

<style scoped>

  .lane-container .card-header {
    box-shadow: none;
  }

  .lane-items {
    height:90%;
    overflow-y:auto
  }
  .lane-container {
    padding: 1rem 1rem;
    height:90%;
  }

  .lane-container > .card-header .card-header-title {
    color: #fff;
  }

  .lane-container.is-info {
    background-color: #3273dc;
  }

  .archived > .lane-container.is-info {
    background-color:#aaa;
  }

</style>
