package com.btech_dev.quotebro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.btech_dev.quotebro.ui.favorites.FavoritesScreen
import com.btech_dev.quotebro.ui.home.MainHomeContent
import com.btech_dev.quotebro.ui.login.AuthScreen
import com.btech_dev.quotebro.ui.login.AuthViewModel
import com.btech_dev.quotebro.ui.settings.SettingsScreen
import com.btech_dev.quotebro.ui.settings.SettingsViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: Screen,
    onLogOut: () -> Unit,
    modifier: Modifier = Modifier,
    settingsViewModel: SettingsViewModel? = null,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable<Screen.Auth> {
            AuthScreen(
                viewModel = authViewModel,
                onAuthSuccess = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Auth) { inclusive = true }
                    }
                },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword) }
            )
        }

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
                onLogOutClick = onLogOut,
                settingsViewModel = settingsViewModel
                    ?: androidx.lifecycle.viewmodel.compose.viewModel()
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
        
        composable<Screen.ForgotPassword> {
            com.btech_dev.quotebro.ui.login.RequestResetScreen(
                onBackClick = { navController.popBackStack() },
                onEmailSent = { navController.popBackStack() }
            )
        }
        
        composable<Screen.UpdatePassword> { backStackEntry: NavBackStackEntry ->
            val args = backStackEntry.toRoute<Screen.UpdatePassword>()
            com.btech_dev.quotebro.ui.login.UpdatePasswordScreen(
                args.redirectUrl,
                onPasswordUpdated = {
                    navController.navigate(Screen.Home) {
                        popUpTo(Screen.Auth) { inclusive = true }
                    }
                }
            )
        }
    }
}
