<template>
    <v-container fluid class="responsive-container">
        <v-row class="mb-4">
            <v-col cols="12" md="6">
                <h2 class="text-h5 font-weight-bold">Reporting</h2>
            </v-col>
            <v-col cols="12" md="6" class="text-right">
                <v-btn color="primary" @click="downloadReportAllTransaction">
                    <v-icon left>mdi-file-chart</v-icon> Generate All Report
                </v-btn>
            </v-col>
        </v-row>
        <!--search filter form from date to date-->
        <v-card class="pa-4 mb-4 elevation-3">
            <v-row>
                <v-col cols="12" md="4">
                    <v-text-field label="From Date" v-model="fromDate" type="date"></v-text-field>
                </v-col>
                <v-col cols="12" md="4">
                    <v-text-field label="To Date" v-model="toDate" type="date">
                    </v-text-field>
                </v-col>
                <v-col cols="12" md="1">
                    <v-btn color="primary" class="mt-2" @click="searchTransactionByDate">
                        <v-icon left>mdi-magnify</v-icon> Search
                    </v-btn>
                </v-col>
                <v-col cols="12" md="1">
                    <v-btn color="primary" class="mt-2" @click="clearSearch">
                        <v-icon left>mdi-delete</v-icon> Clear
                    </v-btn>
                </v-col>
            </v-row>
        </v-card>

        <v-card class="pa-4 elevation-3">
            <v-card-title>Reporting Transaction</v-card-title>
            <v-divider></v-divider>
            <v-sheet class="table-container">
                <v-data-table-server :headers="headers" :items-length="totalItems" :items="transactionReports"
                    v-model:options="options" @update:options="handlePagination" class="elevation-1 mt-3 wider-table"
                    fixed-header dense>
                    <!--row number-->
                    <template v-slot:item.index="{ index }">
                        {{ getIndex(index) }}
                    </template>
                    <!--actions-->
                    <template v-slot:item.actions="{ item }">
                        <v-btn icon color="primary" @click="fetchTransactionByID(item.transactionId)">
                            <v-icon>mdi-eye</v-icon>
                        </v-btn>
                    </template>
                </v-data-table-server>
            </v-sheet>
        </v-card>

        <!--popup modal show detail transaction -->
        <v-dialog v-model="showPopUpTransactionDetail" max-width="700">
            <v-card>
                <v-card-title class="headline d-flex align-center">
                    <v-icon left class="mr-2" color="primary">mdi-eye</v-icon>
                    Transaction Detail
                </v-card-title>
                <v-divider></v-divider>
                <v-card-text v-if="transactionDetail">
                    <v-container fluid>
                        <v-form>
                            <v-text-field v-model="transactionDetail.transactionId" label="Transaction ID" readonly />
                            <v-text-field v-model="transactionDetail.createdAt" label="Created At" readonly />
                            <v-text-field v-model="transactionDetail.total" label="Grand Total" />
                            <v-text-field v-model="transactionDetail.transactionCode" label="Transaction Group Code" />
                        </v-form>
                        <v-row class="mt-4">
                            <v-col cols="12">
                                <v-list two-line subheader>
                                    <v-subheader>Items</v-subheader>
                                    <v-divider></v-divider>
                                    <v-list-item v-for="(item, index) in transactionDetail.transactionDetailsResponse"
                                        :key="index">
                                        <v-list-item-content>
                                            <v-list-item-title class="font-weight-bold">
                                                {{ item.productResponse.name }}
                                            </v-list-item-title>

                                            <v-list-item-subtitle>
                                                Quantity: {{ item.quantity }}
                                            </v-list-item-subtitle>

                                            <v-list-item-subtitle>
                                                Total Price : {{ item.totalPrice }}
                                            </v-list-item-subtitle>

                                            <v-list-item-subtitle>
                                                Transaction Group Code: {{ item.transactionCode }}
                                            </v-list-item-subtitle>

                                        </v-list-item-content>
                                        <v-list-item-icon>
                                            <v-icon color="deep-purple lighten-1">mdi-cart</v-icon>
                                        </v-list-item-icon>
                                    </v-list-item>
                                </v-list>
                            </v-col>
                        </v-row>
                    </v-container>
                </v-card-text>
                <v-card-actions>
                    <v-btn color="primary">Generate Report</v-btn>
                    <v-btn color="red" @click="cancelPopUpTransactionDetailID">Cancel</v-btn>
                </v-card-actions>
            </v-card>
        </v-dialog>

    </v-container>
</template>
<script setup>
import { useAuthStore } from "@/store/auth";
import axios from "axios";
import { onMounted, ref } from "vue";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const loading = ref(false);
const auth = useAuthStore();
const fromDate = ref('');
const transactionDetail = ref({});
const toDate = ref('');
const totalItems = ref(0);
const transactionReports = ref([]);
const showPopUpTransactionDetail = ref(false);

const headers = [
    { title: "No", key: "index", width: "10px" },
    { title: "ID Transaction", key: "transactionId", width: "120px" },
    { title: "Transaction Time", key: "createdAt", width: "200px" },
    { title: "Grand Total", key: "total", width: "200px" },
    { title: "Transaction Code", key: "transactionCode", width: "200px" },
    { title: "Actions", key: "actions", width: "120px" }
];

const clearSearch = () => {
    fromDate.value = ''
    toDate.value = ''
    fetchTransactionReports();
};

const searchTransactionByDate = () => {
    console.log("from date : ", fromDate.value);
    console.log("to date : ", toDate.value);
    fetchTransactionReports();
};

const getIndex = (index) => {
    return index + 1 + (options.value.page - 1) * options.value.itemsPerPage;
};

const cancelPopUpTransactionDetailID = () => {
    showPopUpTransactionDetail.value = false;
};

const options = ref({
    page: 1,
    itemsPerPage: 10
});

const handlePagination = (newOptions) => {
    console.log("New Pagination Options : ", newOptions);
    console.log("Current Page : ", options.value.page);
    console.log("Items Per Page : ", options.value.itemsPerPage);

    options.value.page = Number(newOptions.page) || 1;
    options.value.itemsPerPage = Number(newOptions.itemsPerPage);
    fetchTransactionReports();
};

const downloadReportAllTransaction = async () => {
    console.log("download transaction report");
    try {
        const endpoint = `${API_BASE_URL}api/v1/transaction/report-pdf`;
        const fileType = 'application/pdf';
        const fileExtension = 'pdf'

        const response = await axios.get(endpoint, {
            headers: {
                Authorization: 'Bearer ' + auth.token
            },
            responseType: 'blob'
        });

        const disposition = response.headers['content-disposition'];
        let fileName = `transaction_all_report.${fileExtension}`;
        if (disposition && disposition.includes('filename=')) {
            fileName = disposition.split('filename=')[1]
                .replace(/["']/g, '')
                .trim();
        }

        const url = window.URL.createObjectURL(new Blob([response.data], { type: fileType }));
        const link = document.createElement('a');
        link.href = url;
        link.setAttribute('download', fileName);
        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
        console.log("Success download : ", fileName);
    } catch (error) {
        console.log("Error download report : ", error);
    }
};

const fetchTransactionByID = async (id) => {
    try {
        const response = await axios.get(`${API_BASE_URL}api/v1/transaction/findbyid/${id}`, {
            headers: {
                Authorization: "Bearer " + auth.token,
                "Content-Type": "application/json"
            }
        });
        console.log("response from fetching id : ", response);
        if (response.data.status === 200) {
            transactionDetail.value = response.data.data;
            showPopUpTransactionDetail.value = true;
            console.log("response from fetching id : ", JSON.stringify(transactionDetail));
        }
    } catch (error) {
        console.log("Error fetch transaction by id : ", error);
    }
};

const fetchTransactionReports = async () => {
    loading.value = true;
    try {
        const { page, itemsPerPage } = options.value;
        const response = await axios.get(`${API_BASE_URL}api/v1/transaction/search`, {
            params: {
                fromDate: fromDate.value,
                toDate: toDate.value,
                page: page - 1,
                size: itemsPerPage
            },
            headers: {
                Authorization: "Bearer " + auth.token,
                "Content-Type": "application/json"
            }
        });
        console.log("response from fetching transaction response :", response);
        if (response.data.status === 200) {
            transactionReports.value = response.data.data.content.map((transaction) => ({
                transactionId: transaction.transactionId,
                createdAt: transaction.createdAt,
                total: transaction.total,
                transactionCode: transaction.transactionCode
            }));
            totalItems.value = Number(response.data.data?.totalElements) || 0;
        }
    } catch (error) {
        console.log("Error fething transactions : ", error);
    } finally {
        loading.value = false;
    }
};

onMounted(() => {
    fetchTransactionReports();
})

</script>
<style>
/* Wider container */
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
</style>