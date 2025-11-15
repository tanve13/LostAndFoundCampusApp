package com.tanveer.lostandcampusapp.Admin.AdminScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanveer.lostandcampusapp.Admin.AdminModel.AdminViewModel

@Composable
fun ContributorOverviewScreen(
    adminViewModel: AdminViewModel,
    onUserClick: (String) -> Unit
) {
    val contributors = remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    LaunchedEffect(Unit) {
        adminViewModel.fetchAllPosts()
        // Group posts by user and count
        val posts = adminViewModel.allPosts
        val countsByRegNo = posts.groupingBy { it.userRegNo }.eachCount()
        contributors.value = countsByRegNo.mapKeys { entry ->
            val user = adminViewModel.allUsers.find { it.regNo == entry.key }
            "${user?.name ?: entry.key} (${entry.key})"
        }

    }
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Top Contributors", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(12.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            contributors.value.forEach { (name, count) ->
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().clickable { onUserClick(name) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE1F5FE))
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Assessment, contentDescription = null, tint = Color(0xFF0288D1))
                            Spacer(Modifier.width(10.dp))
                            Column {
                                Text(name, fontWeight = FontWeight.Medium)
                                Text("$count posts", fontSize = 14.sp, color = Color.Gray)
                            }
                        }
                    }
                }
            }
        }
    }
}
