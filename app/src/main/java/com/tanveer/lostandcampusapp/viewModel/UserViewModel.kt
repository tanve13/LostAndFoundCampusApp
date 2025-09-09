package com.tanveer.lostandcampusapp.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tanveer.lostandcampusapp.data.PostRepo
import com.tanveer.lostandcampusapp.data.StatsRepository
import com.tanveer.lostandcampusapp.data.UserStats
import com.tanveer.lostandcampusapp.model.NotificationDataClass
import com.tanveer.lostandcampusapp.model.Post
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class UserViewModel : ViewModel() {
    var name = mutableStateOf("")
    var regNo = mutableStateOf("")
    var allPosts = mutableStateOf<List<Post>>(emptyList())
    var myPosts = mutableStateOf<List<Post>>(emptyList())
    private val _userStats = MutableStateFlow(UserStats())
    val userStats: StateFlow<UserStats> = _userStats
    private val statsRepository = StatsRepository()
    private val firestore = FirebaseFirestore.getInstance()
    val _notifications = MutableStateFlow<List<NotificationDataClass>>(emptyList())
    val notifications: StateFlow<List<NotificationDataClass>> = _notifications
    fun setUserData(userName: String, userReg: String) {
        name.value = userName
        regNo.value = userReg
    }

    //je user ke stats ke leia hai function
    fun fetchUserStats(userId: String) {
        viewModelScope.launch {
            try {
                _userStats.value = statsRepository.getUserStats(userId)
            } catch (e: Exception) {
                // Handle error (optional)
            }
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
//                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
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
///je item claim krne ke liea....
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
        val firestore = FirebaseFirestore.getInstance()

        val notification = NotificationDataClass(
            title = title,
            message = message,
            type = postType,
            timestamp = System.currentTimeMillis(),
            userId = null,
            isRead = false
        )

        firestore.collection("notifications")
            .add(notification)
            .addOnSuccessListener {
                println("Notification added successfully")
            }
            .addOnFailureListener {
                println("Error adding notification: ${it.message}")
            }
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
}
