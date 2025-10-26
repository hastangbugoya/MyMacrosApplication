package com.example.mymacrosapplication.view.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.mymacrosapplication.model.nutrition.Food
import com.example.mymacrosapplication.viewmodel.nutrition.BarcodeViewState
import com.example.mymacrosapplication.viewmodel.nutrition.FoodViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FoodPager(
    state: BarcodeViewState,
    foodViewModel: FoodViewModel = hiltViewModel<FoodViewModel>(),
) {
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
fun FoodItemPage(
    food: Food,
    viewModel: FoodViewModel = hiltViewModel<FoodViewModel>(),
) {
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
            Text(it, modifier = myHeaderMod, style = MaterialTheme.typography.titleMedium)
        }
        food.subbrandName?.let {
            Text(it, modifier = myHeaderMod, style = MaterialTheme.typography.titleMedium)
        }
        food.description?.let {
            Text(it, modifier = myMod, style = MaterialTheme.typography.titleSmall)
        }
        Text(
            "Serving Size: ${food.servingSize} ${food.servingSizeUnit}",
            modifier = myMod,
            style = MaterialTheme.typography.bodyLarge,
        )
        food.packageWeight?.let {
            Text(it, modifier = myMod, style = MaterialTheme.typography.bodyMedium)
        }
        if (!food.shortDescription.isNullOrEmpty()) {
            Text(food.shortDescription!!, modifier = myMod)
        }
        food.foodNutrients
            ?.filter {
                viewModel.showPreferedNutrients.value.contains(it.nutrientId.toString()) ||
                    viewModel.showPreferedNutrients.value.contains(it.nutrientName)
            }?.forEach {
                Text(
                    "${it.nutrientName} ${it.nutrientId}: ${it.value} ${it.unitName}",
                    modifier = myMod,
                )
            }
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
