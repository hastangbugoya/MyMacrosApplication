package com.example.mymacrosapplication.view.alerts

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FoodSearchCountBottomSheet(
    count: Int,
    onClose: (() -> Unit)? = null,
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Found $count food${if (count != 1) "s" else ""}",
            style = MaterialTheme.typography.titleMedium,
        )
        Spacer(Modifier.height(16.dp))
        if (onClose != null) {
            Button(onClick = onClose) {
                Text("Close")
            }
        }
    }
}
