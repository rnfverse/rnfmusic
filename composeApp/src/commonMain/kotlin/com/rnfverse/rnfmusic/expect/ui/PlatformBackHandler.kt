package com.rnfverse.rnfmusic.expect.ui

import androidx.compose.runtime.Composable

@Composable
internal expect fun PlatformBackHandler(
    enabled: Boolean,
    onBack: () -> Unit,
)
