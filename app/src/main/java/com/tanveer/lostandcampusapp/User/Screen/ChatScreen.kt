package com.tanveer.lostandcampusapp.User.Screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

data class Message(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    postOwnerName: String,
    navController: NavController? = null
) {
    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var newMessage by remember { mutableStateOf("") }

    // 🔄 Listen for message updates
    LaunchedEffect(chatId) {
        db.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatScreen", "Listen failed: ${error.message}")
                    return@addSnapshotListener
                }
                val msgs = snapshot?.documents?.mapNotNull { it.toObject(Message::class.java) } ?: emptyList()
                messages = msgs

                // Auto-scroll to bottom on new message
                coroutineScope.launch {
                    listState.animateScrollToItem(msgs.lastIndex)
                }
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = postOwnerName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    navController?.let {
                        IconButton(onClick = { it.popBackStack() }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                }
            )
        },
        containerColor = Color(0xFFF6F6F6)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 🗨️ Messages
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                state = listState
            ) {
                items(messages) { msg ->
                    MessageItem(
                        msg = msg,
                        isMine = msg.senderId == currentUserId
                    )
                }
            }

            // ✏️ Message Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newMessage,
                    onValueChange = { newMessage = it },
                    modifier = Modifier
                        .weight(1f)
                        .clip(MaterialTheme.shapes.large),
                    placeholder = { Text("Type a message...") },
                    singleLine = true
                )

                Button(
                    onClick = {
                        if (newMessage.isNotBlank()) {
                            val message = mapOf(
                                "senderId" to currentUserId,
                                "text" to newMessage.trim(),
                                "timestamp" to System.currentTimeMillis()
                            )
                            db.collection("chats")
                                .document(chatId)
                                .collection("messages")
                                .add(message)
                                .addOnSuccessListener { newMessage = "" }
                        }
                    },
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .height(56.dp)
                ) {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
fun MessageItem(msg: Message, isMine: Boolean) {
    val time = remember(msg.timestamp) {
        SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(msg.timestamp))
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Column(
            horizontalAlignment = if (isMine) Alignment.End else Alignment.Start
        ) {
            Surface(
                color = if (isMine) Color(0xFF4F8EF7) else Color(0xFFE5E5EA),
                shape = MaterialTheme.shapes.large,
                shadowElevation = 2.dp,
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .padding(horizontal = 4.dp)
            ) {
                Text(
                    text = msg.text,
                    modifier = Modifier.padding(12.dp),
                    color = if (isMine) Color.White else Color.Black,
                    fontSize = 16.sp
                )
            }

            Text(
                text = time,
                fontSize = 11.sp,
                color = Color.Gray,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
    }
}
