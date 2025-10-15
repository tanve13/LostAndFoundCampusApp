package com.tanveer.lostandcampusapp.User.Screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.rememberDismissState
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
import androidx.navigation.NavHostController
import com.tanveer.lostandcampusapp.model.NotificationDataClass
import com.tanveer.lostandcampusapp.viewModel.UserViewModel


import kotlinx.coroutines.launch

//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun NotificationScreen(
//    userId: String,
//    navController : NavController,
//    viewModel: UserViewModel = viewModel()
//) {
//    val notifications by viewModel.notifications.collectAsState()
//
//    val scope = rememberCoroutineScope()
//    var notificationToDelete by remember { mutableStateOf<NotificationDataClass?>(null) }
//
//    LaunchedEffect(userId) {
//        viewModel.observeUserNotifications(userId)
//    }
//
//    Scaffold(
//        topBar = {
//            TopAppBar(
//                title = { Text("Notifications", fontWeight = FontWeight.Bold) }
//            )
//        }
//    ) { padding ->
//        if (notifications.isEmpty()) {
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding),
//                contentAlignment = Alignment.Center
//            ) {
//                Text("No notifications yet")
//            }
//        } else {
//            LazyColumn(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .padding(padding),
//                verticalArrangement = Arrangement.spacedBy(8.dp),
//                contentPadding = PaddingValues(12.dp)
//            ) {
//                items(notifications, key = { it.id }) { notification ->
//                    var showDialog by remember { mutableStateOf(false) }
//
//                    // Swipe to delete
//                    SwipeToDismissBox(
//                        onDismissed = {
//                            notificationToDelete = notification
//                            showDialog = true
//                        },
//                        onCancel = {
//                            showDialog = false
//                        }
//                    ){
//                        NotificationCard(notification = notification) { clickedNotif ->
//                            scope.launch {
//                                viewModel.markNotificationAsRead(clickedNotif.id)
//                            }
//                            clickedNotif.postId?.let {
//                                navController.navigate("postDetail/$it")
//                            }
//                        }
//                    }
//
//                    if (showDialog) {
//                        AlertDialog(
//                            onDismissRequest = { showDialog = false },
//                            confirmButton = {
//                                TextButton(onClick = {
//                                    scope.launch {
//                                        viewModel.deleteNotification(notification.id)
//                                    }
//                                    showDialog = false
//                                }) {
//                                    Text("Delete", color = Color.Red)
//                                }
//                            },
//                            dismissButton = {
//                                TextButton(onClick = { showDialog = false }) {
//                                    Text("Cancel")
//                                }
//                            },
//                            title = { Text("Delete Notification") },
//                            text = { Text("Are you sure you want to delete this notification?") }
//                        )
//                    }
//                }
//            }
//        }
//    }
//}
//@Composable
//fun NotificationCard(
//    notification: NotificationDataClass,
//    onClick: (NotificationDataClass) -> Unit
//) {
//    val backgroundColor =
//        if (notification.isRead) MaterialTheme.colorScheme.surfaceVariant
//        else MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
//
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .padding(vertical = 6.dp)
//            .clickable { onClick(notification) },
//        colors = CardDefaults.cardColors(containerColor = backgroundColor),
//        shape = MaterialTheme.shapes.medium,
//        border = CardDefaults.outlinedCardBorder()
//    ) {
//        Column(modifier = Modifier.padding(16.dp)) {
//            Text(
//                text = notification.title,
//                style = MaterialTheme.typography.titleMedium,
//                fontWeight = FontWeight.Bold
//            )
//            Spacer(modifier = Modifier.height(4.dp))
//            Text(
//                text = notification.message,
//                style = MaterialTheme.typography.bodyMedium
//            )
//            Spacer(modifier = Modifier.height(8.dp))
//            Row(
//                horizontalArrangement = Arrangement.SpaceBetween,
//                modifier = Modifier.fillMaxWidth()
//            ) {
//                Text(
//                    text = "Type: ${notification.type}",
//                    style = MaterialTheme.typography.labelSmall,
//                    color = MaterialTheme.colorScheme.outline
//                )
//                Text(
//                    text = android.text.format.DateFormat.format(
//                        "dd MMM, hh:mm a",
//                        notification.timestamp
//                    ).toString(),
//                    style = MaterialTheme.typography.labelSmall,
//                    color = MaterialTheme.colorScheme.outline
//                )
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun SwipeToDismissBox(
//    onDismissed: () -> Unit,
//    onCancel: () -> Unit,
//    content: @Composable () -> Unit
//) {
//    val state = rememberSwipeToDismissBoxState(
//        confirmValueChange = { value ->
//            if (value == SwipeToDismissBoxValue.EndToStart) {
//                onDismissed()
//                false
//            } else true
//        }
//    )
//
//    SwipeToDismissBox(
//        state = state,
//        enableDismissFromStartToEnd = false,
//        backgroundContent = {
//            Row(Modifier.fillMaxSize(), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
//                Text("Delete", color = MaterialTheme.colorScheme.error)
//                Spacer(Modifier.width(16.dp))
//            }
//        },
//        content = { content() }
//    )
//
//    // Reset on cancel: call this when dialog dismissed without delete
//    LaunchedEffect(state.currentValue) {
//        if (state.currentValue != SwipeToDismissBoxValue.Settled) {
//            onCancel()
//            state.reset()
//        }
//    }
//}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    userId: String,
    navController: NavController,
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
            CenterAlignedTopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold) }
            )
        }
    ) { paddingValues ->
        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No notifications yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(12.dp)
            ) {
                items(notifications, key = { it.id }) { notification ->
                    var showDialog by remember { mutableStateOf(false) }

                    SwipeToDismissBox(
                        onDismissed = {
                            notificationToDelete = notification
                            showDialog = true
                        },
                        onCancel = {
                            showDialog = false
                        }
                    ) {
                        NotificationCard(
                            notification = notification,
                            onClick = { clickedNotif ->
                                scope.launch {
                                    viewModel.markNotificationAsRead(clickedNotif.id)
                                }
                                clickedNotif.postId?.let {
                                    navController.navigate("postDetail/$it")
                                }
                            }
                        )
                    }

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
                                    Text("Delete", color = MaterialTheme.colorScheme.error)
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
fun NotificationCard(
    notification: NotificationDataClass,
    onClick: (NotificationDataClass) -> Unit
) {
    val backgroundColor =
        if (notification.isRead) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick(notification) },
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .padding(end = 12.dp)
                    .background(
                        color = if (notification.isRead) Color.Transparent else MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = notification.message,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = android.text.format.DateFormat.format(
                        "dd MMM, hh:mm a",
                        notification.timestamp
                    ).toString(),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SwipeToDismissBox(
    onDismissed: () -> Unit,
    onCancel: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberSwipeToDismissBoxState(
        confirmValueChange = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onDismissed()
                false
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
                    tint = MaterialTheme.colorScheme.error
                )
                Spacer(Modifier.width(12.dp))
                Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
            }
        },
        content = { content() }
    )

    LaunchedEffect(state.currentValue) {
        if (state.currentValue != SwipeToDismissBoxValue.Settled) {
            onCancel()
            state.reset()
        }
    }
}




