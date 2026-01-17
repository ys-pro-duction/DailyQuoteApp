package com.btech_dev.quotebro.ui.home

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

data class HomeUiState(
    val quotes: List<Quote> = emptyList(),
    val likedQuoteIds: Set<Long> = emptySet(),
    val activeQuoteCollectionIds: Set<Long> = emptySet(), // Added
    val collections: List<Collection> = emptyList(),
    val selectedCategory: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val repository: QuoteRepository = QuoteRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        fetchQuotes()
        fetchQuotes()
        fetchCollections()
        fetchFavorites()
    }

    fun fetchQuotes() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val category = _uiState.value.selectedCategory
                val quotes = if (category == null || category == "For You") {
                    repository.getQuotes()
                } else {
                    repository.getQuotesByCategory(category)
                }
                _uiState.update { it.copy(quotes = quotes, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }

    fun selectCategory(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        fetchQuotes()
    }

    fun toggleFavorite(quote: Quote) {
        viewModelScope.launch {
            val quoteId = quote.id ?: return@launch
            
            // Optimistic update
            _uiState.update { state ->
                val currentIds = state.likedQuoteIds.toMutableSet()
                if (currentIds.contains(quoteId)) {
                    currentIds.remove(quoteId)
                } else {
                    currentIds.add(quoteId)
                }
                state.copy(likedQuoteIds = currentIds)
            }

            try {
                repository.toggleFavorite(quoteId)
            } catch (e: Exception) {
                // Revert on failure
                _uiState.update { state ->
                    val currentIds = state.likedQuoteIds.toMutableSet()
                    if (currentIds.contains(quoteId)) {
                        currentIds.remove(quoteId)
                    } else {
                        currentIds.add(quoteId)
                    }
                    state.copy(likedQuoteIds = currentIds)
                }
            }
        }
    }

    private fun fetchFavorites() {
        viewModelScope.launch {
            try {
                val ids = repository.getFavoriteIds()
                _uiState.update { it.copy(likedQuoteIds = ids) }
            } catch (e: Exception) {
                // Handle error or ignore
            }
        }
    }
    
    fun fetchCollections() {
        viewModelScope.launch {
            try {
                val collections = repository.getCollections()
                _uiState.update { it.copy(collections = collections) }
            } catch (e: Exception) {
                // Silently fail, collections are optional
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

    fun fetchCollectionsForQuote(quoteId: Long) {
        viewModelScope.launch {
            try {
                val ids = repository.getCollectionsForQuote(quoteId)
                _uiState.update { it.copy(activeQuoteCollectionIds = ids) }
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
}
