package com.tanveer.lostandcampusapp.model

data class ClaimRequest(
    val id: String = "",
    val postId: String = "",
    val claimerId: String = "",
    val ownerId: String = "",
    val itemTitle: String = "",
    val itemImageUrl: String? = null,
    val userName: String = "",
    val userEmail: String = "",
    val description: String = "",
    val proofImageUrl: String? = null,
    val claimTimestamp: Long = 0,
    val status: String = "PENDING"  // PENDING, APPROVED, REJECTED
)
