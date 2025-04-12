import { createRouter, createWebHistory } from "vue-router";
import LoginComponent from "@/components/LoginComponent.vue";
import RegisterComponent from "@/components/RegisterComponent.vue";
import DashboardView from "@/views/DashboardView.vue";
import HomeView from "@/views/HomeView.vue";
import CategoryView from "@/views/CategoryView.vue";
import ProductView from "@/views/ProductView.vue";
import { useAuthStore } from "@/store/auth";
import TransactionView from "@/views/TransactionView.vue";
import ReportingView from "@/views/ReportingView.vue";

const routes = [
  { path: '/', component: LoginComponent},
  { path: '/register',component: RegisterComponent},
  {
    path: '/admin',
    component: DashboardView,
    children: [
      { path: 'home', component: HomeView },
      { path: 'category', component: CategoryView },
      { path: 'product', component: ProductView },
      { path: 'transaction', component: TransactionView},
      { path: 'reporting', component: ReportingView}
    ],
    beforeEnter: (to, from, next) => {
      const auth = useAuthStore();
      if (!auth.isAuthenticated){
        next('/');
      }else{
        next();
      }
    }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;