/**
 * ユーザ管理画面のjs
 */
import Vue from 'vue'
import App from './components/user/App'
import Menu from './components/header/Menu'

new Vue({
  el: '#main-content',
  components: {
    'app-header' : Menu,
    'user-app' : App
  },
});
