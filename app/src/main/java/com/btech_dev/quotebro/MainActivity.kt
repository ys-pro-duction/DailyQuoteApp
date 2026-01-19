package com.btech_dev.quotebro

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.btech_dev.quotebro.data.remote.SupabaseClient
import com.btech_dev.quotebro.ui.home.TopBar
import com.btech_dev.quotebro.ui.login.AuthViewModel
import com.btech_dev.quotebro.ui.navigation.AppNavGraph
import com.btech_dev.quotebro.ui.navigation.Screen
import com.btech_dev.quotebro.ui.settings.SettingsViewModel
import com.btech_dev.quotebro.ui.theme.PrimaryColor
import com.btech_dev.quotebro.ui.theme.QuoteBroTheme
import com.btech_dev.quotebro.ui.theme.TextGray
import io.github.jan.supabase.annotations.SupabaseInternal
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.handleDeeplinks
import io.github.jan.supabase.auth.parseFragmentAndImportSession

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val settingsViewModel: SettingsViewModel = viewModel()
            val settingsState by settingsViewModel.uiState.collectAsState()
            enableEdgeToEdge(
                statusBarStyle =
                    if (settingsState.isDarkMode) SystemBarStyle.dark(Color.Transparent.toArgb())
                    else SystemBarStyle.light(
                        Color.Transparent.toArgb(),
                        Color.Transparent.toArgb()
                    )
            )

            QuoteBroTheme(
                darkTheme = settingsState.isDarkMode,
                fontScale = settingsState.fontSize
            ) {
                MainScreen(settingsViewModel = settingsViewModel)
            }
        }
    }
}

data class BottomNavItem(
    val route: Screen,
    val icon: ImageVector,
    val label: String
)

@Composable
fun MainScreen(
    authViewModel: AuthViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val navController = rememberNavController()

    val bottomNavItems = listOf(
        BottomNavItem(Screen.Home, Icons.Default.Home, "Home"),
        BottomNavItem(Screen.Favorites, Icons.Default.Favorite, "Saved"),
        BottomNavItem(Screen.Settings, Icons.Default.Person, "Me")
    )

    val context = LocalContext.current
    val activity = context as? ComponentActivity

    // Handle deep link for password reset
    LaunchedEffect(Unit) {
        activity?.intent?.data?.let { uri ->
            if (uri.scheme == "http" && uri.host == "reset-callback.quotebro" && uri.fragment?.contains("recovery") == true) {
                navController.navigate(Screen.UpdatePassword(uri.toString()))
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (authState.isCheckingAuth) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            // Check if we're on a main screen (show bottom bar) or a detail screen (hide bottom bar)
            val showBottomBar =
                    bottomNavItems.any { currentDestination?.hasRoute(it.route::class) == true }
            // Show top bar only on Home screen
            val showTopBar =
                currentDestination?.hasRoute(Screen.Home::class) == true

            Box(modifier = Modifier.fillMaxSize()) {
                // Main content
                AppNavGraph(
                    navController = navController,
                    startDestination = if (authState.isAuthenticated) Screen.Home else Screen.Auth,
                    onLogOut = { authViewModel.signOut() },
                    modifier = Modifier.fillMaxSize(),
                    settingsViewModel = settingsViewModel,
                    authViewModel = authViewModel
                )

                // Animated Top Bar - only on Home screen
                AnimatedVisibility(
                    visible = showTopBar,
                    enter = slideInVertically(
                        initialOffsetY = { -it },
                        animationSpec = tween(300)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { -it },
                        animationSpec = tween(300)
                    ),
                    modifier = Modifier.align(Alignment.TopCenter)
                ) {
                    TopBar()
                }

                // Animated Bottom Bar
                AnimatedVisibility(
                    visible = showBottomBar,
                    enter = slideInVertically(
                        initialOffsetY = { it },
                        animationSpec = tween(300)
                    ),
                    exit = slideOutVertically(
                        targetOffsetY = { it },
                        animationSpec = tween(300)
                    ),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    NavigationBar(
                        modifier = Modifier
                            .navigationBarsPadding()
                            .shadow(4.dp),
                        containerColor = MaterialTheme.colorScheme.surface,
                    ) {
                        bottomNavItems.forEach { item ->
                            val selected = currentDestination?.hasRoute(item.route::class) == true
                            NavigationBarItem(
                                icon = { Icon(item.icon, contentDescription = null) },
                                label = { Text(item.label) },
                                selected = selected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = PrimaryColor,
                                    selectedTextColor = PrimaryColor,
                                    indicatorColor = PrimaryColor.copy(alpha = 0.1f),
                                    unselectedIconColor = TextGray,
                                    unselectedTextColor = TextGray
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
