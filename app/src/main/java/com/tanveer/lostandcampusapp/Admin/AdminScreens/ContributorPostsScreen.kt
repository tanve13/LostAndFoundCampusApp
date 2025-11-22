package com.tanveer.lostandcampusapp.Admin.AdminScreens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.tanveer.lostandcampusapp.Admin.AdminModel.AdminViewModel
import com.tanveer.lostandcampusapp.model.Post

@Composable
fun ContributorPostsScreen(
    adminViewModel: AdminViewModel,
    regNo: String
) {
    val isLoading = adminViewModel.isLoading
    val posts = adminViewModel.getPostsByUserRegNo(regNo)
    val userName = posts.firstOrNull()?.userName ?: "User"

    LaunchedEffect(Unit) {
        adminViewModel.refreshAllPosts() // Data load
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            text = "$userName's Posts",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )


        Spacer(Modifier.height(12.dp))

        // ---------- Show Loader ----------
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(50.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        } else {
            // ---------- Show Posts List ----------
            LazyColumn(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items(posts) { post ->
                    PostItemCard(post)
                }
            }
        }
    }
}
@Composable
fun PostItemCard(post: Post) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color.Black),   // 🔥 Black border
        colors = CardDefaults.cardColors(
            containerColor = Color.White            // 🔥 White background
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.Top
        ) {

            // --------- LEFT SIDE IMAGE ---------
            AsyncImage(
                model = post.imageUrl,
                contentDescription = "Post Image",
                modifier = Modifier
                    .width(120.dp)
                    .height(120.dp)
                    .padding(end = 12.dp)
                    .clip(RoundedCornerShape(10.dp)),
                contentScale = ContentScale.Crop
            )

            // --------- RIGHT SIDE TEXT DETAILS ---------
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = post.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Category: ${post.category}",
                    color = Color.Gray,
                    fontSize = 13.sp
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    text = "Posted on: ${
                        java.text.SimpleDateFormat("dd MMM yyyy")
                            .format(java.util.Date(post.timestamp))
                    }",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text = post.description,
                    fontSize = 14.sp,
                    color = Color.Black,
                    maxLines = 4
                )
            }
        }
    }
}


