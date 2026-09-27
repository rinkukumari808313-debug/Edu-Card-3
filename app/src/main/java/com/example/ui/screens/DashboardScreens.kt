package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Grade
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EduBadge
import com.example.data.model.LeaderboardUser
import com.example.ui.EduViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.theme.EduPrimary
import com.example.ui.theme.EduSecondary
import com.example.ui.theme.EduTertiary

@Composable
fun DashboardScreen(
    viewModel: EduViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsState()
    val testHistory by viewModel.testHistory.collectAsState()
    val recentActivities by viewModel.recentActivities.collectAsState()
    val badges = viewModel.getBadges()
    val leaderboard = viewModel.getLeaderboard()

    var dashboardTab by remember { mutableStateOf(0) } // 0: Overview/Progress, 1: Leaderboard, 2: Badges

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("dashboard_screen")
    ) {
        // Dashboard Tab Selector
        ScrollableTabRow(
            selectedTabIndex = dashboardTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(
                selected = dashboardTab == 0,
                onClick = { dashboardTab = 0 },
                text = { Text("Overview & Progress", fontWeight = if (dashboardTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = dashboardTab == 1,
                onClick = { dashboardTab = 1 },
                text = { Text("Leaderboard", fontWeight = if (dashboardTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = dashboardTab == 2,
                onClick = { dashboardTab = 2 },
                text = { Text("Badges (${badges.count { it.isUnlocked }}/${badges.size})", fontWeight = if (dashboardTab == 2) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        when (dashboardTab) {
            0 -> OverviewProgressTab(viewModel, user, testHistory, recentActivities, onNavigate)
            1 -> LeaderboardTab(leaderboard)
            2 -> BadgesTab(badges)
        }
    }
}

@Composable
fun OverviewProgressTab(
    viewModel: EduViewModel,
    user: com.example.data.local.UserEntity?,
    testHistory: List<com.example.data.local.TestHistoryEntity>,
    activities: List<com.example.data.local.StudyActivityEntity>,
    onNavigate: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Student Profile Summary Card
        item {
            Card(
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(EduPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (user?.displayName?.firstOrNull() ?: 'A').toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = user?.displayName ?: "Aarav Sharma",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Class ${user?.classNum ?: 10} • Student ID: ${user?.username ?: "student_alpha"}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = EduTertiary.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = EduTertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${user?.streakDays ?: 7} Days",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EduTertiary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 Key Stats
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        StatItem(label = "Study Points", value = "${user?.points ?: 1450} pts", color = EduPrimary)
                        StatItem(label = "Tests Taken", value = "${user?.testsCompletedCount ?: 14}", color = EduSecondary)
                        StatItem(label = "Chapters", value = "${user?.completedChaptersCount ?: 8} Done", color = EduTertiary)
                    }
                }
            }
        }

        // Weekly Study Progress & Overall Completion
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Weekly Study Target", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(text = "78% Complete", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduPrimary)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { 0.78f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = EduPrimary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Great momentum! You are on track to master Class ${user?.classNum ?: 10} syllabus before the next test cycle.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Strong Topics vs Topics Needing More Practice
        item {
            Text(text = "Performance Diagnostics", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = EduSecondary.copy(alpha = 0.1f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingUp, contentDescription = null, tint = EduSecondary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Strong Topics", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduSecondary)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Light Reflection (95%)", fontSize = 11.sp)
                        Text("• Real Numbers (90%)", fontSize = 11.sp)
                        Text("• Chemical Reactions (88%)", fontSize = 11.sp)
                    }
                }

                Card(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Red.copy(alpha = 0.08f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TrendingDown, contentDescription = null, tint = Color.Red, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Need Practice", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Red)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("• Electricity Circuits (62%)", fontSize = 11.sp)
                        Text("• Quadratic Formula (68%)", fontSize = 11.sp)
                        Text("• Organic Bonds (70%)", fontSize = 11.sp)
                    }
                }
            }
        }

        // Recent Activity Feed
        item {
            SectionHeader(title = "Recent Activities", subtitle = "Your continuous learning timeline")
        }

        if (activities.isEmpty()) {
            item {
                Text("No recent activities yet. Start by solving a practice quiz!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(activities.take(5)) { act ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(EduPrimary.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EduPrimary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = act.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(text = act.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardTab(leaderboard: List<LeaderboardUser>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🏆 Weekly Academic Leaderboard",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Privacy-friendly aliases protect student identities while fostering friendly educational encouragement.",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        items(leaderboard) { user ->
            val isTop3 = user.rank <= 3
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (user.name.contains("(You)")) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isTop3) 3.dp else 1.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when (user.rank) {
                                    1 -> Color(0xFFFFD700)
                                    2 -> Color(0xFFC0C0C0)
                                    3 -> Color(0xFFCD7F32)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${user.rank}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (isTop3) Color.Black else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = user.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${user.testsCount} tests • ${user.streak}d streak • ${user.badgesCount} badges",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "${user.points} pts",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = EduPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun BadgesTab(badges: List<EduBadge>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "Achievements & Badges",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Text(
                text = "Unlock badges by solving questions, maintaining daily streaks, and mastering chapters.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        items(badges) { badge ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (badge.isUnlocked) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = if (badge.isUnlocked) 2.dp else 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                if (badge.isUnlocked) EduTertiary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (badge.icon) {
                                "CheckCircle" -> Icons.Default.CheckCircle
                                "LocalFireDepartment" -> Icons.Default.LocalFireDepartment
                                "Psychology" -> Icons.Default.Psychology
                                "Grade" -> Icons.Default.Grade
                                "WorkspacePremium" -> Icons.Default.WorkspacePremium
                                else -> Icons.Default.MilitaryTech
                            },
                            contentDescription = badge.title,
                            tint = if (badge.isUnlocked) EduTertiary else Color.Gray,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = badge.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            if (badge.isUnlocked) {
                                Text(text = "✓ Unlocked", fontSize = 10.sp, color = EduSecondary, fontWeight = FontWeight.Bold)
                            } else {
                                Text(text = "Progress: ${badge.progress}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Text(text = badge.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = color)
        Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
