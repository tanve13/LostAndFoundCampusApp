package com.tanveer.lostandcampusapp.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.tanveer.lostandcampusapp.data.PostRepo
import com.tanveer.lostandcampusapp.model.Post
import kotlinx.coroutines.launch
import java.io.File
import java.util.UUID

class UserViewModel : ViewModel() {
    var name = mutableStateOf("")
    var regNo = mutableStateOf("")

    var allPosts = mutableStateOf<List<Post>>(emptyList())
    var myPosts = mutableStateOf<List<Post>>(emptyList())

    fun setUserData(userName: String, userReg: String) {
        name.value = userName
        regNo.value = userReg
    }

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
                    val post = Post(
                        id = UUID.randomUUID().toString(),
                        userId = regNo.value, // ya FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        category = category,
                        title = title,
                        description = desc,
                        location = location,
                        imageUrl = url,
                        timestamp = System.currentTimeMillis()
                    )

                    FirebaseFirestore.getInstance()
                        .collection("posts")
                        .document(post.id)
                        .set(post)
                        .addOnSuccessListener {
                            loadAllPosts()
                            loadMyPosts()
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

    fun loadAllPosts() {
        viewModelScope.launch {
            PostRepo.getAllPosts().collect { posts ->
                allPosts.value = posts
            }
        }
    }


    fun loadMyPosts() {
        viewModelScope.launch {
            myPosts.value = PostRepo.getMyPosts()
        }
    }

    fun deletePost(postId: String) {
        viewModelScope.launch {
            PostRepo.deletePost(postId)
            loadMyPosts()
            loadAllPosts()
        }
    }
    fun claimPost(postId: String, claimerId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                db.collection("posts")
                    .document(postId)
                    .update("claimedBy", claimerId)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e -> onError(e.message ?: "Error claiming post") }
            } catch (e: Exception) {
                onError(e.message ?: "Unknown error")
            }
        }
    }

}
