package com.tanveer.lostandcampusapp.Admin.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tanveer.lostandcampusapp.Admin.AdminScreens.AdminHomeScreen
import com.tanveer.lostandcampusapp.Admin.AdminScreens.AllPostsScreen
import com.tanveer.lostandcampusapp.Admin.AdminScreens.ClaimsScreen
import com.tanveer.lostandcampusapp.Admin.AdminScreens.SettingScreen

@Composable
fun AdminNavigation(navController : NavHostController) {

    Scaffold(
        bottomBar = {
            NavigationBar {
                val currentRoute = navController.currentBackStackEntry?.destination?.route
                AdminNavItem.items.forEach { item ->
                    NavigationBarItem(
                        selected = currentRoute == item.route,
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
                        icon = { Icon(item.icon, contentDescription = item.route) },
                        label = { Text(item.route) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AdminNavItem.Dashboard.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(AdminNavItem.Dashboard.route) { AdminHomeScreen() }
            composable(AdminNavItem.AllPosts.route) { AllPostsScreen() }
            composable(AdminNavItem.Claims.route) { ClaimsScreen() }
            composable(AdminNavItem.Settings.route) { SettingScreen() }
        }
    }
}

