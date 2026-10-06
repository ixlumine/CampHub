package com.camphub.app.ui.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.camphub.app.ui.model.ForumThread
import com.camphub.app.ui.theme.CampHubTheme
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDate(isoDate: String): String {
    if (isoDate.isBlank()) return ""
    return try {
        val parsed = LocalDateTime.parse(isoDate.take(19))
        val formatter = DateTimeFormatter.ofPattern("d MMM yyyy", Locale.forLanguageTag("id-ID"))
        parsed.format(formatter)
    } catch (e: Exception) {
        isoDate
    }
}

@Composable
fun ForumThreadCard(
    thread: ForumThread,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = thread.title,
                style = MaterialTheme.typography.titleMedium
            )
            val dateFormatted = formatDate(thread.createdAt)
            val authorAndDate = if (dateFormatted.isNotBlank()) "${thread.authorName} · $dateFormatted" else thread.authorName
            Text(
                text = authorAndDate,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.ChatBubbleOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "${thread.commentCount} komentar",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ForumThreadCardPreview() {
    CampHubTheme {
        ForumThreadCard(
            thread = ForumThread(
                id = 1,
                authorId = 2,
                authorName = "Andi Saputra",
                title = "Apakah ada kelas malam untuk karyawan?",
                content = "Saya bekerja sampai pukul 17.00 di hari kerja...",
                commentCount = 3,
                createdAt = "2026-09-20T19:00:00"
            ),
            onClick = {}
        )
    }
}
