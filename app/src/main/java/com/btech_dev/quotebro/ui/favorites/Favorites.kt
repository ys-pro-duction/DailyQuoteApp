package com.btech_dev.quotebro.ui.favorites

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.btech_dev.quotebro.data.model.Quote
import com.btech_dev.quotebro.ui.theme.BackgroundWhite
import com.btech_dev.quotebro.ui.theme.Black
import com.btech_dev.quotebro.ui.theme.DarkGray
import com.btech_dev.quotebro.ui.theme.ErrorRed
import com.btech_dev.quotebro.ui.theme.PrimaryColor
import com.btech_dev.quotebro.ui.theme.QuoteBroTheme
import com.btech_dev.quotebro.ui.theme.SegmentedControlBg
import com.btech_dev.quotebro.ui.theme.TextDarkSlate
import com.btech_dev.quotebro.ui.theme.TextGray
import com.btech_dev.quotebro.ui.theme.Transparent
import com.btech_dev.quotebro.ui.theme.White
import com.btech_dev.quotebro.ui.theme.icons.Bookmark
import kotlinx.coroutines.launch
import com.btech_dev.quotebro.ui.theme.icons.Quote as QuoteIcon

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = viewModel(),
    onNavigateToCollectionDetails: (Long, String) -> Unit = { _, _ -> },
    onShareQuote: (String, String) -> Unit = { _, _ -> }
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()
    val uiState by viewModel.uiState.collectAsState()

    // Refresh on entry
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundWhite)
            .statusBarsPadding()
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            SegmentedControl(
                items = listOf("All Favorites", "Collections"),
                pagerState = pagerState,
                onItemSelected = { index ->
                    scope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                },
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.Top
            ) { page ->
                if (page == 0) {
                    if (uiState.isLoading) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator()
                        }
                    } else if (uiState.favoriteQuotes.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("No favorites yet", color = TextGray)
                        }
                    } else {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalItemSpacing = 16.dp
                        ) {
                            items(uiState.favoriteQuotes) { quote ->
                                FavoriteQuoteCard(
                                    quote = quote,
                                    isLiked = true,
                                    onFavoriteClick = { viewModel.toggleFavorite(quote) },
                                    onShareQuote = { onShareQuote(quote.content, quote.author) }
                                )
                            }
                        }
                    }
                } else {
                    // Collections
                    if (uiState.collections.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Bookmark,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = TextGray.copy(alpha = 0.5f)
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = "No collections yet",
                                    color = TextGray,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                                Text(
                                    text = "Tap + to create one",
                                    color = TextGray.copy(alpha = 0.7f),
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    } else {
                        LazyVerticalStaggeredGrid(
                            columns = StaggeredGridCells.Fixed(2),
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalItemSpacing = 16.dp
                        ) {
                            items(uiState.collections) { collection ->
                                CollectionCard(
                                    collection = collection,
                                    onClick = {
                                        collection.id?.let { id ->
                                            onNavigateToCollectionDetails(id, collection.name)
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // FAB for adding collection
        val currentPage by remember { derivedStateOf { pagerState.currentPage } }
        AnimatedVisibility(
            currentPage == 1, modifier = Modifier.align(Alignment.BottomEnd),
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it }),
        ) {
            FloatingActionButton(
                onClick = { viewModel.showCreateCollectionDialog() },
                containerColor = PrimaryColor,
                contentColor = White,
                shape = CircleShape,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 24.dp, bottom = 90.dp)
                    .size(64.dp)
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Collection",
                    modifier = Modifier.size(32.dp)
                )
            }
        }


        // Create Collection Dialog
        if (uiState.showCreateCollectionDialog) {
            CreateCollectionDialog(
                onDismiss = { viewModel.hideCreateCollectionDialog() },
                onCreate = { name, description ->
                    viewModel.createCollection(name, description)
                }
            )
        }
    }
}

@Composable
fun SegmentedControl(

    items: List<String>,
    pagerState: PagerState,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp),
        shape = RoundedCornerShape(50),
        color = SegmentedControlBg
    ) {
        BoxWithConstraints(modifier = Modifier.padding(4.dp)) {
            val maxWidth = maxWidth
            val indicatorWidth = maxWidth / items.size
            // Sliding indicator
            Box(
                modifier = Modifier
                    .offset(x = indicatorWidth * (pagerState.currentPage + pagerState.currentPageOffsetFraction))
                    .width(indicatorWidth)
                    .fillMaxHeight()
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(50))
                    .background(White, shape = RoundedCornerShape(50))
            )

            Row(modifier = Modifier.fillMaxSize()) {
                items.forEachIndexed { index, title ->
                    val isSelected = pagerState.currentPage == index
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onItemSelected(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) PrimaryColor else TextGray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun FavoriteQuoteCard(
    quote: Quote,
    isLiked: Boolean = true,
    onFavoriteClick: () -> Unit = {},
    onShareQuote: () -> Unit
) {
    // For visual variety, we could hash the ID to pick a style, but for now use Default
    // Or we could add a style field to our DB? For now, stick to Default/Simple.

    Surface(
        shape = RoundedCornerShape(32.dp),
        color = White,
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier
                .clickable { onShareQuote() }
                .padding(20.dp)
        ) {
            Icon(
                imageVector = QuoteIcon,
                contentDescription = null,
                tint = PrimaryColor.copy(alpha = 0.3f),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = quote.content,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = TextDarkSlate,
                textAlign = TextAlign.Start,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = quote.author,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Footer or actions
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onFavoriteClick) {
                        Icon(
                            imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Liked",
                            tint = if (isLiked) ErrorRed else TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CollectionCard(
    collection: com.btech_dev.quotebro.data.model.Collection,
    onClick: () -> Unit = {}
) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = White,
        shadowElevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            PrimaryColor.copy(alpha = 0.1f),
                            PrimaryColor.copy(alpha = 0.05f)
                        )
                    )
                )
                .clickable { onClick() }
                .padding(20.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxSize()
            ) {
                Column {
                    Icon(
                        imageVector = Bookmark,
                        contentDescription = null,
                        tint = PrimaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = collection.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextDarkSlate,
                        maxLines = 1
                    )
                    if (collection.description != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = collection.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextGray,
                            maxLines = 2
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionDetailsScreen(
    collectionId: Long,
    collectionName: String,
    onBackClick: () -> Unit,
    onShareQuote: (String, String) -> Unit,
    viewModel: FavoritesViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(collectionId) {
        viewModel.fetchQuotesForCollection(collectionId)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(collectionName, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextDarkSlate
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Transparent,
                    titleContentColor = TextDarkSlate
                )
            )
        },
        containerColor = BackgroundWhite
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.collectionQuotes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Bookmark,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = TextGray.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No quotes in this collection yet",
                        color = TextGray,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        } else {
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalItemSpacing = 16.dp
            ) {
                items(uiState.collectionQuotes) { quote ->
                    val isLiked = uiState.favoriteQuotes.any { it.id == quote.id }
                    FavoriteQuoteCard(
                        quote = quote,
                        isLiked = isLiked,
                        onFavoriteClick = { viewModel.toggleFavorite(quote) },
                        onShareQuote = { onShareQuote(quote.content, quote.author) }
                    )
                }
            }
        }
    }
}

@Composable
fun CreateCollectionDialog(
    onDismiss: () -> Unit,
    onCreate: (String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        containerColor = BackgroundWhite,
        titleContentColor = TextDarkSlate,
        textContentColor = TextDarkSlate,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create Collection",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        focusedLabelColor = PrimaryColor,
                        unfocusedLabelColor = DarkGray,
                        focusedTextColor = Black,
                        unfocusedTextColor = TextDarkSlate,
                    )
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (optional)") },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        focusedLabelColor = PrimaryColor,
                        unfocusedLabelColor = DarkGray,
                        focusedTextColor = Black,
                        unfocusedTextColor = TextDarkSlate,
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onCreate(name, description.ifBlank { null })
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor,
                    disabledContainerColor = PrimaryColor.copy(0.5f)
                ),
                enabled = name.isNotBlank()
            ) {
                Text("Create", color = White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextGray)
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FavoritesScreenPreview() {
    QuoteBroTheme {
        FavoritesScreen()
    }
}
