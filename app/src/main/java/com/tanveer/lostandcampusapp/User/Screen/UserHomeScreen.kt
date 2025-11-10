package com.tanveer.lostandcampusapp.User.Screen

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
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

    var sheetVisible by remember { mutableStateOf(false) }
    var selectedPostForAction by remember { mutableStateOf<Post?>(null) }

    Scaffold(
        containerColor = Color(0xFFF0F4FF),
        topBar = {}
    ) { innerPadding ->
        Column(
            Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFDEE8FF), Color(0xFFF4F7FF))
                    )
                )
                .padding(innerPadding)
        ) {
            // Gradient header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(Color(0xFFCCE5FF), Color(0xFFD5FFD9)),
                            start = Offset(0f, 0f),
                            end = Offset(0f, 300f)
                        ),
                        shape = RoundedCornerShape(bottomEnd = 36.dp, bottomStart = 36.dp)
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Hello, User 👋",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp,
                                color = Color(0xFF28406B)
                            )
                        )
                        Text(
                            text = "Find your lost & found items",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF6B8AA6),
                                fontSize = 15.sp
                            )
                        )
                        Spacer(modifier = Modifier.width(20.dp))

                        // Search bar, direct below header

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search lost or found items") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier
                                .fillMaxWidth(),
//                                .height(56.dp)
//                                .shadow(6.dp, RoundedCornerShape(14.dp)),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF28406B),
                                unfocusedBorderColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                    }
                    Surface(
                        shape = CircleShape,
                        color = Color.White,
                        shadowElevation = 8.dp,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "profile",
                            tint = Color.Black,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }

            }

            // Body content on white big rounded surface
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp),
                color = Color.White,
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
                tonalElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp)
                ) {

                    // Filters row
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
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
                                    selectedContainerColor = Color.Black,
                                    containerColor = Color(0xFFF2F2F2)
                                ),
                                shape = RoundedCornerShape(18.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Recent posts",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = Color(0xFF28406B),
                                fontWeight = FontWeight.Bold
                            )
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Text(
                            text = "See all",
                            color = Color.Gray,
                            modifier = Modifier.clickable { /* see all posts */ }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (filteredPosts.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No posts found", color = Color.Gray)
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(18.dp),
                            contentPadding = PaddingValues(bottom = 40.dp)
                        ) {
                            items(filteredPosts) { post ->
                                // Big card with gradient style
                                Card(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                        .shadow(8.dp, RoundedCornerShape(24.dp)),
                                    shape = RoundedCornerShape(24.dp),
                                    elevation = CardDefaults.cardElevation(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    ),
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                brush = Brush.verticalGradient(
                                                    colors = listOf(
                                                        Color(0xFFE5F6FF),
                                                        Color(0xFFF5F9FF)
                                                    )
                                                ),
                                                shape = RoundedCornerShape(20.dp)
                                            )
                                            .padding(24.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        AsyncImage(
                                            model = post.imageUrl,
                                            contentDescription = post.title,
                                            modifier = Modifier
                                                .size(110.dp)
                                                .clip(RoundedCornerShape(16.dp)),
                                            contentScale = ContentScale.Crop
                                        )
                                        Spacer(modifier = Modifier.width(20.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    post.title,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 17.sp,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis,
                                                    color = Color(0xFF28406B)
                                                )
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(7.dp),
                                                    color = if (post.category == "Lost") Color(
                                                        0xFFFFE5E5
                                                    ) else Color(0xFFE5FFE5)
                                                ) {
                                                    Text(
                                                        post.category,
                                                        color = if (post.category == "Lost") Color(
                                                            0xFFD74848
                                                        ) else Color(0xFF48D78E),
                                                        fontSize = 12.sp,
                                                        modifier = Modifier.padding(
                                                            horizontal = 8.dp,
                                                            vertical = 4.dp
                                                        )
                                                    )
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Text(
                                                post.description,
                                                maxLines = 2,
                                                color = Color(0xFF6B8AA6),
                                                overflow = TextOverflow.Ellipsis,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))

                                            Text(
                                                "📍 ${post.location}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF28406B)
                                            )
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(
                                                text = "🕒 ${formatTimestamp(post.timestamp)}",
                                                color = Color.Gray,
                                                style = MaterialTheme.typography.bodySmall
                                            )

                                            val currentUserId =
                                                FirebaseAuth.getInstance().currentUser?.uid
                                                    ?: ""
                                            if (post.userId != currentUserId) {
                                                Button(
                                                    onClick = {
                                                        selectedPostForAction = post
                                                        sheetVisible = true
                                                    },
                                                    colors = ButtonDefaults.buttonColors(
                                                        containerColor = Color.Black
                                                    ),
                                                    shape = RoundedCornerShape(14.dp),
                                                    contentPadding = PaddingValues(
                                                        horizontal = 12.dp,
                                                        vertical = 8.dp
                                                    )
                                                ) {
                                                    Text("Claim", color = Color.White)
                                                }
                                            } else {
                                                Surface(
                                                    shape = RoundedCornerShape(14.dp),
                                                    color = Color(0xFFEAF2FF)
                                                ) {
                                                    Text(
                                                        "Your post",
                                                        modifier = Modifier.padding(
                                                            horizontal = 8.dp,
                                                            vertical = 6.dp
                                                        ),
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color(0xFF28406B)
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
            }

            if (sheetVisible && selectedPostForAction != null) {
                ModalBottomSheet(
                    onDismissRequest = { sheetVisible = false },
                    containerColor = Color.White,
                    shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Claim Options", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val currentUserId =
                                    FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                val postOwnerId = selectedPostForAction!!.userId
                                val safeName =
                                    selectedPostForAction!!.userName?.ifBlank { "User" } ?: "User"
                                val chatId =
                                    listOf(currentUserId, postOwnerId).sorted().joinToString("_")
                                sheetVisible = false
                                navController.navigate("chat/$chatId/$safeName")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Black),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("💬 Chat with owner", color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val postId = selectedPostForAction!!.id
                                sheetVisible = false
                                navController.navigate("claimProof/$postId")
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                            border = BorderStroke(1.dp, Color.LightGray),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("📎 Provide claim proof", color = Color.Black)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                val currentUserId =
                                    FirebaseAuth.getInstance().currentUser?.uid ?: ""
                                val postOwnerId = selectedPostForAction!!.userId
                                viewModel.claimPost(
                                    postId = selectedPostForAction!!.id,
                                    claimerId = currentUserId,
                                    postOwnerId = postOwnerId,
                                    onSuccess = {
                                        Toast.makeText(
                                            context,
                                            "Claim requested — owner will be notified",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        sheetVisible = false
                                    },
                                    onError = { msg ->
                                        Toast.makeText(
                                            context,
                                            msg,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF111111)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Submit Claim Request", color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { sheetVisible = false }) {
                            Text("Cancel")
                        }
                    }
                }
            }
        }
    }
}

fun formatTimestamp(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        ""
    }
}
