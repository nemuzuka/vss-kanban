<template>
  <div class="modal" id="lane-edit-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">レーン{{buttonLabel}}</p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body">

        <article class="message is-danger" v-if="msg.globalErrMsg !== ''">
          <div class="message-body" v-html="msg.globalErrMsg"></div>
        </article>

        <div>
          <label class="label">レーン名 <span class="tag is-danger">必須</span></label>
          <p class="control">
            <input class="input" type="text" v-model="form.laneName" placeholder="レーン名を入力してください" id="lane-edit-dialog-lane-name">
            <span class="help is-danger" v-html="msg.laneNameErrMsg"></span>
          </p>
        </div>

        <div>
          <label class="label">Appendix</label>
          <p class="control">
            <label class="checkbox">
              <input type="checkbox" v-model="form.archiveStatus" :true-value="1" :false-value="0">
              このレーンをアーカイブする
            </label>
          </p>
          <p class="control">
            <label class="checkbox">
              <input type="checkbox" v-model="form.completeLane" :true-value="1" :false-value="0">
              このレーンに紐づく付箋は完了扱いとする
            </label>
          </p>
        </div>

      </section>
      <footer class="modal-card-foot">
        <a class="button is-info" @click="saveDialog">{{buttonLabel}}</a>
        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>
</template>

<script>

  import Utils from '../../utils'

  export default {
    name: 'lane-edit-dialog',
    props: ['lane'],
    data() {
      return {
        form:{
          id:"",
          laneName:"",
          archiveStatus:"0",
          completeLane:"0",
          index:null
        },
        msg:{
          globalErrMsg:"",
          laneNameErrMsg:""
        },
        clearMsg(){
          this.msg.globalErrMsg = "";
          this.msg.laneNameErrMsg = "";
        }
      }
    },
    methods: {
      openEditDialog(item, index) {
        const self = this;
        if(item === null || typeof item === "undefined") {
          self.form.id = "";
          self.form.laneName = "";
          self.form.archiveStatus = "0";
          self.form.completeLane = "0";
          self.form.index = null;
        } else {
          self.form.id = item.laneId;
          self.form.laneName = item.laneName;
          self.form.archiveStatus = item.archiveStatus;
          self.form.completeLane = item.completeLane ? "1" : "0";
          self.form.index = index;
        }
        self.clearMsg();
        Utils.openDialog('lane-edit-dialog');

        setTimeout(function(){
          $('#lane-edit-dialog-lane-name').focus();
        }, 100);
      },
      closeDialog() {
        Utils.closeDialog('lane-edit-dialog');
      },
      saveDialog(e) {
        const self = this;
        self.clearMsg();

        if(self.form.laneName === '') {
          self.msg.laneNameErrMsg = "レーン名 は 必ず入力してください";
          return false;
        }

        const lane = {
          laneId : self.form.index === null ? Utils.getUniqueStr() : self.form.id,
          laneName : self.form.laneName,
          archiveStatus : self.form.archiveStatus.toString(),
          completeLane : self.form.completeLane.toString() === '1'
        };

        if(self.form.index === null) {
          self.lane.lanes.push(lane);
        } else {
          self.lane.lanes.splice(self.form.index, 1, lane);
        }
        self.closeDialog();
      }
    },
    computed: {
      buttonLabel() {
        const self = this;
        return self.form.index === null ? '登録' : '変更';
      }
    }
  }

</script>

<style scoped>
  a.button {
    text-decoration: none;
  }
</style>
