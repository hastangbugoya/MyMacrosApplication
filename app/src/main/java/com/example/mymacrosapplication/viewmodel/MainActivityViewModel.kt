package com.example.mymacrosapplication.viewmodel

import androidx.lifecycle.ViewModel
import com.example.mymacrosapplication.view.BottomBarItems
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel
    @Inject
    constructor() : ViewModel() {
        val bottomNavItems =
            listOf(
                BottomBarItems.Home,
                BottomBarItems.Search,
                BottomBarItems.Profile,
            )
        val currentRoute = "home"
        val currentScreen = bottomNavItems.find { it.route == currentRoute }

        private val _state = MutableStateFlow(MainActivityState())
        val state = _state
    }
