package com.tanveer.lostandcampusapp.Admin.AdminScreens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import kotlinx.coroutines.delay

@Composable
fun ContributorOverviewScreen(
    adminViewModel: AdminViewModel,
    onUserClick: (String) -> Unit
) {
    val contributors = remember { mutableStateOf<Map<String, Int>>(emptyMap()) }
    val isLoading = adminViewModel.isLoading

    LaunchedEffect(Unit) {
        adminViewModel.isLoading = true

        adminViewModel.fetchAllPosts()
        adminViewModel.fetchAllUsers()
        delay(500)
        val posts = adminViewModel.allPosts

        val countsByRegNo = posts.groupingBy { it.userRegNo }.eachCount()

        contributors.value = countsByRegNo.mapKeys { entry ->
            val user = adminViewModel.allUsers.find { it.regNo == entry.key }
            "${user?.name ?: "Unknown"} (${entry.key})"
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(
            "Top Contributors",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            fontSize = 22.sp
        )

        Spacer(Modifier.height(14.dp))
        if (isLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .height(60.dp)
                        .width(60.dp),
                    strokeWidth = 6.dp
                )
            }

        }else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                contributors.value.forEach { (name, count) ->

                    item {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onUserClick(
                                        name.substringAfter("(").dropLast(1)
                                    )
                                }, // Only regNo pass
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(6.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = Color.White
                            )
                        ) {

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {

                                // ——— ICON BOX ———
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(
                                            0xFF0288D1
                                        )
                                    ),
                                    modifier = Modifier.size(50.dp)
                                ) {
                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Assessment,
                                            contentDescription = null,
                                            tint = Color.White
                                        )
                                    }
                                }

                                Spacer(Modifier.width(16.dp))

                                // ——— USER DETAILS ———
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = name.substringBefore("(").trim(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Text(
                                        text = "Reg No: ${name.substringAfter("(").dropLast(1)}",
                                        fontSize = 13.sp,
                                        color = Color.Gray
                                    )
                                }

                                // ——— COUNTS BADGE ———
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color(
                                            0xFFE3F2FD
                                        )
                                    )
                                ) {
                                    Text(
                                        text = "$count Posts",
                                        modifier = Modifier.padding(
                                            horizontal = 12.dp,
                                            vertical = 6.dp
                                        ),
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFF0288D1),
                                        fontSize = 13.sp
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

