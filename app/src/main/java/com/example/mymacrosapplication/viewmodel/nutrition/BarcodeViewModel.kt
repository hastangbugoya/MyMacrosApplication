package com.example.mymacrosapplication.viewmodel.nutrition

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymacrosapplication.network.nutrition.NutritionRepository
import com.google.android.datatransport.runtime.BuildConfig
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject

@HiltViewModel
class BarcodeViewModel
    @Inject
    constructor(
        private val repository: NutritionRepository,
    ) : ViewModel() {
        val intent = Channel<BarcodeIntent>(Channel.UNLIMITED)
        private val _state = MutableStateFlow(BarcodeViewState())
        val state: StateFlow<BarcodeViewState> = _state.asStateFlow()
        private val _barcodeValue = MutableStateFlow<String?>(null)
        val barcodeValue: StateFlow<String?> = _barcodeValue

        val key = com.example.mymacrosapplication.BuildConfig.USDA_FDA_API_KEY
        private var barcodeLocked = AtomicBoolean(false)

        fun setBarcode(value: String?) {
            if (!barcodeLocked.compareAndSet(false, true)) {
                return
            }
            _barcodeValue.value = value
            _state.value =
                _state.value.copy(
                    barcodeValue = value,
                )
            value?.let {
//                barcodeLocked = AtomicBoolean(true)
                searchFood(value)
            } ?: run {
                _state.value =
                    _state.value.copy(
                        foodResult = null,
                    )
//                _foodResult.value = null
            }
        }

        private fun handleIntent() {
            viewModelScope.launch {
                intent.consumeAsFlow().collect { intent ->
                    _state.update { it.copy(lastIntent = intent, isLoading = true) }
                    when (intent) {
                        is BarcodeIntent.SetBarcode -> {
                            Log.d("Meow", "viewModel > when(intent) > SetBarCode ? $intent")
                            setBarcode(intent.code)
                        }

                        is BarcodeIntent.SearchFood -> {
                            Log.d("Meow", "viewModel > when(intent) > SearchFood ? $intent")
                            searchFood(intent.query)
                        }
                    }
                }
            }
        }

        private fun searchFood(query: String?) {
            Log.d("Meow", "Entering SearchFood: $query")
            if (query == null) {
                _state.update { it.copy(errorMessage = "No query provided") }
                return
            }
//            if (_state.value.isLoading) {
//                return
//            }
            viewModelScope.launch {
                Log.d("Meow", "SearchFood: $query entered viewModelScope")
                try {
                    val result = repository.searchFoods(query)
                    val count = result.foods?.size ?: 0
                    _state.value =
                        _state.value.copy(
                            foodResult = result,
                            foodSearchResultCount = count,
                        )
                    if (count == 0) {
                        _state.value =
                            _state.value.copy(
                                errorMessage = "No matches found",
                            )
                    }
                    Log.d("Meow", "SearchFood: $query >> Found ($count) matches")
                } catch (e: Exception) {
                    Log.d("Meow", "SearchFood: $query >> Error: ${e.message} ")
                    _state.value =
                        _state.value.copy(
                            errorMessage = e.message,
                            exception = e,
                        )
                    barcodeLocked.compareAndSet(true, false)
                    e.printStackTrace()
                }
                _state.value = _state.value.copy(isLoading = false)
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

        fun unlockBarcode() {
            barcodeLocked = AtomicBoolean(false)
        }

        fun updateSearchString(searchString: String) {
            _state.value = _state.value.copy(searchString = searchString)
        }

        fun stopLoading() {
            _state.value = _state.value.copy(isLoading = false)
        }

        init {
            handleIntent()
        }
    }
