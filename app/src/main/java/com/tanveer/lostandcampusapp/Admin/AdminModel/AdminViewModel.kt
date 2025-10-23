package com.tanveer.lostandcampusapp.Admin.AdminModel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tanveer.lostandcampusapp.Admin.Repository.AdminUserRepository
import com.tanveer.lostandcampusapp.Admin.Repository.ClaimRepository
import com.tanveer.lostandcampusapp.data.FileUtils
import com.tanveer.lostandcampusapp.model.ClaimRequest
import com.tanveer.lostandcampusapp.model.Post
import com.tanveer.lostandcampusapp.model.ProfileDataClass
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class AdminViewModel @Inject constructor(private val repo: AdminUserRepository,
                                         private val repository: ClaimRepository) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var adminName by mutableStateOf("")
    var adminEmail by mutableStateOf("")
    var profileUrl by mutableStateOf<String?>(null)
    var deletedPosts by mutableStateOf(0)

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

    // for allpost screen
    var allPosts by mutableStateOf<List<Post>>(emptyList())
    var filter by mutableStateOf("ALL") // ALL, PENDING, RESOLVED

    private val _user = MutableStateFlow<ProfileDataClass?>(null)
    val user = _user.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving = _isSaving.asStateFlow()

    private val _profileUpdated = MutableStateFlow(false)
    val profileUpdated = _profileUpdated.asStateFlow()

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
    // Load all claims
    fun fetchClaimRequests() {
        _isClaimLoading.value = true
        viewModelScope.launch {
            val claims = repository.getAllClaimRequests() // fetch from server/db
            _claimRequests.value = claims
            filterClaims()
            _isClaimLoading.value = false
        }
    }

    fun refreshClaimRequests() = fetchClaimRequests()
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
    // Approve claim
    fun approveClaim(claimId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateClaimStatus(claimId, "APPROVED")
                fetchClaimRequests()
                onSuccess()
            } catch (e: Exception) {
                onFailure()
            }
        }
    }
    // Reject claim
    fun rejectClaim(claimId: String, onSuccess: () -> Unit, onFailure: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.updateClaimStatus(claimId, "REJECTED")
                fetchClaimRequests()
                onSuccess()
            } catch (e: Exception) {
                onFailure()
            }
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

    fun fetchAdminInfo() {
        val uid = auth.currentUser?.uid ?: return
        firestore.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                adminName = doc.getString("name") ?: "Admin"
                adminEmail = doc.getString("email") ?: "Not available"
                profileUrl = doc.getString("profileImageUrl")
            }
    }

    fun fetchStats() {
        firestore.collection("posts").get().addOnSuccessListener { query ->
            totalPosts = query.size()
            deletedPosts = query.documents.count { it.getBoolean("isDeleted") == true }
        }
    }

    fun loadProfile(id: String) {
        viewModelScope.launch {
            try {
                _user.value = repo.getAdminProfile(id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    fun updateProfile(context: Context, id: String, name: String, email: String, photoUri: Uri?) {
        viewModelScope.launch {
            _isSaving.value = true
            _profileUpdated.value = false

            try {
                if (photoUri != null) {
                    val file = FileUtils.from(context, photoUri)
                    // Call Cloudinary upload (callback style)
                    CloudinaryHelper.uploadImage(file) { success, url ->
                        if (success && url != null) {
                            // Image uploaded, update Firestore profile with photo url
                            viewModelScope.launch {
                                repo.updateAdminProfile(context, id, name, email, photoUri)
                                _user.value = repo.getAdminProfile(id)
                                _profileUpdated.value = true
                                _isSaving.value = false
                            }
                        } else {
                            // Upload failed, update without photo:
                            viewModelScope.launch {
                                repo.updateAdminProfile(context, id, name, email, null)
                                _user.value = repo.getAdminProfile(id)
                                _profileUpdated.value = true
                                _isSaving.value = false
                            }
                        }
                    }
                } else {
                    // No photo change, update text fields only
                    repo.updateAdminProfile(context, id, name, email, null)
                    _user.value = repo.getAdminProfile(id)
                    _profileUpdated.value = true
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isSaving.value = false
            }
        }
    }


    fun logout(onDone: () -> Unit) {
        auth.signOut()
        onDone()
    }
}
