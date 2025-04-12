
import { registerPlugins } from '@/plugins'
import App from './App.vue'
import { createApp } from 'vue'
import 'vuetify/styles';
import router from './router';
import { createPinia } from 'pinia';
import { createVuetify } from 'vuetify';


const vuetify = createVuetify();
const pinia = createPinia();

const app = createApp(App);
registerPlugins(app)
app.use(vuetify);
app.use(router);
app.use(pinia);
app.mount('#app')
