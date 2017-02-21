<template>
  <div>
    <div v-if="lane.lanes.length > 0">

      <table class="table is-bordered is-striped is-narrow">
        <thead><tr><th style="width:50px"></th><th>レーン名</th><th style="width:50px"></th></tr></thead>
        <tbody>

        <template v-for="item in lane.lanes">
          <tr>
            <td class="has-text-centered handle" style="cursor: move;">
              <span class="icon">
                <i class="fa fa-arrows"></i>
              </span>
            </td>
            <td style="cursor: pointer;" @click="openEditDialog">{{item.laneName}}</td>
            <td class="has-text-centered">
              <a class="button is-danger is-small" @click="deleteRow">
                <span class="icon is-small">
                  <i class="fa fa-times"></i>
                </span>
              </a>
            </td>
          </tr>
        </template>

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
  export default {
    name: 'settings-lane',
    props: ["lane"]
  }
</script>
