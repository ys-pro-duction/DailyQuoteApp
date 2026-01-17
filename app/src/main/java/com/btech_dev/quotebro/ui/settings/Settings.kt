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
import com.btech_dev.quotebro.ui.theme.ArrowTint
import com.btech_dev.quotebro.ui.theme.AvatarBg
import com.btech_dev.quotebro.ui.theme.BackgroundWhite
import com.btech_dev.quotebro.ui.theme.DarkGray
import com.btech_dev.quotebro.ui.theme.ErrorRed
import com.btech_dev.quotebro.ui.theme.LightGray
import com.btech_dev.quotebro.ui.theme.PrimaryColor
import com.btech_dev.quotebro.ui.theme.QuickShareBg
import com.btech_dev.quotebro.ui.theme.QuoteBroTheme
import com.btech_dev.quotebro.ui.theme.SliderInactive
import com.btech_dev.quotebro.ui.theme.TextDarkSlate
import com.btech_dev.quotebro.ui.theme.TextGray
import com.btech_dev.quotebro.ui.theme.TextMediumSlate
import com.btech_dev.quotebro.ui.theme.Transparent
import com.btech_dev.quotebro.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit = {},
    onLogOutClick: () -> Unit = {}
) {
    Scaffold(
        containerColor = BackgroundWhite
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            item { ProfileSection() }
            
            item {
                SettingsSectionTitle("APPEARANCE")
                SettingsCard {
                    SettingsToggleItem(
                        icon = Icons.Default.ThumbUp,
                        title = "Dark Mode",
                        subtitle = "Reduce eye strain",
                        checked = false,
                        onCheckedChange = {}
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
                    SettingsActionItem(
                        icon = Icons.Default.AddCircle,
                        title = "Daily Quote",
                        subtitle = "Set your reminder time",
                        action = {
                            Surface(
                                color = PrimaryColor.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(50.dp),
                                onClick = {}
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("08:00 AM", color = PrimaryColor, style = MaterialTheme.typography.labelLarge)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = PrimaryColor)
                                }
                            }
                        }
                    )
                }
            }

            item {
                SettingsSectionTitle("ACCOUNT")
                SettingsCard {
                    SettingsNavigationItem(icon = Icons.Default.Person, title = "Edit Profile")
                    SettingsDivider()
                    SettingsNavigationItem(icon = Icons.Default.Lock, title = "Change Password")
                    SettingsDivider()
                    SettingsNavigationItem(icon = Icons.Default.Add, title = "Privacy Policy")
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
                    colors = ButtonDefaults.buttonColors(containerColor = White),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = ErrorRed)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Log Out", color = ErrorRed, style = MaterialTheme.typography.titleMedium)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "QuoteVault v1.0.2",
                    color = TextGray,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }
        }
    }
}

@Composable
fun ProfileSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 24.dp)
    ) {
        Box {
            Surface(
                modifier = Modifier.size(120.dp),
                shape = CircleShape,
                color = AvatarBg, // Flesh tone background
                border = BorderStroke(4.dp, White)
            ) {
                // In a real app, use AsyncImage here
                Icon(
                    Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.padding(24.dp),
                    tint = DarkGray
                )
            }
            Surface(
                modifier = Modifier
                    .size(32.dp)
                    .align(Alignment.BottomEnd)
                    .offset(x = (-4).dp, y = (-4).dp),
                shape = CircleShape,
                color = PrimaryColor,
                border = BorderStroke(2.dp, White)
            ) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit Profile",
                    modifier = Modifier.padding(6.dp),
                    tint = White
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text("Alex Sterling", style = MaterialTheme.typography.headlineMedium)
        Text("@alex_quotes", color = TextGray, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = TextMediumSlate,
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
        color = White,
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
        color = LightGray.copy(alpha = 0.3f)
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
            color = QuickShareBg
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
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextDarkSlate)
            if (subtitle != null) {
                Text(subtitle, color = TextGray, style = MaterialTheme.typography.bodySmall)
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
                checkedThumbColor = White,
                checkedTrackColor = PrimaryColor,
                uncheckedThumbColor = White,
                uncheckedTrackColor = SliderInactive,
                uncheckedBorderColor = Transparent
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
            Text("A", style = MaterialTheme.typography.labelMedium, color = DarkGray)
            Slider(
                value = 0.5f,
                onValueChange = {},
                modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
                colors = SliderDefaults.colors(
                    thumbColor = White,
                    activeTrackColor = PrimaryColor,
                    inactiveTrackColor = SliderInactive
                )
            )
            Text("A", style = MaterialTheme.typography.titleLarge, color = DarkGray)
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
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = ArrowTint
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
