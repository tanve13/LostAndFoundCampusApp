package com.tanveer.lostandcampusapp.viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.tanveer.lostandcampusapp.data.PostRepo
import com.tanveer.lostandcampusapp.model.Post
import kotlinx.coroutines.launch
import java.util.UUID

class UserViewModel: ViewModel() {
    var name = mutableStateOf("")
    var regNo = mutableStateOf("")

    // ✅ posts ke liye state
    var allPosts = mutableStateOf<List<Post>>(emptyList())
    var myPosts = mutableStateOf<List<Post>>(emptyList())

    fun setUserData(userName: String, userReg: String) {
        name.value = userName
        regNo.value = userReg
    }
    // ✅ submitPost function
    fun submitPost(category: String, title: String, desc: String, location: String, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            val post = Post(
                id = UUID.randomUUID().toString(),
                userId = regNo.value, // ya jo user ki ID hai
                category = category,
                title = title,
                description = desc,
                location = location,
                timestamp = System.currentTimeMillis().toString()
            )

            FirebaseFirestore.getInstance()
                .collection("posts")
                .document(post.id)
                .set(post)
                .addOnSuccessListener {
                    Log.d("Post", "Post added successfully")
                    loadAllPosts()
                    loadMyPosts()
                    onSuccess()
                }
                .addOnFailureListener {
                    Log.e("Post", "Error adding post", it)
                }
        }
    }

    // ✅ saare posts load karna
    fun loadAllPosts() {
        viewModelScope.launch {
            allPosts.value = PostRepo.getAllPosts()
        }
    }

    // ✅ sirf current user ke posts
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

}