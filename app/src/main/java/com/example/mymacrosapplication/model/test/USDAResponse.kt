package com.example.mymacrosapplication.model.test

import com.example.mymacrosapplication.model.nutrition.Food

data class USDAResponse(
    val aggregations: Aggregations? = Aggregations(),
    val currentPage: Int? = 0,
    val foodSearchCriteria: FoodSearchCriteria? = FoodSearchCriteria(),
    val foods: List<Food>? = listOf(),
    val pageList: List<Int>? = listOf(),
    val totalHits: Int? = 0,
    val totalPages: Int? = 0
)