package com.example.mymacrosapplication.model.test

data class Foodx(
    val additionalDescriptions: String? = "",
    val allHighlightFields: String? = "",
    val commonNames: String? = "",
    val dataType: String? = "",
    val description: String? = "",
    val fdcId: Int? = 0,
    val finalFoodInputFoods: List<FinalFoodInputFood>? = listOf(),
    val foodAttributeTypes: List<FoodAttributeType>? = listOf(),
    val foodAttributes: List<FoodAttributeX>? = listOf(),
    val foodCategory: String? = "",
    val foodCategoryId: Int? = 0,
    val foodCode: Int? = 0,
    val foodMeasures: List<FoodMeasure>? = listOf(),
    val foodNutrients: List<FoodNutrient>? = listOf(),
    val foodVersionIds: List<Any?>? = listOf(),
    val microbes: List<Any?>? = listOf(),
    val mostRecentAcquisitionDate: String? = "",
    val ndbNumber: Int? = 0,
    val publishedDate: String? = "",
    val scientificName: String? = "",
    val score: Double? = 0.0
)