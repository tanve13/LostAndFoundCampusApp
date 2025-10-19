package com.tanveer.lostandcampusapp.Admin.AdminScreens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.compose.ui.viewinterop.AndroidView
import com.tanveer.lostandcampusapp.Admin.AdminModel.AdminViewModel
import dagger.hilt.android.lifecycle.HiltViewModel

@Composable
fun AdminHomeScreen(
    adminViewModel: AdminViewModel = AdminViewModel()
) {
    LaunchedEffect(Unit) {
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Admin Dashboard",
            style = MaterialTheme.typography.titleLarge.copy(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
            StatCard("Total", totalPosts, MaterialTheme.colorScheme.primary)
            StatCard("Lost", lostPosts, Color.Red)
            StatCard("Found", foundPosts, Color.Green)
        }
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), Arrangement.SpaceEvenly) {
            StatCard("Pending", pendingClaims, Color(0xFFFF9800))
            StatCard("Resolved", resolvedCases, Color(0xFF4CAF50))
        }
        Spacer(Modifier.height(24.dp))
        // -- Users --
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("App Users", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                Text("Total Registered: $totalUsers")
                Text("New This Week: $newUsers", color = Color(0xFF1976D2))
            }
        }
        // -- Posts Lost/Found Ratio --
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Lost vs Found Ratio", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))
                // Mini pie chart representation (text only), Compose donut charts available via 3rd-party
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        Modifier
                            .size(36.dp)
                            .padding(end = 8.dp)
                            .background(Color(0xFFd32f2f), shape = CircleShape)
                    ) // Lost color
                    Text("Lost: $lostCount", color = Color(0xFFd32f2f))
                    Spacer(Modifier.width(12.dp))
                    Box(
                        Modifier
                            .size(36.dp)
                            .padding(end = 8.dp)
                            .background(Color(0xFF388e3c), shape = CircleShape)
                    ) // Found color
                    Text("Found: $foundCount", color = Color(0xFF388e3c))
                }
                val percent = if (lostCount + foundCount != 0) (foundCount * 100) / (lostCount + foundCount) else 0
                Spacer(Modifier.height(4.dp))
                Text("Recovery %: $percent%")
            }
        }
        // -- Most Common Item Category --
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
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
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp)) {
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


@Composable
fun StatCard(title: String, count: Int, color: Color) {
    Card(
        modifier = Modifier
            .size(110.dp)
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = 0.1f))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = count.toString(),
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp,
                color = color
            )
            Text(
                text = title,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface
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