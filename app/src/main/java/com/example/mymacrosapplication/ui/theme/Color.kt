package com.example.mymacrosapplication.ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val TurkishRose = Color(0xffb77678)
val NewYorkPink = Color(0xffdd8b88)
val RoseBud = Color(0xfff8aaaf)
val ApricotPeach = Color(0xfffcd3c5)

val PersianPlum = Color(0xff691b1e)
val TallPoppy = Color(0xffb82d28)
val Carnation = Color((0xfff05660))
val Cinderella = Color(0xfffce3d4)

val MainButtonColors =
    ButtonColors(
        containerColor = Purple40,
        contentColor = Color.White,
        disabledContainerColor = PurpleGrey80,
        disabledContentColor = Color.Gray.copy(0.25f),
    )

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun getMainOutlinedTextFieldColors() =
    OutlinedTextFieldDefaults.colors(
        focusedBorderColor = PurpleGrey40,
        unfocusedBorderColor = Purple40,
        focusedLabelColor = PurpleGrey40,
        cursorColor = PurpleGrey40,
        disabledTextColor = Purple40.copy(0.25f),
        disabledBorderColor = Purple40.copy(0.25f),
        focusedTextColor = PurpleGrey40,
        unfocusedTextColor = Purple40,
        unfocusedLabelColor = Purple40,
        focusedLeadingIconColor = PurpleGrey40,
        unfocusedLeadingIconColor = Purple40,
        focusedTrailingIconColor = PurpleGrey40,
        unfocusedTrailingIconColor = Purple40,
        disabledLeadingIconColor = Purple40.copy(0.25f),
        disabledTrailingIconColor = Purple40.copy(0.25f),
        selectionColors =
            TextSelectionColors(
                handleColor = Purple80,
                backgroundColor = Purple80,
            ),
    )
