package com.example.mymacrosapplication.view

import androidx.annotation.DrawableRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.mymacrosapplication.R

sealed class BottomBarItems(
    val label: String,
    @DrawableRes val icon: Int,
    val route: String,
) {
    object Home : BottomBarItems("Home", R.drawable.house_blank, "home")

    object Search : BottomBarItems("Search", R.drawable.search, "search")

    object Profile : BottomBarItems("Profile", R.drawable.user, "profile")
}
