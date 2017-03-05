<template>
  <div class="modal" id="stage-edit-dialog">
    <div class="modal-background"></div>
    <div class="modal-card">
      <header class="modal-card-head">
        <p class="modal-card-title">ステージ{{buttonLabel}}</p>
        <button class="delete" @click="closeDialog"></button>
      </header>
      <section class="modal-card-body">

        <article class="message is-danger" v-if="msg.globalErrMsg !== ''">
          <div class="message-body" v-html="msg.globalErrMsg"></div>
        </article>

        <div>
          <label class="label">ステージ名 <span class="tag is-danger">必須</span></label>
          <p class="control">
            <input class="input" type="text" v-model="form.stageName" placeholder="ステージ名を入力してください" id="stage-edit-dialog-stage-name">
            <span class="help is-danger" v-html="msg.stageNameErrMsg"></span>
          </p>
        </div>

        <div>
          <label class="label">Appendix</label>
          <p class="control">
            <label class="checkbox">
              <input type="checkbox" v-model="form.archiveStatus" :true-value="1" :false-value="0">
              このステージをアーカイブする
            </label>
          </p>
          <p class="control">
            <label class="checkbox">
              <input type="checkbox" v-model="form.completeStage" :true-value="1" :false-value="0">
              このステージに紐づく付箋は完了扱いとする
            </label>
          </p>
        </div>

      </section>
      <footer class="modal-card-foot">
        <a class="button is-success" @click="saveDialog">
          <span class="icon is-small">
            <i class="fa fa-floppy-o"></i>
          </span>
          <span>{{buttonLabel}}</span>
        </a>
        <a class="button" @click="closeDialog">Cancel</a>
      </footer>
    </div>
  </div>
</template>

<script>

  import Utils from '../../utils'

  export default {
    name: 'stage-edit-dialog',
    props: ['stage'],
    data() {
      return {
        form:{
          id:"",
          stageName:"",
          archiveStatus:"0",
          completeStage:"0",
          index:null
        },
        msg:{
          globalErrMsg:"",
          stageNameErrMsg:""
        },
        clearMsg(){
          this.msg.globalErrMsg = "";
          this.msg.stageNameErrMsg = "";
        }
      }
    },
    methods: {
      openEditDialog(item, index) {
        const self = this;
        if(item === null || typeof item === "undefined") {
          self.form.id = "";
          self.form.stageName = "";
          self.form.archiveStatus = "0";
          self.form.completeStage = "0";
          self.form.index = null;
        } else {
          self.form.id = item.stageId;
          self.form.stageName = item.stageName;
          self.form.archiveStatus = item.archiveStatus;
          self.form.completeStage = item.completeStage ? "1" : "0";
          self.form.index = index;
        }
        self.clearMsg();
        Utils.openDialog('stage-edit-dialog');

        setTimeout(function(){
          $('#stage-edit-dialog-stage-name').focus();
        }, 100);
      },
      closeDialog() {
        Utils.closeDialog('stage-edit-dialog');
      },
      saveDialog(e) {
        const self = this;
        self.clearMsg();

        if(self.form.stageName === '') {
          self.msg.stageNameErrMsg = "ステージ名 は 必ず入力してください";
          return false;
        }

        const stage = {
          stageId : self.form.index === null ? Utils.getUniqueStr() : self.form.id,
          stageName : self.form.stageName,
          archiveStatus : self.form.archiveStatus.toString(),
          completeStage : self.form.completeStage.toString() === '1'
        };

        if(self.form.index === null) {
          self.stage.stages.push(stage);
        } else {
          self.stage.stages.splice(self.form.index, 1, stage);
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
