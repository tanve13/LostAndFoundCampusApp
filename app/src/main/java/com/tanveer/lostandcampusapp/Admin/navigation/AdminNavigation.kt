package com.tanveer.lostandcampusapp.Admin.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tanveer.lostandcampusapp.Admin.AdminScreens.AdminClaimsScreen
import com.tanveer.lostandcampusapp.Admin.AdminScreens.AdminHomeScreen
import com.tanveer.lostandcampusapp.Admin.AdminScreens.AllPostsScreen
import com.tanveer.lostandcampusapp.Admin.AdminScreens.SettingScreen

@Composable
fun AdminNavigation(navController : NavHostController, rootNavController: NavHostController) {

    Scaffold(
        bottomBar = {
            NavigationBar( containerColor = Color.Black,
                contentColor = MaterialTheme.colorScheme.onBackground,
                tonalElevation = 8.dp) {
                val currentRoute = navController.currentBackStackEntry?.destination?.route


                AdminNavItem.items.forEach { item ->
                    NavigationBarItem(
                        icon = { Icon(item.icon, contentDescription = item.route) },
                        selected = currentRoute == item.route,
                        alwaysShowLabel = false,
                        label = null,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            indicatorColor = Color.DarkGray,
                            unselectedIconColor = Color.White,
                        ),
                        onClick = {
                            navController.navigate(item.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        },
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
            composable(AdminNavItem.Dashboard.route) {
                val regNo = "12200672"
                AdminHomeScreen( adminRegNo = regNo) }
            composable(AdminNavItem.AllPosts.route) { AllPostsScreen() }
            composable(AdminNavItem.Claims.route) { AdminClaimsScreen() }
            composable(AdminNavItem.Settings.route){
                val regNo = "12200672"
                SettingScreen(
                    adminRegNo = regNo,
                    navController = navController,
                    rootNavController = rootNavController,
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("adminHome") { inclusive = true }
                        }
                    }
                )
            }

        }
    }
}

