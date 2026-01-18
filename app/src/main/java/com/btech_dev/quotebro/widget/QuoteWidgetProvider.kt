package com.btech_dev.quotebro.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.btech_dev.quotebro.MainActivity
import com.btech_dev.quotebro.R
import com.btech_dev.quotebro.data.model.Quote
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

class QuoteWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // Update all widget instances
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        
        if (intent.action == ACTION_REFRESH_WIDGET) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                android.content.ComponentName(context, QuoteWidgetProvider::class.java)
            )
            onUpdate(context, appWidgetManager, appWidgetIds)
        }
    }

    companion object {
        const val ACTION_REFRESH_WIDGET = "com.btech_dev.quotebro.REFRESH_WIDGET"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            // Load quote from SharedPreferences (same source as HomeViewModel)
            val prefs = context.getSharedPreferences("daily_quote", Context.MODE_PRIVATE)
            val quoteJson = prefs.getString("quote_json", null)

            val views = RemoteViews(context.packageName, R.layout.widget_quote_of_the_day)

            if (quoteJson != null) {
                try {
                    val quote = Json.decodeFromString<Quote>(quoteJson)
                    views.setTextViewText(R.id.widget_quote_text, "\"${quote.content}\"")
                    views.setTextViewText(R.id.widget_author_text, "— ${quote.author}")
                } catch (e: Exception) {
                    views.setTextViewText(R.id.widget_quote_text, "Tap to open QuoteBro")
                    views.setTextViewText(R.id.widget_author_text, "")
                }
            } else {
                views.setTextViewText(R.id.widget_quote_text, "Open app to load quote")
                views.setTextViewText(R.id.widget_author_text, "")
            }

            // Click to open app
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_quote_text, pendingIntent)

            // Refresh button
            val refreshIntent = Intent(context, QuoteWidgetProvider::class.java).apply {
                action = ACTION_REFRESH_WIDGET
            }
            val refreshPendingIntent = PendingIntent.getBroadcast(
                context, 0, refreshIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_refresh_button, refreshPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                android.content.ComponentName(context, QuoteWidgetProvider::class.java)
            )
            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
}
