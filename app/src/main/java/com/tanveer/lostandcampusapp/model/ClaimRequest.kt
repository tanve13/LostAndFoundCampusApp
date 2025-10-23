package com.tanveer.lostandcampusapp.model

data class ClaimRequest(val id: String,
                        val itemTitle: String,
                        val itemImageUrl: String?,
                        val userName: String,
                        val userEmail: String,
                        val description: String,
                        val proofImageUrl: String?,
                        val claimTimestamp: Long,
                        val status: String // "PENDING", "APPROVED", "REJECTED"
                         )
