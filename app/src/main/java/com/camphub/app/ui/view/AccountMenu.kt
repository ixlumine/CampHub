package com.camphub.app.ui.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.camphub.app.data.container.CampHubServerContainer

// Avatar in the TopAppBar of the three tabs (spec 6.5, mockup "Menu avatar")
@Composable
fun AccountMenu(onLogout: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(imageVector = Icons.Outlined.AccountCircle, contentDescription = "Akun")
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            // Name and role row: not clickable (enabled = false), text keeps normal color
            DropdownMenuItem(
                text = {
                    Column {
                        Text(text = CampHubServerContainer.CURRENT_NAME, style = MaterialTheme.typography.titleSmall)
                        Text(
                            text = roleLabel(CampHubServerContainer.CURRENT_ROLE),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                onClick = {},
                enabled = false,
                colors = MenuDefaults.itemColors(disabledTextColor = MaterialTheme.colorScheme.onSurface)
            )
            HorizontalDivider()
            DropdownMenuItem(
                text = { Text(text = "Keluar") },
                leadingIcon = { Icon(imageVector = Icons.AutoMirrored.Outlined.Logout, contentDescription = null) },
                onClick = {
                    expanded = false
                    onLogout()
                }
            )
        }
    }
}

// Role labels (spec 6.5)
private fun roleLabel(role: String): String = when (role) {
    "ADMIN" -> "Admin"
    "PROVIDER" -> "Penyedia Bootcamp"
    else -> "Pengguna"
}