package com.tanveer.lostandcampusapp.User.Screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.auth.FirebaseAuth
import com.tanveer.lostandcampusapp.model.Post
import com.tanveer.lostandcampusapp.viewModel.UserViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(viewModel: UserViewModel, navController: NavController) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    val posts by viewModel.allPosts
    val context = LocalContext.current

    LaunchedEffect(Unit) { viewModel.loadAllPosts() }

    val filteredPosts = posts.filter { post ->
        val matchesSearch = searchQuery.isBlank() ||
                post.title.contains(searchQuery, ignoreCase = true) ||
                post.description.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "Lost" -> post.category.equals("Lost", ignoreCase = true)
            "Found" -> post.category.equals("Found", ignoreCase = true)
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {

        // 🔹 Header (fixed position)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color.Black, Color(0xFF333333))
                    )
                )
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            Column {
                Text(
                    text = "Hello, User 👋",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Find your lost and found items easily",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search lost or found items", color = Color.Gray) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                )
            }
        }

        // 🔹 Scrollable list only for content
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            color = Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {

                Spacer(modifier = Modifier.height(12.dp))

                // Filter Chips Row
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(listOf("All", "Lost", "Found")) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = {
                                Text(
                                    filter,
                                    color = if (selectedFilter == filter) Color.White else Color.Black
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                containerColor = if (selectedFilter == filter) Color.Black else Color.Transparent,
                                selectedContainerColor = Color.Black
                            ),
                            border = BorderStroke(1.dp, Color.Black)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Available Items",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 70.dp)
                ) {
                    if (filteredPosts.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No posts found", color = Color.Gray)
                            }
                        }
                    } else {
                        items(filteredPosts) { post ->
                            PostCard(post, navController, viewModel, context,onClick = {
                                navController.navigate("postDetails/${post.id}")
                            })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostCard(
    post: Post,
    navController: NavController,
    viewModel: UserViewModel,
    context: android.content.Context,
    onClick: () -> Unit = {}

) {
    var showClaimDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = post.imageUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(90.dp)
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(10.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    post.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(post.category, color = Color.Gray, fontSize = 12.sp)
                Text("📍 ${post.location}", color = Color.Gray, fontSize = 12.sp)
                Text("🕒 ${formatTimestamp(post.timestamp)}", color = Color.Gray, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(6.dp))

                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                if (post.userId != currentUserId) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = { showClaimDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.height(34.dp)
                        ) {
                            Text("Claim", fontSize = 12.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }

    if (showClaimDialog) {
        ClaimDialog(
            post = post,
            navController = navController,
            onDismiss = { showClaimDialog = false },
            viewModel = viewModel,
            context = context
        )
    }
}

@Composable
fun ClaimDialog(
    post: Post,
    navController: NavController,
    onDismiss: () -> Unit,
    viewModel: UserViewModel,
    context: android.content.Context
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("How would you like to proceed?", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        val chatId = listOf(currentUserId, post.userId).sorted().joinToString("_")
                        navController.navigate("chat/$chatId/${post.userName}")
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("\uD83D\uDCAC Chat with Owner", color = Color.White)
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (post.category.equals("Lost", true)) {
                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "\uD83D\uDCCE Upload proof clicked!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        border = BorderStroke(1.dp, Color.Black),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Upload Proof", color = Color.Black)
                    }
                }
            }
        }
    )
}

fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}


