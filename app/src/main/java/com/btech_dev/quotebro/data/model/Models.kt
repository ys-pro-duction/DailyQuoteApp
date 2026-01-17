package com.btech_dev.quotebro.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Quote(
    val id: Long? = null,
    val content: String,
    val author: String,
    val category: String,
    val created_at: String? = null
)

@Serializable
data class Profile(
    val id: String,
    val full_name: String? = null,
    val avatar_url: String? = null,
    val updated_at: String? = null
)

@Serializable
data class Favorite(
    val id: Long? = null,
    val user_id: String,
    val quote_id: Long,
    val created_at: String? = null
)

@Serializable
data class Collection(
    val id: Long? = null,
    val user_id: String,
    val name: String,
    val description: String? = null,
    val created_at: String? = null
)

@Serializable
data class CollectionItem(
    val id: Long? = null,
    val collection_id: Long,
    val quote_id: Long,
    val added_at: String? = null
)
