package com.tanveer.lostandcampusapp.model

data class Post(
    val id: String = "",
    val userId: String = "",
    val category: String = "",
    val title: String = "",
    val description: String = "",
    val location: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val claimedBy: String? = null,
    val userName: String?= "",
    val userRegNo: String?= "",
     val status: String = ""
)

