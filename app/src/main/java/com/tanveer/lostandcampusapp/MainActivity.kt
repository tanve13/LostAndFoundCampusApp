package com.tanveer.lostandcampusapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import com.tanveer.lostandcampusapp.Admin.AdminScreens.AdminHomeScreen
import com.tanveer.lostandcampusapp.Admin.navigation.AdminNavigation
import com.tanveer.lostandcampusapp.User.navigation.BottomNavigation
import com.tanveer.lostandcampusapp.User.navigation.BottomNavigationBar
import com.tanveer.lostandcampusapp.User.navigation.MainNavigation
import com.tanveer.lostandcampusapp.ui.theme.LostAndCampusAppTheme
import com.tanveer.lostandcampusapp.viewModel.UserViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LostAndCampusAppTheme {
                AppNavigation()
            }
        }
    }

    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        val currentUser = FirebaseAuth.getInstance().currentUser

        NavHost(
            navController = navController,
            startDestination = if (currentUser != null) "userHome" else "signup"
        ) {
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
                    }, userViewModel = userViewModel   // ✅ yaha pass karo

                )
            }

            composable("userHome") {
                val userNavController = rememberNavController()

                MainNavigation(userNavController, rootNavController = navController)
            }
            composable("adminHome") {
                val adminNavController = rememberNavController()
                AdminNavigation(adminNavController)
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LostAndCampusAppTheme {

    }
}
