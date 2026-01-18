package com.btech_dev.quotebro.data.repository

import com.btech_dev.quotebro.data.model.Profile
import com.btech_dev.quotebro.data.remote.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class AuthRepository {

    private val auth = SupabaseClient.client.auth
    private val postgrest = SupabaseClient.client.postgrest

    suspend fun signUp(email: String, password: String, fullName: String) = withContext(Dispatchers.IO) {
        auth.signUpWith(Email) {
            this.email = email
            this.password = password
            data = buildJsonObject { put("full_name", fullName) }

        }
    }

    suspend fun signIn(email: String, password: String) = withContext(Dispatchers.IO) {
        auth.signInWith(Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signOut() = withContext(Dispatchers.IO) {
        auth.signOut()
    }

    suspend fun getCurrentProfile(): Profile? = withContext(Dispatchers.IO) {
        val user = auth.currentUserOrNull() ?: return@withContext null
        postgrest["profiles"].select {
            filter {
                eq("id", user.id)
            }
        }.decodeSingleOrNull<Profile>()
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentSessionOrNull() != null
    }

    suspend fun loadSessionFromStorage(): Boolean {
        return auth.loadFromStorage()
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUserOrNull()?.email
    }
}
