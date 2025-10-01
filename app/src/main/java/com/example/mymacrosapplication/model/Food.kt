package com.example.mymacrosapplication.model

import com.example.mymacrosapplication.model.test.FoodAttributeType

data class Food(
    val additionalDescriptions: String? = "",
    val allHighlightFields: String? = "",
    val brandName: String? = null,
    val brandOwner: String? = null,
    val commonNames: String? = null,
    val dataSource: String? = null,
    val dataType: String? = null,
    val description: String? = null,
    val discontinuedDate: String? = null,
    val fdcId: Int? = 0,
    val finalFoodInputFoods: List<FinalFoodInputFood>? = listOf(),
    val foodAttributeTypes: List<com.example.mymacrosapplication.model.FoodAttributeType>? = listOf(),
    val foodAttributes: List<FoodAttributeX>? = listOf(),
    val foodCategory: String? = null,
    val foodMeasures: List<Any?>? = listOf(),
    val foodNutrients: List<FoodNutrient>? = listOf(),
    val foodVersionIds: List<Any?>? = null,
    val gpcClassCode: Int? = 0,
    val gtinUpc: String? = null,
    val householdServingFullText: String? = null,
    val ingredients: String? = null,
    val marketCountry: String? = null,
    val microbes: List<Any?>? = listOf<Any>(),
    val modifiedDate: String? = null,
    val packageWeight: String? = null,
    val preparationStateCode: String? = null,
    val publishedDate: String? = null,
    val score: Double? = 0.0,
    val servingSize: Double? = 0.0,
    val servingSizeUnit: String? = "g",
    val shortDescription: String? = null,
    val subbrandName: String? = null,
    val tradeChannels: List<String>? = listOf()
)