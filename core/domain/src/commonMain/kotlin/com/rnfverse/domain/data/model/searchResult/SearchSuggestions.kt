package com.rnfverse.domain.data.model.searchResult

import com.rnfverse.domain.data.type.SearchResultType

data class SearchSuggestions(
    val queries: List<String>,
    val recommendedItems: List<SearchResultType>,
)