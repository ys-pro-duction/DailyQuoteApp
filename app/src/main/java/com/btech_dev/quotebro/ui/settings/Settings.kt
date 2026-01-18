package com.btech_dev.quotebro.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import android.app.TimePickerDialog
import android.content.Context
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.outlined.ExitToApp
import androidx.compose.material.icons.outlined.Warning
import com.btech_dev.quotebro.util.AlarmScheduler
import java.util.Calendar
import androidx.lifecycle.viewmodel.compose.viewModel
import com.btech_dev.quotebro.ui.login.AuthViewModel
import com.btech_dev.quotebro.ui.theme.PrimaryColor
import com.btech_dev.quotebro.ui.theme.QuoteBroTheme
import androidx.core.content.edit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onLogOutClick: () -> Unit = {},
    viewModel: AuthViewModel = viewModel(),
    settingsViewModel: SettingsViewModel = viewModel()
) {
    val authState by viewModel.uiState.collectAsState()
    val settingsState by settingsViewModel.uiState.collectAsState()
    val name = authState.userProfile?.full_name ?: "User"
    val email = authState.userEmail ?: "user@email.com"

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { ProfileSection(name, email) }
            
            item {
                SettingsSectionTitle("APPEARANCE")
                SettingsCard {
                    SettingsToggleItem(
                        icon = Icons.Default.ThumbUp,
                        title = "Dark Mode",
                        subtitle = "Reduce eye strain",
                        checked = settingsState.isDarkMode,
                        onCheckedChange = { settingsViewModel.toggleDarkMode(it) }
                    )
                    SettingsDivider()
                    SettingsSliderItem(
                        icon = Icons.Default.Create,
                        title = "Font Size",
                        badgeText = "Medium"
                    )
                }
            }

            item {
                SettingsSectionTitle("NOTIFICATIONS")
                SettingsCard {
                    val context = LocalContext.current
                    val prefs = remember { context.getSharedPreferences("settings", Context.MODE_PRIVATE) }
                    
                    var isEnabled by remember { 
                        mutableStateOf(prefs.getBoolean("daily_quote_enabled", false)) 
                    }
                    var hour by remember { 
                        mutableStateOf(prefs.getInt("daily_quote_hour", 8)) 
                    }
                    var minute by remember { 
                        mutableStateOf(prefs.getInt("daily_quote_minute", 0)) 
                    }

                    val permissionLauncher = rememberLauncherForActivityResult(
                        ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        if (isGranted && isEnabled) {
                             AlarmScheduler.scheduleDailyQuote(context, hour, minute)
                        }
                    }

                    SettingsToggleItem(
                        icon = Icons.Default.Notifications,
                        title = "Daily Quote",
                        subtitle = "Get a random quote every day",
                        checked = isEnabled,
                        onCheckedChange = { enabled ->
                            isEnabled = enabled
                            prefs.edit { putBoolean("daily_quote_enabled", enabled) }
                            if (enabled) {
                                if (android.os.Build.VERSION.SDK_INT >= 33) {
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                                        AlarmScheduler.scheduleDailyQuote(context, hour, minute)
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                    }
                                } else {
                                    AlarmScheduler.scheduleDailyQuote(context, hour, minute)
                                }
                            } else {
                                AlarmScheduler.cancelDailyQuote(context)
                            }
                        }
                    )
                    
                    if (isEnabled) {
                        SettingsDivider()
                        
                        val timeString = String.format("%02d:%02d %s", 
                            if (hour % 12 == 0) 12 else hour % 12, 
                            minute, 
                            if (hour < 12) "AM" else "PM"
                        )
                        
                        SettingsActionItem(
                            icon = Icons.Default.Notifications,
                            title = "Reminder Time",
                            subtitle = "Set your reminder time",
                            action = {
                                Surface(
                                    color = PrimaryColor.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(50.dp),
                                    onClick = {
                                        TimePickerDialog(
                                            context,
                                            { _, h, m ->
                                                hour = h
                                                minute = m
                                                prefs.edit().putInt("daily_quote_hour", h)
                                                        .putInt("daily_quote_minute", m).apply()
                                                AlarmScheduler.scheduleDailyQuote(context, h, m)
                                            },
                                            hour,
                                            minute,
                                            false
                                        ).show()
                                    }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(timeString, color = PrimaryColor, style = MaterialTheme.typography.labelLarge)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = PrimaryColor)
                                    }
                                }
                            }
                        )
                    }
                }
            }

            item {
                SettingsSectionTitle("ACCOUNT")
                SettingsCard {
                    SettingsNavigationItem(icon = Icons.Outlined.Warning, title = "Privacy Policy")
                }
            }

            item {
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onLogOutClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .height(56.dp),
                    shape = RoundedCornerShape(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "QuoteVault v1.0.2",
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileSection(name: String, email: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 24.dp)
    ) {
        Box {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                border = BorderStroke(4.dp, MaterialTheme.colorScheme.surface)
            ) {
                // In a real app, use AsyncImage here
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(24.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(name, style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.onBackground)
        Spacer(modifier = Modifier.height(8.dp))
        Text(email, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 16.dp, bottom = 12.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 0.5.dp
    ) {
        Column(content = content)
    }
}

@Composable
fun SettingsDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    )
}

@Composable
fun SettingsItemBase(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailingContent: @Composable () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.surfaceVariant
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = PrimaryColor
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            if (subtitle != null) {
                Text(subtitle, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f), style = MaterialTheme.typography.bodySmall)
            }
        }
        trailingContent()
    }
}

@Composable
fun SettingsToggleItem(icon: ImageVector, title: String, subtitle: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    SettingsItemBase(icon, title, subtitle) {
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colorScheme.onPrimary,
                checkedTrackColor = PrimaryColor,
                uncheckedThumbColor = MaterialTheme.colorScheme.surface,
                uncheckedTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                uncheckedBorderColor = MaterialTheme.colorScheme.outline
            )
        )
    }
}

@Composable
fun SettingsSliderItem(icon: ImageVector, title: String, badgeText: String) {
    Column(modifier = Modifier.padding(bottom = 8.dp)) {
        SettingsItemBase(icon, title) {
            Surface(
                color = PrimaryColor.copy(alpha = 0.1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(
                    text = badgeText,
                    color = PrimaryColor,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("A", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)
            Slider(
                value = 0.5f,
                onValueChange = {},
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                colors = SliderDefaults.colors(
                    thumbColor = MaterialTheme.colorScheme.primary,
                    activeTrackColor = PrimaryColor,
                    inactiveTrackColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
            )
            Text("A", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

@Composable
fun SettingsActionItem(icon: ImageVector, title: String, subtitle: String, action: @Composable () -> Unit) {
    SettingsItemBase(icon, title, subtitle, action)
}

@Composable
fun SettingsNavigationItem(icon: ImageVector, title: String) {
    SettingsItemBase(icon, title) {
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SettingsScreenPreview() {
    QuoteBroTheme {
        SettingsScreen()
    }
}
