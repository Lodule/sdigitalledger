package com.sdigitalledger.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun FeedbackBoardDialog(
    onDismiss: () -> Unit,
    onSubmitFeedback: (rating: Int, feedbackText: String, email: String) -> Unit
) {
    var rating by remember { mutableIntStateOf(5) }
    var feedbackText by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("App Feedback Board", style = MaterialTheme.typography.titleLarge) },
        text = {
            Column(modifier = Modifier.fillMaxWidth().padding(4.dp)) {
                Text("Help us improve Digital Ledger! Share your thoughts or suggestions.")
                Spacer(modifier = Modifier.height(12.dp))

                // Star Rating Selection
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    (1..5).forEach { index ->
                        Icon(
                            imageVector = if (index <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = "Star $index",
                            tint = if (index <= rating) Color(0xFFFFB800) else Color.Gray,
                            modifier = Modifier
                                .size(36.dp)
                                .clickable { rating = index }
                                .padding(4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = feedbackText,
                    onValueChange = { feedbackText = it },
                    label = { Text("Your Feedback") },
                    placeholder = { Text("Tell us what features you'd like...") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 4
                )

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Optional for updates)") },
                    placeholder = { Text("simonlodule18@gmail.com") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSubmitFeedback(rating, feedbackText, email)
                    onDismiss()
                }
            ) {
                Text("Submit")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
