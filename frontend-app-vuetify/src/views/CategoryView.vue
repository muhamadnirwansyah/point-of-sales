<template>
  <v-container fluid class="responsive-container">
    <v-row class="mb-4">
      <v-col cols="12" md="6">
        <h2 class="text-h5 font-weight-bold">Category Management</h2>
      </v-col>
      <v-col cols="12" md="6" class="text-right">
        <v-btn color="primary" @click="addCategory">
          <v-icon left>mdi-plus</v-icon> Add Category
        </v-btn>
      </v-col>
    </v-row>

    <!-- Table Container -->
    <v-card class="pa-4 elevation-3">
      <v-card-title>Category List</v-card-title>
      <v-divider></v-divider>

      <v-sheet class="table-container">
        <v-data-table-server 
          v-model:options="options" 
          :headers="headers" 
          :items="categories" 
          :items-length="totalItems"
          :items-per-page="options.itemsPerPage" 
          :loading="loading" 
          class="elevation-1 mt-3 wider-table" fixed-header
          dense 
          @update:options="handlePagination">
          <!--row record-->
          <template v-slot:item.index="{ index }">
            {{ getIndex(index) }}
          </template>
          <!--row record-->
          <!--button actions-->
          <template v-slot:item.actions="{ item }">
            <v-btn icon color="red" @click="showPopUpCategory(item,'deleted')">
              <v-icon>mdi-delete</v-icon>
            </v-btn>
            &nbsp;
            <v-btn icon color="primary" @click="showPopUpCategory(item,'edit')">
              <v-icon>mdi-pencil</v-icon>
            </v-btn>
          </template>
          <!--button actions-->
        </v-data-table-server>
      </v-sheet>
    </v-card>

    <!--modal popup insert-->
    <v-dialog v-model="showPopupInsertCategory" max-width="600">
      <v-card>
        <v-card-title v-if="!isUpdated && !isDeleted" class="headline">New Category</v-card-title>
        <v-card-title v-if="isUpdated" class="headline">Update Category</v-card-title>
        <v-card-title v-if="isDeleted" class="headline">Delete Category</v-card-title>
        <v-card-text v-if="showMessageErrors.length > 0" style="padding:30px">
          <ul>
            <li style="color:red" v-for="(error, index) in showMessageErrors" :key="index">{{ error }}</li>
          </ul>
        </v-card-text>
        <v-card-text>
           <p v-if="!isUpdated && !isDeleted">Manage new category data</p>
           <p v-if="isUpdated">Manage update category data</p>
           <p v-if="isDeleted">Manage delete category data</p>
          <v-spacer></v-spacer>
          <v-form>
            <v-text-field v-if="isUpdated || isDeleted" v-model="id" label="ID"></v-text-field>
            <v-text-field v-model="name" label="Name"></v-text-field>
            <v-text-field v-model="description" label="Description"></v-text-field>
          </v-form>
        </v-card-text>
        <v-card-actions>
          <v-spacer></v-spacer>
          <v-btn color="primary" v-if="!isUpdated && !isDeleted" @click="savedCategory">Save</v-btn>
          <v-btn color="primary" v-if="isUpdated" @click="updateCategory">Update</v-btn>
          <v-btn color="primary" v-if="isDeleted" @click="deleteCategory">Delete</v-btn>
          <v-btn color="red" @click="showPopupInsertCategory = false">Cancel</v-btn>
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
const auth = useAuthStore();
const showPopupInsertCategory = ref(false);
const showMessageErrors = ref([]);
const name = ref('');
const id = ref(0);
const loading = ref(false);
const isUpdated = ref(true);
const isDeleted = ref(true);
const description = ref('');
const categories = ref([]);
const totalItems = ref(0);
const options = ref({
  page: 1,
  itemsPerPage: 10
});

const getIndex = (index) => {
  return index + 1 + (options.value.page - 1) * options.value.itemsPerPage;
}

const fetchCategories = async () => {
  loading.value = true;
  try {
    const { page, itemsPerPage } = options.value;
    const response = await axios.get(`${API_BASE_URL}api/v1/category/search`, {
      params: { page: page - 1, size: itemsPerPage },
      headers: { Authorization: "Bearer " + auth.token, "Content-Type": "application/json" }
    });
    if (response.data.status === 200) {
      categories.value = response.data.data.content;
      totalItems.value = Number(response.data.data?.totalElements) || 0;
      console.log("Total Items:", totalItems.value);
    } else {
      totalItems.value = 0;
    }
  } catch (error) {
    loading.value = false;
    console.log("Error fetching categories : ", error);
  } finally {
    loading.value = false;
  }
};

const handlePagination = (newOptions) => {
  console.log("New Pagination Options:", newOptions);
  console.log("Current Page:", options.value.page);
  console.log("Items Per Page:", options.value.itemsPerPage);

  options.value.page = Number(newOptions.page) || 1;
  options.value.itemsPerPage = Number(newOptions.itemsPerPage) || 10;
  fetchCategories();
};

onMounted(fetchCategories);

const headers = ref([
  { title: "No", key: "index", width: "10px" },
  { title: "Code ID", key: "id", width: "70px" },
  { title: "Name", key: "name", width: "200px" },
  { title: "Description", key: "description", width: "250px" },
  { title: "Actions", key: "actions", width: "200px" },
]);

const addCategory = () => {
  console.log("Add Category Clicked");
  showPopupInsertCategory.value = true;
  isUpdated.value = false;
  isDeleted.value = false;
  showMessageErrors.value = [];
};


const savedCategory = async () => {
  var categoryPayload = {
    "id": 0,
    "name": name.value,
    "description": description.value
  };
  try {
    const response = await axios.post(`${API_BASE_URL}api/v1/category/save`, categoryPayload, {
      headers: {
        Authorization: "Bearer " + auth.token,
        "Content-Length": "application/json"
      }
    });
    console.log("Response save category : ", response);
    if (response.data.status === 200) {
      console.log("success..");
      fetchCategories();
      clearFormCategory();
      showPopupInsertCategory.value = false;
    }
  } catch (error) {
    console.log("Error saved category : ", error.response?.data?.errors);
    if (error.response?.data?.errors) {
      showMessageErrors.value = error.response.data.errors;
    } else {
      showMessageErrors.value = ["Please check in more detail."];
    }
  }
  console.log(JSON.stringify(categoryPayload));
};

const clearFormCategory = () => {
  id.value = 0;
  name.value = "";
  description.value = "";
};

const deleteCategory = async () => {
  console.log("process delete category");
  try{
    const response = await axios.delete(`${API_BASE_URL}api/v1/category/delete/${id.value}`,{headers:{
      Authorization: "Bearer "+auth.token,
      "Content-Type": "application/json"
    }});
    if (response.data.status === 200){
        clearFormCategory();
        fetchCategories();
        isDeleted.value = false;
        showPopupInsertCategory.value = false;
    }
  }catch(error){
    console.log("Error delete category : ",error);
  }
};

const updateCategory = async () => {
  console.log("process update category");
  try{
    var payloadUpdate = {
      "id" : id.value,
      "name" : name.value,
      "description" : name.value
    };
    const response = await axios.put(`${API_BASE_URL}api/v1/category/update`,payloadUpdate,{
      headers: {
        Authorization: "Bearer "+auth.token,
        "Content-Length": "application/json"
      }
    });
    console.log("Response from update category : ",response);
    if (response.data.status === 200){
        clearFormCategory();
        fetchCategories();
        showPopupInsertCategory.value = false;
        isUpdated.value = false;
    }
  }catch(error){
    console.log("Error update category : ",error);
    if (error.response?.data?.errors) {
      showMessageErrors.value = error.response.data.errors;
    } else {
      showMessageErrors.value = ["Please check in more detail."];
    }
  }
};

const showPopUpCategory = async (item,type) => {
  console.log("show update category : ", JSON.stringify(item));
  const currentDataCategory = JSON.parse(JSON.stringify(item));
  console.log("id category : ",currentDataCategory.id);
  if (type === 'edit'){
      isUpdated.value = true;
      isDeleted.value = false;
  }else{
      isDeleted.value = true;
      isUpdated.value = false;
  }
  loading.value = true;
  try{
    const response = await axios.get(`${API_BASE_URL}api/v1/category/findById/${currentDataCategory.id}`,{
      headers: {
        Authorization: "Bearer "+auth.token,
        "Content-Type" : "application/json",
      }
    });
    console.log("Response find by id category : ",response);
    if (response.data.status === 200){
      id.value = currentDataCategory.id;
      name.value = currentDataCategory.name;
      description.value = currentDataCategory.description;
      showPopupInsertCategory.value = true;
    }
  }catch(error){
    console.log("Error show update category : ",error);
  }finally{
    loading.value = false;
  }
}

</script>

<style scoped>
/* Wider container */
.responsive-container {
  max-width: 1400px;
  /* Adjust this for a wider table */
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
  min-width: 800px;
  /* Ensures table has more space */
  max-width: 100%;
}

/* Actions column doesn't shrink too much */
.v-data-table th,
.v-data-table td {
  white-space: nowrap;
  /* Prevents text wrapping */
  text-overflow: ellipsis;
  /* Adds "..." when text is too long */
  overflow: hidden;
}
</style>
