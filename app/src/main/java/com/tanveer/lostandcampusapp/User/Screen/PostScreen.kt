package com.tanveer.lostandcampusapp.User.Screen

import android.content.Context
import android.icu.util.Calendar
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.tanveer.lostandcampusapp.data.PostRepo
import com.tanveer.lostandcampusapp.viewModel.UserViewModel
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostScreen(navController: NavController, viewModel: UserViewModel = androidx.lifecycle.viewmodel.compose.viewModel()) {
    var selectedCategory by remember { mutableStateOf("Lost") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    val date = remember { Calendar.getInstance().time.toString() }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Image picker launcher
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        imageUri = uri
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
        .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = " Create a Post",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf("Lost", "Found").forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(category) }
                )
            }
        }
        if (imageUri != null) {
            Spacer(modifier = Modifier.height(16.dp))
            AsyncImage(
                model = imageUri,
                contentDescription = "Selected Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
        }
        Button(
            onClick = { launcher.launch("image/*") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Upload Image")
        }


        Spacer(modifier = Modifier.height(20.dp))
        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text(" Title") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text(" Description") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            minLines = 3
        )

        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text(" Location") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text(" Date") },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            enabled = false
        )

        Button(onClick = {
            imageUri?.let { uri ->
                val file = uriToFile(context, uri)
                isLoading = true
                CloudinaryHelper.uploadImage(file) { success, url ->
                    if (success && url != null) {
                        scope.launch {
                            try {
                                PostRepo.createPost(
                                    category = selectedCategory,
                                    title = title,
                                    description = description,
                                    location = location,
                                    imageUrl = url
                                )
                                isLoading = false
                                viewModel.loadAllPosts()
                                viewModel.loadMyPosts()
                                Toast.makeText(context, "Post submitted!", Toast.LENGTH_SHORT).show()
                                 navController.navigate("userHome"){
                                     popUpTo("post"){inclusive = true}
                                 }
                            }catch (e:Exception){
                                isLoading = false
                                Toast.makeText(context, " Error: ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }else{
                        isLoading = false
                        Toast.makeText(context, " Image upload failed!", Toast.LENGTH_SHORT).show()

                    }

                }
            }

        },
            enabled = !isLoading,
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(50.dp),
            shape = MaterialTheme.shapes.medium
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Submit Post")
            }
        }

    }
}

fun uriToFile(context: Context, uri: Uri): File {
    val inputStream = context.contentResolver.openInputStream(uri)
    val file = File(context.cacheDir, "temp_image.jpg")
    inputStream?.use { input ->
        file.outputStream().use { output ->
            input.copyTo(output)
        }
    }
    return file
}
