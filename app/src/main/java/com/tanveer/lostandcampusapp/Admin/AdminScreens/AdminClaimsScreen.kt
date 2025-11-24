package com.tanveer.lostandcampusapp.Admin.AdminScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import com.tanveer.lostandcampusapp.Admin.AdminModel.AdminViewModel
import com.tanveer.lostandcampusapp.model.ClaimRequest

@Composable
fun AdminClaimsScreen(adminViewModel: AdminViewModel = hiltViewModel()) {
    val claimsList by adminViewModel.claimRequests.collectAsState()
    val isLoading by adminViewModel.isClaimLoading.collectAsState()
    val swipeRefreshState = rememberSwipeRefreshState(isLoading)
    val (showConfirmDialog, setShowConfirmDialog) = remember { mutableStateOf(false) }
    var claimToActOn by remember { mutableStateOf<ClaimRequest?>(null) }
    var actionType by remember { mutableStateOf("") }
    var selectedClaim by remember { mutableStateOf<ClaimRequest?>(null) }
    var showDetailDialog by remember { mutableStateOf(false) }
    val filters = listOf("All", "Pending", "Approved", "Rejected")

    LaunchedEffect(Unit) {
        adminViewModel.claimFilter = "ALL"
        adminViewModel.fetchClaimRequests()
    }

    SwipeRefresh(
        state = swipeRefreshState,
        onRefresh = { adminViewModel.refreshClaimRequests() }
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp, horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                filters.forEach { filter ->
                    val isSelected = adminViewModel.claimFilter.equals(filter, ignoreCase = true)
                    val chipBg = if (isSelected) Color.Black else Color.White
                    val chipText = if (isSelected) Color.White else Color.Black
                    Button(
                        onClick = {
                            adminViewModel.claimFilter = filter.uppercase()
                            adminViewModel.fetchClaimRequests()   },
                        shape = RoundedCornerShape(22.dp),
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = chipBg),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 18.dp, vertical = 6.dp),
                        elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(2.dp)
                    ) {
                        Text(
                            text = filter,
                            color = chipText,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            LazyColumn(
                Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(claimsList, key = { it.id }) { claim ->
                    ClaimListItem(
                        claim = claim,
                        onClick = {
                            selectedClaim = claim
                            showDetailDialog = true
                        },
                        onApprove = {
                            claimToActOn = claim
                            actionType = "APPROVE"
                            setShowConfirmDialog(true)
                        },
                        onReject = {
                            claimToActOn = claim
                            actionType = "REJECT"
                            setShowConfirmDialog(true)
                        }
                    )
                }
            }
        }
    }

    if (showDetailDialog && selectedClaim != null) {
        ClaimDetailDialog(
            claim = selectedClaim!!,
            onDismiss = { showDetailDialog = false }
        )
    }

    if (showConfirmDialog && claimToActOn != null) {
        AlertDialog(
            onDismissRequest = { setShowConfirmDialog(false) },
            title = { Text("${actionType.capitalize()} Claim") },
            text = { Text("Are you sure you want to $actionType this claim?") },
            confirmButton = {
                TextButton(onClick = {
                    if (actionType == "APPROVE") {
                        adminViewModel.approveClaim(
                            claimId = claimToActOn!!.id,
                            postId = claimToActOn!!.postId,
                            onSuccess = {
                                setShowConfirmDialog(false)
                                adminViewModel.fetchClaimRequests()
                            },
                            onFailure = {
                                setShowConfirmDialog(false)
                            }
                        )
                    } else {
                        adminViewModel.rejectClaim(
                            claimId = claimToActOn!!.id,
                            onSuccess = {
                                setShowConfirmDialog(false)
                                adminViewModel.fetchClaimRequests()
                            },
                            onFailure = {
                                setShowConfirmDialog(false)
                            }
                        )
                    }
                }) {
                    Text(actionType.capitalize(), color = if (actionType == "APPROVE") Color.Green else Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { setShowConfirmDialog(false) }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ClaimListItem(
    claim: ClaimRequest,
    onClick: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF4F8FB))
    ) {
        Row(
            Modifier.padding(10.dp).height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Item image
            AsyncImage(
                model = claim.itemImageUrl,
                contentDescription = "Item Image",
                modifier = Modifier.size(70.dp).clip(RoundedCornerShape(10.dp)).background(Color(0xFFE0E0E0))
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(claim.itemTitle, fontWeight = FontWeight.Bold)
                Text("Claimed by: ${claim.userName}", style = MaterialTheme.typography.bodySmall)
                Text("Date: ${getFormattedDate(claim.claimTimestamp)}", style = MaterialTheme.typography.bodySmall)
                StatusLabel(status = claim.status)
            }
            if (claim.status == "PENDING") {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.End
                ) {
                    TextButton(onClick = onApprove) { Text("Approve", color = Color.Green) }
                    TextButton(onClick = onReject) { Text("Reject", color = Color.Red) }
                }
            }
        }
    }
}

@Composable
fun ClaimDetailDialog(claim: ClaimRequest, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(claim.itemTitle) },
        text = {
            Column {
                AsyncImage(
                    model = claim.itemImageUrl,
                    contentDescription = "Item Image",
                    modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.height(10.dp))
                Text("Claimed by: ${claim.userName}")
                Text("User email: ${claim.userEmail}")
                Text("Claim Description: ${claim.description}")
                if (claim.proofImageUrl != null)
                    AsyncImage(
                        model = claim.proofImageUrl,
                        contentDescription = "Proof Image",
                        modifier = Modifier.height(100.dp).clip(RoundedCornerShape(12.dp))
                    )
                Text("Status: ${claim.status}")
                Text("Claim Date/Time: ${getFormattedDate(claim.claimTimestamp)}")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) { Text("Close") }
        }
    )
}


