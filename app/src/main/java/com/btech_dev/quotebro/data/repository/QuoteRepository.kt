package com.btech_dev.quotebro.data.repository

import com.btech_dev.quotebro.data.model.Quote
import com.btech_dev.quotebro.data.model.Favorite
import com.btech_dev.quotebro.data.model.Collection
import com.btech_dev.quotebro.data.model.CollectionItem
import com.btech_dev.quotebro.data.remote.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuoteRepository {

    private val postgrest = SupabaseClient.client.postgrest
    private val auth = SupabaseClient.client.auth

    suspend fun getQuotes(): List<Quote> = withContext(Dispatchers.IO) {
        postgrest["quotes"].select().decodeList<Quote>()
    }

    suspend fun getQuotesByCategory(category: String): List<Quote> = withContext(Dispatchers.IO) {
        postgrest["quotes"].select {
            filter {
                eq("category", category)
            }
        }.decodeList<Quote>()
    }

    suspend fun toggleFavorite(quoteId: Long) = withContext(Dispatchers.IO) {
        val userId = auth.currentUserOrNull()?.id ?: return@withContext null

        // Check if already favorited
        val existing = postgrest["favorites"].select {
            filter {
                eq("user_id", userId)
                eq("quote_id", quoteId)
            }
        }.decodeSingleOrNull<Favorite>()

        if (existing != null) {
            postgrest["favorites"].delete {
                filter {
                    eq("user_id", userId)
                    eq("quote_id", quoteId)
                }
            }
        } else {
            postgrest["favorites"].insert(Favorite(user_id = userId, quote_id = quoteId))
        }
    }

    suspend fun getFavoriteIds(): Set<Long> = withContext(Dispatchers.IO) {
        val userId = auth.currentUserOrNull()?.id ?: return@withContext emptySet()
        postgrest["favorites"].select {
            filter { eq("user_id", userId) }
        }.decodeList<Favorite>().map { it.quote_id }.toSet()
    }
    
    suspend fun getFavorites(): List<Quote> = withContext(Dispatchers.IO) {
        val userId = auth.currentUserOrNull()?.id ?: return@withContext emptyList()
        
        // This is a join. In Supabase KT we can use select with columns
        // or fetch IDs and then fetch quotes. For simplicity with the current schema:
        val favoriteIds = postgrest["favorites"].select {
            filter { eq("user_id", userId) }
        }.decodeList<Favorite>().map { it.quote_id }

        if (favoriteIds.isEmpty()) return@withContext emptyList()

        postgrest["quotes"].select {
            filter {
                isIn("id", favoriteIds)
            }
        }.decodeList<Quote>()
    }

    suspend fun createCollection(name: String, description: String?) = withContext(Dispatchers.IO) {
        val userId = auth.currentUserOrNull()?.id ?: throw Exception("User not authenticated")
        postgrest["collections"].insert(Collection(user_id = userId, name = name, description = description))
    }

    suspend fun getCollections(): List<Collection> = withContext(Dispatchers.IO) {
        val userId = auth.currentUserOrNull()?.id ?: return@withContext emptyList()
        postgrest["collections"].select {
            filter { eq("user_id", userId) }
        }.decodeList<Collection>()
    }

    suspend fun addQuoteToCollection(collectionId: Long, quoteId: Long) = withContext(Dispatchers.IO) {
        // Check if already in collection
        val existing = postgrest["collection_items"].select {
            filter {
                eq("collection_id", collectionId)
                eq("quote_id", quoteId)
            }
        }.decodeSingleOrNull<CollectionItem>()
        
        if (existing == null) {
            postgrest["collection_items"].insert(CollectionItem(collection_id = collectionId, quote_id = quoteId))
        }
        // If already exists, silently do nothing
    }
    
    suspend fun getQuotesByCollection(collectionId: Long): List<Quote> = withContext(Dispatchers.IO) {
        // Get all quote IDs in this collection
        val collectionItems = postgrest["collection_items"].select {
            filter { eq("collection_id", collectionId) }
        }.decodeList<CollectionItem>()
        
        val quoteIds = collectionItems.map { it.quote_id }
        
        if (quoteIds.isEmpty()) return@withContext emptyList()
        
        // Fetch the actual quotes
        postgrest["quotes"].select {
            filter {
                isIn("id", quoteIds)
            }
        }.decodeList<Quote>()
    }

    suspend fun removeQuoteFromCollection(collectionId: Long, quoteId: Long) = withContext(Dispatchers.IO) {
        postgrest["collection_items"].delete {
            filter {
                eq("collection_id", collectionId)
                eq("quote_id", quoteId)
            }
        }
    }

    suspend fun getCollectionsForQuote(quoteId: Long): Set<Long> = withContext(Dispatchers.IO) {
        postgrest["collection_items"].select {
            filter { eq("quote_id", quoteId) }
        }.decodeList<CollectionItem>().map { it.collection_id }.toSet()
    }

    suspend fun deleteCollection(collectionId: Long) = withContext(Dispatchers.IO) {
        postgrest["collections"].delete {
            filter { eq("id", collectionId) }
        }
    }

    suspend fun getRandomQuote(): Quote? = withContext(Dispatchers.IO) {
        // Fetch a batch of recent quotes and pick one randomly
        postgrest["quotes"].select {
            order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
            limit(50)
        }.decodeList<Quote>().randomOrNull()
    }
}
