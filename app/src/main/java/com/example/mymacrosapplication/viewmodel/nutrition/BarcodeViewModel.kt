package com.example.mymacrosapplication.viewmodel.nutrition

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymacrosapplication.model.nutrition.USDAResponse
import com.example.mymacrosapplication.network.nutrition.NutritionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BarcodeViewModel @Inject constructor(private val repository: NutritionRepository) :
    ViewModel() {

    val intent = Channel<BarcodeIntent>(Channel.UNLIMITED)

    private val _state = MutableStateFlow(BarcodeViewState())
    val state: StateFlow<BarcodeViewState> = _state.asStateFlow()
    private val _barcodeValue = MutableStateFlow<String?>(null)
    val barcodeValue: StateFlow<String?> = _barcodeValue

    private val _foodResult = MutableStateFlow<USDAResponse?>(null)
    val foodResult: StateFlow<USDAResponse?> = _foodResult

    fun setBarcode(value: String?, apiKey: String) {
        _barcodeValue.value = value
        _state.value = _state.value.copy(
            barcodeValue = value
        )
        value?.let {
            searchFood(value, apiKey)
        } ?: run {
            _state.value = _state.value.copy(
                foodResult = null
            )
            _foodResult.value = null
        }
    }

    private fun handleIntent() {
        viewModelScope.launch {
            intent.consumeAsFlow().collect { intent ->
                _state.update { it.copy(lastIntent = intent, isLoading = true) }
                when (intent) {
                    is BarcodeIntent.SetBarcode -> {
                        Log.d("Meow", "viewModel > when(intent) > SetBarCode ? $intent")
                        setBarcode(intent.code, intent.apiKey)
                    }

                    is BarcodeIntent.SearchFood -> {
                        Log.d("Meow", "viewModel > when(intent) > SearchFood ? $intent")
                        searchFood(intent.query, intent.apiKey)
                    }
                }
            }
        }
    }

    private fun searchFood(query: String?, apiKey: String) {
        if (query == null) {
            _state.value = _state.value.copy(
                errorMessage = "No query provided"
            )
            return
        }
        viewModelScope.launch {
            try {
                val result = repository.searchFoods(query, apiKey)
                val count = result.foods?.size ?: 0
                _state.value = _state.value.copy(
                    foodResult = result,
                    resultCount = count
                )
                if (count == 0) _state.value = _state.value.copy(
                    errorMessage = "No matches found"
                )
                _foodResult.value = result
                Log.d("Meow", "Found ($count) matches")
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    errorMessage = e.message,
                    exception = e
                )
                _foodResult.value = null
                e.printStackTrace()
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(errorMessage = null, exception = null) }
    }

    fun retryLastAction() {
        val lastIntent = _state.value.lastIntent
        if (lastIntent != null) {
            viewModelScope.launch {
                intent.send(lastIntent)

            }
        }
    }

    init {
        handleIntent()
    }
}