package com.tanveer.lostandcampusapp.data

import com.google.firebase.firestore.FirebaseFirestore

data class AppUser(
    val name: String = "",
    val email: String = "",
    val regNo: String = ""
)

object UserRepo {
    private val db by lazy { FirebaseFirestore.getInstance() }

    fun getUserByRegNo(
        regNo: String,
        onSuccess: (AppUser) -> Unit,
        onError: (String) -> Unit
    ) {
        db.collection("users").document(regNo).get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val user = AppUser(
                        name = doc.getString("name") ?: "",
                        email = doc.getString("email") ?: "",
                        regNo = doc.getString("regNo") ?: regNo
                    )
                    onSuccess(user)
                } else onError("User not found")
            }
            .addOnFailureListener { e -> onError(e.message ?: "Error") }
    }
}
