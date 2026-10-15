package com.rnfverse.rnfmusic.ui.component

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.rnfverse.rnfmusic.expect.ui.PlatformBackdrop
import com.rnfverse.rnfmusic.viewModel.SharedViewModel
import kotlin.reflect.KClass

@Composable
actual fun LiquidGlassAppBottomNavigationBar(
    startDestination: Any,
    navController: NavController,
    backdrop: PlatformBackdrop,
    viewModel: SharedViewModel,
    isScrolledToTop: Boolean,
    showAnalyticsTab: Boolean,
    showMixForYouTab: Boolean,
    onOpenNowPlaying: () -> Unit,
    liquidGlass: Boolean,
    reloadDestinationIfNeeded: (KClass<*>) -> Unit
) {
}