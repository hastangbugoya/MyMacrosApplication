import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.mymacrosapplication.viewmodel.nutrition.BarcodeViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CarouselPageScreen(viewModel: BarcodeViewModel) {
    val state by viewModel.state.collectAsState()
    val itemCount = state.foodResult?.foods?.size ?: 0
    val pagerState = rememberPagerState(pageCount = { itemCount }) // ✅ correct way
    val scope = rememberCoroutineScope()
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
    ) {
        // Pager
        HorizontalPager(
            state = pagerState,
            modifier =
                Modifier
                    .fillMaxSize(),
        ) { page ->
            Box(
                modifier =
                    Modifier
                        .fillMaxSize(),
//                        .background(
//                            when (page) {
//                                0 -> Color.Red
//                                1 -> Color.Green
//                                2 -> Color.Blue
//                                3 -> Color.Yellow
//                                else -> Color.Magenta
//                            },
//                        ),
                contentAlignment = Alignment.TopStart,
            ) {
                Text(text = "Page $page", style = MaterialTheme.typography.headlineMedium)
                val myMod =
                    Modifier
                        .fillMaxWidth()
                        .padding(0.dp, 2.dp)
                state.foodResult?.foods?.get(page)?.let {
                    with(it) {
                        brandName?.let { text ->
                            Text(
                                text,
                                modifier = myMod,
                            )
                        }
                        subbrandName?.let { text ->
                            Text(
                                text,
                                modifier = myMod,
                            )
                        }
                        description?.let { text ->
                            Text(
                                text,
                                modifier = myMod,
                            )
                        }
                        Text(
                            "Serving Size: $servingSize $servingSizeUnit ",
                            modifier = myMod,
                        )
                        packageWeight?.let { text ->
                            Text(
                                text,
                                modifier = myMod,
                            )
                        }
                        if (!shortDescription.isNullOrEmpty()) {
                            Text(
                                shortDescription,
                                modifier = myMod,
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Indicator Dots
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(pagerState.pageCount) { index ->
                val isSelected = pagerState.currentPage == index
                Box(
                    modifier =
                        Modifier
                            .padding(4.dp)
                            .size(if (isSelected) 12.dp else 8.dp)
                            .background(
                                if (isSelected) MaterialTheme.colorScheme.primary else Color.Gray,
                                shape = CircleShape,
                            ),
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Example: Move programmatically to next page
        Button(onClick = {
            scope.launch {
                val nextPage = (pagerState.currentPage + 1) % pagerState.pageCount
                pagerState.animateScrollToPage(nextPage)
            }
        }) {
            Text("Next Page")
        }
    }
}
