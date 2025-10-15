package com.tanveer.lostandcampusapp.User.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.tanveer.lostandcampusapp.User.Screen.ChatScreen
import com.tanveer.lostandcampusapp.User.Screen.ClaimScreen
import com.tanveer.lostandcampusapp.User.Screen.HomeScreen
import com.tanveer.lostandcampusapp.User.Screen.NotificationScreen
import com.tanveer.lostandcampusapp.User.Screen.PostScreen
import com.tanveer.lostandcampusapp.User.Screen.UserMyPostScreen
import com.tanveer.lostandcampusapp.User.Screen.UserProfileScreen
import com.tanveer.lostandcampusapp.viewModel.UserViewModel


@Composable
fun BottomNavigation(
    navController: NavHostController,
    rootNavController: NavHostController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val userViewModel: UserViewModel = viewModel()

    NavHost(navController = navController, startDestination = BottomNavItems.Home.route) {
        composable(BottomNavItems.Home.route) {
            HomeScreen(viewModel = userViewModel, navController)
        }
        composable(BottomNavItems.MyPost.route) {
            UserMyPostScreen(viewModel = userViewModel)
        }
        composable(BottomNavItems.Post.route) {
            PostScreen(navController, viewModel = userViewModel)
        }

        composable(BottomNavItems.Notification.route) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            NotificationScreen(
                userId = userId,
                navController = navController,
                viewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            )
        }
        // ===== Profile =====
        composable(BottomNavItems.Profile.route) {
            UserProfileScreen(
                rootNavController = rootNavController,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange,
                userViewModel = userViewModel
            )
        }

        composable(
            route = "claim/{postId}",
            arguments = listOf(navArgument("postId") { type = NavType.StringType })
        ) { backStackEntry ->

            val postId = backStackEntry.arguments?.getString("postId")
            val posts = userViewModel.allPosts.value
            val post = posts.find { it.id == postId }

            post?.let {
                ClaimScreen(
                    post = it,
                    viewModel = userViewModel,
                    navController = navController
                )
            }
        }
        composable(
            route = "chat/{chatId}/{postOwnerName}",
            arguments = listOf(
                navArgument("chatId") { type = NavType.StringType },
                navArgument("postOwnerName") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val chatId = backStackEntry.arguments?.getString("chatId") ?: ""
            val postOwnerName = backStackEntry.arguments?.getString("postOwnerName") ?: "User"
            ChatScreen(chatId = chatId, postOwnerName = postOwnerName)
        }


    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItems.Home,
        BottomNavItems.MyPost,
        BottomNavItems.Post,
        BottomNavItems.Notification,
        BottomNavItems.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.name) },
                label = { Text(item.name) },
                selected = currentRoute == item.route,

                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

@Composable
fun MainNavigation(navController: NavHostController, rootNavController: NavHostController,
                   isDarkTheme: Boolean,
                   onThemeChange: (Boolean) -> Unit) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute?.startsWith("chat") == false) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            BottomNavigation(navController, rootNavController,
                isDarkTheme = isDarkTheme,
                onThemeChange = onThemeChange)
        }
    }
}


