package com.tanveer.lostandcampusapp

import android.widget.Toast
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.tanveer.lostandcampusapp.data.AuthRepo
import com.tanveer.lostandcampusapp.data.DataStoreManager
import com.tanveer.lostandcampusapp.viewModel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(
    onLoginSuccess: (Any?) -> Unit,
    onBackToSignUp: () -> Unit,userViewModel: UserViewModel

) {
    var regNo by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()

    LaunchedEffect(Unit) {
        val (savedName, savedReg) = DataStoreManager.getUserData(context)
        if (savedName.isNotEmpty() && savedReg.isNotEmpty()) {
            userViewModel.setUserData(savedName, savedReg)
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Icon(
            imageVector = Icons.Default.LockOpen,
            contentDescription = "Sign Up Icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .size(72.dp)
                .padding(bottom = 16.dp)
        )
        Text("Login", style = MaterialTheme.typography.headlineSmall)

        OutlinedTextField(
            value = regNo,
            onValueChange = { regNo = it },
            label = { Text("Registration Number") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val icon = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(imageVector = icon, contentDescription = if (isPasswordVisible) "Hide password" else "Show password")
                }
            },            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),

        )

        Button(
            onClick = {
                when {
                    regNo.isBlank() -> {
                        Toast.makeText(
                            context,
                            "Please enter Registration Number",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    regNo.length < 8 -> {
                        Toast.makeText(context, "Invalid Registration Number", Toast.LENGTH_SHORT)
                            .show()
                    }

                    password.isBlank() -> {
                        Toast.makeText(context, "Please enter Password", Toast.LENGTH_SHORT).show()
                    }

                    password.length < 6 -> {
                        Toast.makeText(
                            context,
                            "Password must be at least 6 characters",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    else -> {
                        AuthRepo.loginWithRegNoPassword(
                            regNo = regNo,
                            password = password,
                            context = context,
                            onSuccess = { name, regNo,role ->
                                onLoginSuccess(role)
                                Toast.makeText(context, "Login Successful!", Toast.LENGTH_SHORT).show()
                                userViewModel.setUserData(name, regNo)
                                userViewModel.saveUserToken()

                                CoroutineScope(Dispatchers.IO).launch {
                                    DataStoreManager.saveUserData(context, name, regNo)
                                }

                            },
                            onError = { msg ->
                                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                            }
                        )

                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp)
        ) {
            Text("Login")
        }
        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            onClick = {
                if (regNo.isBlank()) {
                    Toast.makeText(context, "Enter registration number first", Toast.LENGTH_SHORT).show()
                } else {
                    AuthRepo.sendResetToRegNo(
                        regNo = regNo,
                        context = context
                    ) { ok, msg ->
                        Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                    }
                }
            }
        ) {
            Text("Forgot Password?")
        }


        TextButton(onClick = { onBackToSignUp() }) {
            Text("Don’t have an account? Sign Up")
        }
    }
}
