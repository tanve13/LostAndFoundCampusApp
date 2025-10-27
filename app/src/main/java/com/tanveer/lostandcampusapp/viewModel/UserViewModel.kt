package com.tanveer.lostandcampusapp.viewModel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tanveer.lostandcampusapp.data.DataStoreManager
import com.tanveer.lostandcampusapp.data.FileUtils
import com.tanveer.lostandcampusapp.data.PostRepo
import com.tanveer.lostandcampusapp.data.StatsRepository
import com.tanveer.lostandcampusapp.data.UserStats
import com.tanveer.lostandcampusapp.model.NotificationDataClass
import com.tanveer.lostandcampusapp.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.io.File
import java.io.IOException
import java.util.UUID

class UserViewModel(application: Application): AndroidViewModel(application) {
    private val sharedPref = application.getSharedPreferences("UserSession", Context.MODE_PRIVATE)

    // ✅ Get saved user data
    private val savedRegNo = sharedPref.getString("regNo", "") ?: ""
    private val savedName = sharedPref.getString("name", "") ?: ""
    var name = mutableStateOf(savedName)
    var regNo = mutableStateOf(savedRegNo)
    var bio = mutableStateOf("")
    var profileImageUrl = mutableStateOf("")

    //posts ke leia
    var allPosts = mutableStateOf<List<Post>>(emptyList())
    var myPosts = mutableStateOf<List<Post>>(emptyList())

    //userstats
    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats
    private val statsRepository = StatsRepository()

    //firebase
    private val firestore = FirebaseFirestore.getInstance()
    private val db = FirebaseFirestore.getInstance()

    //notifications
    val _notifications = MutableStateFlow<List<NotificationDataClass>>(emptyList())
    val notifications: StateFlow<List<NotificationDataClass>> = _notifications

    fun setUserData(userName: String, userReg: String,userBio: String = "") {
        name.value = userName
        regNo.value = userReg
        bio.value = userBio
        updateUserProfileField("name", userName)
        updateUserProfileField("regNo", userReg)
    }
    fun saveUserToDataStore(context: Context) {
        viewModelScope.launch {
            DataStoreManager.saveUserData(context, name.value, regNo.value)
        }
    }
    fun loadUserFromDataStore(context: Context) {
        viewModelScope.launch {
            val (storedName, storedReg) = DataStoreManager.getUserData(context)
            if (storedName.isNotEmpty() && storedReg.isNotEmpty()) {
                name.value = storedName
                regNo.value = storedReg
                fetchUserProfile(storedReg)
            }
        }
    }
    fun fetchUserProfile(userRegNo: String) {
        viewModelScope.launch {
            try {
                val doc = db.collection("users").document(userRegNo).get().await()
                if (doc.exists()) {
                    name.value = doc.getString("name") ?: ""
                    regNo.value = doc.getString("regNo") ?: ""
                    bio.value = doc.getString("bio") ?: ""
                    profileImageUrl.value = doc.getString("profileImageUrl") ?: ""
                }
                fetchUserStats(userRegNo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // --- Fetch stats ---
    fun fetchUserStats(userRegNo: String) {
        viewModelScope.launch {
            try {
                val regNo = regNo.value // <-- userViewModel.regNo.value hona chahiye, not uid
                Log.d("PROFILE_SCREEN", "FETCHING STATS FOR: $regNo")
                _userStats.value = statsRepository.getUserStats(regNo)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    // 🧠 Update Firestore user profile data
    private fun updateUserProfileField(field: String, value: Any) {
        val regNo = regNo.value
        firestore.collection("users").document(regNo)
            .update(field, value)
            .addOnFailureListener { Log.e("UserViewModel", "Failed to update $field: ${it.message}") }
    }
    // ✨ Profile Image Upload (from gallery)
    fun uploadProfileImageToCloudinary(
        context: Context,
        imageUri: Uri,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        val file = FileUtils.getFileFromUri(context, imageUri)
        if (file == null) {
            onError("Failed to read image")
            return
        }

        // Use the same CloudinaryHelper
        CloudinaryHelper.uploadImage(file) { success, url ->
            if (success && url != null) {
                val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@uploadImage
                FirebaseFirestore.getInstance()
                    .collection("users")
                    .document(uid)
                    .update("profileImageUrl", url)
                    .addOnSuccessListener {
                        profileImageUrl.value = url
                        onSuccess()
                    }
                    .addOnFailureListener { e -> onError("Firestore update failed: ${e.message}") }
            } else {
                onError("Cloudinary upload failed")
            }
        }
    }

    /** Update user name & bio in Firestore */
    fun updateUserProfile(context: Context, newName: String, newBio: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(uid)
            .update(
                mapOf(
                    "name" to newName,
                    "bio" to newBio
                )
            )
            .addOnSuccessListener {
                name.value = newName
                bio.value = newBio
                Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Update failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }


    ///je jab user post submit krega jab
    fun submitPost(
        category: String,
        title: String,
        desc: String,
        location: String,
        imageFile: File?,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        if (imageFile == null) {
            onError("Image is required")
            return
        }
        CloudinaryHelper.uploadImage(imageFile) { success, url ->
            if (success && url != null) {
                viewModelScope.launch {
                    val currentUser = FirebaseAuth.getInstance().currentUser
                    Log.d("SubmitPost", "CurrentUser = $currentUser")

                    val uid = currentUser?.uid ?: return@launch
                    val post = Post(
                        id = UUID.randomUUID().toString(),
                        userId = uid,
                        category = category,
                        title = title,
                        description = desc,
                        location = location,
                        imageUrl = url,
                        timestamp = System.currentTimeMillis(),
                        userName = name.value,
                        userRegNo = regNo.value
                    )

                    FirebaseFirestore.getInstance()
                        .collection("posts")
                        .document(post.id)
                        .set(post)
                        .addOnSuccessListener {
                            loadAllPosts()
                            loadMyPosts()
                            sendPostNotification(
                                postType = category,
                                title = "New $category Post",
                                message = "${name.value} added a new $category post: $title"
                            )
                            onSuccess()
                        }
                        .addOnFailureListener {
                            onError("Error adding post: ${it.message}")
                        }
                }
            } else {
                onError("Image upload failed")
            }
        }
    }

    //it load post for my homescreen
    fun loadAllPosts() {
        viewModelScope.launch {
            PostRepo.getAllPosts().collect { posts ->
                allPosts.value = posts
            }
        }
    }

    //it load post only for user woh posted it like for mypostscreen
    fun loadMyPosts() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        viewModelScope.launch {
            val posts = PostRepo.getMyPosts(uid)
            myPosts.value = posts
        }
    }
    //je post delete krne ke leia
    fun deletePost(postId: String) {
        viewModelScope.launch {
            PostRepo.deletePost(postId)
            loadMyPosts()
            loadAllPosts()
        }
    }
   //je item claim krne ke liea....
    fun claimPost(
        postId: String,
        claimerId: String,
        postOwnerId: String,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                db.collection("posts")
                    .document(postId)
                    .update("claimedBy", claimerId)
                    .addOnSuccessListener {
                        val chatId = listOf(claimerId, postOwnerId).sorted().joinToString("_")
                        onSuccess(chatId)
                    }
                    .addOnFailureListener { e -> onError(e.message ?: "Error claiming post") }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }
   ////to send notification ....
    fun sendPostNotification(postType: String, title: String, message: String) {
       val emoji = when(postType.lowercase()) {
           "lost" -> "\uD83D\uDEA8"  // ❓ (Question Mark)
           "found" -> "\uD83D\uDD0E" // 🔎 Magnifying Glass emoji (Found)
           else -> ""
       }
       val fullTitle = "$emoji $title"
       val fullMessage = "$emoji $message"
        val firestore = FirebaseFirestore.getInstance()
       val notification = NotificationDataClass(
            title = fullTitle,
            message = fullMessage,
            type = postType,
            timestamp = System.currentTimeMillis(),
            userId = null,
            isRead = false
        )
       Log.d("NotificationDebug", "Trying to add notification: $notification")

        firestore.collection("notifications")
            .add(notification)
            .addOnSuccessListener {
                println("Notification added successfully")
                Log.d("NotificationDebug", "Notification added successfully with id: ${it.id}")

            }
            .addOnFailureListener {
                println("Error adding notification: ${it.message}")
                Log.e("NotificationDebug", "Error adding notification: ${it.message}")

            }
       sendOneSignalNotification(fullTitle, fullMessage)

   }
    private fun sendOneSignalNotification(title: String, message: String) {
        val url = "https://onesignal.com/api/v1/notifications"
        val appId = "812c59fb-aed1-4bf7-b899-b87dbc43880e"
        val apiKey = "os_v2_app_qewft65o2ff7poezxb63yq4ib3pmek4kto3edmuywjkntg4zq4dmtf4bwcvdu7ucu42mhov3cyhyuxixkoxfzu322w56x7olphzc6gi"
        val json = """
        {
            "app_id": "$appId",
            "included_segments": ["All"],    
            "headings": {"en": "$title"},
            "contents": {"en": "$message"}
        }
    """.trimIndent()
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaType())
        val request = Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", "Basic $apiKey")
            .addHeader("Content-Type", "application/json")
            .build()
        OkHttpClient().newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("OneSignal", "Failed: ${e.message}")
            }
            override fun onResponse(call: Call, response: Response) {
                Log.d("OneSignal", "Resp: ${response.body?.string()}")
            }
        })
    }



    fun observeUserNotifications(userId: String) {
        firestore.collection("notifications")
            .addSnapshotListener { snapshot, _ ->
                val allNotifications = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(NotificationDataClass::class.java)?.copy(id = doc.id)
                } ?: emptyList()
                val userNotifications = allNotifications.filter { it.userId == userId || it.userId == null }
                _notifications.value = userNotifications.sortedByDescending { it.timestamp }
            }
    }
   ////notification delete krne ke leia ...
    fun deleteNotification(notificationId: String) {
        firestore.collection("notifications")
            .document(notificationId)
            .delete()
    }
    //notification read krne vla
    fun markNotificationAsRead(notificationId: String) {
        firestore.collection("notifications")
            .document(notificationId)
            .update("isRead", true)
    }

}
