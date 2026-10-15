package com.rnfverse.rnfmusic.expect

import com.rnfverse.logger.Logger
import com.rnfverse.rnfmusic.ui.mini_player.MiniPlayerManager

actual fun toggleMiniPlayer() {
    Logger.d("MiniPlayer", "Toggle called, current state: ${MiniPlayerManager.isOpen}")
    MiniPlayerManager.isOpen = !MiniPlayerManager.isOpen
    Logger.d("MiniPlayer", "New state: ${MiniPlayerManager.isOpen}")
}
