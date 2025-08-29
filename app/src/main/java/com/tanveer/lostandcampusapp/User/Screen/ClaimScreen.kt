package com.tanveer.lostandcampusapp.User.Screen

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.tanveer.lostandcampusapp.model.Post
import com.tanveer.lostandcampusapp.viewModel.UserViewModel
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import com.tanveer.lostandcampusapp.data.DataStoreManager

@Composable
fun ClaimScreen(post: Post, viewModel: UserViewModel, navController: NavController,
                userViewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        val (savedName, savedRegNo) = DataStoreManager.getUserData(context)
        userViewModel.setUserData(savedName, savedRegNo)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Claim Item", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(12.dp))

        AsyncImage(
            model = post.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp),
            contentScale = ContentScale.Crop
        )

        Spacer(Modifier.height(12.dp))
        Text("Title: ${post.title}", style = MaterialTheme.typography.titleMedium)
        Text("Description: ${post.description}")
        Text("Location: ${post.location}")
        Text("Posted by: ${post.userName} (${post.userRegNo})",
            style = MaterialTheme.typography.bodySmall)


        Spacer(Modifier.height(20.dp))

        Button(
            onClick = {
                val currentUserId = viewModel.regNo.value // ya FirebaseAuth.getInstance().currentUser?.uid!!
                viewModel.claimPost(
                    postId = post.id,
                    claimerId = currentUserId,
                    onSuccess = {
                        Toast.makeText(context, "Claimed successfully!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    },
                    onError = {
                        Toast.makeText(context, "Error: $it", Toast.LENGTH_SHORT).show()
                    }
                )
            },
            enabled = post.claimedBy == null
        ) {
            Text(if (post.claimedBy == null) "Claim Now" else "Already Claimed")
        }
    }
}
