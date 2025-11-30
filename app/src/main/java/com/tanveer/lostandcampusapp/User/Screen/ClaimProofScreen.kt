package com.tanveer.lostandcampusapp.User.Screen

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.firestore.FirebaseFirestore
import com.tanveer.lostandcampusapp.data.FileUtils
import com.tanveer.lostandcampusapp.viewModel.UserViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClaimProofScreen(
    postId: String,
    viewModel: UserViewModel,
    navController: androidx.navigation.NavController
) {
    val context = LocalContext.current
    var description by remember { mutableStateOf("") }
    var selectedImageFile by remember { mutableStateOf<File?>(null) } // ← HERE

    // Launcher for picking image
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            val file = FileUtils.getFileFromUri(context, uri)
            selectedImageFile = file
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Submit Proof") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(onClick = { launcher.launch("image/*") }, modifier = Modifier.fillMaxWidth()) {
                Text(if (selectedImageFile != null) "Change Image" else "Upload Image")
            }

            selectedImageFile?.let {
                AsyncImage(model = it, contentDescription = "Selected", modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (description.isBlank() && selectedImageFile == null) {
                        Toast.makeText(context, "Add description or image", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    viewModel.submitProofFinal(
                        context = context,
                        postId = postId,
                        description = description,
                        imageFile = selectedImageFile,
                        onSuccess = {
                            Toast.makeText(context, "Proof submitted!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        },
                        onError = {
                            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        }
                    )
                },
                enabled = !viewModel.isSubmittingProof.value,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (viewModel.isSubmittingProof.value) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp))
                } else {
                    Text("Submit Proof")
                }
            }


        }
    }
}



