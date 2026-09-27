package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.EduNotification
import com.example.data.model.SubjectInfo
import com.example.ui.theme.EduPrimary
import com.example.ui.theme.EduSecondary
import com.example.ui.theme.EduTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EduTopBar(
    currentClass: Int,
    onClassSelected: (Int) -> Unit,
    userRole: String,
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    notifications: List<EduNotification>,
    onOpenSearch: () -> Unit,
    onOpenMenu: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showClassMenu by remember { mutableStateOf(false) }
    var showNotificationsDialog by remember { mutableStateOf(false) }

    TopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onOpenMenu() }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(EduPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Edu Card Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Edu Card",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        if (userRole != "STUDENT") {
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (userRole == "TEACHER") EduSecondary else EduTertiary,
                                modifier = Modifier.padding(horizontal = 2.dp)
                            ) {
                                Text(
                                    text = userRole,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                    Text(
                        text = "Learn • Practice • Improve",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        actions = {
            // Class Picker Chip
            Box {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    modifier = Modifier
                        .clickable { showClassMenu = true }
                        .testTag("class_picker_chip")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Class $currentClass",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                DropdownMenu(
                    expanded = showClassMenu,
                    onDismissRequest = { showClassMenu = false }
                ) {
                    (6..12).forEach { c ->
                        DropdownMenuItem(
                            text = { Text("Class $c", fontWeight = if (c == currentClass) FontWeight.Bold else FontWeight.Normal) },
                            onClick = {
                                onClassSelected(c)
                                showClassMenu = false
                            },
                            trailingIcon = {
                                if (c == currentClass) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EduPrimary, modifier = Modifier.size(16.dp))
                                }
                            }
                        )
                    }
                }
            }

            IconButton(onClick = onOpenSearch, modifier = Modifier.testTag("search_icon_button")) {
                Icon(Icons.Default.Search, contentDescription = "Search")
            }

            // Notification button
            IconButton(
                onClick = { showNotificationsDialog = true },
                modifier = Modifier.testTag("notifications_button")
            ) {
                BadgedBox(
                    badge = {
                        if (notifications.isNotEmpty()) {
                            Badge { Text("${notifications.size}") }
                        }
                    }
                ) {
                    Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                }
            }

            // Dark Mode toggle
            IconButton(onClick = onToggleDarkMode, modifier = Modifier.testTag("dark_mode_toggle")) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Default.Brightness7 else Icons.Default.Brightness4,
                    contentDescription = "Toggle Dark Mode"
                )
            }
        },
        modifier = modifier
    )

    if (showNotificationsDialog) {
        Dialog(onDismissRequest = { showNotificationsDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Study Reminders & Notifications",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        IconButton(onClick = { showNotificationsDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    notifications.forEach { notif ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = notif.title,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = notif.timestamp,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = notif.message,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { showNotificationsDialog = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Dismiss")
                    }
                }
            }
        }
    }
}

@Composable
fun EduBottomNavigation(
    currentScreen: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Triple("HOME", "Home", Icons.Default.Home),
        Triple("CLASSES", "Study", Icons.Default.MenuBook),
        Triple("MCQ_PRACTICE", "Practice", Icons.Default.Quiz),
        Triple("AI_ASSISTANT", "AI Tutor", Icons.Default.AutoAwesome),
        Triple("DASHBOARD", "Progress", Icons.Default.Dashboard)
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = modifier
    ) {
        items.forEach { (route, label, icon) ->
            val isSelected = currentScreen == route || 
                (route == "CLASSES" && (currentScreen == "SUBJECTS" || currentScreen == "CHAPTER_DETAIL")) ||
                (route == "MCQ_PRACTICE" && (currentScreen == "MOCK_TESTS" || currentScreen == "ACTIVE_TEST" || currentScreen == "QUESTION_BANK")) ||
                (route == "DASHBOARD" && currentScreen == "LEADERBOARD")

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(route) },
                icon = { Icon(icon, contentDescription = label) },
                label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = EduPrimary,
                    selectedTextColor = EduPrimary,
                    indicatorColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.testTag("nav_item_$route")
            )
        }
    }
}

@Composable
fun SectionHeader(
    title: String,
    subtitle: String? = null,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        if (actionText != null && onActionClick != null) {
            TextButton(onClick = onActionClick, contentPadding = PaddingValues(horizontal = 8.dp)) {
                Text(actionText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = EduPrimary)
            }
        }
    }
}
