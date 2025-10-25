package com.example.mymacrosapplication.view.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mymacrosapplication.model.nutrition.Food
import com.example.mymacrosapplication.viewmodel.nutrition.BarcodeViewState

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FoodPager(state: BarcodeViewState) {
    val foods = state.foodResult?.foods ?: emptyList()

    if (foods.isEmpty()) {
        Text("No foods found")
        return
    }

    // Keep track of current page if needed
    val pagerState = rememberPagerState(pageCount = { foods.size })

    Box(modifier = Modifier.fillMaxSize()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth(),
        ) { page ->
            val food = foods[page]
            FoodItemPage(food = food)
        }

        // Optional indicator below pager
        SimplePagerIndicator(
            pageCount = foods.size,
            currentPage = pagerState.currentPage,
            modifier =
                Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomCenter),
        )
    }
}

@Composable
fun FoodItemPage(food: Food) {
    val myMod =
        Modifier
            .fillMaxWidth()
            .padding(8.dp)
    val myHeaderMod =
        Modifier
            .fillMaxWidth()
            .padding(10.dp)

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .verticalScroll(rememberScrollState()),
    ) {
        food.brandName?.let {
            Text(it, modifier = myHeaderMod)
        }
        food.subbrandName?.let {
            Text(it, modifier = myHeaderMod)
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
        food.foodNutrients?.forEach {
            Text("${it.nutrientName} ${it.nutrientId}: ${it.value} ${it.unitName}", modifier = myMod)
        }
        // scroll nutritional value
//        val listState = rememberLazyListState()
//        val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
//        food.foodNutrients?.let { list ->
//            LazyColumn(
//                state = listState,
//                flingBehavior = flingBehavior,
//                modifier =
//                    Modifier
//                        .fillMaxSize()
//                        .padding(5.dp),
//            ) {
//                itemsIndexed(list) { index, nutrient ->
//                    with(nutrient) {
//                        Card(
//                            modifier =
//                                Modifier
//                                    .padding(5.dp)
//                                    .fillMaxWidth()
//                                    .shadow(3.dp, RoundedCornerShape(8.dp))
//                                    .background(Color(0xfffce3d4)),
//                            shape = RoundedCornerShape(8.dp),
//                            colors =
//                                CardDefaults.cardColors(
//                                    containerColor = Color(0xfffce3d4),
//                                    contentColor = Color(0xff691b1e),
//                                    disabledContainerColor = Color(0xfffef2ec),
//                                    disabledContentColor = Color(0xffb77678),
//                                ),
//                        ) {
//                            Text(
//                                "$nutrientName($nutrientId): $value $unitName",
//                                modifier = Modifier.padding(10.dp),
//                            )
//                        }
//                    }
//                }
//            }
//        }
        // scroll nutritional value
    }
}

@Composable
fun SimplePagerIndicator(
    pageCount: Int,
    currentPage: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier,
    ) {
        repeat(pageCount) { index ->
            val color =
                if (index == currentPage) Color.Black else Color.LightGray

            Box(
                modifier =
                    Modifier
                        .padding(4.dp)
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color),
            )
        }
    }
}
