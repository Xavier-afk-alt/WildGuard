package com.example.wildguard.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wildguard.data.newsList
import kotlinx.coroutines.delay

//possible use horizontalpager for random 3 news card then
//show 1 "see more" card -> directly navigate to more news page with lazycolumn feature as well
// (recommended since need to consider for future situations when news data keep increasing,
// horizontal pager cant capable to show all in once)
@Composable
fun NewsSection(){

    val pagerState = rememberPagerState(

        pageCount = {

            newsList.size

        }

    )

    LaunchedEffect(Unit){

        while(true){

            delay(4000)

            val next =

                (pagerState.currentPage + 1) % newsList.size

            pagerState.animateScrollToPage(next)

        }

    }

    Column{

        Text(

            text = "News",

            style = MaterialTheme.typography.titleLarge

        )

        Spacer(Modifier.height(8.dp))

        HorizontalPager(

            state = pagerState

        ){ page ->

            NewsCard(

                newsList[page]

            )

        }

        Spacer(Modifier.height(8.dp))

        PageIndicator(

            currentPage = pagerState.currentPage,

            pageCount = newsList.size

        )

    }

}