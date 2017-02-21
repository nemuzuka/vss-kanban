<template>
  <div>
    <div v-if="lane.lanes.length > 0">

      <table class="table is-bordered is-striped is-narrow">
        <thead><tr><th style="width:50px"></th><th>レーン名</th><th style="width:50px"></th></tr></thead>
        <tbody>

          <settings-lane-item v-for="item in lane.lanes" :item="item"></settings-lane-item>

        </tbody>
      </table>

    </div>

    <div v-if="lane.lanes.length <= 0 && lane.lanesMsg !== ''">
      <article class="message is-danger">
        <div class="message-body">{{lane.lanesMsg}}</div>
      </article>
    </div>
  </div>
</template>

<script>

  import SettingsLaneItem from './SettingsLaneItem'

  export default {
    name: 'settings-lane',
    props: ["lane"],
    components:{
      'settings-lane-item':SettingsLaneItem
    }
  }
</script>
