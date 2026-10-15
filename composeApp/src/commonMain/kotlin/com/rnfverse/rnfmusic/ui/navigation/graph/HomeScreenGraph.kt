package com.rnfverse.rnfmusic.ui.navigation.graph

import androidx.compose.foundation.layout.PaddingValues
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.rnfverse.rnfmusic.ui.navigation.destination.home.AppLogDestination
import com.rnfverse.rnfmusic.ui.navigation.destination.home.CreditDestination
import com.rnfverse.rnfmusic.ui.navigation.destination.home.ListenTogetherDestination
import com.rnfverse.rnfmusic.ui.navigation.destination.home.ListenTogetherSettingsDestination
import com.rnfverse.rnfmusic.ui.navigation.destination.home.MoodDestination
import com.rnfverse.rnfmusic.ui.navigation.destination.home.NotificationDestination
import com.rnfverse.rnfmusic.ui.navigation.destination.home.RecentlySongsDestination
import com.rnfverse.rnfmusic.ui.navigation.destination.home.SettingsDestination
import com.rnfverse.rnfmusic.ui.screen.home.ListenTogetherScreen
import com.rnfverse.rnfmusic.ui.screen.home.ListenTogetherSettingsScreen
import com.rnfverse.rnfmusic.ui.screen.home.MoodScreen
import com.rnfverse.rnfmusic.ui.screen.home.NotificationScreen
import com.rnfverse.rnfmusic.ui.screen.home.RecentlySongsScreen
import com.rnfverse.rnfmusic.ui.screen.home.SettingScreen
import com.rnfverse.rnfmusic.ui.screen.other.AppLogScreen
import com.rnfverse.rnfmusic.ui.screen.other.CreditScreen

fun NavGraphBuilder.homeScreenGraph(
    innerPadding: PaddingValues,
    navController: NavController,
) {
    composable<CreditDestination> {
        CreditScreen(
            paddingValues = innerPadding,
            navController = navController,
        )
    }
    composable<AppLogDestination> {
        AppLogScreen(
            paddingValues = innerPadding,
            navController = navController,
        )
    }
    composable<MoodDestination> { entry ->
        val params = entry.toRoute<MoodDestination>().params
        MoodScreen(
            navController = navController,
            params = params,
        )
    }
    composable<ListenTogetherDestination> {
        ListenTogetherScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
    composable<ListenTogetherSettingsDestination> {
        ListenTogetherSettingsScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
    composable<NotificationDestination> {
        NotificationScreen(
            navController = navController,
        )
    }
    composable<RecentlySongsDestination> {
        RecentlySongsScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
    composable<SettingsDestination> {
        SettingScreen(
            navController = navController,
            innerPadding = innerPadding,
        )
    }
}