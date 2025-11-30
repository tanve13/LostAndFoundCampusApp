package com.tanveer.lostandcampusapp

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.google.firebase.auth.FirebaseAuth
import com.tanveer.lostandcampusapp.Admin.navigation.AdminNavigation
import com.tanveer.lostandcampusapp.User.navigation.MainNavigation
import com.tanveer.lostandcampusapp.screens.SplashScreen
import com.tanveer.lostandcampusapp.ui.theme.LostAndCampusAppTheme
import com.tanveer.lostandcampusapp.ui.theme.ThemePreference
import com.tanveer.lostandcampusapp.viewModel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val context = LocalContext.current
            val scope = rememberCoroutineScope()
            val isDarkTheme by ThemePreference.getTheme(context = context)
                .collectAsState(initial = false)
            LostAndCampusAppTheme(darkTheme = isDarkTheme) {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = false
                SideEffect {
                    systemUiController.setStatusBarColor(
                        color = androidx.compose.ui.graphics.Color.Black,
                        darkIcons = useDarkIcons
                    )
                    systemUiController.setNavigationBarColor(
                        color = androidx.compose.ui.graphics.Color.Black,
                        darkIcons = useDarkIcons
                    )
                }
                AppNavigation(
                    isDarkTheme = isDarkTheme,
                    onThemeChange = { newTheme ->
                        scope.launch {
                            ThemePreference.saveTheme(context, newTheme)
                        }
                    }
                )
            }
        }

    }

    @Composable
    fun AppNavigation(
        isDarkTheme: Boolean,
        onThemeChange: (Boolean) -> Unit
    ) {
        val context = LocalContext.current
        val navController = rememberNavController()
        val currentUser = FirebaseAuth.getInstance().currentUser
        // ✅ Check saved session
        val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val savedRole = sharedPref.getString("role", "user")
        // ✅ Determine start destination based on session
        val startDestination = when {
            !isLoggedIn -> "signup"
            savedRole == "admin" -> "adminHome"
            else -> "userHome"
        }
        NavHost(
            navController = navController,
//            startDestination = if (currentUser != null) "userHome" else "signup"
            startDestination = "splash"

        ) {
            composable("splash") {
                SplashScreen(navController)
            }
            composable("signup") {
                SignUpScreen(
                    onSignUpClick = { name, email, regNo, password ->
                        navController.navigate("userHome") {
                            popUpTo("signup") { inclusive = true }
                        }
                    },
                    onLoginClick = {
                        navController.navigate("login")
                    }
                )
            }

            composable("login") {
                val userViewModel: UserViewModel = viewModel()

                LoginScreen(
                    onLoginSuccess = { role ->
                        if (role == "admin") {
                            navController.navigate("adminHome") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            navController.navigate("userHome") {
                                popUpTo("login") { inclusive = true }
                            }
                        }
                    },
                    onBackToSignUp = {
                        navController.navigate("signup")
                    }, userViewModel = userViewModel

                )
            }

            composable("userHome") {
                val userNavController = rememberNavController()
                MainNavigation(
                    userNavController, rootNavController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange
                )
            }
            composable("adminHome") {
                val adminNavController = rememberNavController()
                AdminNavigation(adminNavController,
                    rootNavController = navController
                )
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    LostAndCampusAppTheme {
//
//    }
//}
