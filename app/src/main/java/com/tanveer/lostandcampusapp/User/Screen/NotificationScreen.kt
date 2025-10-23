package com.tanveer.lostandcampusapp.User.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.tanveer.lostandcampusapp.model.NotificationDataClass
import com.tanveer.lostandcampusapp.viewModel.UserViewModel


import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    userId: String,
    navController: NavController,
    viewModel: UserViewModel = viewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val scope = rememberCoroutineScope()
    var multiSelectMode by remember { mutableStateOf(false) }
    var selectedNotifications by remember { mutableStateOf(setOf<String>()) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var notificationToDelete by remember { mutableStateOf<NotificationDataClass?>(null) }
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(userId) { viewModel.observeUserNotifications(userId) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) },
                actions = {
                    Box {
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More")
                        }
                        // 3-dot menu dropdown
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Select All") },
                                onClick = {
                                    multiSelectMode = true
                                    selectedNotifications = notifications.map { it.id }.toSet()
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (multiSelectMode && selectedNotifications.isNotEmpty()) {
                Surface(
                    shadowElevation = 8.dp,
                    color = Color.White
                ) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 24.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${selectedNotifications.size} selected", fontWeight = FontWeight.SemiBold)
                        IconButton(onClick = { showDeleteConfirm = true }) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Delete Selected",
                                tint = Color.Red,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) { Text("No notifications yet", color = MaterialTheme.colorScheme.outline) }
        } else {
            LazyColumn(
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    var showSingleDeleteDialog by remember { mutableStateOf(false) }
                    SwipeToDismissBox(
                        onDismiss = {
                            notificationToDelete = notification
                            showSingleDeleteDialog = true
                        }
                    ) {
                        NotificationCardLinkedInStyle(
                            notification = notification,
                            isSelected = selectedNotifications.contains(notification.id),
                            multiSelectMode = multiSelectMode,
                            onClick = {
                                if (multiSelectMode) {
                                    selectedNotifications =
                                        if (selectedNotifications.contains(notification.id))
                                            selectedNotifications - notification.id
                                        else
                                            selectedNotifications + notification.id
                                } else {
                                    // mark as read + nav to post
                                    scope.launch {
                                        viewModel.markNotificationAsRead(notification.id)
                                    }
                                    notification.postId?.let { postId ->
                                        navController.navigate("postDetail/$postId")
                                    }
                                }
                            }
                        )
                        if (showSingleDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = { showSingleDeleteDialog = false },
                                title = { Text("Delete this notification?") },
                                text = { Text("Are you sure you want to delete this notification?") },
                                confirmButton = {
                                    TextButton(onClick = {
                                        scope.launch {
                                            viewModel.deleteNotification(notification.id)
                                            showSingleDeleteDialog = false
                                        }
                                    }) { Text("Delete", color = Color.Red) }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showSingleDeleteDialog = false }) { Text("Cancel") }
                                }
                            )
                        }
                    }
                }
            }
        }
        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                title = { Text("Delete notifications?") },
                text = { Text("Are you sure you want to delete all selected notifications?") },
                confirmButton = {
                    TextButton(onClick = {
                        scope.launch {
                            selectedNotifications.forEach { viewModel.deleteNotification(it) }
                            selectedNotifications = emptySet()
                            multiSelectMode = false
                            showDeleteConfirm = false
                        }
                    }) { Text("Delete", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteConfirm = false }) { Text("Cancel") }
                }
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissBox(
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDismiss()
                false // Prevent state from auto-settling
            } else true
        }
    )

    SwipeToDismissBox(
        state = state,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            Row(
                Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = Color.Red
                )
                Spacer(Modifier.width(12.dp))
                Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        },
        content = { content() }
    )
}

@Composable
fun NotificationCardLinkedInStyle(
    notification: NotificationDataClass,
    isSelected: Boolean,
    multiSelectMode: Boolean,
    onClick: () -> Unit
) {
    val unreadBg = Color(0xFFE5F3FF)
    val readBg = Color.White
    val backgroundColor = if (notification.isRead) readBg else unreadBg

    val emoji = when (notification.type.lowercase()) {
        "found" -> "🔎"
        "lost" -> "❓"
        else -> ""
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            if (multiSelectMode) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onClick() },
                    modifier = Modifier.padding(end = 12.dp)
                )
            } else {
                Box(
                    Modifier
                        .size(10.dp)
                        .background(
                            color = if (notification.isRead) Color.Transparent else Color(0xFF1976D2),
                            shape = CircleShape
                        )
                        .padding(end = 12.dp)
                )
            }
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (emoji.isNotEmpty()) {
                        Text("$emoji ", fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = notification.title.removePrefix("🔎 ").removePrefix("❓ "),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(Modifier.height(3.dp))
                Text(
                    text = notification.message
                        .replace("🔎", "")
                        .replace("❓", ""),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.DarkGray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = android.text.format.DateFormat.format(
                        "dd MMM, hh:mm a",
                        notification.timestamp
                    ).toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Gray
                )
            }
            if (!multiSelectMode) {
                Spacer(Modifier.width(6.dp))
                Icon(Icons.Default.MoreVert, contentDescription = "More", modifier = Modifier.size(22.dp))
            }
        }
    }
}





