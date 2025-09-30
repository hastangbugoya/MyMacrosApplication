package com.example.mymacrosapplication.model

data class FoodAttributeType(
    val description: String,
    val foodAttributes: List<FoodAttributeX>,
    val id: Int,
    val name: String
)