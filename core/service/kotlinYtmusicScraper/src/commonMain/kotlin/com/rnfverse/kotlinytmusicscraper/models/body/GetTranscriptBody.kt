package com.rnfverse.kotlinytmusicscraper.models.body

import com.rnfverse.kotlinytmusicscraper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class GetTranscriptBody(
    val context: Context,
    val params: String,
)