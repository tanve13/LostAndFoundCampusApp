package com.tanveer.lostandcampusapp.User.Screen


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.tanveer.lostandcampusapp.R
import com.tanveer.lostandcampusapp.data.DataStoreManager
import com.tanveer.lostandcampusapp.viewModel.UserViewModel

@Composable
fun UserProfileScreen(
    navController: NavHostController,rootNavController: NavHostController,
    userViewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val scrollState = rememberScrollState()

    val totalPosts = 0
    val lostPosts = 0
    val foundPosts = 0
    val claimsMade = 0
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val (savedName, savedRegNo) = DataStoreManager.getUserData(context)
        userViewModel.setUserData(savedName, savedRegNo)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ==== Top Profile Section ====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.primaryContainer)
                .padding(vertical = 32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = R.drawable.ic_launcher_foreground),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = userViewModel.name.value.ifEmpty { "User Name" },
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = userViewModel.regNo.value.ifEmpty { "Registration No" },
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        // ==== Stats Section ====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatCard("Total", totalPosts)
            StatCard("Lost", lostPosts)
            StatCard("Found", foundPosts)
            StatCard("Claims", claimsMade)
        }

        Spacer(Modifier.height(24.dp))

        // ==== My Posts Section ====
        Text(
            text = "My Posts",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(12.dp))

        // Example Post Cards
        repeat(3) { index ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_foreground),
                        contentDescription = null,
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Post Title $index", fontWeight = FontWeight.Bold)
                        Text("Short description of the post goes here...", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }

        Spacer(Modifier.height(40.dp))

        // ==== Action Buttons ====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            OutlinedButton(onClick = { /* TODO Edit Profile */ }) {
                Icon(Icons.Default.Edit, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("Edit Profile")
            }

//            Button(
//                onClick = {
//                    FirebaseAuth.getInstance().signOut()
//                    navController.navigate("login") {
//                        popUpTo("userHome") { inclusive = true }
//                    }
//                },
//                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
//            ) {
//                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
//                Spacer(Modifier.width(8.dp))
//                Text("Logout")
//            }
            Button(
                onClick = {
                    FirebaseAuth.getInstance().signOut()
                    rootNavController.navigate("login") {
                        popUpTo("userHome") { inclusive = true }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Logout, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("Logout")
            }

        }

        Spacer(Modifier.height(24.dp))

        // ==== Extra Sections ====
        Text(
            text = "Settings & Preferences",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier
                .align(Alignment.Start)
                .padding(horizontal = 16.dp)
        )

        Spacer(Modifier.height(8.dp))

        // Example: notifications, privacy, theme settings...
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Notifications: ON")
                Text("Theme: Light Mode")
                Text("Privacy: Public")
            }
        }
    }
}

@Composable
fun StatCard(title: String, count: Int) {
    Card(
        modifier = Modifier.size(80.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$count", fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodySmall)
        }
    }
}
