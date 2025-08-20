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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            LostAndCampusAppTheme {
                AppNavigation()
            }
        }
    }

    //    @Composable
//    fun AppNavigation() {
//        val navController = rememberNavController()
//        val currentUser = FirebaseAuth.getInstance().currentUser
//
//        NavHost(
//            navController = navController,
//            startDestination = if (currentUser != null) "userHome" else "signup"
//        ) {
//            composable("signup") {
//                SignUpScreen(
//                    onSignUpClick = { name, email, regNo, password ->
//                        navController.navigate("userHome") {
//                            popUpTo("signup") { inclusive = true }
//                        }
//                    },
//                    onLoginClick = {
//                        navController.navigate("login")
//                    }
//                )
//            }
//
//            composable("login") {
//                LoginScreen(
//                    onLoginSuccess = { role ->
//                        if (role == "admin") {
//                            navController.navigate("adminHome") {
//                                popUpTo("login") { inclusive = true }
//                            }
//                        } else {
//                            navController.navigate("userHome") {
//                                popUpTo("login") { inclusive = true }
//                            }
//                        }
//                    },
//                    onBackToSignUp = {
//                        navController.navigate("signup")
//                    }
//                )
//            }
//
//            composable("userHome") {
//                MainNavigation(navController)
//            }
//            composable("adminHome") {
//                AdminNavigation(navController)
//            }
//        }
//    }
//}
    @Composable
    fun AppNavigation() {
        val navController = rememberNavController()
        val currentUser = FirebaseAuth.getInstance().currentUser
        var startDestination by remember { mutableStateOf<String?>(null) } // initially unknown

        LaunchedEffect(currentUser) {
            if (currentUser == null) {
                startDestination = "signup"
            } else {
                // fetch role from Firestore
                val uid = currentUser.uid
                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        val role = document.getString("role") ?: "user"
                        startDestination = if (role == "admin") "adminHome" else "userHome"
                    }
                    .addOnFailureListener {
                        startDestination = "userHome" // fallback
                    }
            }
        }

        if (startDestination != null) {
            NavHost(
                navController = navController,
                startDestination = startDestination!!
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
                        }
                    )
                }

                composable("userHome") { MainNavigation(navController) }
                composable("adminHome") { AdminNavigation(navController) }
            }
        } else {
            // loading screen jab tak role fetch ho raha hai
            androidx.compose.material3.CircularProgressIndicator()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    LostAndCampusAppTheme {
        val navController = rememberNavController()
        Scaffold(
            bottomBar = { BottomNavigationBar(navController = navController) }
        ) { innerPadding ->
            BottomNavigation(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
