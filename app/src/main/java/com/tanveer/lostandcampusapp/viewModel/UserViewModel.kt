package com.tanveer.lostandcampusapp.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
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
                    val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
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
                        .add(post)
//                       .document(post.id)
//                        .set(post)
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
        viewModelScope.launch {
            val posts = PostRepo.getMyPosts(regNo.value)
            myPosts.value = posts
        }
    }


    fun deletePost(postId: String) {
        viewModelScope.launch {
            PostRepo.deletePost(postId)
            loadMyPosts()
            loadAllPosts()
        }
    }

    fun claimPost(
        postId: String,
        claimerId: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
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
