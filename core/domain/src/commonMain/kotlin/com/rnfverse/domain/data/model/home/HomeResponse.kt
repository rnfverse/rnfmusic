package com.rnfverse.domain.data.model.home

import com.rnfverse.domain.data.model.home.chart.Chart
import com.rnfverse.domain.data.model.mood.Mood
import com.rnfverse.domain.utils.Resource

data class HomeResponse(
    val homeItem: Resource<ArrayList<HomeItem>>,
    val exploreMood: Resource<Mood>,
    val exploreChart: Resource<Chart>,
)