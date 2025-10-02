package com.example.mymacrosapplication.model.nutrition

data class FoodAttributeType(
    val description: String? = null,
    val foodAttributes: List<FoodAttributeX>? = listOf(),
    val id: Int? = null,
    val name: String? = null,
)
