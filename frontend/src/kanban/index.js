/**
 * 直接指定画面のjs
 */
import Vue from 'vue'
import Dashboard from '../components/notification/Dashboard'
import Menu from '../components/header/Menu'

new Vue({
  el: '#main-content',
  components: {
    'app-header' : Menu,
    'dashboard' : Dashboard
  }
});
