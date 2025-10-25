package com.example.mymacrosapplication.viewmodel

import com.example.mymacrosapplication.view.alerts.MainActivityAlert

data class MainActivityState(
    var alert: MainActivityAlert? = null,
    var alertShown: Boolean = false,
)
