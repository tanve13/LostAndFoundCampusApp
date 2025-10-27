package com.tanveer.lostandcampusapp.User.Screen

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tanveer.lostandcampusapp.viewModel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMyPostScreen(viewModel: UserViewModel) {
    val context = LocalContext.current
    val posts by viewModel.myPosts
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadMyPosts()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("My Posts", style = MaterialTheme.typography.headlineSmall) }
            )
        }
    ) { innerPadding ->
        if (posts.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.PostAdd,
                    contentDescription = "No Posts",
                    modifier = Modifier.size(72.dp),
                    tint = Color.LightGray
                )
                Text("You haven't created any posts yet", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                items(posts) { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .shadow(3.dp, shape = RoundedCornerShape(18.dp)),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9)),
                    ) {
                        Column(
                            Modifier.padding(14.dp)
                        ) {
                            if (!post.imageUrl.isNullOrEmpty()) {
                                AsyncImage(
                                    model = post.imageUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(170.dp)
                                        .clip(RoundedCornerShape(14.dp)),
                                    contentScale = ContentScale.Crop
                                )
                                Spacer(Modifier.height(10.dp))
                            }
                            Text(post.title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(Modifier.height(4.dp))
                            Text(post.description, style = MaterialTheme.typography.bodyMedium)
                            Spacer(Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("📍 ${post.location}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                                Spacer(modifier = Modifier.width(14.dp))
                                Text("🕒 ${formatTimestamp(post.timestamp)}", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(onClick = {
                                    Toast.makeText(context, "Update feature coming soon", Toast.LENGTH_SHORT).show()
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Update", tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(Modifier.width(6.dp))
                                IconButton(onClick = { showDeleteDialog = true }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
                                }
                                if (showDeleteDialog) {
                                    AlertDialog(
                                        onDismissRequest = { showDeleteDialog = false },
                                        title = { Text("Delete Post?") },
                                        text = { Text("Are you sure you want to delete this post? This action cannot be undone.") },
                                        confirmButton = {
                                            TextButton(onClick = {
                                                showDeleteDialog = false
                                                CoroutineScope(Dispatchers.IO).launch {
                                                    viewModel.deletePost(post.id)
                                                    CoroutineScope(Dispatchers.Main).launch {
                                                        Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
                                                    }
                                                }
                                            }) {
                                                Text("Delete", color = Color.Red)
                                            }
                                        },
                                        dismissButton = {
                                            TextButton(onClick = { showDeleteDialog = false }) {
                                                Text("Cancel")
                                            }
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
}


