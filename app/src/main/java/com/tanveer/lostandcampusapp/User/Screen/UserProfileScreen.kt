//package com.tanveer.lostandcampusapp.User.Screen
//
//import android.content.Intent
//import android.net.Uri
//import android.widget.Toast
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.shape.CircleShape
//import androidx.compose.foundation.verticalScroll
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
//import androidx.compose.material.icons.filled.Edit
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.draw.clip
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.navigation.NavHostController
//import coil.compose.AsyncImage
//import com.google.firebase.auth.FirebaseAuth
//import com.tanveer.lostandcampusapp.data.AuthRepo
//import com.tanveer.lostandcampusapp.data.DataStoreManager
//import com.tanveer.lostandcampusapp.viewModel.UserViewModel
//
//@Composable
//fun UserProfileScreen(
//    rootNavController: NavHostController,
//    isDarkTheme: Boolean,
//    onThemeChange: (Boolean) -> Unit,
//    userViewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
//) {
//    val scrollState = rememberScrollState()
//    val context = LocalContext.current
//    var showLogoutDialog by remember { mutableStateOf(false) }
//    var showChangePasswordDialog by remember { mutableStateOf(false) }
//    var showEditProfileDialog by remember { mutableStateOf(false) }
//    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
//    val profileUrl = userViewModel.profileImageUrl.value
//
//    // Load user data from DataStore
//    LaunchedEffect(Unit) {
//        val (savedName, savedRegNo) = DataStoreManager.getUserData(context)
//        userViewModel.setUserData(savedName, savedRegNo)
//        // Fetch user stats
//        if (currentUserId.isNotEmpty()) {
//            userViewModel.fetchUserStats(currentUserId)
//        }
//    }
//    val userStats by userViewModel.userStats.collectAsState()
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .verticalScroll(scrollState)
//            .background(MaterialTheme.colorScheme.background)
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//
//        // ==== Top Profile Section ====
//        Column(horizontalAlignment = Alignment.CenterHorizontally,
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Box(contentAlignment = Alignment.Center) {
//                val profileImageModifier = Modifier
//                    .size(100.dp)
//                    .clip(CircleShape)
//                    .clickable { showProfileDialog = true }
//
//                if (profileUrl.isNotEmpty()) {
//                    AsyncImage(
//                        model = profileUrl,
//                        contentDescription = "Profile Picture",
//                        modifier = Modifier
//                            .size(100.dp)
//                            .clip(CircleShape)
//                    )
//                } else {
//                    Icon(
//                        imageVector = Icons.Default.Person,
//                        contentDescription = "Default Profile Picture",
//                        modifier = Modifier
//                            .size(100.dp)
//                            .clip(CircleShape),
//                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
//                    )
//                }
//
//            }
//
//            Spacer(modifier = Modifier.height(12.dp))
//
//            Text(
//                text = userViewModel.name.value.ifEmpty { "User Name" },
//                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
//            )
//            Text(
//                text = userViewModel.regNo.value.ifEmpty { "Registration No" },
//                style = MaterialTheme.typography.bodyMedium
//            )
//
//            Spacer(modifier = Modifier.height(8.dp))
//
//            IconButton(onClick = { showEditProfileDialog = true }) {
//                Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
//            }
//        }
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // ==== Stats Section ====
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceAround
//        ) {
//            StatCard("Total", userStats.totalPosts)
//            StatCard("Lost", userStats.lostPosts)
//            StatCard("Found", userStats.foundPosts)
//            StatCard("Claims", userStats.claimsMade)
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // ==== Settings & Options Section ====
////        ProfileOption(text = "Change Password") {
////            showChangePasswordDialog = true
////        }
//        Divider()
//        ThemeOption(text = "App Theme", isChecked = isDarkTheme,
//            onCheckedChange = { onThemeChange(it) })
////        Divider()
////        ProfileOption(text = "Contact Support") {
////            val intent = Intent(Intent.ACTION_SENDTO).apply {
////                data = Uri.parse("mailto:yourapp@gmail.com")
////                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
////            }
////            context.startActivity(intent)
////        }
////        Divider()
////        ProfileOption(text = "Privacy Policy") {
////            val url = "https://yourapp.com/privacy"
////            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
////            context.startActivity(intent)
////        }
//        Divider()
//        ProfileOption(text = "Logout") {
//            showLogoutDialog = true
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//
////        Text(
////            text = "Version 1.0.0",
////            fontSize = 13.sp,
////            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
////            modifier = Modifier.align(Alignment.CenterHorizontally)
////        )
//    }
//
//    // === Dialogs ===
//    if (showLogoutDialog) {
//        AlertDialog(
//            onDismissRequest = { showLogoutDialog = false },
//            title = { Text("Logout") },
//            text = { Text("Are you sure you want to logout?") },
//            confirmButton = {
//                TextButton(onClick = {
//                    AuthRepo.logout()
//                    rootNavController.navigate("login") {
//                        popUpTo("userHome") { inclusive = true }
//                    }
//                }) { Text("Yes") }
//            },
//            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } }
//        )
//    }
//
//    if (showChangePasswordDialog) {
//        ChangePasswordDialog(
//            onDismiss = { showChangePasswordDialog = false },
//            onUpdate = { newPass ->
//                val email = FirebaseAuth.getInstance().currentUser?.email
//                if (email != null) {
//                    FirebaseAuth.getInstance().currentUser?.updatePassword(newPass)
//                        ?.addOnSuccessListener {
//                            Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
//                        }
//                        ?.addOnFailureListener { e ->
//                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//                        }
//                }
//                showChangePasswordDialog = false
//            }
//        )
//    }
//
//    if (showEditProfileDialog) {
//        EditProfileDialog(
//            currentName = userViewModel.name.value,
//            onDismiss = { showEditProfileDialog = false },
//            onSave = { newName ->
//                val regNo = userViewModel.regNo.value
//                if (regNo.isNotEmpty()) {
//                    // Update in Firestore
//                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
//                    db.collection("users").document(regNo)
//                        .update("name", newName)
//                        .addOnSuccessListener {
//                            userViewModel.setUserData(newName, regNo)
//                            Toast.makeText(context, "Profile updated", Toast.LENGTH_SHORT).show()
//                        }
//                        .addOnFailureListener { e ->
//                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
//                        }
//                }
//                showEditProfileDialog = false
//            }
//        )
//    }
//}
//
//// === Reusable Components ===
//@Composable
//fun StatCard(title: String, count: Int) {
//    Card(
//        modifier = Modifier.size(80.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
//    ) {
//        Column(
//            modifier = Modifier.fillMaxSize(),
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.CenterHorizontally
//        ) {
//            Text("$count", fontWeight = FontWeight.Bold)
//            Text(title, style = MaterialTheme.typography.bodySmall)
//        }
//    }
//}
//
//@Composable
//fun ProfileOption(text: String, onClick: () -> Unit) {
//    Card(
//        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
//        onClick = onClick,
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth().padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(text = text, fontSize = 16.sp)
//            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
//        }
//    }
//}
//
//@Composable
//fun ThemeOption(text: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
//    Card(
//        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
//        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
//    ) {
//        Row(
//            modifier = Modifier.fillMaxWidth().padding(16.dp),
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween
//        ) {
//            Text(text = text, fontSize = 16.sp)
//            Switch(checked = isChecked, onCheckedChange = onCheckedChange)
//        }
//    }
//}
//
//// === Dialogs ===
//@Composable
//fun ChangePasswordDialog(onDismiss: () -> Unit, onUpdate: (String) -> Unit) {
//    var newPass by remember { mutableStateOf("") }
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Change Password") },
//        text = {
//            OutlinedTextField(
//                value = newPass,
//                onValueChange = { newPass = it },
//                label = { Text("New Password") }
//            )
//        },
//        confirmButton = {
//            Button(onClick = { if (newPass.isNotEmpty()) onUpdate(newPass) }) {
//                Text("Update")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) { Text("Cancel") }
//        }
//    )
//}
//
//@Composable
//fun EditProfileDialog(currentName: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
//    var newName by remember { mutableStateOf(currentName) }
//    AlertDialog(
//        onDismissRequest = onDismiss,
//        title = { Text("Edit Profile") },
//        text = {
//            OutlinedTextField(
//                value = newName,
//                onValueChange = { newName = it },
//                label = { Text("Name") }
//            )
//        },
//        confirmButton = {
//            Button(onClick = { if (newName.isNotEmpty()) onSave(newName) }) {
//                Text("Save")
//            }
//        },
//        dismissButton = {
//            TextButton(onClick = onDismiss) { Text("Cancel") }
//        }
//    )
//}
//jaha se start hai
package com.tanveer.lostandcampusapp.User.Screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.tanveer.lostandcampusapp.data.AuthRepo
import com.tanveer.lostandcampusapp.data.DataStoreManager
import com.tanveer.lostandcampusapp.viewModel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    userViewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showAvatarSheet by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    val currentUserId = remember { FirebaseAuth.getInstance().currentUser?.uid.orEmpty() }
    val profileUrl by userViewModel.profileImageUrl

    // Load data & stats once
    LaunchedEffect(Unit) {
        val (savedName, savedRegNo) = DataStoreManager.getUserData(context)
        userViewModel.setUserData(savedName, savedRegNo)
        if (currentUserId.isNotEmpty()) userViewModel.fetchUserStats(currentUserId)
    }
    val userStats by userViewModel.userStats.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Cover header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            // Optional cover image (use campus banner or solid)
            Box(Modifier.fillMaxSize()) {
                // Replace with AsyncImage for a cover if you have one
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.primaryContainer)
                )
            }
            // Gradient for legibility
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.25f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.35f)
                            )
                        )
                    )
            )
            // Avatar floating
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp) .offset(y = (-36).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Avatar(
                    url = profileUrl,
                    name = userViewModel.name.value,
                    onClick = { showAvatarSheet = true }
                )
                Spacer(Modifier.width(12.dp))
                Column(
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = userViewModel.name.value.ifEmpty { "User Name" },
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Filled.Verified,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.tertiary
                        )
                    }
                    Text(
                        text = userViewModel.regNo.value.ifEmpty { "Registration No" },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.85f)
                    )
                }
            }
        }

        Spacer(Modifier.height(48.dp))

        // Bio + edit
        Card(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = "About",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = userViewModel.bio.value.ifEmpty { "Add a short bio to help others identify items and contact faster." },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                IconButton(onClick = { showEditProfileDialog = true }) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit Profile")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Badges / achievements
        BadgesRow(
            badges = listOf(
                BadgeItem("Verified Email", Icons.Filled.Email),
                BadgeItem("Helpful Finder", Icons.Filled.Badge)
            )
        )

        Spacer(Modifier.height(16.dp))

        // Stats
        StatsRow(
            total = userStats.totalPosts,
            lost = userStats.lostPosts,
            found = userStats.foundPosts,
            claims = userStats.claimsMade
        )

        Spacer(Modifier.height(16.dp))

        // Quick actions
        QuickActions(
            onMyPosts = { navController.navigate("myPosts") },
            onMyClaims = { navController.navigate("myClaims") },
            onSaved = { navController.navigate("saved") },
            onReportIssue = {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:yourapp@gmail.com")
                    putExtra(Intent.EXTRA_SUBJECT, "Support Request")
                }
                context.startActivity(intent)
            }
        )

        Spacer(Modifier.height(8.dp))

        // Preferences
        SettingsGroup(
            title = "Preferences",
            content = {
                ThemeOption(
                    text = "App Theme",
                    isChecked = isDarkTheme,
                    onCheckedChange = onThemeChange
                )
                NotificationOption(
                    text = "Notifications",
                    isChecked = notificationsEnabled,
                    onCheckedChange = { notificationsEnabled = it }
                )
            }
        )

        // Account
        SettingsGroup(
            title = "Account",
            content = {
                ProfileOption(text = "Edit Profile", icon = Icons.Default.Edit) {
                    showEditProfileDialog = true
                }
                ProfileOption(text = "Change Password", icon = Icons.Default.Lock) {
                    showChangePasswordDialog = true
                }
                ProfileOption(text = "Logout", icon = Icons.Default.ExitToApp) {
                    showLogoutDialog = true
                }
            }
        )

        Spacer(Modifier.height(24.dp))

        // Version
        Text(
            text = "Version 1.0.0",
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 24.dp)
        )
    }

    // Dialogs
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    AuthRepo.logout()
                    navController.navigate("login") {
                        popUpTo("userHome") { inclusive = true }
                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onUpdate = { newPass ->
                val user = FirebaseAuth.getInstance().currentUser
                user?.updatePassword(newPass)
                    ?.addOnSuccessListener {
                        Toast.makeText(context, "Password updated", Toast.LENGTH_SHORT).show()
                    }
                    ?.addOnFailureListener { e ->
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                showChangePasswordDialog = false
            }
        )
    }

    if (showEditProfileDialog) {
        EditProfileDialog(
            currentName = userViewModel.name.value,
            currentBio = userViewModel.bio.value,
            onDismiss = { showEditProfileDialog = false },
            onSave = { newName, newBio ->
                val regNo = userViewModel.regNo.value
                if (regNo.isNotEmpty()) {
                    val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                    db.collection("users").document(regNo)
                        .update(mapOf("name" to newName, "bio" to newBio))
                        .addOnSuccessListener {
                            userViewModel.setUserData(newName, regNo)
                            userViewModel.setBio(newBio)
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

    // Avatar sheet: view/change photo
    if (showAvatarSheet) {
        ModalBottomSheet(
            onDismissRequest = { showAvatarSheet = false },
            dragHandle = {}
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Profile Photo", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(12.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(onClick = {
                        // TODO: launch picker/camera and update in storage + ViewModel
                        showAvatarSheet = false
                    }) { Text("Change Photo") }
                    OutlinedButton(onClick = {
                        // TODO: remove photo and fallback to initials
                        showAvatarSheet = false
                    }) { Text("Remove Photo") }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun Avatar(url: String, name: String, onClick: () -> Unit) {
    val ringBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF8E9BFF),
                Color(0xFF69E0FF),
                Color(0xFF8E9BFF)
            )
        )
    }
    Box(
        modifier = Modifier
            .size(96.dp)
            .clickable(onClick = onClick)
            .shadow(6.dp, CircleShape, clip = false)
    ) {
        // Ring
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(ringBrush, CircleShape)
                .padding(3.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surface)
        )
        // Photo or initials
        if (url.isNotEmpty()) {
            AsyncImage(
                model = url,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
                    .padding(6.dp)
                    .clip(CircleShape)
            )
        } else {
            // Initials fallback
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                val initials = name
                    .trim()
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .take(2)
                    .joinToString("") { it.first().uppercase() }
                    .ifEmpty { "U" }
                Text(
                    text = initials,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

data class BadgeItem(val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

@Composable
private fun BadgesRow(badges: List<BadgeItem>) {
    if (badges.isEmpty()) return
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        badges.take(3).forEach { badge ->
            AssistChip(
                onClick = { },
                label = { Text(badge.label) },
                leadingIcon = {
                    Icon(
                        imageVector = badge.icon,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            )
        }
    }
}

@Composable
private fun StatsRow(total: Int, lost: Int, found: Int, claims: Int) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard2("Total", total)
        StatCard2("Lost", lost)
        StatCard2("Found", found)
        StatCard2("Claims", claims)
    }
}

@Composable
private fun StatCard2(title: String, count: Int) {
    ElevatedCard(
        modifier = Modifier
            .height(80.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("$count", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(title, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
private fun QuickActions(
    onMyPosts: () -> Unit,
    onMyClaims: () -> Unit,
    onSaved: () -> Unit,
    onReportIssue: () -> Unit
) {
    Text(
        text = "Quick actions",
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        QuickActionCard("My Posts", Icons.Default.PostAdd, onMyPosts)
        QuickActionCard("My Claims", Icons.Default.Badge, onMyClaims)
        QuickActionCard("Saved", Icons.Default.Bookmark, onSaved)
        QuickActionCard("Report Issue", Icons.Default.Email, onReportIssue)
    }
}

@Composable
private fun QuickActionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    FilledTonalButton(
        onClick = onClick,
        modifier = Modifier

            .height(56.dp),
        shape = RoundedCornerShape(14.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(title, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
private fun SettingsGroup(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
    Card(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(Modifier.padding(6.dp)) {
            content()
        }
    }
}

@Composable
fun ProfileOption(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    ListItem(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        leadingContent = { Icon(icon, contentDescription = null) },
        headlineContent = { Text(text) },
        trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) }
    )
    Divider()
}

@Composable
fun ThemeOption(text: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    ListItem(
        headlineContent = { Text(text) },
        trailingContent = {
            Switch(checked = isChecked, onCheckedChange = onCheckedChange)
        }
    )
    Divider()
}

@Composable
private fun NotificationOption(text: String, isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    ListItem(
        leadingContent = { Icon(Icons.Outlined.Notifications, contentDescription = null) },
        headlineContent = { Text(text) },
        trailingContent = { Switch(checked = isChecked, onCheckedChange = onCheckedChange) }
    )
    Divider()
}

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
            Button(onClick = { if (newPass.isNotEmpty()) onUpdate(newPass) }) { Text("Update") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentBio: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var newName by remember { mutableStateOf(currentName) }
    var newBio by remember { mutableStateOf(currentBio) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Profile") },
        text = {
            Column {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Name") },
                    singleLine = true
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = newBio,
                    onValueChange = { newBio = it },
                    label = { Text("Bio") },
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (newName.isNotEmpty()) onSave(newName, newBio) }) { Text("Save") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}
