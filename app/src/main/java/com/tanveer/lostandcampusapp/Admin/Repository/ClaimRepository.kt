package com.tanveer.lostandcampusapp.Admin.Repository

import com.tanveer.lostandcampusapp.model.ClaimRequest

interface ClaimRepository  {
    suspend fun getAllClaimRequests(): List<ClaimRequest>
    suspend fun updateClaimStatus(claimId: String, status: String)
}