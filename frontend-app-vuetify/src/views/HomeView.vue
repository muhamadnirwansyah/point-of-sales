<template>
  <v-container fluid class="dashboard-container">
    <v-row>
      <!-- Statistics Cards -->
      <v-col cols="12" md="4">
        <v-card class="stat-card" elevation="3">
          <v-card-title>Total Products</v-card-title>
          <v-card-text class="stat-value">
            {{ totalProducts }}
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card class="stat-card" elevation="3">
          <v-card-title>Total Transactions</v-card-title>
          <v-card-text class="stat-value">
            {{ totalTransactions }}
          </v-card-text>
        </v-card>
      </v-col>

      <v-col cols="12" md="4">
        <v-card class="stat-card" elevation="3">
          <v-card-title>Total Categories</v-card-title>
          <v-card-text class="stat-value">
            {{ totalCategories }}
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- User Profile Card -->
    <v-row>
      <v-col cols="12" md="6" offset-md="3">
        <v-card class="profile-card" elevation="4">
          <v-card-title>User Profile</v-card-title>
          <v-divider></v-divider>
          <v-card-text>
            <v-list>
              <v-list-item>
                <v-list-item-icon>
                  <v-icon>mdi-account</v-icon>
                </v-list-item-icon>
                <v-list-item-content>
                  <v-list-item-title>{{ user.username }}</v-list-item-title>
                  <v-list-item-subtitle>Username</v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>

              <v-list-item>
                <v-list-item-icon>
                  <v-icon>mdi-email</v-icon>
                </v-list-item-icon>
                <v-list-item-content>
                  <v-list-item-title>{{ user.email }}</v-list-item-title>
                  <v-list-item-subtitle>Email</v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>

              <v-list-item>
                <v-list-item-icon>
                  <v-icon>mdi-phone</v-icon>
                </v-list-item-icon>
                <v-list-item-content>
                  <v-list-item-title>{{ user.phone }}</v-list-item-title>
                  <v-list-item-subtitle>Phone</v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>

              <v-list-item>
                <v-list-item-icon>
                  <v-icon>mdi-lock</v-icon>
                </v-list-item-icon>
                <v-list-item-content>
                  <v-list-item-title>{{ user.permission }}</v-list-item-title>
                  <v-list-item-subtitle>Permission</v-list-item-subtitle>
                </v-list-item-content>
              </v-list-item>
            </v-list>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { onMounted, ref, watchEffect } from "vue";
import { useAuthStore } from "@/store/auth";
import axios from "axios";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const totalProducts = ref(150);
const totalTransactions = ref(320);
const totalCategories = ref(10);
const auth = useAuthStore();

const user = ref({
  username: "",
  email: "",
  phone: "",
  permission: "",
});

onMounted(async () => {
    if (auth.isAuthenticated){
        auth.loadCurrentUser(auth.token);
    }
});

const loadSummary = async () => {
  console.log("load summary data.");
  try{
    const response = await axios.get(`${API_BASE_URL}api/v1/transaction/summary`,{headers: {
      Authorization: "Bearer "+auth.token,
      "Content-Type": "application/json"
    }});
    console.log("response load summary : ",JSON.stringify(response));
    if (response.data.status === 200){
      const dataSummary = response.data.data[0];
      totalProducts.value = dataSummary.totalProduct;
      totalTransactions.value = dataSummary.grandTotal;
      totalCategories.value = dataSummary.totalCategory;
    }
  }catch(error){
    console.log("error load summary data : ",error);
  }
}

watchEffect(() => {
  console.log("mapping data : ",auth.userInformation);
  if (auth.userInformation){
      var currentUser = JSON.parse(auth.userInformation);
      user.value.username = currentUser.email,
      user.value.email = currentUser.email,
      user.value.phone = currentUser.phoneNumber,
      user.value.permission = currentUser.roles
      loadSummary();
  }
});


</script>

<style scoped>
.dashboard-container {
  max-width: 1200px;
  margin: auto;
}

.stat-card {
  text-align: center;
  padding: 20px;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #1976d2;
}

.profile-card {
  padding: 20px;
}
</style>
