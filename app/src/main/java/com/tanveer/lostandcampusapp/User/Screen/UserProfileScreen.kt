//package com.tanveer.lostandcampusapp.User.Screen
//
//import android.content.Context
//import android.graphics.Bitmap
//import android.net.Uri
//import androidx.activity.compose.rememberLauncherForActivityResult
//import androidx.activity.result.contract.ActivityResultContracts
//import androidx.compose.foundation.background
//import androidx.compose.foundation.border
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.shape.RoundedCornerShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.CameraAlt
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material.icons.filled.Logout
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.graphics.Brush
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.NavHostController
//import coil.compose.AsyncImage
//import com.google.firebase.auth.FirebaseAuth
//import com.google.firebase.firestore.FirebaseFirestore
//import com.tanveer.lostandcampusapp.data.FileUtils
//import com.tanveer.lostandcampusapp.data.StatsRepository
//import com.tanveer.lostandcampusapp.data.UserStats
//import com.tanveer.lostandcampusapp.viewModel.UserViewModel
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
//import java.io.File
//import java.io.FileOutputStream
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun UserProfileScreen(
//    rootNavController: NavHostController,
//    isDarkTheme: Boolean,
//    onThemeChange: (Boolean) -> Unit,
//    userViewModel: UserViewModel = viewModel()
//) {
//    val context = LocalContext.current
//    val coroutineScope = rememberCoroutineScope()
//    val stats by userViewModel.userStats.collectAsState()
//    val profileUrl = userViewModel.profileImageUrl.value
//    val userName = userViewModel.name.value
//    val userRegNo = userViewModel.regNo.value
//    val userBio = userViewModel.bio.value
//    var showEditDialog by remember { mutableStateOf(false) }
//    var showLogoutDialog by remember { mutableStateOf(false) }
//    var profileImageUri by remember { mutableStateOf<Uri?>(null) }
//
//// Load user from DataStore on start
//    LaunchedEffect(Unit) {
//        userViewModel.loadUserFromDataStore(context)
//    }
//
//
//    val galleryLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri: Uri? ->
//        uri?.let {
//            profileImageUri = it
//            userViewModel.uploadProfileImageToCloudinary(context, it)
//        }
//    }
//
//    val cameraLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.TakePicturePreview()
//    ) { bitmap ->
//        bitmap?.let {
//            // Convert bitmap to file if needed (for Cloudinary)
//            val file = File(context.cacheDir, "profile_${System.currentTimeMillis()}.jpg")
//            val outputStream = FileOutputStream(file)
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
//            outputStream.close()
//            userViewModel.uploadProfileImageToCloudinary(context, Uri.fromFile(file))
//        }
//    }
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
//                actions = {
//                    IconButton(onClick = { onThemeChange(!isDarkTheme) }) {
//                        Icon(
//                            imageVector = Icons.Default.Edit,
//                            contentDescription = "Toggle Theme"
//                        )
//                    }
//                }
//            )
//        }
//    ) { innerPadding ->
//
//        Column(
//            modifier = Modifier
//                .padding(innerPadding)
//                .fillMaxSize()
//                .verticalScroll(rememberScrollState())
//                .background(
//                    Brush.verticalGradient(
//                        listOf(
//                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
//                            MaterialTheme.colorScheme.background
//                        )
//                    )
//                ),
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Spacer(modifier = Modifier.height(24.dp))
//
//            // ==== Profile Photo ====
//            Box(contentAlignment = Alignment.BottomEnd) {
//                if (profileUrl.isNotEmpty()) {
//                    AsyncImage(
//                        model = profileUrl,
//                        contentDescription = "Profile Picture",
//                        modifier = Modifier
//                            .size(120.dp)
//                            .clip(CircleShape)
//                            .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape),
//                        contentScale = ContentScale.Crop
//                    )
//                } else {
//                    Box(
//                        modifier = Modifier
//                            .size(120.dp)
//                            .clip(CircleShape)
//                            .background(Color.Gray.copy(alpha = 0.3f)),
//                        contentAlignment = Alignment.Center
//                    ) {
//                        Icon(
//                            imageVector = Icons.Default.CameraAlt,
//                            contentDescription = "Add Photo",
//                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
//                            modifier = Modifier.size(40.dp)
//                        )
//                    }
//                }
//
//                // === Camera & Gallery Buttons ===
//                // Camera & Gallery buttons
//                Row(modifier = Modifier.align(Alignment.BottomEnd)) {
//                    SmallActionButton(icon = Icons.Default.CameraAlt) {
//                        cameraLauncher.launch(null)
//                    }
//                    Spacer(modifier = Modifier.width(4.dp))
//                    SmallActionButton(icon = Icons.Default.Edit) {
//                        galleryLauncher.launch("image/*")
//                    }
//                }
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Text(
//                text = userViewModel.name.value.ifEmpty { "User Name" },
//                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
//            )
//            Text("Reg No: $userRegNo", style = MaterialTheme.typography.bodyMedium)
//
//
//            Spacer(modifier = Modifier.height(20.dp))
//
//            // ==== Stats Section ====
//            Card(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .padding(horizontal = 20.dp),
//                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
//                shape = RoundedCornerShape(20.dp),
//                elevation = CardDefaults.cardElevation(6.dp)
//            ) {
//                Row(
//                    modifier = Modifier
//                        .padding(vertical = 16.dp)
//                        .fillMaxWidth(),
//                    horizontalArrangement = Arrangement.SpaceEvenly
//                ) {
//                    StatBox("Total", stats.totalPosts)
//                    StatBox("Lost", stats.lostPosts)
//                    StatBox("Found", stats.foundPosts)
//                    StatBox("Claims", stats.claimsMade)
//                }
//            }
//
//            Spacer(modifier = Modifier.height(24.dp))
//            // ===== Options =====
//            ProfileOptionCard("Edit Profile") { showEditDialog = true }
//            Divider()
//            ProfileOptionCard("My Posts") { rootNavController.navigate("myposts") }
//            Divider()
//            ProfileOptionCard("My Claims") { rootNavController.navigate("myclaims") }
//            Divider()
//            ProfileOptionCard("Logout") { showLogoutDialog = true }
//            Spacer(modifier = Modifier.height(16.dp))
//
//            Button(
//                onClick = { showLogoutDialog = true },
//                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
//            ) {
//                Icon(Icons.Default.Logout, contentDescription = null)
//                Spacer(modifier = Modifier.width(8.dp))
//                Text("Logout", color = Color.White)
//            }
//
//            Spacer(modifier = Modifier.height(40.dp))
//        }
//
//        // ===== Logout Dialog =====
//        if (showLogoutDialog) {
//            AlertDialog(
//                onDismissRequest = { showLogoutDialog = false },
//                title = { Text("Logout") },
//                text = { Text("Are you sure you want to logout?") },
//                confirmButton = {
//                    TextButton(onClick = {
//                        com.tanveer.lostandcampusapp.data.AuthRepo.logout()
//                        rootNavController.navigate("login") {
//                            popUpTo("userHome") { inclusive = true }
//                        }
//                    }) {
//                        Text("Yes")
//                    }
//                },
//                dismissButton = {
//                    TextButton(onClick = { showLogoutDialog = false }) {
//                        Text("Cancel")
//                    }
//                }
//            )
//        }
//        // ===== Edit Profile Dialog =====
//        if (showEditDialog) {
//            EditProfileDialog(userViewModel = userViewModel, context = context, onDismiss = { showEditDialog = false })
//        }
//    }
//}
//@Composable
//fun EditProfileDialog(userViewModel: UserViewModel, context: Context, onDismiss: () -> Unit) {
//    var newName by remember { mutableStateOf(userViewModel.name.value) }
//    var newImageUri by remember { mutableStateOf<Uri?>(null) }
//
//    val imagePickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.GetContent()
//    ) { uri -> newImageUri = uri }
//
//    AlertDialog(
//        onDismissRequest = { onDismiss() },
//        title = { Text("Edit Profile") },
//        text = {
//            Column(horizontalAlignment = Alignment.CenterHorizontally) {
//                Box(
//                    modifier = Modifier.size(100.dp).clip(CircleShape).clickable { imagePickerLauncher.launch("image/*") },
//                    contentAlignment = Alignment.Center
//                ) {
//                    AsyncImage(
//                        model = newImageUri ?: userViewModel.profileImageUrl.value,
//                        contentDescription = "Profile Image",
//                        modifier = Modifier.fillMaxSize()
//                    )
//                }
//
//                Spacer(modifier = Modifier.height(12.dp))
//                OutlinedTextField(
//                    value = newName,
//                    onValueChange = { newName = it },
//                    label = { Text("Username") }
//                )
//            }
//        },
//        confirmButton = {
//            Button(onClick = {
//                // Update name
//                userViewModel.setUserData(newName, userViewModel.regNo.value)
//                userViewModel.saveUserToDataStore(context)
//
//                // Update profile image if changed
//                newImageUri?.let {
//                    val file = FileUtils.getFileFromUri(context, it)
//                    userViewModel.uploadProfileImageToCloudinary(context, Uri.fromFile(file))
//                }
//
//                onDismiss()
//            }) {
//                Text("Save")
//            }
//        },
//        dismissButton = { TextButton(onClick = { onDismiss() }) { Text("Cancel") } }
//    )
//}
//
//@Composable
//fun StatBox(title: String, count: Int) {
//    Column(horizontalAlignment = Alignment.CenterHorizontally) {
//        Text(
//            text = count.toString(),
//            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
//        )
//        Text(title, color = Color.Gray)
//    }
//}
//
//@Composable
//fun SmallActionButton(icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
//    Box(
//        modifier = Modifier
//            .size(36.dp)
//            .clip(CircleShape)
//            .background(MaterialTheme.colorScheme.primary)
//            .clickable { onClick() },
//        contentAlignment = Alignment.Center
//    ) {
//        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
//    }
//}
//
//@Composable
//fun ProfileOptionCard(text: String, onClick: () -> Unit) {
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(horizontal = 20.dp, vertical = 8.dp)
//            .clickable { onClick() },
//        shape = RoundedCornerShape(14.dp),
//        elevation = CardDefaults.cardElevation(5.dp)
//    ) {
//        Box(modifier = Modifier.padding(16.dp)) {
//            Text(text, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium))
//        }
//    }
//}
package com.tanveer.lostandcampusapp.User.Screen


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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
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

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val profileUrl = userViewModel.profileImageUrl.value

    // Load user data from DataStore
    LaunchedEffect(Unit) {
        val (savedName, savedRegNo) = DataStoreManager.getUserData(context)
        userViewModel.setUserData(savedName, savedRegNo)
        // Fetch user stats
        if (currentUserId.isNotEmpty()) {
            userViewModel.fetchUserStats(currentUserId)
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
            StatCard("Total", userStats.totalPosts)
            StatCard("Lost", userStats.lostPosts)
            StatCard("Found", userStats.foundPosts)
            StatCard("Claims", userStats.claimsMade)
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
                    AuthRepo.logout()
                    rootNavController.navigate("login") {
                        popUpTo("userHome") { inclusive = true }
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
                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
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