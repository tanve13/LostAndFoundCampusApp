package com.tanveer.lostandcampusapp.User.Screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tanveer.lostandcampusapp.R
import com.tanveer.lostandcampusapp.model.Post
import com.tanveer.lostandcampusapp.viewModel.UserViewModel

@Composable
fun HomeScreen(viewModel: UserViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }

    val posts = viewModel.allPosts.value

    // Posts load karte hi fetch ho jaye
    LaunchedEffect(Unit) {
        viewModel.loadAllPosts()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // 🔍 Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search lost or found items") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 🔘 Filter Chips - All, Lost, Found
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("All", "Lost", "Found").forEach { category ->
                AssistChip(
                    onClick = { selectedFilter = category },
                    label = { Text(category) },
                    colors = AssistChipDefaults.assistChipColors(
                        if (selectedFilter == category) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 🔄 Apply Search & Filter
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

        // 📝 Show Posts
        if (filteredPosts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text("No posts found")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(filteredPosts) { post ->
                    PostCard(post)
                }
            }
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column {
            // 📷 Image (abhi ke liye static image, bad me cloudinary url)
            Image(
                painter = painterResource(id = R.drawable.wallet),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.padding(8.dp)) {
                Text(post.title, style = MaterialTheme.typography.titleMedium)
                Text(post.description, style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("📍 ${post.location}  |  ${post.timestamp}")
                    Button(onClick = { /* Claim action later */ }) {
                        Text("Claim")
                    }
                }
            }
        }
    }
}
