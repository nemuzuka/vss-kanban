<template>
  <div class="column is-one-quarter-desktop is-one-quarter-tablet is-half-mobile" style="height:100%">

    <div class="lane-container is-info">

      <div class="card-header">
        <p class="card-header-title">{{laneItem.laneName}}</p>
        <a class="card-header-icon button is-success" @click="openEditDialog">
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
</template>

<script>
  import Utils from '../../utils'
  export default {
    props:['laneItem', 'noteMap'],
    methods: {
      openEditDialog(e) {
        const self = this;
        self.$emit("OpenEditDialog", e, self.laneItem.laneId, "");
      }
    }
  }
</script>

<style scoped>
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
</style>
