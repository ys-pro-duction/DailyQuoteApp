package com.btech_dev.quotebro.ui.navigation

import kotlinx.serialization.Serializable

// Define routes using type-safe navigation
@Serializable
sealed class Screen {
    @Serializable
    data object Auth : Screen()
    
    @Serializable
    data object Home : Screen()
    
    @Serializable
    data object Favorites : Screen()
    
    @Serializable
    data object Settings : Screen()
    
    @Serializable
    data class CollectionDetails(val collectionId: Long, val collectionName: String) : Screen()
    
    @Serializable
    data class ShareQuote(val quoteText: String, val quoteAuthor: String) : Screen()
}