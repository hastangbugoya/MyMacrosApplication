package com.example.mymacrosapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymacrosapplication.model.USDAResponse
import com.example.mymacrosapplication.network.NutritionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarcodeViewModel @Inject constructor(private val repository: NutritionRepository) : ViewModel() {
    private val _barcodeValue = MutableStateFlow<String?>(null)
    val barcodeValue: StateFlow<String?> = _barcodeValue

    private val _foodResult = MutableStateFlow<USDAResponse?>(null)
    val foodResult : StateFlow<USDAResponse?> = _foodResult

    fun setBarcode(value: String, apiKey: String) {
        _barcodeValue.value = value
        searchFood(value, apiKey)
    }
    private fun searchFood(query: String, apiKey: String) {
        viewModelScope.launch {
            try {
                val result = repository.searchFoods(query, apiKey)
                _foodResult.value = result
                Log.d("Meow", result.foods.size.toString())
            } catch (e: Exception) {
                _foodResult.value = null
                e.printStackTrace()
            }
        }
    }
}