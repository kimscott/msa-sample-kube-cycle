import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'
import axios from 'axios'


Vue.config.productionTip = false
Vue.prototype.$http = axios

window.API_HOST = "http://serviceapi:8086"
// window.API_HOST = "http://localhost:8086"


console.log(process.env);
console.log('process.env.API_HOST = ' + API_HOST);

new Vue({
  router,
  store,

  render: function (h) { return h(App) }
}).$mount('#app')
