<template>
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

<script>
  export default {
    name: 'settings-lane-item',
    props: ["item"]
  }
</script>
