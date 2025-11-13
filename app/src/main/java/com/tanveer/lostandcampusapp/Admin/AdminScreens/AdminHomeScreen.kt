package com.tanveer.lostandcampusapp.Admin.AdminScreens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AdminHomeScreen(
    adminViewModel: AdminViewModel = hiltViewModel(),
    adminRegNo: String,
) {
    LaunchedEffect(Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
        if (currentUserId != null) {
            adminViewModel.fetchAdminProfile(adminRegNo)
        }
        adminViewModel.fetchAdminStats()
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
    val skyBlue = Color(0xFFE3F2FD)
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
                    MaterialTheme.colorScheme.primary,
                    Icons.Default.Assessment,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    "Lost",
                    lostPosts,
                    Color.Red,
                    Icons.Default.ReportProblem,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    "Found",
                    foundPosts,
                    Color.Green,
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
                    Color(0xFFFF9800),
                    Icons.Default.HourglassEmpty,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    "Resolved",
                    resolvedCases,
                    Color(0xFF4CAF50),
                    Icons.Default.DoneAll,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(24.dp))
            // -- Users --
            Card(
                Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = skyBlue)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("App Users", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text("Total Registered: $totalUsers")
                    Text("New This Week: $newUsers", color = Color(0xFF1976D2))
                }
            }
            // -- Most Common Item Category --
            Card(
                Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = skyBlue)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Most Common Category", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        mostCommon?.first ?: "N/A",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text("${mostCommon?.second ?: "--"} posts")
                }
            }
            // -- Most Active Contributor --
            Card(
                Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = skyBlue)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Text("Top Contributor", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        mostActive?.first ?: "N/A",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color(0xFF1976D2)
                    )
                    Text("${mostActive?.second ?: "--"} posts this week")
                }
            }
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                LostFoundPieChart(lostCount, foundCount, Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
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
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
//            .height(110.dp)
            .size(110.dp) .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f)),
//        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp) // soft shadow
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = count.toString(),
                    color = color,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Circle background for icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(color.copy(alpha = 0.15f), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = color,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}


@Composable
fun LostFoundPieChart(lostCount: Int, foundCount: Int, modifier: Modifier = Modifier) {
    val total = lostCount + foundCount
    val lostRatio = if (total == 0) 0f else lostCount.toFloat() / total
    val foundRatio = if (total == 0) 0f else foundCount.toFloat() / total

    val pastelOrange = Color(0xFFFFCC80)
    val pastelBlue = Color(0xFFB3E5FC)

    Box(
        modifier
            .size(118.dp)
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val sweepLost = 360f * lostRatio
            val sweepFound = 360f * foundRatio

            // Lost slice
            drawArc(
                color = pastelOrange, startAngle = -90f, sweepAngle = sweepLost,
                useCenter = true
            )
            // Found slice
            drawArc(
                color = pastelBlue, startAngle = -90f + sweepLost, sweepAngle = sweepFound,
                useCenter = true
            )
        }
        // Center label (optional)
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("L/F", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(
                "${if (total == 0) 0 else (foundCount * 100 / total)}%",
                fontSize = 16.sp,
                color = pastelBlue
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