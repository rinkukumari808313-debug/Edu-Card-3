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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Class
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Leaderboard
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.curriculum.CurriculumData
import com.example.ui.EduViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.theme.EduPrimary
import com.example.ui.theme.EduSecondary
import com.example.ui.theme.EduTertiary

@Composable
fun HomeScreen(
    viewModel: EduViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsState()
    val dailyQuote by viewModel.dailyQuote.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchResults by viewModel.searchResults.collectAsState()
    val selectedClass by viewModel.selectedClass.collectAsState()
    val isStreakClaimed by viewModel.isStreakClaimedToday.collectAsState()
    val examCountdowns = viewModel.getExamCountdowns()

    var dailyAnswerSelected by remember { mutableStateOf<Int?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Hero Header Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(EduPrimary, Color(0xFF1D4ED8), Color(0xFF312E81))
                        )
                    )
                    .padding(20.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Welcome, ${user?.displayName ?: "Student"}! 🎓",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                            Text(
                                text = "Class $selectedClass Syllabus • CBSE / State Board",
                                color = Color.White.copy(alpha = 0.85f),
                                fontSize = 13.sp
                            )
                        }

                        // Streak Badge
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.clickable { onNavigate("DASHBOARD") }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalFireDepartment,
                                    contentDescription = "Streak",
                                    tint = EduTertiary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${user?.streakDays ?: 7}d Streak",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Global Search Bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.onSearchQueryChange(it) },
                        placeholder = { Text("Search subjects, chapters, questions…", fontSize = 13.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EduPrimary) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = Color(0xFF0F172A),
                            unfocusedTextColor = Color(0xFF0F172A),
                            focusedPlaceholderColor = Color(0xFF64748B),
                            unfocusedPlaceholderColor = Color(0xFF64748B)
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("home_search_input"),
                        singleLine = true
                    )
                }
            }
        }

        // Live Search Results Dropdown
        if (searchQuery.isNotBlank() && searchResults.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Search Results (${searchResults.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        searchResults.take(6).forEach { res ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        when (res.targetType) {
                                            "chapter" -> viewModel.openChapter(res.targetId)
                                            "subject" -> {
                                                viewModel.setSubject(res.targetId)
                                                onNavigate("CLASSES")
                                            }
                                            "question" -> onNavigate("QUESTION_BANK")
                                            "test" -> onNavigate("MOCK_TESTS")
                                            else -> onNavigate("CLASSES")
                                        }
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = MaterialTheme.colorScheme.primaryContainer,
                                        modifier = Modifier.padding(end = 10.dp)
                                    ) {
                                        Text(
                                            text = res.category,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = res.title,
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 13.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = res.description,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Streak Claim Booster Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isStreakClaimed) EduSecondary.copy(alpha = 0.12f) else EduTertiary.copy(alpha = 0.12f)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isStreakClaimed) EduSecondary.copy(alpha = 0.5f) else EduTertiary.copy(alpha = 0.5f)
                )
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isStreakClaimed) EduSecondary else EduTertiary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isStreakClaimed) Icons.Default.CheckCircle else Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isStreakClaimed) "Today's Streak XP Claimed! 🎉" else "Claim Today's Study Streak (+50 XP)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (isStreakClaimed) "Current streak: ${user?.streakDays ?: 7} consecutive study days. Keep it up!" else "Study daily to build long-term retention & unlock badges.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    if (!isStreakClaimed) {
                        Button(
                            onClick = { viewModel.claimDailyStreakXp() },
                            colors = ButtonDefaults.buttonColors(containerColor = EduTertiary),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("Claim", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Daily Motivational Quote Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(EduTertiary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FormatQuote,
                            contentDescription = "Quote",
                            tint = EduTertiary
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Daily Study Quote",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EduTertiary
                            )
                            Text(
                                text = "Inspire Me",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EduPrimary,
                                modifier = Modifier.clickable {
                                    val quotes = CurriculumData.DAILY_QUOTES
                                    val nextQuote = quotes.filter { it != dailyQuote }.randomOrNull() ?: quotes.first()
                                    viewModel.dailyQuote.value = nextQuote
                                }
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = dailyQuote,
                            fontStyle = FontStyle.Italic,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Quick Access Grid Buttons
        item {
            SectionHeader(title = "Quick Access", subtitle = "Explore learning pathways")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickNavCard(
                    title = "Classes",
                    subtitle = "6th to 12th",
                    icon = Icons.Default.Class,
                    color = EduPrimary,
                    modifier = Modifier.weight(1f)
                ) { onNavigate("CLASSES") }

                QuickNavCard(
                    title = "Subjects",
                    subtitle = "8 Disciplines",
                    icon = Icons.Default.MenuBook,
                    color = EduSecondary,
                    modifier = Modifier.weight(1f)
                ) { onNavigate("CLASSES") }

                QuickNavCard(
                    title = "Mock Tests",
                    subtitle = "Timed drills",
                    icon = Icons.Default.Quiz,
                    color = Color(0xFF7C3AED),
                    modifier = Modifier.weight(1f)
                ) { onNavigate("MOCK_TESTS") }

                QuickNavCard(
                    title = "Notes",
                    subtitle = "Summaries",
                    icon = Icons.Default.NoteAlt,
                    color = EduTertiary,
                    modifier = Modifier.weight(1f)
                ) { onNavigate("SAVED_ITEMS") }
            }
        }

        // Upcoming Exam Countdowns Carousel
        item {
            Spacer(modifier = Modifier.height(6.dp))
            SectionHeader(title = "Upcoming Exam Countdowns", subtitle = "Days remaining & preparation focus")
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(examCountdowns) { exam ->
                    Card(
                        modifier = Modifier.width(220.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (exam.daysRemaining <= 30) Color.Red.copy(alpha = 0.15f) else EduPrimary.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "${exam.daysRemaining} Days Left",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        color = if (exam.daysRemaining <= 30) Color.Red else EduPrimary,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                                Text(
                                    text = exam.dateStr,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = exam.examName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "💡 Tip: ${exam.tip}",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        // AI Assistant Feature Callout Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .clickable { onNavigate("AI_ASSISTANT") },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(EduPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Assistant",
                            tint = Color.White
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Edu Card AI Study Assistant",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Get step-by-step math solutions, concept explanations, and practice questions instantly.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Go",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        // Featured Study Materials (Horizontal Scroll)
        item {
            SectionHeader(
                title = "Featured Study Materials",
                subtitle = "Top chapters for Class $selectedClass",
                actionText = "View All",
                onActionClick = { onNavigate("CLASSES") }
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val featuredList = listOf(
                    Triple("10_phys_light", "Light: Reflection & Refraction", "Physics • Spherical Mirrors & Lenses"),
                    Triple("10_phys_electricity", "Electricity", "Physics • Ohm's Law & Circuit Analysis"),
                    Triple("10_math_quadratics", "Quadratic Equations", "Mathematics • Roots & Formula Method"),
                    Triple("10_chem_reactions", "Chemical Reactions", "Chemistry • Balancing & Redox Types")
                )

                items(featuredList) { (chId, title, sub) ->
                    Card(
                        modifier = Modifier
                            .width(240.dp)
                            .clickable { viewModel.openChapter(chId) },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer
                                ) {
                                    Text(
                                        text = "High-Yield",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(text = "🔥 Most Tested", fontSize = 10.sp, color = EduTertiary)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = sub,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Notes • Formulas • Q&A",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Daily Question of the Day Card
        item {
            Spacer(modifier = Modifier.height(12.dp))
            SectionHeader(title = "Daily Question of the Day", subtitle = "Solve to maintain your streak")
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Physics • Spherical Mirrors",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EduPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Which mirror always forms a virtual, erect, and diminished image regardless of distance?",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val options = listOf("Plane Mirror", "Concave Mirror", "Convex Mirror", "Parabolic Mirror")
                    val correct = 2

                    options.forEachIndexed { index, text ->
                        val isSelected = dailyAnswerSelected == index
                        val isCorrectOption = index == correct
                        val showResult = dailyAnswerSelected != null

                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = when {
                                showResult && isCorrectOption -> EduSecondary.copy(alpha = 0.2f)
                                showResult && isSelected -> Color.Red.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable {
                                    if (dailyAnswerSelected == null) {
                                        dailyAnswerSelected = index
                                    }
                                }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${('A' + index)}. $text",
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    modifier = Modifier.weight(1f)
                                )
                                if (showResult && isCorrectOption) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Correct",
                                        tint = EduSecondary,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }

                    if (dailyAnswerSelected != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (dailyAnswerSelected == correct) "🎉 Correct! +20 Streak points earned." else "💡 Convex mirrors always diverge light, forming virtual and diminished images.",
                            fontSize = 12.sp,
                            color = if (dailyAnswerSelected == correct) EduSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Extra Educational Utilities
        item {
            Spacer(modifier = Modifier.height(14.dp))
            SectionHeader(title = "Study Tools & Utilities", subtitle = "Boost your learning productivity")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                ToolAccessCard(
                    title = "Flashcards",
                    subtitle = "Spaced Repetition",
                    icon = Icons.Default.Style,
                    modifier = Modifier.weight(1f)
                ) { onNavigate("FLASHCARDS") }

                ToolAccessCard(
                    title = "Question Bank",
                    subtitle = "PYQs & HOTS",
                    icon = Icons.Default.Psychology,
                    modifier = Modifier.weight(1f)
                ) { onNavigate("QUESTION_BANK") }

                ToolAccessCard(
                    title = "Tools",
                    subtitle = "Formula & Timer",
                    icon = Icons.Default.Calculate,
                    modifier = Modifier.weight(1f)
                ) { onNavigate("TOOLS") }
            }
        }

        // App Footer Attribution
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "made by Praveen Kumar",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Edu Card • Learn • Practice • Improve",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
fun QuickNavCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = title, tint = color, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = subtitle, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ToolAccessCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = EduPrimary, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(text = subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
