/**
 * エラーページ用.
 */

import Vue from 'vue'
import NoMenu from './components/header/LoginMenu'

new Vue({
  el: '#main-content',
  components: {
    'app-header' : NoMenu
  },
});

setTimeout(function () {
  window.location.href = "/";
}, 5000);
