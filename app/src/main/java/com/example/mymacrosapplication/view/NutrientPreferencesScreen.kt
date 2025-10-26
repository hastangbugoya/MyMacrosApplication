package com.example.mymacrosapplication.view

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.mymacrosapplication.viewmodel.data.NutrientViewModel

@Composable
fun NutrientPreferences(viewModel: NutrientViewModel = hiltViewModel()) {
    val nutrients by viewModel.nutrients.collectAsState(listOf<String>())

    var newNutrient by remember { mutableStateOf("") }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Text("Your Nutrients", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        // Current list
        if (nutrients.isEmpty()) {
            Text("No nutrients saved.")
        } else {
            nutrients.forEach { nutrient ->
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(nutrient)
                    TextButton(onClick = { viewModel.removeNutrient(nutrient) }) {
                        Text("Remove")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Add new nutrient
        OutlinedTextField(
            value = newNutrient,
            onValueChange = { newNutrient = it },
            label = { Text("Add nutrient ID") },
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(Modifier.height(8.dp))

        Button(
            onClick = {
                if (newNutrient.isNotBlank()) {
                    viewModel.addNutrient(newNutrient.trim())
                    newNutrient = ""
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Add")
        }
    }
}
