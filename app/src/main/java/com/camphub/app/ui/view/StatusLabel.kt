package com.camphub.app.ui.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Work
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.camphub.app.ui.theme.CampHubTheme

// Non-clickable status label: 16 dp icon and short text
// highlighted = primaryContainer, otherwise surfaceVariant
@Composable
fun StatusLabel(text: String, icon: ImageVector, highlighted: Boolean = true) {
    val containerColor = if (highlighted) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (highlighted) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant

    Row(
        modifier = Modifier
            .background(containerColor, CircleShape)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = contentColor, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, style = MaterialTheme.typography.labelMedium, color = contentColor)
    }
}

@Preview(showBackground = true)
@Composable
private fun StatusLabelPreview() {
    CampHubTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            StatusLabel(text = "Dibuka", icon = Icons.Outlined.CheckCircle)
            StatusLabel(text = "Ditutup", icon = Icons.Outlined.Block, highlighted = false)
            StatusLabel(text = "Diterima Bekerja", icon = Icons.Outlined.Work)
            StatusLabel(text = "Mencari Kerja", icon = Icons.Outlined.Search, highlighted = false)
        }
    }
}