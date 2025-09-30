package com.example.mymacrosapplication.model

data class FoodNutrient(
    val derivationCode: String,
    val derivationDescription: String,
    val derivationId: Int,
    val foodNutrientId: Int,
    val foodNutrientSourceCode: String,
    val foodNutrientSourceDescription: String,
    val foodNutrientSourceId: Int,
    val indentLevel: Int,
    val nutrientId: Int,
    val nutrientName: String,
    val nutrientNumber: String,
    val percentDailyValue: Int,
    val rank: Int,
    val unitName: String,
    val value: Double
)