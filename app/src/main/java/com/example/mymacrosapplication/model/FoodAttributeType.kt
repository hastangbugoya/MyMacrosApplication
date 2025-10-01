package com.example.mymacrosapplication.model

data class FoodAttributeType(
    val description: String? = null,
    val foodAttributes: List<FoodAttributeX>? = listOf(),
    val id: Int? = null,
    val name: String? = null
)