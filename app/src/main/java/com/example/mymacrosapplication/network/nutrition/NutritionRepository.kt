package com.example.mymacrosapplication.network.nutrition

import android.util.Log
import com.example.mymacrosapplication.model.nutrition.USDAResponse
import javax.inject.Inject

class NutritionRepository @Inject constructor(
    private val api: FoodApi
) {
    suspend fun searchFoods(query: String, apiKey: String): USDAResponse {
        Log.d("Meow", "SearchFoods: $query $apiKey")
        return api.searchFoods(query, apiKey)
    }
}