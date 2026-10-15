package com.rnfverse.kotlinytmusicscraper.models.body

import com.rnfverse.kotlinytmusicscraper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class AccountMenuBody(
    val context: Context,
    val deviceTheme: String = "DEVICE_THEME_SELECTED",
    val userInterfaceTheme: String = "USER_INTERFACE_THEME_DARK",
)