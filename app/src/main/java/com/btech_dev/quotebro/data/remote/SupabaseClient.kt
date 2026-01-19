package com.btech_dev.quotebro.data.remote

import android.content.Context
import com.btech_dev.quotebro.BuildConfig
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import io.github.jan.supabase.SupabaseClient as SupabaseKtClient
import androidx.core.content.edit

object SupabaseClient {
    private var _client: SupabaseKtClient? = null

    fun initialize(context: Context) {
        if (_client == null) {
            _client = createSupabaseClient(
                supabaseUrl = BuildConfig.SUPABASE_URL,
                supabaseKey = BuildConfig.SUPABASE_KEY
            ) {
                install(Auth) {
                    sessionManager = object : io.github.jan.supabase.auth.SessionManager {
                        private val prefs =
                            context.getSharedPreferences("supabase_session", Context.MODE_PRIVATE)

                        override suspend fun saveSession(session: io.github.jan.supabase.auth.user.UserSession) {
                            prefs.edit { putString("session", Json.encodeToString(session)) }
                        }

                        override suspend fun loadSession(): io.github.jan.supabase.auth.user.UserSession? {
                            val sessionStr = prefs.getString("session", null) ?: return null
                            return try {
                                Json.decodeFromString(sessionStr)
                            } catch (e: Exception) {
                                null
                            }
                        }

                        override suspend fun deleteSession() {
                            prefs.edit { remove("session") }
                        }
                    }
                }
                install(Postgrest)
                install(Realtime)
                install(Storage)
            }
        }
    }

    val client: SupabaseKtClient
        get() = _client
            ?: throw IllegalStateException("SupabaseClient.initialize(context) must be called first")
}
