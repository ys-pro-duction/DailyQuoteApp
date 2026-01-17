package com.btech_dev.quotebro.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
fun SignUpBottomSheet(
    onSignUpClick: (String, String, String) -> Unit, // email, password, name
    onLoginClick: () -> Unit,
    onSignIn: () -> Unit
) {
    // State for inputs
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // This Surface mimics the white bottom sheet container
        Column(
            modifier = Modifier
                .padding(24.dp)
                .padding(top = 16.dp)
                .navigationBarsPadding() // Handles safe area bottom padding
        ) {

            // --- Header Section ---
            Text(
                text = "Create account",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Please enter your details to sign up.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextGray
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            // --- Name Input ---
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Full Name", color = TextGray) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.height(16.dp))

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
                placeholder = { Text("Password", color = TextGray) },
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

            // --- Sign Up Button ---
            Button(
                onClick = { onSignUpClick(email, password, name) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(50),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryColor
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Text(
                    text = "Sign Up",
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

            // --- Login Link ---
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSignIn() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = TextGray, fontWeight = FontWeight.Normal)) {
                            append("Already have an account? ")
                        }
                        withStyle(style = SpanStyle(color = PrimaryColor, fontWeight = FontWeight.SemiBold)) {
                            append("Sign In")
                        }
                    },
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
