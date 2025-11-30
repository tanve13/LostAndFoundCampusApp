package com.tanveer.lostandcampusapp.data


import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StatsRepository @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getUserStats(userRegNo: String, userUid: String): UserStats {

        // 🔥 1. Fetch posts by REGISTRATION NUMBER
        val postsSnapshot = db.collection("posts")
            .whereEqualTo("userRegNo", userRegNo)
            .get()
            .await()

        val lostPosts = postsSnapshot.count {
            it.getString("category")?.lowercase() == "lost"
        }

        val foundPosts = postsSnapshot.count {
            it.getString("category")?.lowercase() == "found"
        }

        val totalPosts = postsSnapshot.size()

        // 🔥 2. Fetch claims made by this user using UID
        val claimsSnapshot = db.collection("claimRequests")
            .whereEqualTo("claimerId", userUid)
            .get()
            .await()

        val claimsCount = claimsSnapshot.size()

        val stats = UserStats(totalPosts, lostPosts, foundPosts, claimsCount)

        Log.d("STATS_DBG", "Stats = $stats")

        return stats
    }
}


data class UserStats(
    val totalPosts: Int = 0,
    val lostPosts: Int = 0,
    val foundPosts: Int = 0,
    val claimsMade: Int = 0
)