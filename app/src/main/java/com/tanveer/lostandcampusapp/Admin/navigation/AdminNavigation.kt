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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.tanveer.lostandcampusapp.Admin.AdminModel.AdminViewModel
import com.tanveer.lostandcampusapp.Admin.AdminScreens.AdminClaimsScreen
import com.tanveer.lostandcampusapp.Admin.AdminScreens.AdminHomeScreen
import com.tanveer.lostandcampusapp.Admin.AdminScreens.AdminNotificationScreen
import com.tanveer.lostandcampusapp.Admin.AdminScreens.AllPostsScreen
import com.tanveer.lostandcampusapp.Admin.AdminScreens.ContributorOverviewScreen
import com.tanveer.lostandcampusapp.Admin.AdminScreens.ContributorPostsScreen
import com.tanveer.lostandcampusapp.Admin.AdminScreens.RegisteredUsersScreen
import com.tanveer.lostandcampusapp.Admin.AdminScreens.SettingScreen

@Composable
fun AdminNavigation(
    navController: NavHostController,
    rootNavController: NavHostController,
    adminViewModel: AdminViewModel = hiltViewModel()
) {

    Scaffold(
        bottomBar = {
            NavigationBar(
                containerColor = Color.Black,
                contentColor = MaterialTheme.colorScheme.onBackground,
                tonalElevation = 8.dp
            ) {
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
                AdminHomeScreen( navController = navController,adminViewModel,
                    adminRegNo = "12200672", // pass actual regNo,
                    onUsersClick = { navController.navigate("registered_users") },
                    onTopContributorClick = { navController.navigate("contributors") }
                )
            }
            composable("admin_notifications") {
                AdminNotificationScreen(
                    adminViewModel = adminViewModel,
                    onBack = { navController.popBackStack() }
                )
            }

            composable("registered_users") {
                RegisteredUsersScreen(
                    adminViewModel = adminViewModel,
                    onUserClick = { regNo -> navController.navigate("contributor_posts/$regNo") }
                )
            }
            composable(
                "contributor_posts/{regNo}",
                arguments = listOf(navArgument("regNo") { type = NavType.StringType })
            ) { backStackEntry ->
                val regNo = backStackEntry.arguments?.getString("regNo") ?: ""
                ContributorPostsScreen(adminViewModel = adminViewModel, regNo = regNo)
            }
            composable("contributors") {
                ContributorOverviewScreen(adminViewModel = adminViewModel,
                    onUserClick = { regNo -> navController.navigate("contributor_posts/$regNo") }
                )
            }
            composable(AdminNavItem.AllPosts.route) { AllPostsScreen() }
            composable(AdminNavItem.Claims.route) { AdminClaimsScreen() }
            composable(AdminNavItem.Settings.route) {
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

