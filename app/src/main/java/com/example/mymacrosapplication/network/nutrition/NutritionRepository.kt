package com.example.mymacrosapplication.network.nutrition

import android.util.Log
import com.example.mymacrosapplication.BuildConfig
import com.example.mymacrosapplication.model.nutrition.USDAResponse
import javax.inject.Inject

class NutritionRepository
    @Inject
    constructor(
        private val api: FoodApi,
    ) {
        val apiKey = BuildConfig.USDA_FDA_API_KEY

        suspend fun searchFoods(query: String): USDAResponse {
            Log.d("Meow", "SearchFoods: $query $apiKey")
            return api.searchFoods(query, apiKey)
        }
    }
