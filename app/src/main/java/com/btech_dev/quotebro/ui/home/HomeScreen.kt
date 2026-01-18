package com.btech_dev.quotebro.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.btech_dev.quotebro.data.model.Quote
import com.btech_dev.quotebro.ui.theme.BackgroundWhite
import com.btech_dev.quotebro.ui.theme.Black
import com.btech_dev.quotebro.ui.theme.DarkGray
import com.btech_dev.quotebro.ui.theme.ErrorRed
import com.btech_dev.quotebro.ui.theme.LightGray
import com.btech_dev.quotebro.ui.theme.PrimaryColor
import com.btech_dev.quotebro.ui.theme.QuoteBroTheme
import com.btech_dev.quotebro.ui.theme.SecondaryColor
import com.btech_dev.quotebro.ui.theme.SurfaceLightGray
import com.btech_dev.quotebro.ui.theme.SurfaceWhite
import com.btech_dev.quotebro.ui.theme.TextBlack
import com.btech_dev.quotebro.ui.theme.TextDarkSlate
import com.btech_dev.quotebro.ui.theme.TextGray
import com.btech_dev.quotebro.ui.theme.White
import com.btech_dev.quotebro.ui.theme.icons.Bookmark
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import com.btech_dev.quotebro.ui.theme.icons.Quote as QuoteIcon

private val LightGradients = listOf(
    listOf(Color(0xFFE3F2FD), Color(0xFF90CAF9)),
    listOf(Color(0xFFF3E5F5), Color(0xFFCE93D8)),
    listOf(Color(0xFFE0F2F1), Color(0xFF80CBC4)),
    listOf(Color(0xFFFFF3E0), Color(0xFFFFCC80)),
    listOf(Color(0xFFFCE4EC), Color(0xFFF48FB1)),
    listOf(Color(0xFFF1F8E9), Color(0xFFAED581))
)

private val DarkGradients = listOf(
    listOf(Color(0xFF1E3A5F), Color(0xFF2E5A8F)),
    listOf(Color(0xFF3D2B4A), Color(0xFF5C3A5E)),
    listOf(Color(0xFF2A4A47), Color(0xFF3D6B66)),
    listOf(Color(0xFF4A3520), Color(0xFF6B5030)),
    listOf(Color(0xFF4A2A3A), Color(0xFF693A50)),
    listOf(Color(0xFF3A4A2A), Color(0xFF5A6B3A))
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHomeContent(
    viewModel: HomeViewModel = viewModel(),
    onShareQuote: (String, String) -> Unit = { _, _ -> }
) {
    val uiState by viewModel.uiState.collectAsState()

    var showCollectionsSheet by remember { mutableStateOf(false) }
    var selectedQuoteForCollection by remember { mutableStateOf<Quote?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.surface)) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Button(onClick = { viewModel.fetchQuotes() }) {
                        Text("Retry")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface),
                contentPadding = PaddingValues(
                    top = 120.dp,
                    bottom = 120.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    val qod = uiState.quoteOfTheDay
                    QuoteOfTheDay(
                        quote = qod,
                        isLiked = qod != null && uiState.likedQuoteIds.contains(qod.id),
                        onFavoriteClick = { if (qod != null) viewModel.toggleFavorite(qod) },
                        onShareQuote = {onShareQuote(qod!!.content, qod.author)}
                    )
                }
                item {
                    CategoriesSection(
                        selectedCategory = uiState.selectedCategory ?: "For You",
                        onCategorySelected = { viewModel.selectCategory(it) }
                    )
                }
                items(uiState.quotes) { quote ->
                    QuoteCard(
                        quote = quote,
                        isLiked = uiState.likedQuoteIds.contains(quote.id),
                        onFavoriteClick = { viewModel.toggleFavorite(quote) },
                        onShareClick = { onShareQuote(quote.content, quote.author) },
                        onAddToCollection = {
                            selectedQuoteForCollection = quote
                            if (quote.id != null) viewModel.fetchCollectionsForQuote(quote.id)
                            showCollectionsSheet = true
                        }
                    )
                }
            }
        }

        // Collections Bottom Sheet
        if (showCollectionsSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    coroutineScope.launch {
                        sheetState.hide()
                        showCollectionsSheet = false
                    }
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                CollectionsBottomSheet(
                    collections = uiState.collections,
                    activeQuoteCollectionIds = uiState.activeQuoteCollectionIds,
                    onCollectionSelected = { collection ->
                        selectedQuoteForCollection?.id?.let { quoteId ->
                            collection.id?.let { collectionId ->
                                collection.id?.let { collectionId ->
                                    viewModel.toggleQuoteInCollection(quoteId, collectionId)
                                }
                            }
                        }
                    },
                    onDismiss = {
                        coroutineScope.launch {
                            sheetState.hide()
                            showCollectionsSheet = false
                        }
                    }
                )
            }
        }
    }
}

// Keep old HomeScreen for compatibility but mark as deprecated
@Deprecated("Use MainHomeContent with proper navigation instead")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    onLogOut: () -> Unit = {}
) {
    MainHomeContent(viewModel = viewModel)
}

@Composable
fun TopBar() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .shadow(0.4.dp).statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            ,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(40.dp),
                shape = CircleShape,
                color = PrimaryColor.copy(alpha = 0.1f)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = QuoteIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = PrimaryColor
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = "QuoteVault",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
fun QuoteOfTheDay(quote: Quote?, isLiked: Boolean = false, onFavoriteClick: () -> Unit = {}, onShareQuote: () -> Unit = {}) {
    if (quote == null) return

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(SecondaryColor, PrimaryColor)
                )
            )
            .clickable(){onShareQuote()}
            .padding(24.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Surface(
                color = White.copy(alpha = 0.2f),
                shape = RoundedCornerShape(50)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        tint = White
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "QUOTE OF THE DAY",
                        style = MaterialTheme.typography.labelSmall,
                        color = White
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "\"${quote.content}\"",
                style = MaterialTheme.typography.titleLarge,
                color = White,
                lineHeight = 28.sp,
                maxLines = 3
            )
            Spacer(modifier = Modifier.weight(1f))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = quote.author,
                    style = MaterialTheme.typography.bodyMedium,
                    color = White.copy(alpha = 0.8f)
                )
                IconButton(onClick = onFavoriteClick) {
                    Icon(
                        imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isLiked) ErrorRed else White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun CategoriesSection(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    val categories = listOf("For You", "Motivation", "Love", "Wisdom", "Life", "Philosophy")

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            val isSelected = category == selectedCategory
            Surface(
                onClick = { onCategorySelected(category) },
                shape = RoundedCornerShape(50),
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.height(40.dp)
            ) {
                Box(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = category,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun QuoteCard(
    quote: Quote,
    isLiked: Boolean,
    onFavoriteClick: () -> Unit,
    onShareClick: () -> Unit = {},
    onAddToCollection: (Long) -> Unit = {}
) {
    // Check if theme is dark by checking background brightness
    val backgroundColor = MaterialTheme.colorScheme.surface
    val isDarkTheme = (backgroundColor.red + backgroundColor.green + backgroundColor.blue) / 3f < 0.5f
    val backgroundGradient = remember(quote.id, isDarkTheme) {
        val index = (quote.id?.hashCode() ?: 0).absoluteValue % LightGradients.size
        val gradients = if (isDarkTheme) DarkGradients else LightGradients
        Brush.linearGradient(gradients[index])
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(backgroundGradient)
                    .clickable { onShareClick() }// Placeholder for image
            ) {
                // Overlay removed for light theme
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = if (isDarkTheme) 0.15f else 0.05f),
                shape = RoundedCornerShape(50)
            ) {
                Text(
                    text = quote.category,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

                    Column {
                        Text(
                            text = quote.content,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 30.sp,
                            maxLines = 4
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "— ${quote.author}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {

                    Box(contentAlignment = Alignment.CenterStart) {
                        IconButton({ onFavoriteClick() }) {
                            Icon(
                                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Favorite",
                                tint = if (isLiked) ErrorRed else MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .size(20.dp)
                            )
                        }
                        Text(
                            text = "Like",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(start = 42.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton({ onShareClick() }) {
                        Icon(
                            imageVector = com.btech_dev.quotebro.ui.theme.icons.Share,
                            contentDescription = "Share",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(20.dp)
                        )
                    }

                }
                IconButton(onClick = { onAddToCollection(-1) }) { // Pass -1 to trigger bottom sheet
                    Icon(
                        imageVector = Bookmark,
                        contentDescription = "Add to collection",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionsBottomSheet(
    collections: List<com.btech_dev.quotebro.data.model.Collection>,
    activeQuoteCollectionIds: Set<Long> = emptySet(),
    onCollectionSelected: (com.btech_dev.quotebro.data.model.Collection) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(bottom = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Add to Collection",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        HorizontalDivider(color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f))

        if (collections.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No collections yet",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(collections) { collection ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = if (collection.id != null && activeQuoteCollectionIds.contains(
                                collection.id
                            )
                        ) BorderStroke(2.dp, PrimaryColor) else null,
                        onClick = { onCollectionSelected(collection) }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = PrimaryColor.copy(alpha = 0.1f),
                                modifier = Modifier.size(48.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Bookmark,
                                        contentDescription = null,
                                        tint = PrimaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = collection.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (collection.description != null) {
                                    Text(
                                        text = collection.description,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    QuoteBroTheme {
        HomeScreen()
    }
}
