package com.tanveer.lostandcampusapp.Admin.AdminScreens

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.compose.ui.viewinterop.AndroidView
import com.tanveer.lostandcampusapp.Admin.AdminModel.AdminViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AdminHomeScreen(navController: NavController,
                    adminViewModel: AdminViewModel = hiltViewModel(),
                    adminRegNo: String, onUsersClick: () -> Unit,
                    onTopContributorClick: () -> Unit
) {
    LaunchedEffect(Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            adminViewModel.fetchAdminProfile(adminRegNo)
        }
        adminViewModel.fetchAdminStats()
        adminViewModel.startNotificationListener()

    }
    val totalPosts = adminViewModel.totalPosts
    val lostPosts = adminViewModel.lostPosts
    val foundPosts = adminViewModel.foundPosts
    val pendingClaims = adminViewModel.pendingClaims
    val resolvedCases = adminViewModel.resolvedCases
    val totalUsers = adminViewModel.totalUsers
    val lostCount = adminViewModel.lostCount
    val foundCount = adminViewModel.foundCount
    val newUsers = adminViewModel.newUsersThisWeek
    val mostActive = adminViewModel.mostActiveUser
    val mostCommon = adminViewModel.mostCommonCategory
    Column(modifier = Modifier.fillMaxSize()) {

        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black, Color(0xFF333333))
                    )
                )
                .padding(vertical = 18.dp)
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                // LEFT SIDE TEXT
                Column {
                    Text(
                        "Hello, ${adminViewModel.adminName.value.ifEmpty { "Admin" }} 👋",
                        color = Color.White,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "Welcome back to your dashboard",
                        color = Color.White.copy(alpha = 0.9f),
                        fontSize = 14.sp
                    )
                }

                // RIGHT SIDE NOTIFICATION ICON
                val unread = adminViewModel.unreadCount
                IconButton(onClick = { navController.navigate("admin_notifications") }) {
                    Box {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )

                        if (unread > 0) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .align(Alignment.TopEnd)
                                    .offset(x = 4.dp, y = (-4).dp)
                                    .background(Color.Red, RoundedCornerShape(50)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    unread.coerceAtMost(99).toString(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {

            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    "Total",
                    totalPosts,
                    Icons.Default.Assessment,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    "Lost",
                    lostPosts,
                    Icons.Default.ReportProblem,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    "Found",
                    foundPosts,
                    Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(12.dp))

            Row(
                Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    "Pending",
                    pendingClaims,
                    Icons.Default.HourglassEmpty,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    "Resolved",
                    resolvedCases,
                    Icons.Default.DoneAll,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(24.dp))

            val contributorCardGradient = Brush.horizontalGradient(
                listOf(Color(0xFFE0E0E0), Color(0xFFFCFCFC)) // Silver-ish to white
            )


            Card(
                Modifier
                    .fillMaxWidth()
                    .clickable { onUsersClick() }
                    .border(1.dp, Color.Black, RoundedCornerShape(16.dp)),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("App Users", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Total Registered: $totalUsers")
                        Text("New This Week: $newUsers", color = Color.Gray)
                    }
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Go",
                        tint = Color.Black
                    )
                }
            }

            // -- Most Common Item Category --
            Card(
                Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                    .clickable { /* Add action if needed */ },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Most Common Category", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            mostCommon?.first ?: "N/A",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text("${mostCommon?.second ?: "--"} posts")
                    }
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.Black
                    )
                }
            }

            // -- Most Active Contributor --
            Card(
                Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color.Black, RoundedCornerShape(16.dp))
                    .clickable { onTopContributorClick() },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Top Contributor", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            mostActive?.first ?: "N/A",
                            style = MaterialTheme.typography.titleLarge,
                            color = Color(0xFF1976D2)
                        )
                        Text("${mostActive?.second ?: "--"} posts this week")
                    }
                    Icon(
                        Icons.Default.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.Black
                    )
                }
            }

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                var showPieDialog by remember { mutableStateOf(false) }
                LostFoundPieChart(
                    lostCount,
                    foundCount,
                    modifier = Modifier.weight(1f).clickable { showPieDialog = true }
                )
                Spacer(Modifier.width(8.dp))
                if (showPieDialog) {
                    AlertDialog(
                        onDismissRequest = { showPieDialog = false },
                        // Use a larger PieChart here, and display clear numbers:
                        text = {
                            Column {
                                Text("Lost vs Found & Claims")
                                PieChartLarge(
                                    lostCount = adminViewModel.lostCount,
                                    foundCount = adminViewModel.foundCount,
                                    resolvedCount = adminViewModel.resolvedCases,
                                    pendingCount = adminViewModel.pendingClaims
                                )

                            }
                        },
                        confirmButton = {
                            Button(onClick = {
                                showPieDialog = false
                            }) { Text("Close") }
                        }
                    )
                }
                Card(
                    Modifier
                        .weight(2f)
                        .padding(end = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8E9)),
                    shape = RoundedCornerShape(18.dp)
                ) {
                    Column(Modifier.padding(14.dp)) {
                        Text("Lost vs Found Ratio", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Lost: $lostCount    Found: $foundCount", fontSize = 14.sp)
                        val percent =
                            if (lostCount + foundCount != 0) (foundCount * 100) / (lostCount + foundCount) else 0
                        Text("Recovery Rate: $percent%", color = Color(0xFF0296A5))
                    }
                }
            }

            Text(
                text = "Weekly Activity",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            ActivityLineChart(
                data = adminViewModel.weeklyPosts,
                labels = adminViewModel.weekLabels
            )
        }
    }
    }




@Composable
fun StatCard(
    title: String,
    count: Int,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    // Light colors for icons
    val iconColor = when (title) {
        "Total" -> Color(0xFF9575CD)   // Light Purple
        "Lost" -> Color(0xFFFF8A65)    // Light Orange
        "Found" -> Color(0xFF4DB6AC)   // Light Teal
        "Pending" -> Color(0xFFFFD54F) // Light Yellow
        "Resolved" -> Color(0xFF81C784) // Light Green
        else -> Color(0xFF90CAF9)
    }

    Card(
        modifier = modifier
            .size(110.dp)
            .padding(4.dp)
            .border(1.dp, Color.Black, RoundedCornerShape(15.dp)),   // ⭐ NEW BLACK BORDER
        colors = CardDefaults.cardColors(
            containerColor = Color.White  // ⭐ WHITE BACKGROUND
        ),
        shape = RoundedCornerShape(15.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,   // ⭐ NEW LIGHT COLOR FOR ICON
                modifier = Modifier.size(28.dp)
            )
            Text(
                title,
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                count.toString(),
                color = Color.Black,
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PieChartLarge(
    lostCount: Int,
    foundCount: Int,
    resolvedCount: Int,
    pendingCount: Int
) {
    var showDialog by remember { mutableStateOf(false) }

    val total = lostCount + foundCount + resolvedCount + pendingCount

    val lostColor = Color(0xFFFFA9A9)
    val foundColor = Color(0xFF9BD7F2)
    val resolvedColor = Color(0xFFAAD5A0)
    val pendingColor = Color(0xFFFFE7A1)

    // Animation from 0 → 1
    val anim = remember { androidx.compose.animation.core.Animatable(0f) }
    LaunchedEffect(Unit) {
        anim.animateTo(1f, tween(1200))
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(230.dp)
                .padding(12.dp)
                .clickable { showDialog = true },      // ⭐ CLICK OPENS DIALOG
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.matchParentSize()) {

                var start = -90f

                fun arc(value: Int, color: Color) {
                    if (total == 0) return
                    val sweep = (value.toFloat() / total) * 360f * anim.value
                    drawArc(color, start, sweep, true)
                    start += sweep
                }

                arc(lostCount, lostColor)
                arc(foundCount, foundColor)
                arc(resolvedCount, resolvedColor)
                arc(pendingCount, pendingColor)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Lost & Found Stats", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("Total: $total", fontSize = 14.sp, color = Color.DarkGray)
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("● Lost", color = lostColor)
            Text("● Found", color = foundColor)
            Text("● Resolved", color = resolvedColor)
            Text("● Pending", color = pendingColor)
        }
    }

    if (showDialog) {
        LargePieDetailsDialog(
            lostCount = lostCount,
            foundCount = foundCount,
            resolvedCount = resolvedCount,
            pendingCount = pendingCount,
            onDismiss = { showDialog = false }
        )
    }
}
@Composable
fun LargePieDetailsDialog(
    lostCount: Int,
    foundCount: Int,
    resolvedCount: Int,
    pendingCount: Int,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),  // ⭐ BIG DIALOG WIDTH
        title = {
            Text(
                "Detailed Statistics",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Lost Items: $lostCount", fontSize = 18.sp)
                Text("Found Items: $foundCount", fontSize = 18.sp)
                Text("Resolved: $resolvedCount", fontSize = 18.sp)
                Text("Pending: $pendingCount", fontSize = 18.sp)
                Spacer(Modifier.height(12.dp))

                // Percentage
                val total = lostCount + foundCount + resolvedCount + pendingCount
                if (total > 0) {
                    Text(
                        "Found Percentage: ${(foundCount * 100 / total)}%",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", fontSize = 18.sp)
            }
        }
    )
}

@Composable
fun LostFoundPieChart(lostCount: Int, foundCount: Int, modifier: Modifier = Modifier) {
    val total = lostCount + foundCount
    val lostRatio = if (total == 0) 0f else lostCount.toFloat() / total
    val foundRatio = if (total == 0) 0f else foundCount.toFloat() / total

    val lostColor = Color(0xFFE57373)   // soft red
    val foundColor = Color(0xFF64B5F6)  // soft blue

    Box(
        modifier
            .size(150.dp)   // ⭐ Bigger Pie Chart
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val sweepLost = 360f * lostRatio
            val sweepFound = 360f * foundRatio

            drawArc(
                color = lostColor,
                startAngle = -90f,
                sweepAngle = sweepLost,
                useCenter = true
            )
            drawArc(
                color = foundColor,
                startAngle = -90f + sweepLost,
                sweepAngle = sweepFound,
                useCenter = true
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("L/F", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text(
                "${if (total == 0) 0 else (foundCount * 100 / total)}%",
                fontSize = 14.sp,
                color = foundColor
            )
        }
    }
}


@Composable
fun ActivityLineChart(data: List<Int>, labels: List<String>) {
    AndroidView(
        factory = { context ->
            LineChart(context).apply {
                // Create entries
                val entries = data.mapIndexed { index, value ->
                    Entry(index.toFloat(), value.toFloat())
                }

                // Create dataset
                val dataSet = LineDataSet(entries, "Posts").apply {
                    color = android.graphics.Color.BLUE
                    valueTextColor = android.graphics.Color.BLACK
                    lineWidth = 2f
                    circleRadius = 4f
                    setCircleColor(android.graphics.Color.BLUE)
                    mode = LineDataSet.Mode.CUBIC_BEZIER
                    setDrawValues(true)
                }

                // Set data
                this.data = LineData(dataSet)

                // Configure description
                description = Description().apply {
                    text = "Weekly Posts Activity"
                    textSize = 12f
                }

                // Configure axes
                axisRight.isEnabled = false

                // Configure X-axis
                xAxis.apply {
                    position = XAxis.XAxisPosition.BOTTOM
                    valueFormatter = IndexAxisValueFormatter(labels)
                    granularity = 1f
                    setDrawGridLines(false)
                }

                // Configure Y-axis
                axisLeft.apply {
                    axisMinimum = 0f
                    granularity = 1f
                }

                // Animate the chart
                animateXY(1000, 1000)

                // Refresh the chart
                invalidate()
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}
