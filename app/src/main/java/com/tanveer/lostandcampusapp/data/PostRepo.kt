package com.tanveer.lostandcampusapp.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tanveer.lostandcampusapp.model.Post
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

object PostRepo {
    private val db = FirebaseFirestore.getInstance()
    private val postsCollection = db.collection("posts")

    suspend fun createPost(
        category: String,
        title: String,
        description: String,
        location: String,
        imageUrl: String? = null
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
            imageUrl = imageUrl,
            timestamp = System.currentTimeMillis()

        )
        postsCollection.document(postId).set(post).await()
    }

    fun getAllPosts(): Flow<List<Post>> = callbackFlow {
        val listener = postsCollection.addSnapshotListener { snapshot, e ->
            if (e != null) {
                trySend(emptyList())
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val postsList = snapshot.toObjects(Post::class.java)
                trySend(postsList)
            } else {
                trySend(emptyList())
            }
        }
        awaitClose { listener.remove() }
    }

    suspend fun getMyPosts(userRegNo: String): List<Post> {
        val db = FirebaseFirestore.getInstance()
        val snapshot = db.collection("posts")
            .whereEqualTo("userRegNo", userRegNo)
            .get()
            .await()

        return snapshot.toObjects(Post::class.java)
    }


    suspend fun deletePost(postId: String) {
        try {
            postsCollection.document(postId).delete().await()
        } catch (e: Exception) {
            // handle error
        }
    }
}

