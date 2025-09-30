package com.example.mymacrosapplication.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class BarcodeViewModel @Inject constructor() : ViewModel() {
    private val _barcodeValue = MutableStateFlow<String?>(null)
    val barcodeValue: StateFlow<String?> = _barcodeValue

    fun setBarcode(value: String) {
        _barcodeValue.value = value
    }
}