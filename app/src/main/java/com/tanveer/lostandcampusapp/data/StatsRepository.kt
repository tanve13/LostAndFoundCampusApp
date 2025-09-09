package com.tanveer.lostandcampusapp.data


import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StatsRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getUserStats(userId: String): UserStats {
        // Get user's posts count
        val postsQuery = db.collection("posts")
            .whereEqualTo("userId", userId)
        val postsSnapshot = postsQuery.get().await()

        val lostPosts = postsSnapshot.count { it.getString("type") == "lost" }
        val foundPosts = postsSnapshot.count { it.getString("type") == "found" }
        val totalPosts = postsSnapshot.size()

        // Get user's claims count
        val claimsQuery = db.collection("claims")
            .whereEqualTo("userId", userId)
        val claimsSnapshot = claimsQuery.get().await()
        val claimsCount = claimsSnapshot.size()

        return UserStats(totalPosts, lostPosts, foundPosts, claimsCount)
    }
}

data class UserStats(
    val totalPosts: Int = 0,
    val lostPosts: Int = 0,
    val foundPosts: Int = 0,
    val claimsMade: Int = 0
)