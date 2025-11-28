package com.tanveer.lostandcampusapp.Admin.AdminModel

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.firestore
import com.tanveer.lostandcampusapp.Admin.Repository.AdminUserRepository
import com.tanveer.lostandcampusapp.Admin.Repository.ClaimRepository
import com.tanveer.lostandcampusapp.User.Screen.formatTimestamp
import com.tanveer.lostandcampusapp.data.AuthRepo
import com.tanveer.lostandcampusapp.data.FileUtils
import com.tanveer.lostandcampusapp.model.AdminNotificationDataClass
import com.tanveer.lostandcampusapp.model.ClaimRequest
import com.tanveer.lostandcampusapp.model.Post
import com.tanveer.lostandcampusapp.model.ProfileDataClass
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


@HiltViewModel
class AdminViewModel @Inject constructor(
    private val repo: AdminUserRepository,
    private val repository: ClaimRepository
) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    var adminName = mutableStateOf("")
    var adminEmail = mutableStateOf("")
    var adminRole = mutableStateOf("")
    var deletedPosts by mutableStateOf(0)
    var allUsers by mutableStateOf<List<ProfileDataClass>>(emptyList())
        private set

    // Notification list
    private val _notifications = mutableStateListOf<AdminNotificationDataClass>()
    val notifications: List<AdminNotificationDataClass> get() = _notifications
    private val notificationIds = mutableSetOf<String>()    // to avoid duplicates / detect updates
    var notificationTab by mutableStateOf("ALL")
    var unreadCount by mutableStateOf(0)


    //
//    private val seenPostIds = mutableSetOf<String>()
//    private val postClaimMap = mutableMapOf<String, String?>()
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
    var monthlyPosts by mutableStateOf<List<Int>>(listOf())
    var yearlyPosts by mutableStateOf<List<Int>>(emptyList())
    var yearLabels by mutableStateOf<List<String>>(emptyList())

    // for allpost screen
    var allPosts by mutableStateOf<List<Post>>(emptyList())
    var filter by mutableStateOf("ALL") // ALL, PENDING, RESOLVED

    private val _user = MutableStateFlow<ProfileDataClass?>(null)
    val user = _user.asStateFlow()
    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()
    private val _profileUpdated = mutableStateOf<String?>(null)
    val profileUpdated: State<String?> = _profileUpdated

    //claimscreen ke leia
    private val _claimRequests = MutableStateFlow<List<ClaimRequest>>(emptyList())
    val claimRequests: StateFlow<List<ClaimRequest>> = _claimRequests
    private val _isClaimLoading = MutableStateFlow(false)
    val isClaimLoading: StateFlow<Boolean> = _isClaimLoading

    var claimFilter: String = "ALL"
        set(value) {
            field = value
            filterClaims()
        }

    fun startNotificationListener() {
        // call both listeners. They will merge results into _notifications
        listenPostNotifications()
        listenClaimNotifications()
    }

    fun updateProfileImage(url: String, context: Context, onDone: () -> Unit) {
        val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val regNo = sharedPref.getString("regNo", null) ?: return
        val data = mapOf(
            "profileImage" to url
        )
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(regNo)
            .update(data)
            .addOnSuccessListener {
                Log.d("PROFILE", "Image URL updated")
                onDone()
            }
            .addOnFailureListener {
                Log.e("PROFILE", "Error updating", it)
            }
    }

    private val _profileImageUrl = MutableStateFlow<String?>(null)
    val profileImageUrl = _profileImageUrl.asStateFlow()

    fun loadUserProfile(context: Context) {
        val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val regNo = sharedPref.getString("regNo", null) ?: return

        FirebaseFirestore.getInstance()
            .collection("users")
            .document(regNo)
            .get()
            .addOnSuccessListener { doc ->
                _profileImageUrl.value = doc.getString("profileImage")
            }
    }


    fun uploadToCloudinary(
        context: Context,
        uri: Uri,
        onUploaded: (String) -> Unit
    ) {
        val file = FileUtils.from(context, uri)

        CloudinaryHelper.uploadImage(file) { success, url ->
            if (success && url != null) {
                onUploaded(url)
            }
        }
    }


    private fun listenPostNotifications() {
        val db = FirebaseFirestore.getInstance()
        db.collection("notifications")   // your posts notifications collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener

                if (snapshot == null) return@addSnapshotListener

                // process each doc (add/update/remove)
                for (docChange in snapshot.documentChanges) {
                    val doc = docChange.document
                    val rawId = doc.id
                    val id = "post_$rawId" // prefix so ids don't collide with claim docs

                    when (docChange.type) {
                        DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                            // Compose AdminNotificationDataClass
                            val ts = doc.getLong("timestamp") ?: System.currentTimeMillis()
                            val notif = AdminNotificationDataClass(
                                id = id,
                                type = doc.getString("type") ?: "NEW_POST",
                                title = doc.getString("title") ?: "New Post",
                                subtitle = doc.getString("subtitle") ?: (doc.getString("message")
                                    ?: ""),
                                timestamp = ts,
                                read = doc.getBoolean("read") ?: false,
                                refId = doc.getString("postId") ?: doc.getString("refId") ?: "",
                                message = doc.getString("message") ?: "",
                                time = formatTimestamp(ts)
                            )
                            // add or update
                            val existingIndex = _notifications.indexOfFirst { it.id == id }
                            if (existingIndex >= 0) {
                                _notifications[existingIndex] = notif
                            } else if (notificationIds.add(id)) {
                                _notifications.add(0, notif)
                            }
                        }

                        DocumentChange.Type.REMOVED -> {
                            _notifications.removeAll { it.id == id }
                            notificationIds.remove(id)
                        }
                    }
                }

                // sort by timestamp desc
                _notifications.sortByDescending { it.timestamp }
                unreadCount = _notifications.count { !it.read }
            }
    }

    private fun listenClaimNotifications() {
        val db = FirebaseFirestore.getInstance()
        db.collection("admin_notifications")   // your claim notifications collection
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) return@addSnapshotListener
                if (snapshot == null) return@addSnapshotListener

                for (docChange in snapshot.documentChanges) {
                    val doc = docChange.document
                    val rawId = doc.id
                    val id = "claim_$rawId"

                    when (docChange.type) {
                        DocumentChange.Type.ADDED, DocumentChange.Type.MODIFIED -> {
                            val ts = doc.getLong("timestamp") ?: System.currentTimeMillis()
                            val notif = AdminNotificationDataClass(
                                id = id,
                                type = doc.getString("type") ?: "CLAIM_REQUEST",
                                title = doc.getString("title") ?: "New Claim Request",
                                subtitle = doc.getString("subtitle") ?: (doc.getString("claimedBy")
                                    ?: ""),
                                timestamp = ts,
                                read = doc.getBoolean("read") ?: false,
                                refId = doc.getString("postId") ?: doc.getString("refId") ?: "",
                                message = doc.getString("message") ?: "",
                                time = formatTimestamp(ts)
                            )
                            val existingIndex = _notifications.indexOfFirst { it.id == id }
                            if (existingIndex >= 0) {
                                _notifications[existingIndex] = notif
                            } else if (notificationIds.add(id)) {
                                _notifications.add(0, notif)
                            }
                        }

                        DocumentChange.Type.REMOVED -> {
                            _notifications.removeAll { it.id == id }
                            notificationIds.remove(id)
                        }
                    }
                }

                _notifications.sortByDescending { it.timestamp }
                unreadCount = _notifications.count { !it.read }
            }
    }

    fun markAllNotificationsRead() {
        _notifications.forEach { it.read = true }
        unreadCount = 0
    }

    fun markNotificationRead(localId: String) {
        // update local list
        val idx = _notifications.indexOfFirst { it.id == localId }
        if (idx >= 0) {
            val updated = _notifications[idx].copy(read = true)
            _notifications[idx] = updated
        }
        unreadCount = _notifications.count { !it.read }

        // update Firestore — determine which collection by prefix
        val (prefix, rawId) = if (localId.startsWith("post_")) "post" to localId.removePrefix("post_")
        else if (localId.startsWith("claim_")) "claim" to localId.removePrefix("claim_")
        else null to localId

        when (prefix) {
            "post" -> FirebaseFirestore.getInstance().collection("notifications")
                .document(rawId)
                .update("read", true)

            "claim" -> FirebaseFirestore.getInstance().collection("admin_notifications")
                .document(rawId)
                .update("read", true)

            else -> {
                // fallback: try admin_notifications or ignore
                FirebaseFirestore.getInstance().collection("admin_notifications")
                    .document(rawId)
                    .update("read", true)
            }
        }
    }

    fun approveClaim(
        claimId: String,
        postId: String,
        onSuccess: () -> Unit,
        onFailure: () -> Unit
    ) {
        firestore.collection("claims").document(claimId)
            .update("status", "APPROVED")
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    fun rejectClaim(claimId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        firestore.collection("claims").document(claimId)
            .update("status", "REJECTED")
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure() }
    }

    //esko dekhna hai ik bar dobara
//    fun fetchClaimRequests() {
//        _isClaimLoading.value = true
//
////        val query = firestore.collection("claims")
//        var query = firestore.collection("admin_notifications")
//
//        when (claimFilter) {
//            "PENDING" -> query.whereEqualTo("status", "PENDING")
//            "APPROVED" -> query.whereEqualTo("status", "APPROVED")
//            "REJECTED" -> query.whereEqualTo("status", "REJECTED")
//            else -> query
//        }
//            .orderBy("claimTimestamp", Query.Direction.DESCENDING)
//            .get()
//            .addOnSuccessListener { snapshot ->
//                val list = snapshot.documents.mapNotNull { it.toObject(ClaimRequest::class.java) }
//                _claimRequests.value = list
//                _isClaimLoading.value = false
//            }
//            .addOnFailureListener {
//                _isClaimLoading.value = false
//            }
//    }
    fun fetchClaimRequests() {
        _isClaimLoading.value = true

        var query = firestore.collection("admin_notifications")

        when (claimFilter) {
            "PENDING" -> query.whereEqualTo("status", "PENDING")
            "APPROVED" -> query.whereEqualTo("status", "APPROVED")
            "REJECTED" -> query.whereEqualTo("status", "REJECTED")
            else -> query
        }

        query.get()
            .addOnSuccessListener { result ->
                val list = result.documents.mapNotNull { it.toObject(ClaimRequest::class.java) }
                _claimRequests.value = list
                _isClaimLoading.value = false
            }
            .addOnFailureListener {
                _isClaimLoading.value = false
            }
    }

    fun refreshClaimRequests() {
        fetchClaimRequests()
    }


    // FUNCTION TO LOAD USERS
    fun fetchAllUsers() {
        isLoading = true
        firestore.collection("users")
            .get()
            .addOnSuccessListener { query ->
                allUsers = query.documents.mapNotNull { it.toObject(ProfileDataClass::class.java) }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

    fun getPostsByUserRegNo(regNo: String): List<Post> {
        return allPosts.filter { it.userRegNo == regNo }
    }

    fun fetchAdminProfile(regNo: String) {
        FirebaseFirestore.getInstance()
            .collection("users")
            .document(regNo)
            .get()
            .addOnSuccessListener { doc ->
                adminName.value = doc.getString("name") ?: ""
                adminEmail.value = doc.getString("email") ?: ""
                adminRole.value = doc.getString("role") ?: "user"
            }
    }

    // Claims filtering logic
    private fun filterClaims() {
        val allClaims = _claimRequests.value
        _claimRequests.value = when (claimFilter) {
            "PENDING" -> allClaims.filter { it.status == "PENDING" }
            "APPROVED" -> allClaims.filter { it.status == "APPROVED" }
            "REJECTED" -> allClaims.filter { it.status == "REJECTED" }
            else -> allClaims
        }
    }

    //loading ke leia
    var isLoading by mutableStateOf(false)
    fun refreshAllPosts() {
        isLoading = true
        firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { query ->
                allPosts = query.documents.mapNotNull { it.toObject(Post::class.java) }
                isLoading = false
            }
            .addOnFailureListener {
                isLoading = false
            }
    }

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
            // Monthly post distribution (current year)
            val currentYear = cal.get(java.util.Calendar.YEAR)

            val monthlyGrouped = posts
                .filter {
                    val postCal = java.util.Calendar.getInstance()
                    postCal.timeInMillis = it.timestamp
                    postCal.get(java.util.Calendar.YEAR) == currentYear
                }
                .groupBy {
                    val postCal = java.util.Calendar.getInstance()
                    postCal.timeInMillis = it.timestamp
                    postCal.get(java.util.Calendar.MONTH)
                }

            monthlyPosts = (0..11).map { month -> monthlyGrouped[month]?.size ?: 0 }
            // Yearly post distribution (last few years)
            val yearlyGrouped = posts.groupBy {
                val cal = java.util.Calendar.getInstance()
                cal.timeInMillis = it.timestamp
                cal.get(java.util.Calendar.YEAR)
            }

            val sortedYears = yearlyGrouped.keys.sorted()
            yearLabels = sortedYears.map { it.toString() }
            yearlyPosts = sortedYears.map { year -> yearlyGrouped[year]?.size ?: 0 }

        }
    }

    fun fetchAllPosts() {
        firestore.collection("posts")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { query ->
                allPosts = query.documents.mapNotNull { it.toObject(Post::class.java) }
            }
    }

    fun deletePost(postId: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        firestore.collection("posts").document(postId).delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Deletion failed") }
    }

    val filteredPosts: List<Post>
        get() = when (filter) {
            "PENDING" -> allPosts.filter { it.claimedBy.isNullOrEmpty() }
            "RESOLVED" -> allPosts.filter { !it.claimedBy.isNullOrEmpty() }
            else -> allPosts
        }

    fun fetchStats() {
        firestore.collection("posts").get().addOnSuccessListener { query ->
            totalPosts = query.size()
            deletedPosts = query.documents.count { it.getBoolean("isDeleted") == true }
        }
    }

}
