package com.btech_dev.quotebro.ui.share

import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Build
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.btech_dev.quotebro.ui.theme.AccentBlue
import com.btech_dev.quotebro.ui.theme.GradientOrange
import com.btech_dev.quotebro.ui.theme.GradientPink
import com.btech_dev.quotebro.ui.theme.GradientPurple
import com.btech_dev.quotebro.ui.theme.MoodyDark
import com.btech_dev.quotebro.ui.theme.QuoteBroTheme
import com.btech_dev.quotebro.ui.theme.Transparent
import com.btech_dev.quotebro.ui.theme.icons.Quote as QuoteIcon
import kotlinx.coroutines.launch
import androidx.core.graphics.createBitmap
import com.btech_dev.quotebro.ui.theme.SurfaceLightGray
import com.btech_dev.quotebro.ui.theme.icons.Copy
import com.btech_dev.quotebro.ui.theme.icons.Download
import com.btech_dev.quotebro.ui.theme.icons.Instagram
import com.btech_dev.quotebro.ui.theme.icons.Share
import com.btech_dev.quotebro.ui.theme.icons.Whatsapp

val vibrantGradient = Brush.linearGradient(
    colors = listOf(
        GradientPurple,  // Purple
        GradientPink,  // Pink
        GradientOrange   // Orange
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareQuoteScreen(
    quoteText: String = "",
    quoteAuthor: String = "",
    onBackClick: () -> Unit = {},
    viewModel: ShareQuoteViewModel = viewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val selectedStyle by viewModel.selectedStyle.collectAsStateWithLifecycle()
    val quote by viewModel.quote.collectAsStateWithLifecycle()
    val isSaving by viewModel.isSaving.collectAsStateWithLifecycle()

    var capturedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    // Set the quote when the screen is displayed
    LaunchedEffect(quoteText, quoteAuthor) {
        viewModel.setQuote(quoteText, quoteAuthor)
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Share Quote", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onSurface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface)
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Main Quote Preview Card
            Box(
                modifier = Modifier
                    .padding(horizontal = 32.dp)
                    .aspectRatio(1f)
            ) {
                CapturableContainer(selectedStyle,onBitmapCaptured = { capturedBitmap = it }) {
                    QuotePreviewCard(quote = quote, style = selectedStyle)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Style Label
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = when (selectedStyle) {
                        ShareStyle.VIBRANT -> "Vibrant"
                        ShareStyle.MINIMALIST -> "Minimalist"
                        ShareStyle.MOODY -> "Moody"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = when (selectedStyle) {
                        ShareStyle.VIBRANT -> "Abstract Gradient"
                        ShareStyle.MINIMALIST -> "Clean & Simple"
                        ShareStyle.MOODY -> "Glass Morphism"
                    },
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Style Selection Row
            StyleSelector(
                selectedStyle = selectedStyle,
                onStyleChange = { viewModel.setStyle(it) }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Quick Share Section
            QuickShareButtons(onShare = { platform ->
                if (platform == SharePlatform.COPY){
                    viewModel.copyToClipboard(context)
                }else{
                    capturedBitmap?.let { viewModel.shareImage(context, it, platform) }
                }
            })

            Spacer(modifier = Modifier.height(48.dp))

            // Bottom Action Buttons
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                Button(
                    onClick = {
                        capturedBitmap?.let { viewModel.shareImage(context, it, SharePlatform.MORE) }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AccentBlue, contentColor = Color.White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Icon(Share, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share Image", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = {
                        capturedBitmap?.let {
                            scope.launch {
                                val result = viewModel.saveToGallery(context, it)
                                if (result.isSuccess) {
                                    Toast.makeText(context, "Saved to Photos", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !isSaving
                ) {
                    if (isSaving) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Download, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save to Photos", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Medium)
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun QuotePreviewCard(quote: Quote, style: ShareStyle) {
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .then(Modifier),
        shape = RoundedCornerShape(24.dp),
        color = when (style) {
            ShareStyle.VIBRANT -> Transparent
            ShareStyle.MINIMALIST -> Color.White
            ShareStyle.MOODY -> MoodyDark
        },
        shadowElevation = 2.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (style == ShareStyle.VIBRANT) Modifier.background(vibrantGradient) else Modifier
                )
                .padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = QuoteIcon,
                    contentDescription = null,
                    tint = if (style == ShareStyle.MINIMALIST) Color.Black else Color.White,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = quote.text,
                    style = MaterialTheme.typography.headlineSmall,
                    color = if (style == ShareStyle.MINIMALIST) Color.Black else Color.White,
                    textAlign = TextAlign.Center,
                    lineHeight = 32.sp
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = quote.author,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = (if (style == ShareStyle.MINIMALIST) Color.Black else Color.White).copy(alpha = 0.7f),
                    letterSpacing = 1.5.sp
                )
            }
        }
    }
}

@Composable
fun StyleSelector(selectedStyle: ShareStyle, onStyleChange: (ShareStyle) -> Unit) {
    val styles = listOf(ShareStyle.MINIMALIST, ShareStyle.MOODY, ShareStyle.VIBRANT)
    val pagerState = rememberPagerState(initialPage = styles.indexOf(selectedStyle), pageCount = { 3 })

    LaunchedEffect(pagerState.currentPage) {
        onStyleChange(styles[pagerState.currentPage])
    }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
            contentPadding = PaddingValues(horizontal = (LocalContext.current.resources.displayMetrics.widthPixels / LocalContext.current.resources.displayMetrics.density / 2 - 60).dp),
            pageSpacing = 16.dp
        ) { page ->
            val style = styles[page]
            val isSelected = style == selectedStyle

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.width(120.dp)
            ) {
                Surface(
                    modifier = Modifier
                        .size(120.dp)
                        .then(
                            if (isSelected) Modifier.border(
                                2.dp,
                                AccentBlue,
                                RoundedCornerShape(16.dp)
                            ) else Modifier
                        )
                        .clickable { onStyleChange(style) },
                    shape = RoundedCornerShape(16.dp),
                    color = when (style) {
                        ShareStyle.MINIMALIST -> Color.White
                        ShareStyle.MOODY -> MoodyDark
                        ShareStyle.VIBRANT -> Transparent
                    },
                    border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)) else null,
                    shadowElevation = if (isSelected) 4.dp else 0.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (style == ShareStyle.VIBRANT) Modifier.background(
                                    vibrantGradient
                                ) else Modifier
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Aa",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (style == ShareStyle.MINIMALIST) Color.Black else Color.White,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = style.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isSelected) AccentBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Indicator dots
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(3) { index ->
                val active = pagerState.currentPage == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (active) 16.dp else 8.dp, 8.dp)
                        .clip(CircleShape)
                        .background(if (active) AccentBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
                )
            }
        }
    }
}

@Composable
fun QuickShareButtons(onShare: (SharePlatform) -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "QUICK SHARE",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            QuickShareItem(Copy, "Copy") { onShare(SharePlatform.COPY) }
            QuickShareItem(Whatsapp, "Whatsapp") { onShare(SharePlatform.WHATSAPP) }
            QuickShareItem(Instagram, "Instagram") { onShare(SharePlatform.INSTAGRAM) }
            QuickShareItem(Icons.Default.MoreVert, "More") { onShare(SharePlatform.MORE) }
        }
    }
}

@Composable
fun QuickShareItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            modifier = Modifier.size(64.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant,
            onClick = onClick
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(24.dp))
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}

// Helper to capture the quote card as a bitmap
@Composable
fun CapturableContainer(
    style: ShareStyle,
    onBitmapCaptured: (Bitmap) -> Unit,
    content: @Composable () -> Unit
) {
    val composeView = remember { mutableStateOf<ComposeView?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val shadowColor = when(style){
        ShareStyle.VIBRANT -> vibrantGradient
        ShareStyle.MOODY -> Brush.linearGradient(listOf(MoodyDark, MoodyDark))
        else -> Brush.linearGradient(listOf(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f), MaterialTheme.colorScheme.surface))

    }
    AndroidView(modifier = Modifier.dropShadow(RoundedCornerShape(28.dp),
        Shadow(6.dp,shadowColor)),
        factory = { context ->
            val frameLayout = FrameLayout(context)
            val view = ComposeView(context).apply {
                setContent {
                    content()
                }
            }
            composeView.value = view
            frameLayout.addView(view)
            
            view.post {
                if (view.width > 0 && view.height > 0) {
                    coroutineScope.launch{
                        val bitmap = createBitmap(view.width, view.height)
                        val canvas = Canvas(bitmap)
                        view.draw(canvas)
                        onBitmapCaptured(bitmap)
                    }
                }
            }
            
            frameLayout
        },
        update = {
            composeView.value?.setContent { content() }
            composeView.value?.post {
                val view = composeView.value ?: return@post
                if (view.width > 0 && view.height > 0) {
                    val bitmap = createBitmap(view.width, view.height)
                    val canvas = Canvas(bitmap)
                    view.draw(canvas)
                    onBitmapCaptured(bitmap)
                }
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ShareQuoteScreenPreview() {
    QuoteBroTheme {
        ShareQuoteScreen()
    }
}
