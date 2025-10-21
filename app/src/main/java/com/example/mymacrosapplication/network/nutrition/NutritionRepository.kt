package com.example.mymacrosapplication.network.nutrition

import android.content.Context
import android.util.Log
import com.example.mymacrosapplication.BuildConfig
import com.example.mymacrosapplication.model.nutrition.USDAResponse
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.Dispatcher
import javax.inject.Inject

class NutritionRepository
    @Inject
    constructor(
        private val api: FoodApi,
        @ApplicationContext private val context: Context,
    ) {
        val apiKey = BuildConfig.USDA_FDA_API_KEY
        val uSDALocalCache = mutableMapOf<String, USDAResponse>()
        private val dAO = AppCacheDatabase.getDatabase(context).cachedFoodDao()
        private val gSON = Gson()

        suspend fun searchFoods(query: String): USDAResponse =
            withContext(Dispatchers.IO) {
                Log.d("Meow", "SearchFoods: $query $apiKey")

                // check if cached in memory
                uSDALocalCache.get(query)?.let {
                    Log.d("Meow", "$query from cache")
                    return@withContext it
                }
                // check if cached in db
                dAO.getByQuery(query)?.let {
                    Log.d("Meow", "$query from db")
                    val response = gSON.fromJson(it.json, USDAResponse::class.java)
                    uSDALocalCache.put(query, response)
                    return@withContext response
                }
                // get from API
                val aPIResponse = api.searchFoods(query, apiKey)
                aPIResponse.let { response ->
                    uSDALocalCache.put(query, response)
                    dAO.insert(CachedFood(query, gSON.toJson(response), System.currentTimeMillis()))
                }

                return@withContext aPIResponse
            }
    }
