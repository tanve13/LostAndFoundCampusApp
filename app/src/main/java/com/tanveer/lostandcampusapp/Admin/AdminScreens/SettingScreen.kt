package com.tanveer.lostandcampusapp.Admin.AdminScreens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.tanveer.lostandcampusapp.data.FileUtils
import com.tanveer.lostandcampusapp.Admin.AdminModel.AdminViewModel
import com.tanveer.lostandcampusapp.data.AuthRepo

@Composable
fun SettingScreen(
    adminViewModel: AdminViewModel = hiltViewModel(),
    adminRegNo: String,
    rootNavController: NavHostController,
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val user by adminViewModel.user.collectAsStateWithLifecycle()
    val isSaving by adminViewModel.isSaving.collectAsStateWithLifecycle()
    val profileUpdated by adminViewModel.profileUpdated.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showProfileDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var newImageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) {
        it?.let { uri ->
            // Upload to Cloudinary
            val file = FileUtils.from(context, uri)
            CloudinaryHelper.uploadImage(file) { success, url ->
                if (success && url != null) {
                    // Profile update: Id, name, email (old values from user data), new photoUri is NULL, url as update info
                    adminViewModel.updateProfile(context, user?.id ?: "", user?.userName ?: "", user?.email ?: "", uri)
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        adminViewModel.fetchAdminProfile(adminRegNo)
        adminViewModel.fetchStats()
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        Box(
            Modifier
                .size(110.dp)
                .clip(RoundedCornerShape(55.dp))
                .background(Color(0xFFE0F7FA))
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (user?.profilePicUrl.isNullOrEmpty()) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile Picture Default",
                    tint = Color(0xFF0277BD),
                    modifier = Modifier.size(70.dp)
                )
            } else {
                AsyncImage(
                    model = user?.profilePicUrl,
                    contentDescription = "Profile Picture",
                    modifier = Modifier.fillMaxSize()
                )
            }
            // Plus icon for upload
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .background(Color.Blue, shape = RoundedCornerShape(14.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Change Profile Picture",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))
        Text(adminViewModel.adminName.value, fontWeight = FontWeight.Bold, fontSize = 22.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        Text(adminViewModel.adminEmail.value, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.height(24.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatsCard("Total Posts", adminViewModel.totalPosts)
            StatsCard("Deleted Posts", adminViewModel.deletedPosts)
        }
        Spacer(Modifier.height(24.dp))
        Divider()

        Text("Profile Settings", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        SettingsItem("Edit Profile") { showEditProfileDialog = true }
        Divider(Modifier.padding(vertical = 4.dp))
        SettingsItem("Change Password") { showChangePasswordDialog = true }

        Spacer(Modifier.height(24.dp))
        Divider()

        Text("App & Support", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
        SettingsItem("Contact Support") {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:your-support-email@example.com")
                putExtra(Intent.EXTRA_SUBJECT, "Support Request")
            }
            context.startActivity(intent)
        }
        Divider(Modifier.padding(vertical = 4.dp))
        SettingsItem("Privacy Policy") {
            val url = "https://yourapp.com/privacy"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(intent)
        }
        Spacer(Modifier.height(16.dp))
        Divider(Modifier.padding(vertical = 4.dp))
        SettingsItem("Logout", iconTint = Color.Red) { showLogoutDialog = true }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = {
                    AuthRepo.logout(context)
                    rootNavController.navigate("login") {
//                        popUpTo("adminHome") { inclusive = true }
                        popUpTo(0) { inclusive = true }

                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showEditProfileDialog) {
        // TODO: Implement Edit Profile Dialog (similar to your friend’s implementation)
    }

    if (showChangePasswordDialog) {
        // TODO: Implement Change Password Dialog
    }
}

@Composable
fun StatsCard(label: String, count: Int) {
    Card(
        Modifier.size(width = 130.dp, height = 100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE))
    ) {
        Column(
            Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(count.toString(), fontWeight = FontWeight.Bold, fontSize = 22.sp, color = Color(0xFF0277BD))
            Text(label, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun SettingsItem(
    title: String,
    iconTint: Color = Color.Black,
    onClick: () -> Unit
) {
    Row(
        Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.Text(title, fontSize = 16.sp, color = iconTint)
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(18.dp)
        )
    }
}

