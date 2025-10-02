package com.example.mymacrosapplication.model.nutrition

data class FoodSearchCriteria(
    val generalSearchInput: String? = null,
    val numberOfResultsPerPage: Int? = null,
    val pageNumber: Int? = null,
    val pageSize: Int? = null,
    val query: String? = null,
    val requireAllWords: Boolean? = null,
)
