<template>
    <v-container class="d-flex align-center justify-center" style="height: 100vh;">
        <v-card class="pa-5 ma-auto" max-width="400" width="100%">
            <v-card-title>Login</v-card-title>
            <v-card-text>
                <v-form @submit.prevent="handleLogin">
                    <v-text-field v-model="email" label="Email" required></v-text-field>
                    <v-text-field v-model="password" label="Password" type="password" required></v-text-field>
                    <v-btn type="submit" block color="primary" :disabled="loading">Login</v-btn>
                    <v-btn block variant="text" @click="$router.push('/register')">Register</v-btn>
                </v-form>
            </v-card-text>
        </v-card>

        <!--loading-->
        <v-overlay v-model="loading" class="d-flex align-center justify-center" persistent>
            <v-progress-circular indeterminate size="64" color="primary">
                Loading..
            </v-progress-circular>
        </v-overlay>

        <!--error message-->
        <v-dialog v-model="showError" max-width="350">
            <v-card>
                <v-card-title class="text-h6">Login Failed</v-card-title>
                <v-card-text>
                    <ul>
                        <li v-for="(error,index) in authStore.error" :key="index">{{ error }}</li>
                    </ul>
                </v-card-text>
                <v-card-actions>
                    <v-btn color="error" block @click="showError = false">Ok</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>
    </v-container>
</template>

<script setup>
import { ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/store/auth';

const email = ref('');
const password = ref('');
const loading = ref(false);
const showError = ref(false);
const router = useRouter();
const authStore = useAuthStore();

watch(() => authStore.error, (newError) => {
    if (newError) showError.value = true;
});

const handleLogin = async () => {
    await authStore.login(email.value,password.value);
    if (authStore.isAuthenticated){
        router.push('/admin/home');
    }
};
</script>
