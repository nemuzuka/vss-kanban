import toastr from 'toastr/toastr';
import Flatpickr from 'flatpickr/dist/flatpickr'
import FlatpickrJa from 'flatpickr/dist/l10n/ja'
import moment from 'moment'

export default class Utils {
  /**
   * ダイアログOpen,
   * @param {String} id ID要素.
   */
  static openDialog(id) {
    $('html').addClass('is-clipped');
    $("#" + id).addClass("is-active");
  }

  /**
   * ダイアログClose.
   * スクロール部分の位置を先頭に移動し、ダイアログをcloseします
   * @param {String} id ID要素
   */
  static closeDialog(id) {
    $('#' + id + ' section.modal-card-body').scrollTop(0);
    $('html').removeClass('is-clipped');
    $('#' + id).removeClass('is-active');
  }

  /**
   * スクロールをTOPに移動します
   * @param id 対象ID(未設定の場合、body全体が対象)
   */
  static moveTop(id) {
    let targetId;
    if(undefined === id || id === '') {
      targetId = 'body,html';
    } else {
      targetId = '#' + id;
    }
    $(targetId).animate({
      scrollTop: 0
    }, 0);
    return false;
  }

  /**
   * エスケープ処理.
   * ・タグ情報をエスケープ
   * ・改行コードを<br>タグに変更
   * ・http/httpsで始まる文字列をaタグに変換
   * を行います
   * 主にtextArea項目表示時に使用することを想定しています。
   * @param {String} org 対象文字列
   */
  static escapeTextArea(org) {
    if(org === null || org === "") {
      return "";
    }
    let ret = org.replace(/&(?!\w+;)/g, '&amp;').replace(/"/g,"&quot;").replace(/'/g,"&#039;").replace(/</g,"&lt;").replace(/>/g,"&gt;");
    ret = ret.replace(/\r\n/g, "<br>");
    ret = ret.replace(/(\n|\r)/g, "<br>");

    const re = /((http|https|ftp):\/\/[\w?=&.\/-;#~%-]+(?![\w\s?&.\/;#~%"=-]*>))/g
    ret = ret.replace(re, '<a href="$1" class="link" target="_blank">$1</a> ');
    return ret;
  }

  /**
   * 読み込み中メッセージ表示.
   * @param {String} msg 表示対象メッセージ
   */
  static viewBlockMsg(msg = 'Now Loading...') {
    let message = '<div class="ball" style="float:left;margin-right:10px;"></div><div style="float:left;"> ';
    message += msg;
    message += '</div>';

    $.blockUI({
      message: message,
      fadeIn: 0,
      fadeOut: 0,
      showOverlay: true,
      centerY: false,
      centerX: true,
      baseZ: 100000,
      css: {
        top: '85px',
        left: '',
        right: '50px',
        border: '1px solid #fff',
        padding: '5px',
        width: '160px',
        background: '#E87A90',
        '-webkit-border-radius': '10px',
        '-moz-border-radius': '10px',
        opacity: 1.0,
        color: '#fff'
      },
      overlayCSS:  {
        backgroundColor: 'transparent',
        opacity:         0.8
      }
    });
  }

  /**
   * 読み込み中メッセージを表示解除.
   */
  static unBlockMsg() {
    $.unblockUI();
  }

  /**
   * 固定で読み込み中メッセージ表示.
   */
  static viewLoadingMsg() {
    Utils.viewBlockMsg();
  }

  /**
   * Ajax処理の共通処理.
   */
  static setAjaxDefault() {
    const isViewLoadingMsg = true;
    $.ajaxSetup({
      timeout: 0,
      ifModified: true,
      cache: false,
      dataType: "json",
      headers: {
        'X-CSRF-TOKEN': $('meta[name=csrf-token]').attr("content") || $(event.target).data('csrf-token')
      },
      //通信前の処理を定義
      beforeSend: function(jqXHR, settings) {
        if(isViewLoadingMsg) {
          Utils.viewLoadingMsg();
        }
      },
      //通信成功時の処理を定義
      success : function(data, dataType) {
        if(isViewLoadingMsg) {
          Utils.unBlockMsg();
        }
      },
      // エラー・ハンドラを定義（エラー時にダイアログ表示）
      error: function(xhr, status, err) {
        if (isViewLoadingMsg) {
          Utils.unBlockMsg();
        }
        if (xhr.status == 400 || xhr.status == 403) {
          alert("お手数ですが、もう一度ログインしてください");
          Utils.moveUrl("/");
        } else if(xhr.status == 406) {
          alert("この機能を使用する権限はありません");
          Utils.moveUrl("/");
        } else if(xhr.status == 0) {
          //ネットワーク通信エラー時、再送
          $.ajax(this);
        } else {
          alert(xhr.status + ":" + status + ":通信エラーです");
        }
      }
    });
  }

  /**
   * 指定URLへ遷移.
   * @param url URL
   */
  static moveUrl(url) {
    Utils.viewLoadingMsg();
    document.location.href = url;
  }

  /**
   * エラーメッセージ描画.
   * @param self Vueコンポーネント
   * @param data レスポンスデータ
   * @param viewAlert エラー発生時、alert表示する場合、ture
   * @returns {boolean} エラーメッセージが存在する場合、true
   */
  static writeErrorMsg(self, data, viewAlert = true) {
    const vueInstance = self;
    const errorMsg = data.errorMsg;
    Object.keys(errorMsg).forEach(function(key) {
      this[key].forEach(function(val, i){
        vueInstance.msg[key+"ErrMsg"] += val + '<br>';
      });
    }, errorMsg);

    const errorKeyCount = Object.keys(errorMsg).length;
    if(errorKeyCount > 0 && viewAlert) {
      alert("入力内容にエラーがありました。ご確認ください");
    }
    return errorKeyCount !== 0;
  }

  /**
   * エラーメッセージalert表示.
   * グローバル領域にメッセージが設定されている場合、alert表示します
   * @param data レスポンスデータ
   * @returns {boolean} エラーメッセージが存在する場合、true
   */
  static alertErrorMsg(data) {
    const errorMsg = data.errorMsg;
    var msg = "";
    Object.keys(errorMsg).forEach(function(key) {
      if(key !== 'global') {
        return;
      }
      this[key].forEach(function(val, i){
        msg += val + '\n';
      });
    }, errorMsg);

    if(msg !== '') {
      alert(msg);
      return true;
    }
    return false;
  }

  /**
   * メッセージ表示.
   * メッセージが設定されている場合、toast表示します
   * @param data レスポンスデータ
   */
  static viewInfoMsg(data) {
    const msg = data.msgs.join("<br>");
    if(msg !== "") {
      this._viewToastMsg(msg);
    }
  }

  /**
   * メッセージtoast表示.
   * toast形式でメッセージを表示します。
   * @param {String} msg 表示メッセージ
   */
  static _viewToastMsg(msg) {
    toastr.options = {
      "closeButton": false,
      "debug": false,
      "newestOnTop": false,
      "progressBar": false,
      "positionClass": "toast-top-right",
      "preventDuplicates": false,
      "onclick": null,
      "showDuration": "300",
      "hideDuration": "1000",
      "timeOut": "2000",
      "extendedTimeOut": "1000",
      "showEasing": "swing",
      "hideEasing": "linear",
      "showMethod": "fadeIn",
      "hideMethod": "fadeOut"
    };
    toastr.success(msg);
  }

  /**
   * ユニーク文字列生成.
   * @returns {string} "dummy-"で始まるシステム時刻を元にしたユニークと思われる文字列
   */
  static getUniqueStr() {
    return "dummy-" + new Date().getTime().toString(16)  + Math.floor(1000 * Math.random()).toString(16)
  }

  /**
   * ユニーク文字列として生成されたものか？
   * @param org 対象文字列
   * @returns {boolean} ユニーク文字列として生成されたものはtrue
   */
  static isUniqueStr(org) {
    return org.toString().indexOf("dummy-") === 0
  }

  /**
   * 日付入力項目生成.
   * @param selector セレクタ
   * @param defaultDate 日付初期値
   */
  static datepicker(selector, defaultDate) {
    new Flatpickr(document.querySelector(selector), {
      enableTime: false,
      clickOpens: true,
      dateFormat: 'Y/m/d',
      defaultDate: defaultDate === '' ? null : this.toDateString(defaultDate, 'YYYY-MM-DD'),
      "locale" : FlatpickrJa.ja
    });
  }

  /**
   * 日付文字列フォーマット.
   * 引数の日付文字列を指定したフォーマットに変換します。
   * @param target 対象文字列
   * @param formatStr フォーマット文字列
   * @returns {string}
   */
  static toDateString(target, formatStr) {

    if(typeof target == 'undefined' || target === "") {
      return "";
    }
    const targetStr = target.replace(/-/g, "").replace(/\//g, "");
    return moment(targetStr, "YYYYMMDD").format(formatStr);
  }

  /**
   * 期限クラス情報取得.
   * 期日とシステム日付を比べて、
   * 完了レーンの場合、is-lightをtrue
   * 期日 < システム日付の場合、is-primaryをtrue
   * 期日 = システム日付の場合、is-warningをtrue
   * 期日 > システム日付の場合、is-dangerをtrue
   * を返却します
   * @param targetDate 期日
   * @param completeLane 完了レーンの場合、true
   */
  static fixDateClass(targetDate, completeLane) {
    const fixDate = moment(targetDate, "YYYYMMDD");
    const now = moment(moment().format("YYYYMMDD"), "YYYYMMDD");
    return {
      'is-primary': !completeLane && fixDate.isAfter(now),
      'is-warning': !completeLane && fixDate.isSame(now),
      'is-danger': !completeLane && fixDate.isBefore(now),
      'is-light': completeLane
    };
  }

  /**
   * ファイルアップロード共通処理.
   * @param targetFiles アップロード対象ファイル
   * @param files 結果格納List
   */
  static uploadFile(targetFiles, files) {
    if(targetFiles.length <= 0) {
      return;
    }

    const fd = new FormData();
    Object.keys(targetFiles).forEach(function(key){
      fd.append("attachmentFiles", targetFiles[key]);
    });

    this.setAjaxDefault();
    $.ajax({
      data: fd,
      method: 'POST',
      url: "/attachment/file/kanban",
      contentType: false,
      processData: false
    }).then(
      function (data) {
        files.push(...data.result);
      }
    );

  }

}
