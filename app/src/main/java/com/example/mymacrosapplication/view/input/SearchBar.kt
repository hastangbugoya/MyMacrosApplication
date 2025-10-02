package com.example.mymacrosapplication.view.input

import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.mymacrosapplication.ui.theme.getMainOutlinedTextFieldColors

@Composable
fun SearchBar(
    label: String = "Search....",
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }

    OutlinedTextField(
        value = query,
        colors = getMainOutlinedTextFieldColors(),
        modifier = Modifier.fillMaxSize(),
        onValueChange = { query = it },
        label = { Text(label) },
        singleLine = true,
        leadingIcon = {
            IconButton(
                onClick = {
                    Log.d("Meow", "SearchBar > onClick > query: $query")
                    onSearch(query)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search"
                )
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { query = "" }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch(query)
            }
        )
    )
}