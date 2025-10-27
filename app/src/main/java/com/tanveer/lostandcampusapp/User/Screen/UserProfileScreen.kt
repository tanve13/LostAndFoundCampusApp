package com.tanveer.lostandcampusapp.User.Screen

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tanveer.lostandcampusapp.data.AuthRepo
import com.tanveer.lostandcampusapp.data.DataStoreManager
import com.tanveer.lostandcampusapp.viewModel.UserViewModel

@Composable
fun UserProfileScreen(
    rootNavController: NavHostController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    userViewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    val profileUrl = userViewModel.profileImageUrl.value

    // Load user data from DataStore
    LaunchedEffect(Unit) {
        val (savedName, savedRegNo) = DataStoreManager.getUserData(context)
        userViewModel.setUserData(savedName, savedRegNo)
        // Fetch user stats
        if (savedRegNo.isNotEmpty()) {
            userViewModel.fetchUserStats(savedRegNo)
        }

    }
    val userStats by userViewModel.userStats.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // ==== Top Profile Section ====
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(contentAlignment = Alignment.Center) {
                val profileImageModifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .clickable { showProfileDialog = true }

                if (profileUrl.isNotEmpty()) {
                    AsyncImage(
                        model = profileUrl,
                        contentDescription = "Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Default Profile Picture",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                }

            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userViewModel.name.value.ifEmpty { "User Name" },
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = userViewModel.regNo.value.ifEmpty { "Registration No" },
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            IconButton(onClick = { showEditProfileDialog = true }) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // ==== Stats Section ====
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            StatCard("Total", userStats.totalPosts,Color(0xFFE5F3FF))
            StatCard("Lost", userStats.lostPosts,Color(0xFFE5F3FF))
            StatCard("Found", userStats.foundPosts, Color(0xFFE5F3FF))
            StatCard("Claims", userStats.claimsMade,Color(0xFFE5F3FF))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ==== Settings & Options Section ====
//        ProfileOption(text = "Change Password") {
//            showChangePasswordDialog = true
//        }
        Divider()
        ThemeOption(text = "App Theme", isChecked = isDarkTheme,
            onCheckedChange = { onThemeChange(it) })
//        Divider()
//        ProfileOption(text = "Contact Support") {
//            val intent = Intent(Intent.ACTION_SENDTO).apply {
//                data = Uri.parse("mailto:yourapp@gmail.com")
//                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
//            }
//            context.startActivity(intent)
//        }
//        Divider()
//        ProfileOption(text = "Privacy Policy") {
//            val url = "https://yourapp.com/privacy"
//            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
//            context.startActivity(intent)
//        }
        Divider()
        ProfileOption(text = "Logout") {
            showLogoutDialog = true
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Version 1.0.0",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }

    // === Dialogs ===
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    AuthRepo.logout(context)
                    rootNavController.navigate("login") {
//                        popUpTo("userHome") { inclusive = true }
                        popUpTo(0) { inclusive = true }

                    }
                }) { Text("Yes") }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onUpdate = { newPass ->
                val email = FirebaseAuth.getInstance().currentUser?.email
                if (email != null) {
                    FirebaseAuth.getInstance().currentUser?.updatePassword(newPass)
                        ?.addOnSuccessListener {
                            Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
                        }
                        ?.addOnFailureListener { e ->
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                showChangePasswordDialog = false
            }
        )
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = userViewModel.name.value,
            onDismiss = { showEditProfileDialog = false },
            onSave = { newName ->
                val regNo = userViewModel.regNo.value
                if (regNo.isNotEmpty()) {
                    // Update in Firestore
                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(regNo)
                        .update("name", newName)
                        .addOnSuccessListener {
                            userViewModel.setUserData(newName, regNo)
                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                showEditProfileDialog = false
            }
        )
    }
}

// === Reusable Components ===
@Composable
fun StatCard(title: String, count: Int,color: Color) {
    Card(
        modifier = Modifier.size(80.dp),
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$count", fontWeight = FontWeight.Bold,color = Color.Black)
            Text(title, style = MaterialTheme.typography.bodySmall,color = Color.Black.copy(alpha = 0.92f))
        }
    }
}

@Composable
fun ProfileOption(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = text, fontSize = 16.sp)
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
        }
    }
}

@Composable
fun ThemeOption(text: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = text, fontSize = 16.sp)
            Switch(checked = isChecked, onCheckedChange = onCheckedChange)
        }
    }
}

// === Dialogs ===
@Composable
fun ChangePasswordDialog(onDismiss: () -> Unit, onUpdate: (String) -> Unit) {
    var newPass by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Change Password") },
        text = {
            OutlinedTextField(
                value = newPass,
                onValueChange = { newPass = it },
                label = { Text("New Password") }
            )
        },
        confirmButton = {
            Button(onClick = { if (newPass.isNotEmpty()) onUpdate(newPass) }) {
                Text("Update")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun EditProfileDialog(currentName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var newName by remember { mutableStateOf(currentName) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            OutlinedTextField(
                value = newName,
                onValueChange = { newName = it },
                label = { Text("Name") }
            )
        },
        confirmButton = {
            Button(onClick = { if (newName.isNotEmpty()) onSave(newName) }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}