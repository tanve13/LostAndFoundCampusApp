package com.tanveer.lostandcampusapp.User.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.tanveer.lostandcampusapp.User.Screen.HomeScreen
import com.tanveer.lostandcampusapp.User.Screen.NotificationScreen
import com.tanveer.lostandcampusapp.User.Screen.PostScreen
import com.tanveer.lostandcampusapp.User.Screen.UserMyPostScreen
import com.tanveer.lostandcampusapp.User.Screen.UserProfileScreen


@Composable
fun BottomNavigation(navController: NavHostController,modifier: Modifier = Modifier){
    NavHost(navController = navController,startDestination = BottomNavItems.Home.route){
        composable(BottomNavItems.Home.route){
            HomeScreen()
        }
        composable(BottomNavItems.MyPost.route){
            UserMyPostScreen()
        }
        composable(BottomNavItems.Post.route) {
            PostScreen()
        }

        composable(BottomNavItems.Notification.route){
            NotificationScreen()
        }
        composable(BottomNavItems.Profile.route){
           UserProfileScreen(navController = navController)
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
fun MainNavigation(navController: NavHostController) {
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            BottomNavigation(navController)
        }
    }
}


