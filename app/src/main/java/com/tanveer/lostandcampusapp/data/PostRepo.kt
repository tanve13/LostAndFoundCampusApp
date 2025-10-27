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

    suspend fun getMyPosts(userId: String): List<Post> {
        val db = FirebaseFirestore.getInstance()
        val snapshot = db.collection("posts")
            .whereEqualTo("userId", userId)
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

