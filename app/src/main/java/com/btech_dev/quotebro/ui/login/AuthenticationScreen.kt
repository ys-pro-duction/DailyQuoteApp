package com.btech_dev.quotebro.ui.login

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.btech_dev.quotebro.R
import com.btech_dev.quotebro.ui.theme.QuoteBroTheme
import com.btech_dev.quotebro.ui.theme.SecondaryColor
import com.btech_dev.quotebro.ui.theme.icons.Quote

@Composable
fun AuthScreen(
    modifier: Modifier = Modifier,
    viewModel: AuthViewModel = viewModel(),
    onAuthSuccess: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Check for success
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onAuthSuccess()
        }
    }

    // Check for error
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        content = { padding ->
            Box(modifier = Modifier) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(
                                    Color(0xffe8edff),
                                    Color(0xffe5dfff),
                                    Color(0xffe4e1f9),
                                    Color(0xffe4e1f9)
                                )
                            )
                        )
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(
                            24.dp,
                            Alignment.CenterVertically
                        ),
                        modifier = Modifier
                            .padding(start = 24.dp, end = 24.dp)
                    ) {
                        Icon(
                            Quote, "",
                            modifier = Modifier
                                .padding(top = 50.dp)
                                .size(22.dp),
                            tint = SecondaryColor
                        )
                        Column {
                            Text(
                                "\"The only way to do great work is to love what you do.\"",
                                color = Color(0xff0f172a),
                                fontFamily = FontFamily(Font(R.font.playfair_display_italic)),
                                fontSize = 24.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(top = 20.dp)
                            )
                            Text(
                                "— STEVE JOBS",
                                color = SecondaryColor,
                                modifier = Modifier
                                    .align(Alignment.CenterHorizontally)
                                    .padding(top = 16.dp)
                            )
                        }
                    }
                    val showLogin = rememberSaveable { mutableStateOf(true) }
                    Box(
                        modifier = Modifier
                            .background(
                                Color.White,
                                RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)
                            )
                            .animateContentSize(animationSpec = tween(300)),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        if (showLogin.value) LoginBottomSheet(
                            onLoginClick = { email, password -> viewModel.signIn(email, password) },
                            onForgotPasswordClick = onForgotPasswordClick,
                            showSignUp = { showLogin.value = false })
                        else SignUpBottomSheet(
                            onSignUpClick = { email, password, name ->
                                viewModel.signUp(
                                    email,
                                    password,
                                    name
                                )
                            },
                            onLoginClick = {},
                            onSignIn = { showLogin.value = true })

                    }
                }

                if (uiState.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color.White)
                    }
                }
            }
        }
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun AuthScreenPreview() {
    QuoteBroTheme {
        AuthScreen()
    }
}