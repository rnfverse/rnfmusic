package com.rnfverse.data.io

import com.rnfverse.data.db.documentDirectory
import okio.FileSystem

actual fun fileSystem(): FileSystem = FileSystem.SYSTEM
actual fun fileDir(): String = documentDirectory() + "/RnfMusic"