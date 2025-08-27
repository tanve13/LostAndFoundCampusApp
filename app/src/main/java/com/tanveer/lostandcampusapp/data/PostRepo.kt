package com.tanveer.lostandcampusapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tanveer.lostandcampusapp.model.Post
import kotlinx.coroutines.tasks.await


object PostRepo {
    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("posts")

    suspend fun createPost(category: String, title: String, description: String, location: String,imageUrl: String? = null   // 👈 yeh add karo
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val postId = postsCollection.document().id
        val post = Post(
            id = postId,
            userId = userId,
            category = category,
            title = title,
            description = description,
            location = location,
            imageUrl = imageUrl ,
            timestamp = System.currentTimeMillis().toString(),

        )
        postsCollection.document(postId).set(post).await()
    }

    suspend fun getAllPosts(): List<Post> {
        return try {
            postsCollection.get().await().toObjects(Post::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMyPosts(): List<Post> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return emptyList()
        return try {
            postsCollection.whereEqualTo("userId", userId).get().await().toObjects(Post::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

   suspend fun deletePost(postId: String) {
        try {
            postsCollection.document(postId).delete().await()
        } catch (e: Exception) {
            // handle error
        }
    }
}

