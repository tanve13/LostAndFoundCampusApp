package com.tanveer.lostandcampusapp.Admin.AdminScreens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.tanveer.lostandcampusapp.data.FileUtils
import com.tanveer.lostandcampusapp.Admin.AdminModel.AdminViewModel
import com.tanveer.lostandcampusapp.data.AuthRepo
import com.tanveer.lostandcampusapp.viewModel.UserViewModel

@Composable
fun SettingScreen(
    adminViewModel: AdminViewModel = hiltViewModel(),
    adminRegNo: String,
    rootNavController: NavHostController,
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val user by adminViewModel.user.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    val regNo = sharedPref.getString("regNo", null) ?: return
    val docId = regNo
    val imageUrl by adminViewModel.profileImageUrl.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {

            adminViewModel.uploadToCloudinary(
                context = context,
                uri = uri,
                onUploaded = { url ->
                    adminViewModel.updateProfileImage(url,context){
                        Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
                        adminViewModel.loadUserProfile(context)
                    }
                }
            )
        }
    }



    LaunchedEffect(Unit) {
        adminViewModel.fetchAdminProfile(adminRegNo)
        adminViewModel.fetchStats()
        adminViewModel.loadUserProfile(context)
    }

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {

        // 🔥 Profile Image Section
        Box(
            Modifier
                .size(110.dp)
                .align(Alignment.CenterHorizontally)
                .clip(RoundedCornerShape(55.dp))
                .background(Color(0xFFE0F7FA))
                .clickable { launcher.launch("image/*") },
            contentAlignment = Alignment.Center
        ) {
            if (imageUrl.isNullOrEmpty()) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Default Profile",
                    tint = Color.Black,   // 🔥 BLACK ICON
                    modifier = Modifier.size(70.dp)
                )
            } else {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = "Profile Pic",
                    modifier = Modifier.fillMaxSize()
                        .clip(CircleShape)
                )
            }
            Box(
                Modifier
                    .align(Alignment.BottomEnd)
                    .size(28.dp)
                    .background(Color.Black, RoundedCornerShape(14.dp))
                    .clickable { launcher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Pic",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        Text(
            adminViewModel.adminName.value,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            adminViewModel.adminEmail.value,
            fontSize = 14.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

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

        Text("Profile Settings", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

        SettingsItem("Edit Profile") { }
        Divider(Modifier.padding(vertical = 4.dp))
        SettingsItem("Change Password") { }

        Spacer(Modifier.height(24.dp))
        Divider()

        Text("App & Support", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)

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
            val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            context.startActivity(i)
        }

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
                        popUpTo(0) { inclusive = true }
                    }
                }) { Text("Yes") }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
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

