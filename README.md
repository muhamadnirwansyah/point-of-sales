#Example project spring boot & vuetify
hi this is my example project fullstack point of sales. i am here using several technoloy stack including the following.

1. JDK VERSION 21
2. Database PostgreSQL
3. JPA Hibernate
4. Lombok
5. JWT (Json Web Tokens)
6. Jasper Report
7. NVM version 20.18.0
8. VueJS/Vuetify framework
9. Swagger/OpenAPI
10. Spring security


tables in the project POS


<img width="448" alt="image" src="https://github.com/user-attachments/assets/7ea00268-caf4-47b9-aea1-641943112732" />


Documentation Api

<img width="1466" alt="image" src="https://github.com/user-attachments/assets/d4127c11-f707-48c1-a57f-9fa59126bbeb" />


Here are some of the features in it.

1. Login

   <img width="1463" alt="image" src="https://github.com/user-attachments/assets/21822cfc-6ac5-4a52-9ac4-394f40dbe444" />

2. Dashboard

   <img width="1462" alt="image" src="https://github.com/user-attachments/assets/e5dfc542-b7ff-46aa-ad17-af7b637d7ffc" />
  
3. CRUD Category

   <img width="1461" alt="image" src="https://github.com/user-attachments/assets/9945f8ce-6df8-4093-927c-85810519df8c" />

   <img width="1466" alt="image" src="https://github.com/user-attachments/assets/633e3649-e759-4714-b689-739ddb8bfea5" />

   <img width="1463" alt="image" src="https://github.com/user-attachments/assets/a9be272b-c864-4810-88e9-b0233a27174e" />

4. CRUD Product

   <img width="1466" alt="image" src="https://github.com/user-attachments/assets/b8995383-abae-454e-a323-d4881591b2da" />

   <img width="1460" alt="image" src="https://github.com/user-attachments/assets/1c9d6b78-6838-42c5-9a44-92a709b03b6e" />

   <img width="1461" alt="image" src="https://github.com/user-attachments/assets/73b867df-6b96-48dd-9cd2-59824faf49e0" />

   <img width="1461" alt="image" src="https://github.com/user-attachments/assets/b9b1d433-10a6-489a-83dc-f826f319b288" />

   <img width="1461" alt="image" src="https://github.com/user-attachments/assets/f6087f16-d870-4fbb-9fd9-659805908423" />

5. Transaction Process & Report

   <img width="1467" alt="image" src="https://github.com/user-attachments/assets/b39bc019-d92d-4877-92fc-89b6f580a2c9" />

   <img width="1467" alt="image" src="https://github.com/user-attachments/assets/62e0ad4e-8f10-4d3b-9342-098fa33af7be" />

   <img width="1462" alt="image" src="https://github.com/user-attachments/assets/3648f754-74b2-4117-85f3-7c29edc599f4" />

   <img width="1463" alt="image" src="https://github.com/user-attachments/assets/45df7363-81bf-497f-b3a6-6873b52c4297" />
  
   <img width="1464" alt="image" src="https://github.com/user-attachments/assets/f91d0fc7-279c-4ad1-980d-74729226561f" />


# Prepare data to running project

1. Roles

INSERT INTO public.roles (id, "name") VALUES(1, 'ADMIN');
INSERT INTO public.roles (id, "name") VALUES(2, 'CASIER');

2. Account

INSERT INTO public.account (id, deleted, email, "name", "password", roles_id, phone_number) VALUES(1, 0, 'dickanirwansyah@gmail.com', 'muhamad dicka nirwansyah', '$2a$10$WXNugZ3DEYWYJSppmOfBq.PLC.oEZ6GpvzRa7nxIr.WEjdMhZkgza', 1, '087882828283');

# Running Backend
1. cd backend-app -> mvn clean install
and then you can running from IDE, usually im using intellijIDEA..
<img width="1430" alt="image" src="https://github.com/user-attachments/assets/1aeed0df-7844-451d-b933-b3c97283a55d" />
   
   
3. cd frontend-app-vuetify -> npm install -> npm run dev
and then you will show result like this..
<img width="1109" alt="image" src="https://github.com/user-attachments/assets/809bd64f-a5f1-483d-8f7d-04f34204bbb2" />


