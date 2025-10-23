package com.tanveer.lostandcampusapp.Admin.Repository

import com.google.firebase.firestore.FirebaseFirestore
import com.tanveer.lostandcampusapp.model.ClaimRequest
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ClaimRepositoryImpl @Inject constructor() : ClaimRepository {

    private val db = FirebaseFirestore.getInstance()

    override suspend fun getAllClaimRequests(): List<ClaimRequest> {
        return try {
            db.collection("claims")
                .get()
                .await()
                .documents
                .mapNotNull { doc ->
                    doc.toObject(ClaimRequest::class.java)?.copy(id = doc.id)
                }
        } catch (e: Exception) {
            emptyList() // error handle karo
        }
    }

    override suspend fun updateClaimStatus(claimId: String, status: String) {
        try {
            db.collection("claims")
                .document(claimId)
                .update("status", status)
                .await()
        } catch (e: Exception) {
            println("Error updating claim $claimId: ${e.message}")
        }
    }
}
