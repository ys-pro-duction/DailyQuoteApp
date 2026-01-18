package com.btech_dev.quotebro.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.btech_dev.quotebro.data.repository.QuoteRepository
import com.btech_dev.quotebro.util.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailyQuoteReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val repository = QuoteRepository()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val quote = repository.getRandomQuote()
                if (quote != null) {
                    NotificationHelper.showQuoteNotification(context, quote)
                }
            } catch (e: Exception) {
            } finally {
                pendingResult.finish()
            }
        }
    }
}
