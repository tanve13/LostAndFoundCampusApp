package com.tanveer.lostandcampusapp.Admin.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class AdminNavItem(
    val route: String,
    val icon: ImageVector
) {
    object Dashboard : AdminNavItem("adminHome",  Icons.Default.Leaderboard)
    object AllPosts : AdminNavItem("allPosts",  Icons.Default.List)
    object Claims : AdminNavItem("claims",  Icons.Default.Mail)
    object Settings : AdminNavItem("settings",  Icons.Default.Settings)

    companion object {
        val items = listOf(Dashboard, AllPosts, Claims, Settings)
    }
}
