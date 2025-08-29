package com.tanveer.lostandcampusapp.User.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BottomNavItems(
    val name: String,
    val icon: ImageVector,
    val route: String
) {
    object Home : BottomNavItems("userHome", Icons.Default.Home, "Home")
    object MyPost : BottomNavItems("myPost", Icons.AutoMirrored.Filled.List, "My Post")
    object Post : BottomNavItems("Post", Icons.Default.Add, "Post")
    object Notification :
        BottomNavItems("notification", Icons.Default.Notifications, "Notification")
    object Profile : BottomNavItems("Profile", Icons.Default.Person, "Profile")
}
