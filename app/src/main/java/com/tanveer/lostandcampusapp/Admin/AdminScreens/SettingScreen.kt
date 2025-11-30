package com.tanveer.lostandcampusapp.Admin.AdminScreens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReportProblem
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.tanveer.lostandcampusapp.Admin.AdminModel.AdminViewModel
import com.tanveer.lostandcampusapp.User.Screen.EditProfileDialog
import com.tanveer.lostandcampusapp.data.AuthRepo

@Composable
fun SettingScreen(
    adminViewModel: AdminViewModel = hiltViewModel(),
    adminRegNo: String,
    rootNavController: NavHostController,
    navController: NavHostController,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val sharedPref = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
    val regNo = sharedPref.getString("regNo", null) ?: return
    val imageUrl by adminViewModel.profileImageUrl.collectAsState()
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            adminViewModel.uploadToCloudinary(context, uri) { url ->
                adminViewModel.updateProfileImage(url, context) {
                    adminViewModel.loadUserProfile(context)
                    Toast.makeText(context, "Profile Updated!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        adminViewModel.fetchAdminProfile(adminRegNo)
        adminViewModel.fetchStats()
        adminViewModel.loadUserProfile(context)
    }

    Column(Modifier.fillMaxSize()) {

        // 🔥 SAME HEADER AS HOME SCREEN
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black, Color(0xFF303030))
                    )
                )
                .padding(vertical = 20.dp)
        ) {
            Column(
                modifier = Modifier.padding(start = 20.dp)
            ) {
                Text(
                    text = "Settings ⚙️",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Manage your profile & preferences",
                    fontSize = 14.sp,
                    color = Color.White.copy(0.8f)
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            // 🔥 HEADER WITH PROFILE INSIDE (NO CARD)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Black, Color(0xFF303030))
                        )
                    )
                    .padding(vertical = 24.dp, horizontal = 20.dp)
            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    // ---------------- PROFILE IMAGE ----------------
                    Box(
                        modifier = Modifier
                            .size(95.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(0.2f))
                            .clickable { launcher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (imageUrl.isNullOrEmpty()) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(65.dp),
                                tint = Color.White
                            )
                        } else {
                            AsyncImage(
                                model = imageUrl,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(95.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }

                    Spacer(Modifier.width(16.dp))

                    // ---------------- NAME + EMAIL ----------------
                    Column(
                        verticalArrangement = Arrangement.Center
                    ) {

                        Text(
                            text = adminViewModel.adminName.value,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(Modifier.height(4.dp))

                        Text(
                            text = adminViewModel.adminEmail.value,
                            fontSize = 14.sp,
                            color = Color.White.copy(0.8f)
                        )
                    }
                }
            }


            // ---------------- STATS CARDS (SAME AS HOME SCREEN) --------------------
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Total",
                    count = adminViewModel.totalPosts,
                    icon = Icons.Default.Assessment,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Deleted",
                    count = adminViewModel.deletedPosts,
                    icon = Icons.Default.ReportProblem,
                    modifier = Modifier.weight(1f)
                )
            }

            // ---------------- SETTINGS LIST --------------------
            SettingOptionItem("Edit Profile") {showEditProfileDialog = true  }
            SettingOptionItem("Change Password") { }
            SettingOptionItem("Contact Support") {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:your-support-email@example.com")
                    putExtra(Intent.EXTRA_SUBJECT, "Support Request")
                }
                context.startActivity(intent)
            }
            SettingOptionItem("Privacy Policy") {
                val i = Intent(Intent.ACTION_VIEW, Uri.parse("https://yourapp.com/privacy"))
                context.startActivity(i)
            }

            // RED LOGOUT
            SettingOptionItem("Logout", Color.Red) {
                showLogoutDialog = true
            }
            Text(
                text = "Version 1.0.0",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(bottom = 16.dp)
            )
        }
    }

    // LOGOUT CONFIRMATION DIALOG
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Do you really want to logout?") },
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
//    if (showEditProfileDialog) {
//        EditProfileDialog(
//            currentName = adminViewModel.adminName.value,
//            onDismiss = { showEditProfileDialog = false },
//            onSave = { newName ->
//                val regNo = adminViewModel.adminEmail.value
//                if (regNo.isNotEmpty()) {
//                    val db = FirebaseFirestore.getInstance()
//                    db.collection("users").document(regNo)
//                        .update("name", newName)
//                        .addOnSuccessListener {
//                            adminViewModel.fetchAdminProfile(newName, regNo)
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
}

@Composable
fun SettingOptionItem(
    title: String,
    tint: Color = Color.Black,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(1.dp, Color.Black, RoundedCornerShape(14.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(14.dp)
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 16.sp, color = tint, fontWeight = FontWeight.SemiBold)
            Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = Color.Gray
            )
        }
    }
}



