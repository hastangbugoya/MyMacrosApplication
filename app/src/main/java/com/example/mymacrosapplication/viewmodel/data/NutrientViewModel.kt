package com.example.mymacrosapplication.viewmodel.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymacrosapplication.data.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NutrientViewModel
    @Inject
    constructor(
        private val prefs: PreferencesRepository,
    ) : ViewModel() {
        val nutrients =
            prefs.nutrientListFlow.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptySet(),
            )

        init {
            viewModelScope.launch {
                prefs.preloadDefaults()
            }
        }

        fun addNutrient(name: String) {
            viewModelScope.launch { prefs.addNutrient(name) }
        }

        fun removeNutrient(name: String) {
            viewModelScope.launch { prefs.removeNutrient(name) }
        }
    }
