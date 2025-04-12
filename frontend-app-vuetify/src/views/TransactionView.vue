<template>
    <v-container fluid class="responsive-container">
        <v-row class="mb-4">
            <v-col cols="12" md="6">
                <h2 class="text-h5 font-weight-bold">Point of Sales (POS)</h2>
            </v-col>
            <v-col cols="12" md="6" class="text-right">
                <v-btn color="primary" @click="checkoutProduct">
                    <v-icon left>mdi-cart</v-icon> Checkout
                </v-btn>
                <v-btn color="red" class="ml-2" @click="clearTransactions">
                    <v-icon left>mdi-delete</v-icon> Clear All
                </v-btn>
            </v-col>
        </v-row>

        <!-- Search Product -->
        <v-text-field v-model="searchQuery" label="Search Product" prepend-icon="mdi-magnify"
            @keyup.enter="addProductToTransaction" class="mb-3"></v-text-field>

        <!-- Transaction Table -->
        <v-card class="pa-4 elevation-3">
            <v-card-title>Transaction Details</v-card-title>
            <v-divider></v-divider>

            <v-sheet class="table-container">
                <v-data-table :headers="headers" :items="transactions" class="elevation-1 mt-3 wider-table" fixed-header
                    dense>
                    <template v-slot:item.qty="{ item }">
                        <v-text-field v-model.number="item.qty" type="number" min="1" class="qty-input"
                            @change="updateTotal(item)"></v-text-field>
                    </template>

                    <template v-slot:item.total="{ item }">
                        {{ formatPrice(item.qty * item.price) }}
                    </template>

                    <template v-slot:item.actions="{ item }">
                        <v-btn icon color="red" @click="deleteTransaction(item)">
                            <v-icon>mdi-delete</v-icon>
                        </v-btn>
                    </template>
                </v-data-table>
            </v-sheet>
        </v-card>

        <!--pop up confirmation checkout-->
        <v-dialog v-model="showPopUpConfirmation" max-width="600">
            <v-card>
                <v-card-title v-if="checkoutWarningError === ''">Are you sure want checkout this cart ?</v-card-title>
                <v-card-title v-if="checkoutWarningError">{{ checkoutWarningError }}</v-card-title>
                <v-card-text>
                    <p>Confirmation checkout product</p>
                    <v-spacer></v-spacer>
                </v-card-text>
                <v-card-actions>
                    <v-btn v-if="checkoutWarningError === ''" @click="checkout" color="primary">Yes</v-btn>
                    <v-btn @click="cancelCheckout" color="red">Cancel</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

    </v-container>
</template>

<script setup>
import { useAuthStore } from "@/store/auth";
import axios from "axios";
import { ref } from "vue";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const auth = useAuthStore();
const showMessageErrors = ref([]);
const checkoutWarningError = ref('');
const transactions = ref([]);
const searchQuery = ref("");
const showPopUpConfirmation = ref(false);

// Table Headers
const headers = [
    { title: "ID Product", key: "id", width: "100px" },
    { title: "Name of Product", key: "name", width: "200px" },
    { title: "Qty", key: "qty", sortable: false, width: "100px" },
    { title: "Item Price", key: "formattedPrice", width: "120px" },
    { title: "Total", key: "total", width: "120px" },
    { title: "Actions", key: "actions", sortable: false, width: "100px" },
];

const checkoutProduct = () => {
    if (transactions.value.length === 0){
        checkoutWarningError.value = 'No product avaiable in cart !';
        showPopUpConfirmation.value = true;
        return;
    }
    checkoutWarningError.value = '';
    showPopUpConfirmation.value = true;
};

const cancelCheckout = () => {
    showPopUpConfirmation.value = false;
};

const addProductToTransaction = async () => {
    const search = searchQuery.value.toLowerCase();
    console.log("search product by id : ",search);
    try{
        const response = await axios.get(`${API_BASE_URL}api/v1/product/findById/${search}`,{
            headers: {
                Authorization: "Bearer "+auth.token,
                "Content-Length": "application/json"
            }
        });
        console.log("response from add product : ",response);
        if (response.data.status === 200){
            const productData = response.data.data;
            const existingItem = transactions.value.find(t => t.id === productData.id);
            if (existingItem){
                existingItem.qty += 1;
                existingItem.totalPrice = productData.originalPrice * existingItem.qty;
            }else{
                transactions.value.push({
                    id: productData.id,
                    name: productData.name,
                    price: productData.originalPrice,
                    formattedPrice: productData.price,
                    totalPrice: productData.originalPrice,
                    qty: 1
                });
            }
            searchQuery.value = "";
        }
    }catch(error){
        console.log("Failed get data product : ",error);
        if (error.response?.data?.errors){
            showMessageErrors.value = error.response.data.errors;
            console.log("show message errors : ",showMessageErrors.value);
        }else{
            showMessageErrors.value = ["Please check in more detail."];
        }
    }
};

const updateTotal = (item) => {
    if (item.qty < 1) {
        item.qty = 1;
    }
};

const formatPrice = (price) => new Intl.NumberFormat("id-ID", {
    style: "currency",
    currency: "IDR",
    minimumFractionDigits: 0
}).format(price);

const deleteTransaction = (item) => {
    transactions.value = transactions.value.filter(t => t.id !== item.id);
};

const clearTransactions = () => {
    transactions.value = [];
};

const checkout = async () => {
    if (transactions.value.length === 0) {
        alert("No transactions to process!");
        return;
    }
    console.log("Transaction : ",JSON.stringify(transactions.value));
    try{
        var payloadTransaction = {
            "transactionDetails" : transactions.value
        }
        const response = await axios.post(`${API_BASE_URL}api/v1/transaction/checkout`,payloadTransaction,{
            headers: {
                Authorization: "Bearer "+auth.token,
                "Content-Type": "application/json"
            }
        });
        console.log("response checkout transaction : ",JSON.stringify(response));
        if (response.data.status === 200){
            transactions.value = [];
            showPopUpConfirmation.value = false;
        }
    }catch(error){
        console.log("Error checkout transaction : ",error);
    }
};
</script>

<style scoped>
/* Responsive Container */
.responsive-container {
    max-width: 1400px;
    margin: auto;
    padding: 20px;
}

/* Table Styling */
.table-container {
    width: 100%;
    overflow-x: auto;
}

.wider-table {
    min-width: 900px;
}

/* Quantity Input */
.qty-input {
    width: 80px;
}
</style>