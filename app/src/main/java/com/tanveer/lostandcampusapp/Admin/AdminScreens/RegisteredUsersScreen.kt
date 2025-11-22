package com.tanveer.lostandcampusapp.Admin.AdminScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
 import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.tanveer.lostandcampusapp.Admin.AdminModel.AdminViewModel

@Composable
fun RegisteredUsersScreen(
    adminViewModel: AdminViewModel,
    onUserClick: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        adminViewModel.fetchAllUsers()
    }

    var searchText by remember { mutableStateOf("") }
    val users = adminViewModel.allUsers

    val filteredUsers = users.filter {
        it.name.contains(searchText.orEmpty(), ignoreCase = true) ||
                it.regNo.contains(searchText.orEmpty(), ignoreCase = true)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        // ---------- HEADER ----------
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF1976D2)
            ),
            shape = RoundedCornerShape(18.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(
                Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    "Registered Users",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    "Total Users: ${users.size}",
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }

        Spacer(Modifier.height(14.dp))


        // ---------- SEARCH BAR ----------
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            placeholder = { Text("Search by name or reg no...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            }
        )

        Spacer(Modifier.height(12.dp))


        // ---------- LOADING ----------
        if (users.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    strokeWidth = 4.dp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            return
        }

        // ---------- USER LIST ----------
        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {

            items(filteredUsers) { user ->

                val randomColor =
                    listOf(
                        Color(0xFFBBDEFB),
                        Color(0xFFC8E6C9),
                        Color(0xFFFFECB3),
                        Color(0xFFFFCDD2)
                    ).random()

                AnimatedVisibility(visible = true) {

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onUserClick(user.regNo) },
                        shape = RoundedCornerShape(18.dp),
                        elevation = CardDefaults.cardElevation(6.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        )
                    ) {

                        Row(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            // ---------- USER AVATAR ----------
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(randomColor, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    user.name.first().uppercase(),
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0D47A1)
                                )
                            }

                            Spacer(Modifier.width(15.dp))


                            // ---------- USER INFO ----------
                            Column(Modifier.weight(1f)) {
                                Text(
                                    user.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 17.sp
                                )
                                Text(
                                    "Reg No: ${user.regNo}",
                                    color = Color.Gray,
                                    fontSize = 14.sp
                                )
                            }

                            Icon(
                                Icons.Default.ChevronRight,
                                contentDescription = "Go",
                                tint = Color(0xFF0D47A1),
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}



