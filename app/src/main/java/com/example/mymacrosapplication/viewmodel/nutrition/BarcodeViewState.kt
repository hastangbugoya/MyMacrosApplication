package com.example.mymacrosapplication.viewmodel.nutrition

import com.example.mymacrosapplication.model.nutrition.USDAResponse

data class BarcodeViewState(
    val barcodeValue: String? = null,
    val searchString: String? = null,
    val foodResult: USDAResponse? = null,
    // food search may return multiple results if barcode not used
    val foodSearchResultCount: Int? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val exception: Exception? = null,
    val lastIntent: BarcodeIntent? = null,
    val barcodeQuerySent: Boolean = false,
)
