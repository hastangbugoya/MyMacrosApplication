package com.example.mymacrosapplication.viewmodel.nutrition

import com.example.mymacrosapplication.model.nutrition.USDAResponse

data class BarcodeViewState(
    val barcodeValue: String? = null,
    val searchString: String? = null,
    val foodResult: USDAResponse? = null,
    val resultCount: Int? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val exception: Exception? = null,
    val lastIntent: BarcodeIntent? = null
)
