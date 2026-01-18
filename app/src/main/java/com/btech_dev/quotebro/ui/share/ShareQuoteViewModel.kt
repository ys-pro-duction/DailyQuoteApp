package com.btech_dev.quotebro.ui.share

import android.content.ClipData
import android.content.ClipboardManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.OutputStream

enum class ShareStyle {
    MINIMALIST, MOODY, VIBRANT
}

enum class SharePlatform {
    WHATSAPP, COPY, INSTAGRAM, MORE
}

data class Quote(val text: String, val author: String)

class ShareQuoteViewModel : ViewModel() {
    private val _selectedStyle = MutableStateFlow(ShareStyle.VIBRANT)
    val selectedStyle: StateFlow<ShareStyle> = _selectedStyle

    private val _quote = MutableStateFlow(
        Quote(
            "The only way to do great work is to love what you do.",
            "STEVE JOBS"
        )
    )
    val quote: StateFlow<Quote> = _quote

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving

    fun setQuote(text: String, author: String) {
        _quote.value = Quote(text, author)
    }

    fun setStyle(style: ShareStyle) {
        _selectedStyle.value = style
    }

    fun copyToClipboard(context: Context) {
        val quoteData = _quote.value
        val clipboardManager =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData =
            ClipData.newPlainText("Quote", "\"${quoteData.text}\"\n\n— ${quoteData.author}")
        clipboardManager.setPrimaryClip(clipData)
        Toast.makeText(context, "Quote copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    fun shareImage(context: Context, bitmap: Bitmap, platform: SharePlatform) {
        viewModelScope.launch {
            val uri = getImageUri(context, bitmap) ?: return@launch
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "image/png"
                putExtra(Intent.EXTRA_STREAM, uri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                if (platform == SharePlatform.WHATSAPP) {
                    setPackage("com.whatsapp")
                } else if (platform == SharePlatform.INSTAGRAM) {
                    setPackage("com.instagram.android")
                }
            }
            context.startActivity(Intent.createChooser(intent, "Share Quote"))
        }
    }

    suspend fun saveToGallery(context: Context, bitmap: Bitmap): Result<Unit> =
        withContext(Dispatchers.IO) {
            _isSaving.value = true
            try {
                val filename = "Quote_${System.currentTimeMillis()}.png"
                var outputStream: OutputStream? = null
                val imageUri: Uri?

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    val contentValues = ContentValues().apply {
                        put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                        put(MediaStore.MediaColumns.MIME_TYPE, "image/png")
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                    }
                    imageUri = context.contentResolver.insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    outputStream = imageUri?.let { context.contentResolver.openOutputStream(it) }
                } else {
                    val imagesDir =
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                    val image = java.io.File(imagesDir, filename)
                    imageUri = Uri.fromFile(image)
                    outputStream = java.io.FileOutputStream(image)
                }

                outputStream?.use {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                }
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            } finally {
                _isSaving.value = false
            }
        }

    private fun getImageUri(context: Context, bitmap: Bitmap): Uri? {
        // Save temporary image for sharing
        val path = MediaStore.Images.Media.insertImage(
            context.contentResolver,
            bitmap,
            "Shared Quote",
            null
        )
        return Uri.parse(path)
    }
}
