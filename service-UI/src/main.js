import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'


Vue.config.productionTip = false
Vue.prototype.$http = axios

// window.API_HOST = "http://serviceapi:8086"
window.API_HOST = process.env.VUE_APP_API_HOST
console.log(process.env.VUE_APP_API_HOST);

new Vue({
  router,
  store,

  render: function (h) { return h(App) }
}).$mount('#app')
