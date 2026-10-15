package com.rnfverse.rnfmusic.expect

actual fun getDownloadFolderPath(): String = System.getProperty("user.home") + "/Downloads"