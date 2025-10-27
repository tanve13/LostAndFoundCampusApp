package com.tanveer.lostandcampusapp.data


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StatsRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getUserStats(userRegNo: String): UserStats {
        val postsQuery = db.collection("posts")
            .whereEqualTo("userRegNo", userRegNo)
        val postsSnapshot = db.collection("posts")
            .whereEqualTo("userRegNo", userRegNo)
            .get().await()
        postsSnapshot.forEach { doc ->
            Log.d("STATS_DBG", "Post data: ${doc.data}")
        }
        val lostPosts = postsSnapshot.count { it.getString("category")?.lowercase() == "lost" }
        val foundPosts = postsSnapshot.count { it.getString("category")?.lowercase() == "found" }
        val totalPosts = postsSnapshot.size()
        val claimsQuery = db.collection("claims")
            .whereEqualTo("userId", userRegNo)
        val claimsSnapshot = claimsQuery.get().await()
        val claimsCount = claimsSnapshot.size()

//        return UserStats(totalPosts, lostPosts, foundPosts, claimsCount)
        val userStats = UserStats(totalPosts, lostPosts, foundPosts, claimsCount)
        Log.d("STATS_DBG", "Calculated UserStats: $userStats")
        return userStats

    }
}

data class UserStats(
    val totalPosts: Int = 0,
    val lostPosts: Int = 0,
    val foundPosts: Int = 0,
    val claimsMade: Int = 0
)