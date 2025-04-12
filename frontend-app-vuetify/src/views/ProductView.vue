<template>
  <v-container fluid class="responsive-container">
    <v-row class="mb-4">
      <v-col cols="12" md="6">
        <h2 class="text-h5 font-weight-bold">Product Management</h2>
      </v-col>
      <v-col cols="12" md="6" class="text-right">
        <v-btn color="primary" @click="generateReportProduct">
          <v-icon left>mdi-file-chart</v-icon> Generate Report
        </v-btn>
        &nbsp;
        <v-btn color="primary" @click="addProduct">
          <v-icon left>mdi-plus</v-icon> Add Product
        </v-btn>
      </v-col>
    </v-row>

    <!-- Search Form -->
    <v-card class="pa-4 mb-4 elevation-3">
      <v-row>
        <v-col cols="12" md="4">
          <v-text-field 
            v-model="name" 
            label="Search by Name" 
            clearable>
          </v-text-field>
        </v-col>
        <v-col cols="12" md="3">
          <v-select 
            v-model="categoryId" 
            :items="categories" 
            item-title="text"
            item-value="value"
            label="Select Category" 
            clearable>
          </v-select>
        </v-col>
        <v-col cols="12" md="2">
          <v-text-field 
            v-model="fromDate" 
            label="From Date" 
            type="date"></v-text-field>
        </v-col>
        <v-col cols="12" md="2">
          <v-text-field 
            v-model="toDate" 
            label="To Date" 
            type="date"></v-text-field>
        </v-col>
        <v-col cols="12" md="1">
          <v-btn color="primary" class="mt-2" @click="searchProduct">
            <v-icon left>mdi-magnify</v-icon> Search
          </v-btn>
        </v-col>
      </v-row>
    </v-card>

    <!-- Product Table -->
    <v-card class="pa-4 elevation-3">
      <v-card-title>Product List</v-card-title>
      <v-divider></v-divider>
      <v-sheet class="table-container">
        <v-data-table-server 
          :headers="headers" 
          :items="products"
          :items-length="totalItems"
          :items-per-page="options.itemsPerPage"
          v-model:options="options"
          :loading="loading"
          class="elevation-1 mt-3 wider-table" 
          fixed-header
          dense
          @update:options="handlePagination"
          >
          <!--row number-->
          <template v-slot:item.index="{index}">
            {{ getIndex(index) }}
          </template>
          <template v-slot:item.actions="{ item }">
            <v-btn icon color="red" @click="deleteProduct(item.id)">
              <v-icon>mdi-delete</v-icon>
            </v-btn>
            &nbsp;
            <v-btn icon color="primary" @click="updateProduct(item.id)">
              <v-icon>mdi-pencil</v-icon>
            </v-btn>
          </template>
        </v-data-table-server>
      </v-sheet>
    </v-card>

    <!--pop up modal product report-->
    <v-dialog v-model="showPopUpModalReport" max-width="600">
      <v-card>
        <v-card-title>Generate Report Product</v-card-title>
        <v-card-text>
          <p>Generate report product PDF/XLS</p>
          <v-spacer></v-spacer>
          <v-form>
          <v-select v-model="selectReport" 
          :items="reportProducts">
          </v-select>
        </v-form>
        </v-card-text>
        <v-card-actions>
          <v-btn color="primary" @click="downloadReportProduct">Generate</v-btn>
          <v-btn color="red" @click="cancelReportProduct">Back</v-btn>
      </v-card-actions>
      </v-card>
    </v-dialog>

    <!--pop up modal product -->
    <v-dialog v-model="showPopUpModal" max-width="600">
        <v-card>
          <v-card-title v-if="!isUpdated && !isDeleted">New Product</v-card-title>
          <v-card-title v-if="isUpdated">Update Product</v-card-title>
          <v-card-title v-if="isDeleted">Delete Product</v-card-title>
        <!-- <v-card-text style="padding:30px;">
        </v-card-text> -->
        <v-card-text>
          <p v-if="!isUpdated && !isDeleted">Manage new product</p>
          <p v-if="isUpdated">Update Product</p>
          <p v-if="isDeleted">Delete Product</p>
          <v-spacer></v-spacer>
          <v-form>
            <v-text-field v-model="formProduct.name" label="Name"></v-text-field>
            <v-text-field v-model="formProduct.price" label="Price"></v-text-field>
            <v-text-field v-model="formProduct.quantity" label="Quantity"></v-text-field>
            <v-select v-model="formProduct.categoryId" label="Category"
            :items="categories"
            item-title="text"
            item-value="value"
            clearable>
            </v-select>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn v-if="!isUpdated && !isDeleted" color="primary" @click="saveProduct">Save</v-btn>
          <v-btn v-if="isUpdated" color="primary" @click="executeUpdateProduct">Update</v-btn>
          <v-btn v-if="isDeleted" color="primary" @click="executeDeleteProduct">Delete</v-btn>
          <v-btn color="red" @click="closeProductPopUp">Cancel</v-btn>
        </v-card-actions>
        </v-card>
    </v-dialog>
  </v-container>
</template>

<script setup>
import { useAuthStore } from "@/store/auth";
import axios from "axios";
import { ref, onMounted } from "vue";

const isDeleted = ref(false);
const isUpdated = ref(false);
const showMessageErrors = ref([]);
const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
const formProduct = ref({
  id: 0,
  name : '',
  price: 0,
  quantity: 0,
  categoryId: 'Choose One'
});
const reportProducts = ref([
  'PDF','XLS'
]);
const selectReport = ref('');
const products = ref([]);
const categories = ref([]);
const totalItems = ref(0);
const loading = ref(false);
const showPopUpModal = ref(false);
const showPopUpModalReport = ref(false);
const name = ref('');
const categoryId = ref('');
const fromDate = ref('');
const toDate = ref('');
const options = ref({
  page: 1,
  itemsPerPage: 10
});

const getIndex = (index) => {
  return index + 1 + (options.value.page - 1) * options.value.itemsPerPage;
};

const handlePagination = (newOptions) => {
  console.log("New Pagination Options:", newOptions);
  console.log("Current Page:", options.value.page);
  console.log("Items Per Page:", options.value.itemsPerPage);

  options.value.page = Number(newOptions.page) || 1;
  options.value.itemsPerPage = Number(newOptions.itemsPerPage) || 10;
  fetchProduct();
}

const auth = useAuthStore();

const generateReportProduct = () => {
  console.log("generate product reports");
  showPopUpModalReport.value = true;
};

const downloadReportProduct = async () => {
  console.log("download product report : ",selectReport.value);
  try{
    
    const endpoint = selectReport.value === 'PDF' ? `${API_BASE_URL}api/v1/product/report-pdf` : 
    `${API_BASE_URL}api/v1/product/report-xlsx`;

    const fileType = selectReport.value === 'PDF' ? 'application/pdf' : 
      'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet';

   const fileExtension = selectReport.value === 'PDF' ? 'pdf' : 'xls';
   
   const response = await axios.get(endpoint, {
    headers: {
      Authorization: 'Bearer '+auth.token
    },
    responseType: 'blob'
   });

   const disposition = response.headers['content-disposition'];
   let fileName = `product_report.${fileExtension}`;
   if (disposition && disposition.includes('filename=')){
    fileName = disposition.split('filename=')[1]
    .replace(/["']/g, '')
    .trim();
   }
  
   const url = window.URL.createObjectURL(new Blob([response.data], {type: fileType}));
   const link = document.createElement('a');
   link.href = url;
   link.setAttribute('download', fileName);
   document.body.appendChild(link);
   link.click();
   document.body.removeChild(link);
   window.URL.revokeObjectURL(url);
   console.log("Success download : ",fileName);
  }catch(error){
    console.log("Error download report : ",error);
  }
};

const cancelReportProduct = () => {
  showPopUpModalReport.value = false;
}

const addProduct = () => {
  console.log("add product..");
  isUpdated.value = false;
  isDeleted.value = false;
  showPopUpModal.value = true;
};

const executeDeleteProduct = async () => {
  console.log("execute delete product : ",formProduct.value.id);
  try{
    const response = await axios.delete(`${API_BASE_URL}api/v1/product/delete/${formProduct.value.id}`,{
      headers: {
        Authorization: "Bearer "+auth.token,
        "Content-Type": "application/json"
      }
    });
    console.log("response from execute delete product : ",response);
    if (response.data.status === 200){
       fetchProduct();
       closeProductPopUp();
    }
  }catch(error){
    console.log("error execute update product : ",error);
    if (error.response?.data?.errors){
      showMessageErrors.value = error.response.data.errors;
    }else{
      showMessageErrors.value = ["Please check in more detail."];
    }
  }
};

const executeUpdateProduct = async () => {
  console.log("execute update product : ",formProduct.value);
  try{
    const response = await axios.put(`${API_BASE_URL}api/v1/product/update`,formProduct.value,{
      headers: {
        Authorization: "Bearer "+auth.token,
        "Content-Type": "application/json"
      }
    });
    console.log("response from execute update product : ",response);
    if (response.data.status === 200){
        fetchProduct();
        closeProductPopUp();
    }
  }catch(error){
    console.log("error execute update product : ",error);
    if (error.response?.data?.errors){
      showMessageErrors.value = error.response.data.errors;
    }else{
      showMessageErrors.value = ["Please check in more detail."];
    }
  }
};

const deleteProduct = async (id) => {
  console.log("delete product by id : ",id);
  showPopUpModal.value = true;
  isUpdated.value = false;
  isDeleted.value = true;
  try{
    const response = await axios.get(`${API_BASE_URL}api/v1/product/findById/${id}`,{headers: {
      Authorization: "Bearer "+auth.token,
      "Content-Type" : "application/json"
    }});
    console.log("response from find product by id : ",response);
    if (response.data.status === 200){
      const productData = response.data.data;
      formProduct.value.id = productData.id;
      formProduct.value.name = productData.name;
      formProduct.value.price = productData.originalPrice;
      formProduct.value.quantity = productData.quantity;
      formProduct.value.categoryId = productData.category.id;
    }
  }catch(error){
    console.log("error find product by id : ",error);
  }
};

const updateProduct = async (id) => {
  console.log("update product by id : ",id);
  showPopUpModal.value = true;
  isUpdated.value = true;
  isDeleted.value = false;
  try{
    const response = await axios.get(`${API_BASE_URL}api/v1/product/findById/${id}`,{headers: {
      Authorization: "Bearer "+auth.token,
      "Content-Type" : "application/json"
    }});
    console.log("response from find product by id : ",response);
    if (response.data.status === 200){
      const productData = response.data.data;
      formProduct.value.id = productData.id;
      formProduct.value.name = productData.name;
      formProduct.value.price = productData.originalPrice;
      formProduct.value.quantity = productData.quantity;
      formProduct.value.categoryId = productData.category.id;
    }
  }catch(error){
    console.log("error find product by id : ",error);
  }
};

const saveProduct = async () => {
  console.log("Product save : ",JSON.stringify(formProduct.value));
  try{
    const response = await axios.post(`${API_BASE_URL}api/v1/product/save`,formProduct.value,{
      headers: {
        Authorization: "Bearer "+auth.token,
        "Content-Type" : "application/json"
      }
    });
    console.log("Response save : ",JSON.stringify(response));
    if (response.data.status === 200){
      console.log("success save product");
      fetchProduct();
      closeProductPopUp();
    }
  }catch(error){
    console.log("error save product : ",error);
    if (error.response?.data?.errors){
      showMessageErrors.value = error.response.data.errors;
    }else{
      showMessageErrors.value = ["Please check in more detail."];
    }
  }
};

const closeProductPopUp = () => {
  console.log("close popup product");
  showPopUpModal.value = false;
  isDeleted.value = false;
  isUpdated.value = false;
  formProduct.value.categoryId = 'Choose One';
  formProduct.value.name = '';
  formProduct.value.price = 0;
  formProduct.value.quantity = 0;
  formProduct.value.id = 0;
}

const dropdownCategories = async () => {
  try{
    const response = await axios.get(`${API_BASE_URL}api/v1/category/dropdown`,{
      headers: {
        Authorization: "Bearer "+auth.token,
        "Content-Length": "application/json"
      }
    });

    if (response.data.status == 200){
        categories.value = response.data.data.map(category => ({
          value: category.id,
          text: category.name
        }));
    }

  }catch(error){
    console.log("Error fetching dropdown category : ",error);
  }
}

const searchProduct = () => {
  console.log("name product : ",name.value);
  console.log("category id : ",categoryId.value);
  console.log("from date : ",fromDate.value);
  console.log("to date : ",toDate.value);
  fetchProduct();
};

const fetchProduct = async () => {
  loading.value = true;
  try{
    const { page, itemsPerPage } = options.value;
    const response = await axios.get(`${API_BASE_URL}api/v1/product/search`,{
      params : {
        name: name.value,
        categoryId: categoryId.value,
        fromDate: fromDate.value,
        toDate: toDate.value,
        page: page - 1,
        size: itemsPerPage
      },
      headers : {
        Authorization: 'Bearer '+auth.token,
        "Content-Length": 'application/json',
      }
    });
    console.log("Response from fetching product : ",response);
    if (response.data.status === 200){
        products.value = response.data.data.content.map((product) => ({
          id: product.id,
          name: product.name,
          category: product.category.name,
          price: product.price,
          stock: product.quantity,
          createdAt: product.createdAt
       }));
       totalItems.value = Number(response.data.data?.totalElements) || 0;
       console.log("Total items product : ",totalItems);
    }else{
      totalItems.value = 0;
    }
  }catch(error){
    console.log("Error fetching product : ",error);
  }finally{
    loading.value = false;
  }
}

onMounted(() => {
  dropdownCategories();
  fetchProduct();
});

const headers = [
  { title: "No", key: "index", width: "10px"},
  { title: "ID", key: "id", width: "70px" },
  { title: "Name", key: "name", width: "200px" },
  { title: "Category", key: "category", width: "150px" },
  { title: "Price", key: "price", width: "100px" },
  { title: "Stock Available", key: "stock", width: "120px" },
  { title: "Created At", key: "createdAt", width: "150px" },
  { title: "Actions", key: "actions", width: "160px" },
];

</script>

<style scoped>
/* Wider container */
.responsive-container {
  max-width: 1400px;
  margin: auto;
  padding: 20px;
}

/* Prevents table from overflowing */
.table-container {
  width: 100%;
  overflow-x: auto;
}

/* Make table wider */
.wider-table {
  min-width: 1000px;
  max-width: 100%;
}

/* Actions column doesn't shrink too much */
.v-data-table th,
.v-data-table td {
  white-space: nowrap;
  text-overflow: ellipsis;
  overflow: hidden;
}

/* Keep action buttons small */
.v-btn {
  min-width: 32px;
}
</style>
