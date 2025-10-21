package com.tanveer.lostandcampusapp.Admin.AdminScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.tanveer.lostandcampusapp.Admin.AdminModel.AdminViewModel
import com.tanveer.lostandcampusapp.model.Post

@Composable
fun AllPostsScreen(adminViewModel: AdminViewModel = hiltViewModel()) {
    val posts = adminViewModel.filteredPosts
    val (showConfirmDialog, setShowConfirmDialog) = remember { mutableStateOf(false) }
    var (postToDelete, setPostToDelete) = remember { mutableStateOf<Post?>(null) }
    var selectedPost by remember { mutableStateOf<Post?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        adminViewModel.fetchAllPosts()
    }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(12.dp)) {
        // Filter buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterButton("All", adminViewModel.filter == "ALL") { adminViewModel.filter = "ALL" }
            FilterButton("Pending", adminViewModel.filter == "PENDING") {
                adminViewModel.filter = "PENDING"
            }
            FilterButton("Resolved", adminViewModel.filter == "RESOLVED") {
                adminViewModel.filter = "RESOLVED"
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        // Posts List
        LazyColumn(
            Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(posts, key = { it.id }) { post ->
                PostListItem(
                    post = post,
                    onClick = {
                        selectedPost = post
                        showDetailDialog = true
                    },
                    onDelete = { postToDelete = post }
                )
            }
        }
    }

    // Details Dialog/BottomSheet
    if (showDetailDialog && selectedPost != null) {
        PostDetailDialog(post = selectedPost!!, onDismiss = { showDetailDialog = false })
    }



    if (showConfirmDialog && postToDelete != null) {
        AlertDialog(
            onDismissRequest = { setShowConfirmDialog(false) },
            title = { Text("Delete Post") },
            text = { Text("Are you sure you want to delete this post?") },
            confirmButton = {
                TextButton(onClick = {
                    adminViewModel.deletePost(postToDelete!!.id,
                        onSuccess = {
                            setShowConfirmDialog(false)
                            adminViewModel.fetchAllPosts() // refresh list
                        },
                        onFailure = { msg ->
                            setShowConfirmDialog(false)
                            // Show error toast/snackbar etc.
                        })
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { setShowConfirmDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun FilterButton(text: String, selected: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primary else Color(0xFFECECEC)
        ),
        shape = RoundedCornerShape(24.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 4.dp)
    ) {
        Text(text, color = if (selected) Color.White else Color.Black)
    }
}

@Composable
fun PostListItem(post: Post, onClick: () -> Unit, onDelete: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F8FB))
    ) {
        Row(
            Modifier
                .padding(10.dp)
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Image
            AsyncImage(
                model = post.imageUrl,
                contentDescription = "Item Image",
                modifier = Modifier
                    .size(70.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFFE0E0E0))
            )

            Spacer(modifier = Modifier.width(10.dp))

            // Info
            Column(Modifier.weight(1f)) {
                Text(post.title, fontWeight = FontWeight.Bold)
                Text("By: ${post.userName}", style = MaterialTheme.typography.bodySmall)
                Text(
                    "Date: ${getFormattedDate(post.timestamp)}",
                    style = MaterialTheme.typography.bodySmall
                )
                StatusLabel(status = if(post.claimedBy.isNullOrEmpty()) "Pending" else "Resolved")
            }

            // Delete button
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, "Delete", tint = Color.Red)
            }
        }
    }
}

@Composable
fun StatusLabel(status: String) {
    // nice material badge
    val color = when (status) {
        "Pending" -> Color(0xFFFFF59D)
//        "In Progress" -> Color(0xFFFFCC80)
        "Resolved" -> Color(0xFFC8E6C9)
        else -> Color.LightGray
    }
    Box(
        Modifier
            .padding(top = 4.dp)
            .background(color, shape = RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(status, fontWeight = FontWeight.SemiBold, color = Color.Black, fontSize = 13.sp)
    }
}

@Composable
fun PostDetailDialog(post: Post, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(post.title) },
        text = {
            Column {
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "Item Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.height(10.dp))
                Text("Description: ${post.description}")
                Text("Location: ${post.location}")
                Text("Reported by: ${post.userName}")
                Text("Status: ${post.status}")
                Text("Date/Time: ${getFormattedDate(post.timestamp)}")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}

fun getFormattedDate(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("dd MMM yyyy - hh:mm a", java.util.Locale.getDefault())
    return sdf.format(java.util.Date(timestamp))
}

