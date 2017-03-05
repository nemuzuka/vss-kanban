<template>
  <div class="column is-one-quarter-desktop is-one-quarter-tablet is-half-mobile" style="height:100%" :class="{ 'archived': stageItem.archiveStatus === '1' }">

    <div class="stage-container is-info">

      <div class="card-header">
        <p class="card-header-title">{{stageItem.stageName}}</p>
        <a class="card-header-icon button is-success" @click="openEditDialog">
          <span class="icon"><i class="fa fa-plus"></i></span>
        </a>
      </div>

      <draggable :list="getNoteList" :options="{group:'stage',handle:'.drag-item'}" @start="drag=true" @end="drag=false" class="stage-items" @change="noteChange">
        <note-item v-for="noteItem in noteMap[stageItem.stageId]" :noteItem="noteItem" :joinedUserMap="joinedUserMap" :key="noteItem.noteId" :completeStage="stageItem.completeStage" @OpenDetailDialog="openDetailDialog"></note-item>
      </draggable>

    </div>

  </div>
</template>

<script>
  import Utils from '../../utils'
  import NoteItem from './NoteItem'
  import Draggable from 'vuedraggable'

  export default {
    name: 'stage-list',
    props:['stageItem', 'noteMap', 'joinedUserMap'],
    components: {
      'note-item' : NoteItem,
      'draggable':Draggable
    },
    methods: {
      openEditDialog(e) {
        const self = this;
        self.$emit("OpenEditDialog", e, self.stageItem.stageId, "");
      },
      openDetailDialog(e, noteId) {
        const self = this;
        self.$emit("OpenDetailDialog", e, self.stageItem.stageId, noteId);
      },
      noteChange(evt) {
        if(typeof evt.added === 'undefined' && typeof evt.moved === 'undefined') {
            return;
        }

        let noteId = "";
        if(typeof evt.added !== 'undefined') {
          //別のステージに移動したとみなす
          noteId = evt.added.element.noteId;
        }

        const self = this;
        const param = {
          noteId: noteId,
          stageId: self.stageItem.stageId,
          noteIds: self.noteMap[self.stageItem.stageId].map(function(element){
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
        let list = self.noteMap[self.stageItem.stageId];
        if(typeof list === 'undefined') {
          list = [];
          self.$set(self.noteMap, self.stageItem.stageId, list);
        }
        return list;
      }
    },
  }
</script>

<style scoped>

  .stage-container .card-header {
    box-shadow: none;
  }

  .stage-items {
    height:90%;
    overflow-y:auto
  }
  .stage-container {
    padding: 1rem 1rem;
    height:90%;
  }

  .stage-container > .card-header .card-header-title {
    color: #fff;
  }

  .stage-container.is-info {
    background-color: #3273dc;
  }

  .archived > .stage-container.is-info {
    background-color:#aaa;
  }

</style>
