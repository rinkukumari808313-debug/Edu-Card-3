package com.example
import androidx.compose.ui.Modifier

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.EduViewModel
import com.example.ui.components.EduBottomNavigation
import com.example.ui.components.EduTopBar
import com.example.ui.screens.ActiveMockTestScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.ChapterDetailScreen
import com.example.ui.screens.ClassesAndSubjectsScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FlashcardsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.McqPracticeScreen
import com.example.ui.screens.MockTestsListScreen
import com.example.ui.screens.ProfileAuthScreen
import com.example.ui.screens.QuestionBankScreen
import com.example.ui.screens.SavedItemsScreen
import com.example.ui.screens.TeacherAdminScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.theme.EduCardTheme
import com.example.ui.theme.EduPrimary
import com.example.ui.theme.EduSecondary
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: EduViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkMode by viewModel.isDarkMode.collectAsState()
            val currentScreen by viewModel.currentScreen.collectAsState()
            val currentClass by viewModel.selectedClass.collectAsState()
            val user by viewModel.user.collectAsState()
            val notifications = viewModel.getNotifications()

            val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
            val scope = rememberCoroutineScope()

            EduCardTheme(darkTheme = isDarkMode) {
                // Back handler logic
                BackHandler(enabled = currentScreen != "HOME") {
                    when (currentScreen) {
                        "CHAPTER_DETAIL" -> viewModel.setScreen("CLASSES")
                        "ACTIVE_TEST" -> viewModel.setScreen("MOCK_TESTS")
                        else -> viewModel.setScreen("HOME")
                    }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        ModalDrawerSheet(
                            modifier = Modifier.width(300.dp),
                            drawerContainerColor = MaterialTheme.colorScheme.surface
                        ) {
                            DrawerHeader(
                                userDisplayName = user?.displayName ?: "Student",
                                userRole = user?.role ?: "STUDENT",
                                currentClass = currentClass,
                                onClose = { scope.launch { drawerState.close() } }
                            )

                            DrawerMenuItem(
                                label = "Home",
                                icon = Icons.Default.Home,
                                isSelected = currentScreen == "HOME",
                                onClick = {
                                    viewModel.setScreen("HOME")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            DrawerMenuItem(
                                label = "Classes & Subjects",
                                icon = Icons.Default.MenuBook,
                                isSelected = currentScreen == "CLASSES",
                                onClick = {
                                    viewModel.setScreen("CLASSES")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            DrawerMenuItem(
                                label = "MCQ Practice",
                                icon = Icons.Default.Quiz,
                                isSelected = currentScreen == "MCQ_PRACTICE",
                                onClick = {
                                    viewModel.setScreen("MCQ_PRACTICE")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            DrawerMenuItem(
                                label = "Mock Tests",
                                icon = Icons.Default.Timer,
                                isSelected = currentScreen == "MOCK_TESTS",
                                onClick = {
                                    viewModel.setScreen("MOCK_TESTS")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            DrawerMenuItem(
                                label = "Question Bank (PYQs)",
                                icon = Icons.Default.Psychology,
                                isSelected = currentScreen == "QUESTION_BANK",
                                onClick = {
                                    viewModel.setScreen("QUESTION_BANK")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            DrawerMenuItem(
                                label = "AI Study Assistant",
                                icon = Icons.Default.AutoAwesome,
                                isSelected = currentScreen == "AI_ASSISTANT",
                                onClick = {
                                    viewModel.setScreen("AI_ASSISTANT")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            DrawerMenuItem(
                                label = "Dashboard & Leaderboard",
                                icon = Icons.Default.Dashboard,
                                isSelected = currentScreen == "DASHBOARD",
                                onClick = {
                                    viewModel.setScreen("DASHBOARD")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            DrawerMenuItem(
                                label = "Flashcards",
                                icon = Icons.Default.Style,
                                isSelected = currentScreen == "FLASHCARDS",
                                onClick = {
                                    viewModel.setScreen("FLASHCARDS")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            DrawerMenuItem(
                                label = "Study Tools & Calculator",
                                icon = Icons.Default.Calculate,
                                isSelected = currentScreen == "TOOLS",
                                onClick = {
                                    viewModel.setScreen("TOOLS")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            DrawerMenuItem(
                                label = "Bookmarks & Saved Notes",
                                icon = Icons.Default.Bookmark,
                                isSelected = currentScreen == "SAVED_ITEMS",
                                onClick = {
                                    viewModel.setScreen("SAVED_ITEMS")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            DrawerMenuItem(
                                label = "Teacher & Admin Portal",
                                icon = Icons.Default.AdminPanelSettings,
                                isSelected = currentScreen == "TEACHER_ADMIN",
                                onClick = {
                                    viewModel.setScreen("TEACHER_ADMIN")
                                    scope.launch { drawerState.close() }
                                }
                            )
                            DrawerMenuItem(
                                label = "Profile, About & Legal",
                                icon = Icons.Default.Person,
                                isSelected = currentScreen == "PROFILE_AUTH",
                                onClick = {
                                    viewModel.setScreen("PROFILE_AUTH")
                                    scope.launch { drawerState.close() }
                                }
                            )

                            Spacer(modifier = Modifier.weight(1f))
                            Divider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "made by Praveen Kumar",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                ) {
                    Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        topBar = {
                            if (currentScreen != "ACTIVE_TEST") {
                                EduTopBar(
                                    currentClass = currentClass,
                                    onClassSelected = { viewModel.setClass(it) },
                                    userRole = user?.role ?: "STUDENT",
                                    isDarkMode = isDarkMode,
                                    onToggleDarkMode = { viewModel.toggleDarkMode() },
                                    notifications = notifications,
                                    onOpenSearch = {
                                        viewModel.setScreen("HOME")
                                    },
                                    onOpenMenu = {
                                        scope.launch {
                                            if (drawerState.isClosed) drawerState.open() else drawerState.close()
                                        }
                                    }
                                )
                            }
                        },
                        bottomBar = {
                            if (currentScreen != "ACTIVE_TEST") {
                                EduBottomNavigation(
                                    currentScreen = currentScreen,
                                    onNavigate = { screen ->
                                        viewModel.setScreen(screen)
                                    }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            when (currentScreen) {
                                "HOME" -> HomeScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.setScreen(it) }
                                )
                                "CLASSES", "SUBJECTS" -> ClassesAndSubjectsScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.setScreen(it) }
                                )
                                "CHAPTER_DETAIL" -> ChapterDetailScreen(
                                    viewModel = viewModel,
                                    onBack = { viewModel.setScreen("CLASSES") }
                                )
                                "MCQ_PRACTICE" -> McqPracticeScreen(
                                    viewModel = viewModel,
                                    onNavigateToTests = { viewModel.setScreen("MOCK_TESTS") },
                                    onNavigateToBank = { viewModel.setScreen("QUESTION_BANK") }
                                )
                                "MOCK_TESTS" -> MockTestsListScreen(
                                    viewModel = viewModel,
                                    onNavigateToBank = { viewModel.setScreen("QUESTION_BANK") }
                                )
                                "ACTIVE_TEST" -> ActiveMockTestScreen(
                                    viewModel = viewModel,
                                    onBackToList = { viewModel.setScreen("MOCK_TESTS") }
                                )
                                "QUESTION_BANK" -> QuestionBankScreen(
                                    viewModel = viewModel
                                )
                                "AI_ASSISTANT" -> AiAssistantScreen(
                                    viewModel = viewModel
                                )
                                "DASHBOARD", "LEADERBOARD" -> DashboardScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.setScreen(it) }
                                )
                                "FLASHCARDS" -> FlashcardsScreen(
                                    viewModel = viewModel
                                )
                                "TOOLS" -> ToolsScreen(
                                    viewModel = viewModel
                                )
                                "SAVED_ITEMS" -> SavedItemsScreen(
                                    viewModel = viewModel
                                )
                                "TEACHER_ADMIN" -> TeacherAdminScreen(
                                    viewModel = viewModel
                                )
                                "PROFILE_AUTH", "ABOUT_LEGAL" -> ProfileAuthScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.setScreen(it) }
                                )
                                else -> HomeScreen(
                                    viewModel = viewModel,
                                    onNavigate = { viewModel.setScreen(it) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerHeader(
    userDisplayName: String,
    userRole: String,
    currentClass: Int,
    onClose: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.primaryContainer,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(EduPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.School,
                        contentDescription = "Edu Card",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close Menu")
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Edu Card",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Text(
                text = "Learn • Practice • Improve",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$userDisplayName • $userRole • Class $currentClass",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun DrawerMenuItem(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    NavigationDrawerItem(
        label = { Text(label, fontSize = 13.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
        icon = { Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp)) },
        selected = isSelected,
        onClick = onClick,
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedIconColor = EduPrimary,
            selectedTextColor = EduPrimary
        ),
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 2.dp)
            .testTag("drawer_item_$label")
    )
}
