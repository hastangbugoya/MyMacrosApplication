package com.example.mymacrosapplication.view.alerts

sealed class MainActivityAlert {
    data class NetworkAlert(
        val message: String,
    ) : MainActivityAlert()

    data class GenericAlert(
        val message: String,
    ) : MainActivityAlert()
}
