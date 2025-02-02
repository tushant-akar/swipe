package com.tushant.swipe.utils

import android.content.Context
import android.net.Uri
import java.io.File
import java.io.InputStream
import java.io.FileOutputStream

fun uriToFile(context: Context, uri: Uri): File? {
    val contentResolver = context.contentResolver
    val inputStream: InputStream? = contentResolver.openInputStream(uri)
    inputStream?.use { stream ->
        val tempFile = File.createTempFile("temp_image", ".jpg", context.cacheDir)
        val outputStream = FileOutputStream(tempFile)
        stream.copyTo(outputStream)
        outputStream.close()
        return tempFile
    }
    return null
}