import Vue from 'vue'
import Login from './Login'
import NoMenu from './components/header/LoginMenu'

new Vue({
  el: '#main-content',
  components: {
    'app-header' : NoMenu,
    'login' : Login
  },
});
