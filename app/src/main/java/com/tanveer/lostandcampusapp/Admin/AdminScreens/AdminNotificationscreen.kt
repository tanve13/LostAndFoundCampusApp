package com.tanveer.lostandcampusapp.Admin.AdminScreens


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanveer.lostandcampusapp.Admin.AdminModel.AdminViewModel
import com.tanveer.lostandcampusapp.model.AdminNotificationDataClass

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNotificationScreen(
    adminViewModel: AdminViewModel,
    onBack: () -> Unit
) {
    val notifications = adminViewModel.adminNotifications
    val isLoading = adminViewModel.isLoading

    LaunchedEffect(Unit) {
        adminViewModel.fetchAdminNotifications()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Notifications, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.Center),
                        strokeWidth = 6.dp
                    )
                }

                notifications.isEmpty() -> {
                    Text(
                        text = "No Notifications Found",
                        modifier = Modifier.align(Alignment.Center),
                        color = Color.Gray
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(notifications) { noti ->
                            AdminNotificationCard(noti)
                        }
                    }
                }
            }
        }
    }
}
@Composable
fun AdminNotificationCard(n: AdminNotificationDataClass) {

    val borderColor = if (n.read) Color.LightGray else Color(0xFF0288D1)

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(5.dp),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            // Title
            Text(
                text = n.title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(Modifier.height(6.dp))

            // Subtitle
            Text(
                text = n.subtitle,
                color = Color.DarkGray,
                fontSize = 14.sp
            )

            Spacer(Modifier.height(8.dp))

            // Message
            if (n.message.isNotEmpty()) {
                Text(
                    text = n.message,
                    fontSize = 13.sp,
                    color = Color.Black.copy(alpha = 0.75f)
                )
                Spacer(Modifier.height(6.dp))
            }

            // Date/Time
            Text(
                text = n.time,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}

