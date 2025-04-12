<template>
    <v-container class="d-flex align-center justify-center" style="height: 100vh;">
        <v-card class="pa-5 ma-auto" max-width="400" width="100%">
            <v-card-title>Register</v-card-title>
            <v-card-text>
                <v-form @submit.prevent="handleRegister">
                    <v-text-field v-model="email" label="Email" required></v-text-field>
                    <v-text-field v-model="phoneNumber" label="Phone Number" required></v-text-field>
                    <v-text-field v-model="password" label="Password" required type="password"></v-text-field>
                    <v-text-field v-model="name" label="Full Name" required></v-text-field>
                    <v-btn v-if="loading" disabled type="submit" block color="primary">Process</v-btn>
                    <v-btn v-if="!loading" type="submit" block color="primary">Register</v-btn>
                    <v-btn block variant="text" @click="$router.push('/')">Login</v-btn>
                </v-form>
            </v-card-text>
        </v-card>

        <!--pop up modal-->
        <v-dialog v-model="confirmDialog" max-width="400px">
            <v-card>
                <v-card-title class="headline">Success</v-card-title>
                <v-card-text>Data has been successfully created</v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="grey" @click="confirmOk">Ok</v-btn>
                </v-card-actions>
            </v-card>
          </v-dialog>  
        
        <!--pop up modal error-->
        <v-dialog v-model="errorDialog" max-width="400px">
            <v-card>
                <v-card-title class="headline">Error</v-card-title>
                <v-card-text>
                    <ul>
                        <li v-for="(error,index) in errorMessages" :key="index">{{ error }}</li>
                    </ul>
                </v-card-text>
                <v-card-actions>
                    <v-spacer></v-spacer>
                    <v-btn color="red" @click="errorDialog = false">Close</v-btn>
                </v-card-actions>
            </v-card>  
        </v-dialog>

    </v-container>
</template>
<script setup>
    import { ref } from 'vue';
    import { useRouter } from 'vue-router';
    import axios from 'axios';

    const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
    const email = ref('');
    const password = ref('');
    const phoneNumber = ref('');
    const name = ref('');
    const router = useRouter();
    const loading = ref(false);
    const confirmDialog = ref(false);
    const errorDialog = ref(false);
    const errorMessages = ref([]);

    const confirmOk = () => {
        router.push('/');
    };

    const handleRegister = async () => {
        loading.value = true;
        try{
            const response = await axios.post(`${API_BASE_URL}api/v1/auth/register`,{
                email: email.value,
                password: password.value,
                phoneNumber: phoneNumber.value,
                name: name.value,
                rolesId: 2, // hardcode default casier
            });
            console.log('Registration success : ',response.data);
            if (response.data.status === 200){
                confirmDialog.value = true;
            };
        }catch(error){
            console.log('Registration Error ');
            if (error.response && error.response.data && error.response.data.errors){
                console.log("show display error");
                errorMessages.value = error.response.data.errors;
                errorDialog.value = true;
            }else{
                errorMessages.value = ["An unexpected error occured !"];
                errorDialog.value = true;
            }
        }finally{
            loading.value = false;
        }
    };
</script>