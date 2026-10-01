package com.camphub.app.ui.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.camphub.app.ui.model.Program
import com.camphub.app.ui.theme.CampHubTheme
import java.text.NumberFormat
import java.util.Locale

// Program card on the bootcamp detail screen
@Composable
fun ProgramCard(program: Program, modifier: Modifier = Modifier) {
    OutlinedCard(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = program.name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = program.category,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(text = formatRupiah(program.price), style = MaterialTheme.typography.titleSmall)
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Icon(
                        imageVector = Icons.Outlined.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${program.durationWeeks} minggu",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            if (program.registrationOpen) {
                StatusLabel(text = "Dibuka", icon = Icons.Outlined.CheckCircle)
            } else {
                StatusLabel(text = "Ditutup", icon = Icons.Outlined.Block, highlighted = false)
            }
        }
    }
}

// 15000000 -> "Rp15.000.000"
fun formatRupiah(price: Long): String =
    "Rp" + NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID")).format(price)

@Preview(showBackground = true)
@Composable
private fun ProgramCardPreview() {
    CampHubTheme {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            ProgramCard(
                program = Program(
                    id = 1,
                    bootcampId = 1,
                    bootcampName = "Kode Nusantara Academy",
                    name = "Full-Stack Web Developer",
                    category = "Web Development",
                    price = 15000000,
                    durationWeeks = 12,
                    syllabus = "",
                    registrationOpen = true
                )
            )
            ProgramCard(
                program = Program(
                    id = 2,
                    bootcampId = 1,
                    bootcampName = "Kode Nusantara Academy",
                    name = "Back-End Engineer dengan Go",
                    category = "Web Development",
                    price = 12500000,
                    durationWeeks = 10,
                    syllabus = "",
                    registrationOpen = false
                )
            )
        }
    }
}