package com.tanveer.lostandcampusapp.data

import android.content.Context
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object AuthRepo {

    private val auth by lazy { FirebaseAuth.getInstance() }
    private val db by lazy { FirebaseFirestore.getInstance() }

    // --- SIGN UP: email+password, then save mapping regNo->email (+ name)
    fun signUpWithEmailPassword(
        name: String,
        email: String,
        regNo: String,
        password: String,
        context: Context,
        onSuccess: (String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val map = hashMapOf(
                    "name" to name,
                    "email" to email,
                    "regNo" to regNo,
                    "role" to "user"
                )
                // Document ID = regNo (easy lookup later)
                db.collection("users").document(regNo)
                    .set(map)
                    .addOnSuccessListener {
                        onSuccess(name, regNo)
                    }
                    .addOnFailureListener { e ->
                        onError("Failed to save user profile: ${e.message}")
                    }
            }
            .addOnFailureListener { e ->
                onError("Signup failed: ${e.message}")
            }
    }

    // --- LOGIN: regNo+password -> fetch email by regNo -> FirebaseAuth signIn
    fun loginWithRegNoPassword(
        regNo: String,
        password: String,
        context: Context,
        onSuccess: (name: String, regNo: String,role: String) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("users").document(regNo).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val email = doc.getString("email")
                    val name = doc.getString("name") ?: ""
                    val role = doc.getString("role") ?: "user"
                    if (!email.isNullOrBlank()) {
                        auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener {
                                onSuccess(name, regNo,role)
                            }
                            .addOnFailureListener { e ->
                                onError("Login failed: ${e.message}")
                            }
                    } else {
                        onError("Email not found for this reg. number")
                    }
                } else {
                    onError("Registration number not found")
                }
            }
            .addOnFailureListener { e ->
                onError("Lookup failed: ${e.message}")
            }
    }

    // --- FORGOT PASSWORD: regNo -> email lookup -> send reset email
    fun sendResetToRegNo(
        regNo: String,
        context: Context,
        onDone: (Boolean, String) -> Unit
    ) {
        db.collection("users").document(regNo).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val email = doc.getString("email")
                    if (!email.isNullOrBlank()) {
                        FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                            .addOnSuccessListener {
                                onDone(true, "Reset link sent to $email")
                            }
                            .addOnFailureListener { e ->
                                onDone(false, "Error: ${e.message}")
                            }
                    } else {
                        onDone(false, "Email not found for this reg. number")
                    }
                } else {
                    onDone(false, "Registration number not found")
                }
            }
            .addOnFailureListener { e ->
                onDone(false, "Lookup failed: ${e.message}")
            }
    }

    fun logout() {
        auth.signOut()
    }
}
