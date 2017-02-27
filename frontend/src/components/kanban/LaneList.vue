<template>
  <div class="column is-one-quarter-desktop is-one-quarter-tablet is-half-mobile" style="height:100%" :class="{ 'archived': laneItem.archiveStatus === '1' }">

    <div class="lane-container is-info">

      <div class="card-header">
        <p class="card-header-title">{{laneItem.laneName}}</p>
        <a class="card-header-icon button is-success" @click="openEditDialog">
          <span class="icon"><i class="fa fa-plus"></i></span>
        </a>
      </div>

      <div class="lane-items">

        <note-item v-for="noteItem in noteMap[laneItem.laneId]" :noteItem="noteItem" :joinedUserMap="joinedUserMap" :key="noteItem.noteId" :completeLane="laneItem.completeLane" @OpenDetailDialog="openDetailDialog"></note-item>

      </div>

    </div>

  </div>
</template>

<script>
  import Utils from '../../utils'
  import NoteItem from './NoteItem'

  export default {
    name: 'lane-list',
    props:['laneItem', 'noteMap', 'joinedUserMap'],
    components: {
      'note-item' : NoteItem
    },
    methods: {
      openEditDialog(e) {
        const self = this;
        self.$emit("OpenEditDialog", e, self.laneItem.laneId, "");
      },
      openDetailDialog(e, noteId) {
        const self = this;
        self.$emit("OpenDetailDialog", e, self.laneItem.laneId, noteId);
      }
    }
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
    height:95%;
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
