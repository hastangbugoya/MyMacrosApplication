package com.example.mymacrosapplication.model.nutrition

data class FoodNutrient(
    val derivationCode: String? = null,
    val derivationDescription: String? = null,
    val derivationId: Int? = null,
    val foodNutrientId: Int? = null,
    val foodNutrientSourceCode: String? = null,
    val foodNutrientSourceDescription: String? = null,
    val foodNutrientSourceId: Int? = null,
    val indentLevel: Int? = null,
    val nutrientId: Int? = null,
    val nutrientName: String? = null,
    val nutrientNumber: String? = null,
    val percentDailyValue: Int? = null,
    val rank: Int? = null,
    val unitName: String? = null,
    val value: Double? = null
)