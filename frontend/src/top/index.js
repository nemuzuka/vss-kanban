/**
 * TOP画面のjs
 */
import Vue from 'vue'
import Dashboard from '../components/top/Dashboard'
import Menu from '../components/header/Menu'

new Vue({
  el: '#main-content',
  components: {
    'app-header' : Menu,
    'dashboard' : Dashboard
  },
});
