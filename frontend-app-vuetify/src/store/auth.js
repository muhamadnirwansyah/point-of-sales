import { defineStore } from "pinia";
import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: localStorage.getItem("user") || null,
        token: localStorage.getItem("token") || null,
        role: localStorage.getItem("roles") || null,
        emailLogin: localStorage.getItem("user") || null,
        userInformation: localStorage.getItem("user_information") || null,
        loading: false,
        error: null
    }),
    getters: {
        isAuthenticated: (state) => !!state.user,
        currentRoles: (state) => state.role ? state.role : 'GUESS',
        email: (state) => state.emailLogin ? state.emailLogin : 'guess@guest.com',
        userInfo: (state) => state.userInformation ? state.userInformation : '-',
    },
    actions: {
        async login(email, password){
            console.log("process auth store..");
            this.loading = true;
            this.error = null;
            try{
                const response = await axios.post(`${API_BASE_URL}api/v1/auth/sign-in`,{email, password});
                if (response.data.status === 200){
                    this.user = email;
                    this.token = response.data.data.token;
                    this.role = response.data.data.roles;
                    this.emailLogin = this.user
                    //save to local storage
                    localStorage.setItem("user",this.user);
                    localStorage.setItem("token",this.token);
                    localStorage.setItem("roles",this.role);
                }
            }catch(error){
                console.log("Error login : ",error);
                this.error = error.response?.data?.errors || ["Unauthorized system, please try again !"];
            }finally{
                this.loading = false;
            }
        },

        async loadCurrentUser(token){
            var currentToken = "Bearer "+token;
            console.log("process load current user active : ",currentToken);
            try{
                const response = await axios.post(`${API_BASE_URL}api/v1/account/token-active`,{
                    "token" : currentToken
                },
                    {
                        headers: {
                            Authorization: currentToken,
                            'Content-Type': 'application/json'
                        },
                    }
                );
                if (response.data.status === 200){
                    localStorage.setItem("user_information",JSON.stringify(response.data.data));
                    this.userInformation = localStorage.getItem("user_information");
                }
            }catch(error){
                console.log("Error load current user : ",error)
            }
        },

        logout(){
            this.user = null;
            this.token = null;
            localStorage.removeItem("user");
            localStorage.removeItem("token");
            localStorage.removeItem("roles");
            localStorage.removeItem("user_info");
        },
    }
});