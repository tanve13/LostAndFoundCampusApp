package com.tanveer.lostandcampusapp.Admin.AdminModel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.tanveer.lostandcampusapp.model.Post
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class AdminViewModel @Inject constructor() : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    // State for dashboard
    var totalPosts by mutableStateOf(0)
    var lostPosts by mutableStateOf(0)
    var foundPosts by mutableStateOf(0)
    var pendingClaims by mutableStateOf(0)
    var resolvedCases by mutableStateOf(0)
    var totalUsers by mutableStateOf(0)
    var lostCount by mutableStateOf(0)
    var foundCount by mutableStateOf(0)
    var newUsersThisWeek by mutableStateOf(0)
    var mostActiveUser by mutableStateOf<Pair<String, Int>?>(null)
    var mostCommonCategory by mutableStateOf<Pair<String, Int>?>(null)
    var weeklyPosts by mutableStateOf(listOf(0, 0, 0, 0, 0, 0, 0))
    var weekLabels by mutableStateOf(listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))

    // Load all stats from Firestore
    fun fetchAdminStats() {
        // 1. Total users & new users this week
        firestore.collection("users").get().addOnSuccessListener { query ->
            val users = query.documents
            totalUsers = users.size
            val weekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L
            newUsersThisWeek = users.count { (it.get("createdAt") as? Long ?: 0L) >= weekAgo }
        }
        firestore.collection("posts").get().addOnSuccessListener { query ->
            val posts = query.documents.mapNotNull { it.toObject(Post::class.java) }
            totalPosts = posts.size
            lostPosts = posts.count { it.category.equals("Lost", ignoreCase = true) }
            foundPosts = posts.count { it.category.equals("Found", ignoreCase = true) }
            pendingClaims = posts.count { it.claimedBy.isNullOrEmpty() }
            resolvedCases = posts.count { !it.claimedBy.isNullOrEmpty() }
            lostCount = posts.count { it.category.equals("Lost", ignoreCase = true) }
            foundCount = posts.count { it.category.equals("Found", ignoreCase = true) }
            mostCommonCategory = posts.groupingBy { it.category }
                .eachCount()
                .maxByOrNull { it.value }
                ?.let { it.key to it.value }
            // Most active user (by posts)
            mostActiveUser = posts.groupingBy { it.userName }
                .eachCount()
                .maxByOrNull { it.value }
                ?.let { it.key to it.value } as Pair<String, Int>?

            // Weekly post distribution (last 7 days)
            val cal = java.util.Calendar.getInstance()
            val today = cal.get(java.util.Calendar.DAY_OF_WEEK)
            val dayMillis = 24 * 60 * 60 * 1000L
            val weekAgo = System.currentTimeMillis() - 6 * dayMillis
            val grouped = posts.filter { it.timestamp >= weekAgo }
                .groupBy {
                    // Shifts days to Mon-Sun regardless of today (for chart)
                    ((it.timestamp - weekAgo) / dayMillis).toInt()
                }
            weeklyPosts = (0..6).map { day -> grouped[day]?.size ?: 0 }
        }
    }
}
