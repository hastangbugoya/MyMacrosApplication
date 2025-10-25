package com.example.mymacrosapplication.view.lists

import androidx.compose.foundation.ExperimentalFoundationApi

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FoodPager(state: YourStateType) {
    val foods = state.foodResult?.foods ?: emptyList()

    if (foods.isEmpty()) {
        Text("No foods found")
        return
    }

    // Keep track of current page if needed
    val pagerState = rememberPagerState(pageCount = { foods.size })

    HorizontalPager(
        state = pagerState,
        modifier = Modifier.fillMaxWidth(),
    ) { page ->
        val food = foods[page]
        FoodItemPage(food = food)
    }

    // Optional indicator below pager
    HorizontalPagerIndicator(
        pagerState = pagerState,
        modifier =
            Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp),
    )
}

@Composable
fun FoodItemPage(food: Food) {
    val myMod =
        Modifier
            .fillMaxWidth()
            .padding(8.dp)

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
    ) {
        food.brandName?.let {
            Text(it, modifier = myMod)
        }
        food.subbrandName?.let {
            Text(it, modifier = myMod)
        }
        food.description?.let {
            Text(it, modifier = myMod)
        }
        Text(
            "Serving Size: ${food.servingSize} ${food.servingSizeUnit}",
            modifier = myMod,
        )
        food.packageWeight?.let {
            Text(it, modifier = myMod)
        }
        if (!food.shortDescription.isNullOrEmpty()) {
            Text(food.shortDescription!!, modifier = myMod)
        }
    }
}
