package com.example.mymacrosapplication.model

data class USDAResponse(
    val aggregations: Aggregations,
    val currentPage: Int,
    val foodSearchCriteria: FoodSearchCriteria,
    val foods: List<Food>,
    val pageList: List<Int>,
    val totalHits: Int,
    val totalPages: Int
)