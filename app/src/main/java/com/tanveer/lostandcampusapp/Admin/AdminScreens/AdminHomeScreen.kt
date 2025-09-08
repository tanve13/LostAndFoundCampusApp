package com.tanveer.lostandcampusapp.Admin.AdminScreens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineDataSet
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.ui.viewinterop.AndroidView
import com.github.mikephil.charting.data.LineData

@Composable
fun AdminHomeScreen() {

    // TODO: Replace static values with ViewModel/Firebase data
    val totalPosts = 120
    val lostPosts = 70
    val foundPosts = 50
    val pendingClaims = 12
    val resolvedCases = 35

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

        // ---- Stats Row ----
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard("Total", totalPosts, MaterialTheme.colorScheme.primary)
            StatCard("Lost", lostPosts, Color.Red)
            StatCard("Found", foundPosts, Color.Green)
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard("Pending", pendingClaims, Color(0xFFFF9800))
            StatCard("Resolved", resolvedCases, Color(0xFF4CAF50))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // ---- Graph Section ----
        Text(
            text = "Weekly Activity",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        ActivityLineChart(
            data = listOf(5, 8, 6, 12, 9, 14, 10), // sample weekly data
            labels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
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
                val entries = data.mapIndexed { index, value ->
                    DropBoxManager.Entry(
                        index.toFloat().toString(),
                        value.toFloat().toLong()
                    )
                }
                val dataSet = LineDataSet(entries, "Posts").apply {
                    color = android.graphics.Color.BLUE
                    valueTextColor = android.graphics.Color.BLACK
                    lineWidth = 2f
                    circleRadius = 4f
                    setCircleColor(android.graphics.Color.BLUE)
                }

                this.data = LineData(dataSet)
                this.description = EventLogTags.Description().apply { text = "" }
                this.axisRight.isEnabled = false
                this.xAxis.granularity = 1f
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
    )
}
