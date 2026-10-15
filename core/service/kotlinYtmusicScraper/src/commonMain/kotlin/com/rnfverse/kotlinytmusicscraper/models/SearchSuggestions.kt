package com.rnfverse.kotlinytmusicscraper.models

data class SearchSuggestions(
    val queries: List<String>,
    val recommendedItems: List<YTItem>,
)