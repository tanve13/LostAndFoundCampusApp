package com.tanveer.lostandcampusapp.screens

import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tanveer.lostandcampusapp.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {

    val scale = remember { Animatable(0f) }
    LaunchedEffect(true) {
        scale.animateTo(
            targetValue = 1f,
            animationSpec = tween(1200, easing = {
                OvershootInterpolator(2f).getInterpolation(it)
            })
        )
        delay(700)

        // 🟢 CHECK LOGIN SESSION
        val context = navController.context
        val sharedPref = context.getSharedPreferences("UserSession", android.content.Context.MODE_PRIVATE)

        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
        val role = sharedPref.getString("role", "user") ?: "user"

        if (!isLoggedIn) {
            navController.navigate("signup") {
                popUpTo("splash") { inclusive = true }
            }
        } else {
            if (role == "admin") {
                navController.navigate("adminHome") {
                    popUpTo("splash") { inclusive = true }
                }
            } else {
                navController.navigate("userHome") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }
    }

    // UI
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.trackit),
            contentDescription = null,
            modifier = Modifier
                .size(180.dp)
                .scale(scale.value)
        )
    }
}
