package com.camphub.app.ui.view

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.camphub.app.ui.theme.CampHubTheme

// Delete confirmation
// If the server refuses the delete, the screen shows its message in a Snackbar
@Composable
fun DeleteDialog(
    title: String,
    message: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title) },
        text = { Text(text = message) },
        confirmButton = {
            TextButton(
                onClick = onConfirm,
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Text(text = "Hapus")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Batal")
            }
        }
    )
}

@Preview
@Composable
private fun DeleteDialogPreview() {
    CampHubTheme {
        DeleteDialog(
            title = "Hapus bootcamp?",
            message = "Kode Nusantara Academy akan dihapus permanen. Tindakan ini tidak dapat dibatalkan.",
            onConfirm = {},
            onDismiss = {}
        )
    }
}