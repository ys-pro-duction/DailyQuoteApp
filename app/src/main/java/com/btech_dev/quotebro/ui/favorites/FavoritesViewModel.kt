package com.btech_dev.quotebro.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.btech_dev.quotebro.data.model.Quote
import com.btech_dev.quotebro.data.model.Collection
import com.btech_dev.quotebro.data.repository.QuoteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val favoriteQuotes: List<Quote> = emptyList(),
    val collections: List<Collection> = emptyList(),
    val selectedCollection: Collection? = null,
    val collectionQuotes: List<Quote> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val showCreateCollectionDialog: Boolean = false,
    val activeQuoteCollectionIds: Set<Long> = emptySet()
)

class FavoritesViewModel(
    private val repository: QuoteRepository = QuoteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        fetchFavorites()
        fetchCollections()
    }

    fun fetchFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val quotes = repository.getFavorites()
                _uiState.update { it.copy(favoriteQuotes = quotes, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun fetchCollections() {
        viewModelScope.launch {
            try {
                val collections = repository.getCollections()
                _uiState.update { it.copy(collections = collections) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
    
    fun createCollection(name: String, description: String?) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                repository.createCollection(name, description)
                fetchCollections()
                _uiState.update { it.copy(isLoading = false, showCreateCollectionDialog = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun toggleFavorite(quote: Quote) {
        viewModelScope.launch {
            val quoteId = quote.id ?: return@launch
            
            val currentFavorites = _uiState.value.favoriteQuotes
            val isLiked = currentFavorites.any { it.id == quoteId }
            
            // Optimistic update
            if (isLiked) {
                _uiState.update { it.copy(favoriteQuotes = currentFavorites.filter { q -> q.id != quoteId }) }
            } else {
                _uiState.update { it.copy(favoriteQuotes = currentFavorites + quote) }
            }
            
            try {
                repository.toggleFavorite(quoteId)
            } catch (e: Exception) {
                // Revert
                 _uiState.update { it.copy(favoriteQuotes = currentFavorites, error = e.message) }
            }
        }
    }
    
    fun fetchCollectionsForQuote(quoteId: Long) {
        viewModelScope.launch {
            try {
                val collectionIds = repository.getCollectionsForQuote(quoteId)
                _uiState.update { it.copy(activeQuoteCollectionIds = collectionIds) }
            } catch (e: Exception) {
               // Ignore
            }
        }
    }

    fun toggleQuoteInCollection(quoteId: Long, collectionId: Long) {
        viewModelScope.launch {
            val isPresent = _uiState.value.activeQuoteCollectionIds.contains(collectionId)
            
            // Optimistic update
            _uiState.update { 
                val current = it.activeQuoteCollectionIds.toMutableSet()
                if (isPresent) current.remove(collectionId) else current.add(collectionId)
                 it.copy(activeQuoteCollectionIds = current)
            }
            
            try {
                if (isPresent) {
                    repository.removeQuoteFromCollection(collectionId, quoteId)
                    // If we removed from the currently viewed collection, remove from list
                    if (_uiState.value.selectedCollection?.id == collectionId) {
                        _uiState.update { 
                            it.copy(collectionQuotes = it.collectionQuotes.filter { q -> q.id != quoteId })
                        }
                    }
                } else {
                    repository.addQuoteToCollection(collectionId, quoteId)
                }
            } catch (e: Exception) {
                // Revert
                 _uiState.update { 
                   val current = it.activeQuoteCollectionIds.toMutableSet()
                    if (isPresent) current.add(collectionId) else current.remove(collectionId)
                    it.copy(activeQuoteCollectionIds = current, error = e.message)
               }
            }
        }
    }
    
    fun showCreateCollectionDialog() {
        _uiState.update { it.copy(showCreateCollectionDialog = true) }
    }
    
    fun hideCreateCollectionDialog() {
        _uiState.update { it.copy(showCreateCollectionDialog = false) }
    }
    
    fun selectCollection(collection: Collection) {
        _uiState.update { it.copy(selectedCollection = collection, isLoading = true) }
        viewModelScope.launch {
            try {
                val quotes = collection.id?.let { repository.getQuotesByCollection(it) } ?: emptyList()
                _uiState.update { it.copy(collectionQuotes = quotes, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    fun clearSelectedCollection() {
        _uiState.update { it.copy(selectedCollection = null, collectionQuotes = emptyList()) }
    }
    
    fun fetchQuotesForCollection(collectionId: Long) {
        _uiState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            try {
                val quotes = repository.getQuotesByCollection(collectionId)
                _uiState.update { it.copy(collectionQuotes = quotes, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    // Function to remove favorite if needed, or refresh
    fun refresh() {
        fetchFavorites()
        fetchCollections()
    }

    fun deleteCollection(collectionId: Long, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteCollection(collectionId)
                fetchCollections()
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }
}
