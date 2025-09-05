package com.tanveer.lostandcampusapp.User.Screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

data class Message(
    val senderId: String = "",
    val text: String = "",
    val timestamp: Long = 0
)

@Composable
fun ChatScreen(chatId: String) {
    val db = FirebaseFirestore.getInstance()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    var messages by remember { mutableStateOf<List<Message>>(emptyList()) }
    var newMessage by remember { mutableStateOf("") }

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
            }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 🔹 Messages list
        LazyColumn(
            modifier = Modifier.weight(1f).padding(8.dp)
        ) {
            items(messages) { msg ->
                MessageItem(msg = msg, isMine = msg.senderId == currentUserId)
            }
        }

        // 🔹 Text input and Send button
        Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            OutlinedTextField(
                value = newMessage,
                onValueChange = { newMessage = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") }
            )
            Button(
                onClick = {
                    if (newMessage.isNotBlank()) {
                        val message = mapOf(
                            "senderId" to currentUserId,
                            "text" to newMessage,
                            "timestamp" to System.currentTimeMillis()
                        )
                        db.collection("chats")
                            .document(chatId)
                            .collection("messages")
                            .add(message)
                            .addOnSuccessListener { newMessage = "" }
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text("Send")
            }
        }
    }
}

@Composable
fun MessageItem(msg: Message, isMine: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isMine) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            color = if (isMine) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary,
            shape = MaterialTheme.shapes.medium,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = msg.text,
                modifier = Modifier.padding(8.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}
