package com.rnfverse.kotlinytmusicscraper.models.body

import com.rnfverse.kotlinytmusicscraper.models.Context
import kotlinx.serialization.Serializable

@Serializable
data class NotificationBody(
    val context: Context,
    val notificationsMenuRequestType: String = "NOTIFICATIONS_MENU_REQUEST_TYPE_INBOX",
)