package com.tanveer.lostandcampusapp.Admin.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AdminNavItem(
    val route: String,
    val icon: ImageVector,
    val title: String
) {
    object Dashboard : AdminNavItem("adminHome",  Icons.Default.Leaderboard,"Dashboard")
    object AllPosts : AdminNavItem("allPosts",  Icons.Default.List,"All Posts")
    object Claims : AdminNavItem("claims",  Icons.Default.Mail, "Claims")
    object Settings : AdminNavItem("settings",  Icons.Default.Settings,"Settings")

    companion object {
        val items = listOf(Dashboard, AllPosts, Claims, Settings)
    }
}
