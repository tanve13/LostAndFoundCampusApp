package com.tanveer.lostandcampusapp.User.Screen

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.tanveer.lostandcampusapp.model.Post
import com.tanveer.lostandcampusapp.viewModel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemsDetailsScreen(
    postId: String,
    viewModel: UserViewModel,
    navController: androidx.navigation.NavController
) {
    val context = LocalContext.current

    // Try to get the post from viewModel. If your viewModel has a dedicated
    // method getPostById, use it. Otherwise we derive from allPosts.
    val posts by viewModel.allPosts
    // safe lookup
    val post = remember(posts, postId) { posts.find { it.id == postId } }

    // Loading / not found UI
    if (post == null) {
        // If posts may take time to load, show a loading or not found message
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Post not found or still loading...", color = Color.Gray)
        }
        return
    }

    // Main content
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Item details") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        // share post link / text
                        val shareText = buildShareText(post)
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share post"))
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Image
            AsyncImage(
                model = post.imageUrl,
                contentDescription = post.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                // Title + status chip
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.title.ifBlank { "No title" },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )

                    // Status badge (claimed / available)
                    val statusText = when {
                        !post.claimedBy.isNullOrBlank() -> "Claimed"
                        post.status.isNotBlank() -> post.status
                        else -> "Available"
                    }
                    Surface(
                        tonalElevation = 2.dp,
                        shape = RoundedCornerShape(8.dp),
                        color = if (statusText == "Claimed") Color(0xFFFFEDEB) else Color(0xFFEFF7EE)
                    ) {
                        Text(
                            text = statusText,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            color = if (statusText == "Claimed") Color(0xFFD74848) else Color(0xFF288A3F)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Category and posted by block
                Text(text = "Category: ${post.category.ifBlank { "N/A" }}", color = Color.Gray)
                Spacer(Modifier.height(6.dp))
                Text(text = "Posted by: ${post.userName ?: "User"} (${post.userRegNo ?: "-"})", color = Color.Gray)
                Spacer(Modifier.height(8.dp))

                // Location & timestamp
                Text(text = "Location: ${post.location.ifBlank { "Not specified" }}", color = Color.Gray)
                Spacer(Modifier.height(4.dp))
                Text(text = "Posted: ${formatTimestamp(post.timestamp)}", color = Color.Gray)

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(text = "Description", fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = post.description.ifBlank { "No description" }, color = Color.DarkGray)

                Spacer(modifier = Modifier.height(16.dp))

                // ClaimedBy info if present
                if (!post.claimedBy.isNullOrBlank()) {
                    Text("Claimed by: ${post.claimedBy}", color = Color.Gray)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

                if (post.userId != currentUserId) { // ONLY show if not own post
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Chat button
                        Button(
                            onClick = {
                                val chatId = listOf(currentUserId, post.userId).sorted().joinToString("_")
                                navController.navigate("chat/$chatId/${post.userName}")
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black)
                        ) {
                            Text("Chat", color = Color.White)
                        }

                        if (post.category.equals("Lost", true)) {
                            OutlinedButton(
                                onClick = { navController.navigate("claimProof/${post.id}") },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Provide Proof")
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    viewModel.submitClaimRequest(
                                        post = post,
                                        onSuccess = { Toast.makeText(context, "Claim request sent to admin", Toast.LENGTH_SHORT).show() },
                                        onError = { Toast.makeText(context, it, Toast.LENGTH_SHORT).show() }
                                    )
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Claim")
                            }
                        }
                    }
                }


                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

private fun buildShareText(post: Post): String {
    val sb = StringBuilder()
    sb.append("${post.title}\n")
    sb.append("${post.description}\n")
    sb.append("Location: ${post.location}\n")
    sb.append("Posted by: ${post.userName ?: "User"}\n")
    sb.append("Posted: ${SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(post.timestamp))}\n")
    return sb.toString()
}


