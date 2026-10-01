package com.camphub.app.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.StarHalf
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.camphub.app.ui.theme.CampHubTheme

// Five stars: full for the whole part, one half if the decimal is 0.5 or more, the rest outlined
// Per-review ratings (1–5) are whole numbers, so they show only full and outlined stars
@Composable
fun RatingStars(rating: Double, starSize: Dp = 16.dp) {
    val value = rating.coerceIn(0.0, 5.0)
    val fullStars = value.toInt()
    val hasHalf = value - fullStars >= 0.5

    Row {
        repeat(5) { index ->
            val icon = when {
                index < fullStars -> Icons.Filled.Star
                index == fullStars && hasHalf -> Icons.AutoMirrored.Filled.StarHalf
                else -> Icons.Outlined.StarOutline
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(starSize)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RatingStarsPreview() {
    CampHubTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            RatingStars(rating = 4.8)   // 4 full + 1 half
            RatingStars(rating = 3.7)   // 3 full + 1 half + 1 outlined
            RatingStars(rating = 4.0)   // review rating: 4 full + 1 outlined
            RatingStars(rating = 4.0, starSize = 32.dp)
        }
    }
}