package com.tanveer.lostandcampusapp.Admin.AdminScreens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanveer.lostandcampusapp.Admin.AdminModel.AdminViewModel

@Composable
fun ContributorPostsScreen(adminViewModel: AdminViewModel, regNo: String) {
    val posts = adminViewModel.getPostsByUserRegNo(regNo)

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Posts by User", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(posts) { post ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text(post.title, fontWeight = FontWeight.Bold)
                        Text("Category: ${post.category}", color = Color.Gray, fontSize = 13.sp)
                        Text("Posted on: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(post.timestamp))}", fontSize = 13.sp)
                        // add more attributes as needed
                    }
                }
            }
        }
    }
}
