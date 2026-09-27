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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.example.data.curriculum.CurriculumData
import com.example.data.model.MockTestInfo
import com.example.data.model.Question
import com.example.ui.EduViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.theme.EduPrimary
import com.example.ui.theme.EduSecondary
import com.example.ui.theme.EduTertiary

@Composable
fun McqPracticeScreen(
    viewModel: EduViewModel,
    onNavigateToTests: () -> Unit,
    onNavigateToBank: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mcqState by viewModel.mcqState.collectAsState()
    val questions = mcqState.questions

    if (questions.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Quiz, contentDescription = null, tint = EduPrimary, modifier = Modifier.size(56.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Ready to Practice?", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Select a chapter or test your knowledge across all subjects.", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = { viewModel.startMcqPractice() }, modifier = Modifier.testTag("start_quick_quiz_btn")) {
                Text("Start Quick Mixed Practice")
            }
        }
        return
    }

    if (mcqState.isFinished) {
        // Result Screen
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(EduSecondary.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EduSecondary, modifier = Modifier.size(48.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Practice Complete!", fontWeight = FontWeight.Bold, fontSize = 22.sp)
            Spacer(modifier = Modifier.height(8.dp))
            val percent = if (questions.isNotEmpty()) (mcqState.correctAnswersCount * 100) / questions.size else 0
            Text(
                text = "Score: ${mcqState.correctAnswersCount} / ${questions.size} ($percent%)",
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                color = EduPrimary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (percent >= 80) "🌟 Outstanding work! You have mastered these concepts." else "💪 Good effort! Review the explanations to strengthen weak areas.",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { viewModel.startMcqPractice() }) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Practice Again")
                }
                OutlinedButton(onClick = onNavigateToTests) {
                    Text("Take Mock Test")
                }
            }
        }
        return
    }

    val currentQ = questions[mcqState.currentIndex]
    val options = listOf(currentQ.optionA, currentQ.optionB, currentQ.optionC, currentQ.optionD)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("active_mcq_screen"),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        // Difficulty filter chips & progress
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Question ${mcqState.currentIndex + 1} of ${questions.size}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when (currentQ.difficulty.lowercase()) {
                        "easy" -> EduSecondary.copy(alpha = 0.2f)
                        "medium" -> EduTertiary.copy(alpha = 0.2f)
                        else -> Color.Red.copy(alpha = 0.2f)
                    }
                ) {
                    Text(
                        text = currentQ.difficulty,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (currentQ.difficulty.lowercase()) {
                            "easy" -> EduSecondary
                            "medium" -> EduTertiary
                            else -> Color.Red
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { (mcqState.currentIndex + 1).toFloat() / questions.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = EduPrimary,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Question Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(
                        text = "${currentQ.subjectId.replaceFirstChar { it.uppercase() }} • Class ${currentQ.classNum}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentQ.questionText,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        lineHeight = 24.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 4 Options with Instant Feedback
        itemsIndexed(options) { idx, optionText ->
            val isSelected = mcqState.selectedOptionIndex == idx
            val isCorrect = idx == currentQ.correctIndex
            val showFeedback = mcqState.isAnswerRevealed

            val bgColor = when {
                showFeedback && isCorrect -> EduSecondary.copy(alpha = 0.2f)
                showFeedback && isSelected && !isCorrect -> Color.Red.copy(alpha = 0.15f)
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.surface
            }

            val borderColor = when {
                showFeedback && isCorrect -> EduSecondary
                showFeedback && isSelected && !isCorrect -> Color.Red
                isSelected -> EduPrimary
                else -> Color.Transparent
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable(enabled = !showFeedback) {
                        viewModel.selectMcqOption(idx)
                    }
                    .testTag("mcq_option_$idx"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = bgColor),
                border = androidx.compose.foundation.BorderStroke(1.dp, borderColor)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(
                                if (showFeedback && isCorrect) EduSecondary
                                else if (showFeedback && isSelected) Color.Red
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${('A' + idx)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (showFeedback && (isCorrect || isSelected)) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = optionText,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected || (showFeedback && isCorrect)) FontWeight.Bold else FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // Explanation & Next Button
        if (mcqState.isAnswerRevealed) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.HelpOutline, contentDescription = null, tint = EduPrimary, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Explanation:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EduPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(text = currentQ.explanation, fontSize = 13.sp, lineHeight = 20.sp)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { viewModel.nextMcqQuestion() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("next_mcq_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = EduPrimary)
                ) {
                    Text(if (mcqState.currentIndex + 1 < questions.size) "Next Question" else "See Final Results")
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun MockTestsListScreen(
    viewModel: EduViewModel,
    onNavigateToBank: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tests = viewModel.getMockTests()
    val testHistory by viewModel.testHistory.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("mock_tests_screen"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            SectionHeader(
                title = "Mock Tests & Competitive Drills",
                subtitle = "Timed tests with automatic submission & detailed review"
            )
        }

        items(tests) { test ->
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (test.isCompetitive) EduTertiary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = test.examType,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (test.isCompetitive) EduTertiary else MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = EduPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "${test.durationMinutes} Mins", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = test.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Class ${test.classNum} • ${test.totalQuestions} Questions • Real Exam Marking",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.startMockTest(test) },
                        modifier = Modifier.fillMaxWidth().testTag("start_test_${test.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = EduPrimary)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Start Timed Test")
                    }
                }
            }
        }

        // Test History Section
        if (testHistory.isNotEmpty()) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader(title = "Recent Test Scores & Performance")
            }
            items(testHistory.take(5)) { hist ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = hist.testTitle, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text(
                                text = "${hist.correctCount}/${hist.totalQuestions} correct • Spent ${hist.durationSeconds / 60}m ${hist.durationSeconds % 60}s",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (hist.score >= 75) EduSecondary.copy(alpha = 0.2f) else EduTertiary.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "${hist.score}%",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (hist.score >= 75) EduSecondary else EduTertiary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActiveMockTestScreen(
    viewModel: EduViewModel,
    onBackToList: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.testState.collectAsState()
    val testInfo = state.testInfo

    if (testInfo == null) {
        Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Text("No test currently running.")
            Button(onClick = onBackToList) { Text("Back to Tests") }
        }
        return
    }

    if (state.isSubmitted) {
        var reviewFilter by remember { mutableStateOf("ALL") }

        val indexedQuestions = testInfo.questions.mapIndexed { idx, q -> Pair(idx, q) }
        val filteredReviewQuestions = when (reviewFilter) {
            "INCORRECT" -> indexedQuestions.filter { (idx, q) ->
                val userChoice = state.selectedAnswers[idx]
                userChoice == null || userChoice != q.correctIndex
            }
            "CORRECT" -> indexedQuestions.filter { (idx, q) ->
                val userChoice = state.selectedAnswers[idx]
                userChoice != null && userChoice == q.correctIndex
            }
            else -> indexedQuestions
        }

        // Detailed Performance Analysis
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(16.dp)
                .testTag("test_result_screen"),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(text = "Mock Test Result & Analysis", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "${state.scorePercentage}%",
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (state.scorePercentage >= 75) EduSecondary else EduPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Correct: ${state.correctCount}  •  Incorrect: ${state.incorrectCount}  •  Total: ${testInfo.questions.size}",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Solutions & Review", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("ALL", "INCORRECT", "CORRECT").forEach { filter ->
                            val isSelected = reviewFilter == filter
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) EduPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { reviewFilter = filter }
                            ) {
                                Text(
                                    text = filter,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            items(filteredReviewQuestions) { (idx, q) ->
                val userChoice = state.selectedAnswers[idx]
                val isCorrect = userChoice == q.correctIndex
                val opts = listOf(q.optionA, q.optionB, q.optionC, q.optionD)

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Q${idx + 1}: ${q.questionText}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                            Text(
                                text = if (userChoice == null) "Skipped" else if (isCorrect) "✓ Correct" else "✗ Wrong",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isCorrect) EduSecondary else Color.Red
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Correct Answer: ${('A' + q.correctIndex)}. ${opts[q.correctIndex]}", fontSize = 12.sp, color = EduSecondary, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Explanation: ${q.explanation}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            item {
                Button(
                    onClick = onBackToList,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Finish and Return to Tests")
                }
            }
        }
        return
    }

    // Active Test Taking Mode
    val currentQ = testInfo.questions[state.currentQuestionIndex]
    val mins = state.remainingSeconds / 60
    val secs = state.remainingSeconds % 60
    val options = listOf(currentQ.optionA, currentQ.optionB, currentQ.optionC, currentQ.optionD)
    val userSelectedOption = state.selectedAnswers[state.currentQuestionIndex]

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("active_test_taking")
    ) {
        // Timer and Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Question ${state.currentQuestionIndex + 1} of ${testInfo.questions.size}",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = if (state.remainingSeconds < 120) Color.Red.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Timer,
                        contentDescription = "Timer",
                        tint = if (state.remainingSeconds < 120) Color.Red else EduPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (state.remainingSeconds < 120) Color.Red else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Question Box
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Text(
                    text = currentQ.questionText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 24.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Options
        options.forEachIndexed { optIdx, optText ->
            val isChosen = userSelectedOption == optIdx
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable {
                        viewModel.selectTestAnswer(state.currentQuestionIndex, optIdx)
                    },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isChosen) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, if (isChosen) EduPrimary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(if (isChosen) EduPrimary else MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${('A' + optIdx)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isChosen) Color.White else MaterialTheme.colorScheme.onSurface
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = optText,
                        fontSize = 14.sp,
                        fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Navigation Footer: Prev, Next, Submit
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { viewModel.prevTestQuestion() },
                enabled = state.currentQuestionIndex > 0
            ) {
                Text("Previous")
            }

            if (state.currentQuestionIndex + 1 < testInfo.questions.size) {
                Button(
                    onClick = { viewModel.nextTestQuestion() },
                    colors = ButtonDefaults.buttonColors(containerColor = EduPrimary)
                ) {
                    Text("Next")
                }
            } else {
                Button(
                    onClick = { viewModel.submitMockTest() },
                    colors = ButtonDefaults.buttonColors(containerColor = EduSecondary),
                    modifier = Modifier.testTag("submit_test_btn")
                ) {
                    Text("Submit Test")
                }
            }
        }
    }
}

@Composable
fun QuestionBankScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val categoryFilter by viewModel.questionBankCategory.collectAsState()
    val allQuestions = viewModel.getAllQuestions()
    val bookmarks by viewModel.bookmarks.collectAsState()

    val categories = listOf("ALL", "PYQ", "IMPORTANT", "HOTS", "ASSERTION_REASON", "CASE_BASED", "OLYMPIAD")

    val filteredQuestions = if (categoryFilter == "ALL") {
        allQuestions
    } else {
        allQuestions.filter { it.category.equals(categoryFilter, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("question_bank_screen"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader(
                title = "Question Bank",
                subtitle = "Practice PYQs, HOTS, Assertion-Reason, and Olympiad questions"
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = cat == categoryFilter
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isSelected) EduPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { viewModel.questionBankCategory.value = cat }
                    ) {
                        Text(
                            text = cat.replace("_", " "),
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        items(filteredQuestions) { q ->
            var isRevealed by remember { mutableStateOf(false) }
            val isBookmarked = bookmarks.any { it.id == q.id }

            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = q.category.replace("_", " "),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        IconButton(onClick = {
                            viewModel.toggleBookmark(
                                id = q.id,
                                type = "QUESTION",
                                title = q.questionText,
                                subtitle = "${q.subjectId} (${q.category})",
                                snippet = q.explanation
                            )
                        }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) EduPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = q.questionText, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    val opts = listOf(q.optionA, q.optionB, q.optionC, q.optionD)
                    opts.forEachIndexed { i, opt ->
                        Text(
                            text = "${('A' + i)}. $opt",
                            fontSize = 12.sp,
                            color = if (isRevealed && i == q.correctIndex) EduSecondary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isRevealed && i == q.correctIndex) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { isRevealed = !isRevealed }) {
                            Text(if (isRevealed) "Hide Answer" else "Reveal Answer & Explanation")
                        }
                    }

                    if (isRevealed) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "Correct: Option ${('A' + q.correctIndex)}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = EduSecondary
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text = q.explanation, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
