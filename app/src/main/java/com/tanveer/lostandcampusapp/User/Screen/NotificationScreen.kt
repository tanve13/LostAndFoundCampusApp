package com.tanveer.lostandcampusapp.User.Screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.tanveer.lostandcampusapp.model.NotificationDataClass
import com.tanveer.lostandcampusapp.viewModel.UserViewModel
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberDismissState
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.DismissDirection
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    userId: String,
    viewModel: UserViewModel = viewModel()
) {
    val notifications by viewModel.notifications.collectAsState()

    val scope = rememberCoroutineScope()
    var notificationToDelete by remember { mutableStateOf<NotificationDataClass?>(null) }

    LaunchedEffect(userId) {
        viewModel.observeUserNotifications(userId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) }
            )
        }
    ) { padding ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("No notifications yet")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    var showDialog by remember { mutableStateOf(false) }

                    // Swipe to delete
                    SwipeToDismissBox(
                        onDismissed = {
                            notificationToDelete = notification
                            showDialog = true
                        },
                        content = {
                            NotificationCard(notification = notification)
                        }
                    )

                    if (showDialog) {
                        AlertDialog(
                            onDismissRequest = { showDialog = false },
                            confirmButton = {
                                TextButton(onClick = {
                                    scope.launch {
                                        viewModel.deleteNotification(notification.id)
                                    }
                                    showDialog = false
                                }) {
                                    Text("Delete", color = Color.Red)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDialog = false }) {
                                    Text("Cancel")
                                }
                            },
                            title = { Text("Delete Notification") },
                            text = { Text("Are you sure you want to delete this notification?") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationCard(notification: NotificationDataClass) {
    val backgroundColor =
        if (notification.isRead) MaterialTheme.colorScheme.surface
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Type: ${notification.type}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeToDismissBox(
    onDismissed: () -> Unit,
    content: @Composable () -> Unit
) {
    val dismissState = rememberDismissState()

    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
        onDismissed()
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(DismissDirection.EndToStart),
        background = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Text("Delete", color = Color.Red)
            }
        },
        dismissContent = {
            content()
        }
    )
}


