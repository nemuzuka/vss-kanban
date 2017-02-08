/**
 * TOP画面のjs
 */
import Vue from 'vue'
import Dashborad from './components/top/Dashborad'
import Menu from './components/header/Menu'

new Vue({
  el: '#main-content',
  components: {
    'app-header' : Menu,
    'dashboard' : Dashborad
  },
});
