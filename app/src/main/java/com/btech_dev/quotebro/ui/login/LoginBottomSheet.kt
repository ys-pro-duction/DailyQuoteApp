package com.btech_dev.quotebro.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.btech_dev.quotebro.ui.theme.PrimaryColor
import com.btech_dev.quotebro.ui.theme.TextGray
import com.btech_dev.quotebro.ui.theme.icons.FeatherEyeOff
import com.btech_dev.quotebro.ui.theme.icons.FeatherEyeOn


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginBottomSheet(
    onLoginClick: (String, String) -> Unit,
    onForgotPasswordClick: () -> Unit,
    showSignUp: () -> Unit
) {
    // State for inputs
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // This Surface mimics the white bottom sheet container
        Column(
            modifier = Modifier
                .padding(24.dp).padding(top = 16.dp)
                .navigationBarsPadding() // Handles safe area bottom padding
        ) {

            // --- Header Section ---
            Text(
                text = "Welcome back",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please enter your details to sign in.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextGray
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Email Input ---
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Email address", color = TextGray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "Email Icon",
                        tint = TextGray
                    )
                },
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = TextGray,
                    focusedBorderColor = PrimaryColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.DarkGray,
                    unfocusedTextColor = Color.Gray
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // --- Password Input ---
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Password", color = TextGray) }, // Dots placeholder
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Lock Icon",
                        tint = TextGray
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(
                            imageVector = if (isPasswordVisible) FeatherEyeOff else FeatherEyeOn,
                            contentDescription = "Toggle Password",
                            tint = TextGray
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedBorderColor = TextGray,
                    focusedBorderColor = PrimaryColor,
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedTextColor = Color.DarkGray,
                    unfocusedTextColor = Color.Gray
                ),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- Sign In Button ---
            Button(
                onClick = { onLoginClick(email, password) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp), // Taller button as shown in design
                shape = RoundedCornerShape(50), // Full pill shape
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Text(
                    text = "Sign In",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = Color.White
                )
            }

            // --- Register Button  ---
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showSignUp() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = TextGray, fontWeight = FontWeight.Normal)) {
                            append("Don't have an account? ")
                        }
                        withStyle(style = SpanStyle(color = PrimaryColor, fontWeight = FontWeight.SemiBold)) {
                            append("Sign Up")
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }


        }
    }
