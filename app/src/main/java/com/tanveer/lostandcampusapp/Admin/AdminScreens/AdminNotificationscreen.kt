package com.tanveer.lostandcampusapp.Admin.AdminScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val notifications = adminViewModel.notifications
    val unreadCount = adminViewModel.unreadCount
    val isLoading = adminViewModel.isLoading

    // Tabs: All / Unread
    var selectedTab by remember { mutableStateOf(0) }

    val filteredList = if (selectedTab == 1) {
        notifications.filter { !it.read }
    } else notifications

    LaunchedEffect(Unit) {
        adminViewModel.fetchAdminStats()
        adminViewModel.startNotificationListener()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Notifications")
                        if (unreadCount > 0) {
                            Spacer(Modifier.width(8.dp))
                            Badge(containerColor = Color(0xFF0288D1)) {
                                Text("${unreadCount}", color = Color.White)
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (unreadCount > 0) {
                        IconButton(onClick = { adminViewModel.markAllNotificationsRead() }) {
                            Icon(Icons.Default.DoneAll, "Mark all read")
                        }
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val claimList = notifications.filter {
                it.type.contains("CLAIM", ignoreCase = true)
            }
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.White,
                contentColor = Color(0xFF0288D1)
            ) {

                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("All") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Unread")
                            if (unreadCount > 0) {
                                Spacer(Modifier.width(6.dp))
                                Badge(containerColor = Color(0xFF0288D1)) {
                                    Text("${unreadCount}", color = Color.White)
                                }
                            }
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Claims") }
                )
            }

            val finalList = when (selectedTab) {
                1 -> notifications.filter { !it.read }   // Unread
                2 -> claimList                            // Claims Only
                else -> notifications                      // All
            }
            Box(modifier = Modifier.fillMaxSize()) {

                when {
                    isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .size(70.dp),
                            strokeWidth = 6.dp
                        )
                    }

                    finalList.isEmpty() -> {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = when (selectedTab) {
                                    1 -> "No unread notifications 🎉"
                                    2 -> "No claim requests found"
                                    else -> "No notifications found"
                                },
                                color = Color.Gray,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(16.dp)
                        ) {
                            items(filteredList, key = { it.id }) { noti ->
                                AdminNotificationCard(
                                    n = noti,
                                    onClick = {
                                        adminViewModel.markNotificationRead(noti.id)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminNotificationCard(
    n: AdminNotificationDataClass,
    onClick: () -> Unit
) {

    val borderColor = if (!n.read) Color(0xFF0288D1) else Color(0xFFBDBDBD)
    val titleColor = if (!n.read) Color(0xFF0277BD) else Color.Black

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(18.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor)
    ) {

        Column(modifier = Modifier.padding(16.dp)) {

            // -------------------- Title Row --------------------
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = n.title,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 17.sp,
                    color = titleColor,
                    modifier = Modifier.weight(1f)
                )

                // unread dot
                if (!n.read) {
                    Surface(
                        modifier = Modifier.size(10.dp),
                        shape = CircleShape,
                        color = Color(0xFF03A9F4)
                    ) {}
                }
            }

            Spacer(Modifier.height(6.dp))

            Text(
                text = n.subtitle,
                color = Color(0xFF616161),
                fontSize = 14.sp
            )

            if (n.message.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                Text(
                    text = n.message,
                    fontSize = 13.sp,
                    color = Color.Black.copy(alpha = 0.8f)
                )
            }

            Spacer(Modifier.height(10.dp))

            Text(
                text = n.time,
                color = Color.Gray,
                fontSize = 12.sp
            )
        }
    }
}
