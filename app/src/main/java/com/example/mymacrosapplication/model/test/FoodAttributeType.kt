package com.example.mymacrosapplication.model.test

data class FoodAttributeType(
    val description: String? = "",
    val foodAttributes: List<FoodAttributeX>? = listOf(),
    val id: Int? = 0,
    val name: String? = ""
)