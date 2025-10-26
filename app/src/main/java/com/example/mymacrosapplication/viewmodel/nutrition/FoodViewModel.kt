package com.example.mymacrosapplication.viewmodel.nutrition

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymacrosapplication.data.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class FoodViewModel
    @Inject
    constructor(
        private val preferencesRepository: PreferencesRepository,
    ) : ViewModel() {
        val showPreferedNutrients =
            preferencesRepository.nutrientListFlow.stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                emptySet(),
            )
    }
