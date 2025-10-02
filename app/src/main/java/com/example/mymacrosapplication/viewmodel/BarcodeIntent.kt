package com.example.mymacrosapplication.viewmodel

sealed class BarcodeIntent {
    data class SetBarcode(val code: String?, val apiKey: String) : BarcodeIntent()
    data class SearchFood(val query: String?, val apiKey: String) : BarcodeIntent()
}