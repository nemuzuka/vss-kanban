/**
 * 通知一覧画面のjs
 */
import Vue from 'vue'
import App from '../components/notification/App'
import Menu from '../components/header/Menu'

new Vue({
  el: '#main-content',
  components: {
    'app-header' : Menu,
    'app' : App
  },
});
