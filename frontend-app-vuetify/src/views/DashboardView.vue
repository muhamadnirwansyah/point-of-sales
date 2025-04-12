<template>
    <v-app>
        <!-- Sidebar -->
        <v-navigation-drawer v-model="drawer" :mini-variant="mini" app color="blue-grey-darken-4" dark>
            <v-list>
                <!-- User Profile -->
                <v-list-item>
                    <v-list-item-content>
                        <v-row align="center" no-gutters>
                            <v-list-item-avatar>
                                <v-avatar color="primary">
                                    <v-icon>mdi-account</v-icon>
                                </v-avatar>
                                &nbsp;
                            </v-list-item-avatar>
                            <v-col v-if="auth.isAuthenticated" class="text-h6 font-weight-medium">{{ currentRoles }}</v-col>
                        </v-row>
                        <v-col v-if="auth.isAuthenticated" class="text-caption text-grey-lighten-2">{{ auth.email }}</v-col>
                    </v-list-item-content>
                </v-list-item>
                <v-divider></v-divider>

                <!-- Menu Items -->
                <v-list-item 
                    v-for="item in menuItems"
                    :key="item.path"
                    :to="item.path"
                    :class="{ 'active-menu': route.path === item.path }"
                >
                    <v-list-item-icon>
                        <v-icon>{{ item.icon }}</v-icon>
                    </v-list-item-icon>
                    <v-list-item-content>{{ item.label }}</v-list-item-content>
                </v-list-item>
            </v-list>
        </v-navigation-drawer>

        <!-- Top App Bar -->
        <v-app-bar app color="blue-grey-darken-2" dark>
            <v-btn icon @click="toggleDrawer">
                <v-icon>{{ drawer ? (mini ? 'mdi-chevron-right' : 'mdi-chevron-left') : 'mdi-menu' }}</v-icon>
            </v-btn>
            <v-toolbar-title>{{ currentRoles }} DASHBOARD</v-toolbar-title>
            <v-spacer></v-spacer>
            <v-btn icon @click="confirmLogout">
                <v-icon>mdi-logout</v-icon>
            </v-btn>
        </v-app-bar>

        <!-- Main Content -->
        <v-main>
            <router-view></router-view>
        </v-main>

        <!-- Logout Confirmation Dialog -->
        <v-dialog v-model="logoutDialog" max-width="400px">
            <v-card>
                <v-card-title class="headline">Confirm Logout</v-card-title>
                <v-card-text>Are you sure you want to exit from the system?</v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="grey" @click="logoutDialog = false">Cancel</v-btn>
                    <v-btn color="red" @click="logout">Logout</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-app>
</template>

<script setup>
import { useAuthStore } from '@/store/auth';
import { useRoute, useRouter } from 'vue-router';
import { computed, onMounted, ref } from 'vue';

const auth = useAuthStore();
const router = useRouter();
const route = useRoute();
const currentRoles = computed(() => auth.currentRoles);
const drawer = ref(true);
const mini = ref(false);
const logoutDialog = ref(false);

const menuItems = ref([
    { label: "Home", path: "/admin/home", icon: "mdi-home" },
    { label: "Category", path: "/admin/category", icon: "mdi-format-list-bulleted" },
    { label: "Product", path: "/admin/product", icon: "mdi-package-variant" },
    { label: "Transaction", path: "/admin/transaction", icon: "mdi-cart"},
    { label: "Reporting", path: "/admin/reporting", icon: "mdi-file-document"}
]);

onMounted(async () => {
    if (auth.isAuthenticated){
        auth.loadCurrentUser(auth.token);
    }
});

const toggleDrawer = () => {
    if (mini.value) {
        drawer.value = false; 
        mini.value = false;
    } else {
        drawer.value = true;
        mini.value = !mini.value;
    }
};

const confirmLogout = () => {
    logoutDialog.value = true;
};

const logout = () => {
    auth.logout();
    router.push('/');
};
</script>

<style scoped>
/* Sidebar transition */
.v-navigation-drawer {
    transition: width 0.5s ease-in-out;
}

.v-list-item {
    cursor: pointer;
}

/* Active menu */
.active-menu {
    background-color: rgba(255, 255, 255, 0.1);
    border-left: 4px solid #FFFFFF;
}
</style>
