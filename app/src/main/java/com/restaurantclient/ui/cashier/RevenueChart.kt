package com.restaurantclient.ui.cashier

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.ColorUtils

@Composable
fun RevenueChart(
    dailyRevenue: Map<String, Double>,
    lineColor: Color = Color(0xFFDC143C), // Itadaki Crimson
    modifier: Modifier = Modifier
) {
    if (dailyRevenue.isEmpty()) return

    val textMeasurer = rememberTextMeasurer()
    val sortedData = remember(dailyRevenue) {
        dailyRevenue.entries.sortedBy { it.key }.takeLast(7)
    }
    
    val values = sortedData.map { it.value.toFloat() }
    val labels = sortedData.map { it.key.takeLast(5) } // e.g. "01-18"
    
    val maxVal = values.maxOrNull() ?: 0f
    // Ensure we have some range even if max is 0
    val effectiveMax = if (maxVal == 0f) 100f else maxVal * 1.2f 
    val minVal = 0f 
    val range = effectiveMax - minVal

    Canvas(modifier = modifier.fillMaxSize().padding(16.dp)) {
        val width = size.width
        val height = size.height
        
        // Increased padding for axes
        val startPadding = 40.dp.toPx() 
        val bottomPadding = 30.dp.toPx()
        val topPadding = 20.dp.toPx()
        val endPadding = 20.dp.toPx()
        
        val graphWidth = width - startPadding - endPadding
        val graphHeight = height - topPadding - bottomPadding
        
        if (values.isEmpty()) return@Canvas

        // Draw Grid Lines & Y-Axis Labels
        val gridLines = 4
        for (i in 0..gridLines) {
            val progress = i / gridLines.toFloat()
            val y = height - bottomPadding - (progress * graphHeight)
            val value = minVal + (progress * range)
            
            // Grid line
            drawLine(
                color = Color.White.copy(alpha = 0.1f),
                start = Offset(startPadding, y),
                end = Offset(width - endPadding, y),
                strokeWidth = 1.dp.toPx(),
                pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            )
            
            // Y-Axis Label
            val label = if (value >= 1000) {
                "${(value / 1000).toInt()}k"
            } else {
                value.toInt().toString()
            }
            
            val measuredText = textMeasurer.measure(
                text = label,
                style = TextStyle(color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
            )
            
            drawText(
                textLayoutResult = measuredText,
                topLeft = Offset(
                    x = startPadding - measuredText.size.width - 5.dp.toPx(), 
                    y = y - measuredText.size.height / 2
                )
            )
        }

        val xStep = graphWidth / (values.size - 1).coerceAtLeast(1)
        
        val points = values.mapIndexed { index, value ->
            val x = startPadding + (index * xStep)
            val y = height - bottomPadding - ((value - minVal) / range * graphHeight)
            Offset(x, y)
        }

        // Draw fill area
        val path = Path()
        if (points.isNotEmpty()) {
            path.moveTo(points.first().x, height - bottomPadding)
            path.lineTo(points.first().x, points.first().y)
            
            for (i in 0 until points.size - 1) {
                val p1 = points[i]
                val p2 = points[i+1]
                path.lineTo(p2.x, p2.y)
            }
            
            path.lineTo(points.last().x, height - bottomPadding)
            path.close()
            
            drawPath(
                path = path,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        lineColor.copy(alpha = 0.4f),
                        lineColor.copy(alpha = 0.0f)
                    ),
                    startY = topPadding,
                    endY = height - bottomPadding
                )
            )
        }

        // Draw Line connection
        val linePath = Path()
        if (points.isNotEmpty()) {
            linePath.moveTo(points.first().x, points.first().y)
            for (i in 0 until points.size - 1) {
                linePath.lineTo(points[i+1].x, points[i+1].y)
            }
            
            drawPath(
                path = linePath,
                color = lineColor,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        // Draw points, X-labels and value labels
        points.forEachIndexed { index, point ->
            // Dot
            drawCircle(
                color = lineColor,
                radius = 6.dp.toPx(),
                center = point
            )
            drawCircle(
                color = Color.White,
                radius = 3.dp.toPx(),
                center = point
            )
            
            // X-axis label
            val labelDescription = labels.getOrNull(index) ?: ""
            val xLabelText = textMeasurer.measure(
                text = labelDescription,
                style = TextStyle(color = Color.White.copy(alpha = 0.9f), fontSize = 12.sp)
            )
            
            drawText(
                textLayoutResult = xLabelText,
                topLeft = Offset(
                    x = point.x - xLabelText.size.width / 2,
                    y = height - bottomPadding + 8.dp.toPx()
                )
            )
            
            // Value label above point
            val valueText = textMeasurer.measure(
                text = values[index].toInt().toString(),
                style = TextStyle(color = Color.White, fontSize = 12.sp, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
            )
            
            drawText(
                textLayoutResult = valueText,
                topLeft = Offset(
                    x = point.x - valueText.size.width / 2,
                    y = point.y - valueText.size.height - 8.dp.toPx()
                )
            )
        }
    }
}
