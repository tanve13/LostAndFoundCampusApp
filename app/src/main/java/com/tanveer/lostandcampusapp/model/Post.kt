package com.tanveer.lostandcampusapp.model

import android.net.Uri

data class Post(
    val category: String,
    val title: String,
    val description: String,
    val location: String,
    val date: String,
    val imageUri: Uri?
)