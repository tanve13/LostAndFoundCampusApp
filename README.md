# 🎒 Campus Lost & Found App

A **Campus Lost & Found Management System** designed to help students and staff easily report, find, and recover lost items within a campus.  
The app provides separate **User** and **Admin** panels and uses modern cloud services for storage, authentication, and notifications.
 
--- 
 
## 📱 Features

### 👤 User Panel
- User registration & login (Firebase Authentication)
- Post **Lost Item** details with images
- Post **Found Item** details with images
- View all lost & found items
- Search and filter items
- Receive push notifications for item updates
- Profile screen with many setting options 
---

### 🛠️ Admin Panel
- Secure admin login
- View all lost and found posts
- Approve or reject user submissions
- Remove fake or duplicate posts
- Manage users and reports
- Send broadcast notifications to users
- Monitor app activity

---

## 🔔 Notifications
- Real-time push notifications using **OneSignal**
- Alerts when:
  - A matching item is found
  - Post is approved or rejected
  - Admin sends announcements

---

## ☁️ Technologies Used

### Frontend
- Android (Java / Kotlin) *(or Flutter / React Native if applicable)*
- Material UI

### Backend & Services
- **Firebase Authentication** – User & Admin login
- **Firebase Firestore / Realtime Database** – Data storage
- **Firebase Cloud Functions** *(optional)* – Backend logic
- **Cloudinary** – Image upload & cloud storage
- **OneSignal** – Push notifications

---

## 🗂️ Database Structure (Overview)

### Users Collection
- User ID
- Name
- Email
- Role (Admin/User)

### Lost Items Collection
- Item Name
- Description
- Category
- Image URL (Cloudinary)
- Location
- Date
- User ID
- Status (Pending / Approved)

### Found Items Collection
- Item Name
- Description
- Image URL (Cloudinary)
- Found Location
- Date
- User ID
- Status

---

## 🔐 Authentication
- Firebase Authentication (Email & Password)
- Role-based access control for Admin and Users

---

## 📸 Image Storage
- Images are uploaded to **Cloudinary**
- Image URLs stored in Firebase Database

---

## 🔔 Push Notifications
- Integrated using **OneSignal SDK**
- Notifications triggered for:
  - New item posts
  - Admin approvals
  - Item match alerts

---




