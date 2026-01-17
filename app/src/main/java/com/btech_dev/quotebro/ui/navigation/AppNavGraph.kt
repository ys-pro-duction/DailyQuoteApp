package com.btech_dev.quotebro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.btech_dev.quotebro.ui.favorites.FavoritesScreen
import com.btech_dev.quotebro.ui.home.MainHomeContent
import com.btech_dev.quotebro.ui.settings.SettingsScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: Screen,
    onLogOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Screen.Home> {
            MainHomeContent(
                onShareQuote = { text, author ->
                    navController.navigate(Screen.ShareQuote(text, author))
                }
            )
        }
        
        composable<Screen.Favorites> {
            FavoritesScreen(
                onNavigateToCollectionDetails = { collectionId, collectionName ->
                    navController.navigate(Screen.CollectionDetails(collectionId, collectionName))
                },
                onShareQuote = { text, author ->
                    navController.navigate(Screen.ShareQuote(text, author))
                }
            )
        }
        
        composable<Screen.Settings> {
            SettingsScreen(
                onBackClick = { navController.popBackStack() },
                onLogOutClick = onLogOut
            )
        }
        
        composable<Screen.CollectionDetails> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.CollectionDetails>()
            com.btech_dev.quotebro.ui.favorites.CollectionDetailsScreen(
                collectionId = args.collectionId,
                collectionName = args.collectionName,
                onBackClick = { navController.popBackStack() },
                onShareQuote = { text, author ->
                    navController.navigate(Screen.ShareQuote(text, author))
                }
            )
        }
        
        composable<Screen.ShareQuote> { backStackEntry ->
            val args = backStackEntry.toRoute<Screen.ShareQuote>()
            com.btech_dev.quotebro.ui.share.ShareQuoteScreen(
                quoteText = args.quoteText,
                quoteAuthor = args.quoteAuthor,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
