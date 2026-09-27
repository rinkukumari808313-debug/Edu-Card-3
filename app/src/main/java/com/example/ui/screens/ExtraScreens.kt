package com.example.ui.screens

import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.FlashcardEntity
import com.example.data.local.SavedNoteEntity
import com.example.ui.EduViewModel
import com.example.ui.components.SectionHeader
import com.example.ui.theme.EduPrimary
import com.example.ui.theme.EduSecondary
import com.example.ui.theme.EduTertiary
import kotlin.math.sqrt

// ==========================================
// 1. FLASHCARDS SCREEN
// ==========================================
@Composable
fun FlashcardsScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val flashcards by viewModel.flashcards.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize().testTag("flashcards_screen")) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentPadding = PaddingValues(bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                SectionHeader(
                    title = "Interactive Flashcards",
                    subtitle = "Tap any card to flip front/back and test your recall"
                )
            }

            items(flashcards) { card ->
                FlashcardFlipItem(
                    card = card,
                    onToggleMastery = {
                        val next = if (card.masteryLevel >= 2) 0 else card.masteryLevel + 1
                        viewModel.updateFlashcardMastery(card, next)
                    },
                    onDelete = { viewModel.deleteFlashcard(card.id) }
                )
            }
        }

        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier.align(Alignment.BottomEnd).padding(20.dp).testTag("add_flashcard_fab"),
            containerColor = EduPrimary
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Flashcard", tint = Color.White)
        }
    }

    if (showAddDialog) {
        var front by remember { mutableStateOf("") }
        var back by remember { mutableStateOf("") }
        var topic by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showAddDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Create New Flashcard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = topic,
                        onValueChange = { topic = it },
                        label = { Text("Topic / Chapter") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = front,
                        onValueChange = { front = it },
                        label = { Text("Front (Term / Formula / Question)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = back,
                        onValueChange = { back = it },
                        label = { Text("Back (Definition / Solution / Answer)") },
                        modifier = Modifier.fillMaxWidth().height(100.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showAddDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (front.isNotBlank() && back.isNotBlank()) {
                                viewModel.createFlashcard("General", topic.ifBlank { "Study" }, front, back)
                                showAddDialog = false
                            }
                        }) {
                            Text("Create")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FlashcardFlipItem(
    card: FlashcardEntity,
    onToggleMastery: () -> Unit,
    onDelete: () -> Unit
) {
    var isFlipped by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { isFlipped = !isFlipped }
            .testTag("flashcard_item_${card.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isFlipped) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        ),
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
                    color = if (isFlipped) EduPrimary else MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Text(
                        text = if (isFlipped) "BACK (ANSWER)" else "FRONT (PROMPT)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isFlipped) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (card.masteryLevel) {
                            2 -> EduSecondary.copy(alpha = 0.2f)
                            1 -> EduTertiary.copy(alpha = 0.2f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        },
                        modifier = Modifier.clickable { onToggleMastery() }
                    ) {
                        Text(
                            text = when (card.masteryLevel) {
                                2 -> "★ Mastered"
                                1 -> "● Learning"
                                else -> "○ New"
                            },
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (card.masteryLevel) {
                                2 -> EduSecondary
                                1 -> EduTertiary
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    if (card.isCustom) {
                        IconButton(onClick = onDelete, modifier = Modifier.size(24.dp).padding(start = 6.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Text(
                text = if (isFlipped) card.back else card.front,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                lineHeight = 22.sp,
                color = if (isFlipped) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Tap to ${if (isFlipped) "see prompt" else "reveal answer"}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==========================================
// 2. TOOLS SCREEN (CALCULATOR + POMODORO + PLANNER)
// ==========================================
@Composable
fun ToolsScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    var toolTab by remember { mutableStateOf(0) } // 0: Formula Solver, 1: Focus Timer, 2: Revision Planner

    Column(modifier = modifier.fillMaxSize().testTag("tools_screen")) {
        ScrollableTabRow(
            selectedTabIndex = toolTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(selected = toolTab == 0, onClick = { toolTab = 0 }, text = { Text("Formula Calculator") })
            Tab(selected = toolTab == 1, onClick = { toolTab = 1 }, text = { Text("Study Focus Timer") })
            Tab(selected = toolTab == 2, onClick = { toolTab = 2 }, text = { Text("Revision Planner") })
        }

        when (toolTab) {
            0 -> FormulaCalculatorTab()
            1 -> StudyTimerTab(viewModel)
            2 -> RevisionPlannerTab()
        }
    }
}

@Composable
fun FormulaCalculatorTab() {
    var calcType by remember { mutableStateOf(0) } // 0: Quadratic, 1: Ohm's Law, 2: Kinetic Energy

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("Select Educational Calculator", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Quadratic Roots", "Ohm's Law (V=IR)", "Kinetic Energy").forEachIndexed { index, name ->
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (calcType == index) EduPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { calcType = index }
                    ) {
                        Text(
                            text = name,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (calcType == index) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        when (calcType) {
            0 -> item { QuadraticCalculatorCard() }
            1 -> item { OhmsLawCalculatorCard() }
            2 -> item { KineticEnergyCalculatorCard() }
        }
    }
}

@Composable
fun QuadraticCalculatorCard() {
    var aStr by remember { mutableStateOf("2") }
    var bStr by remember { mutableStateOf("-7") }
    var cStr by remember { mutableStateOf("3") }
    var resultText by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Solve Quadratic: ax² + bx + c = 0", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = aStr, onValueChange = { aStr = it }, label = { Text("a") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = bStr, onValueChange = { bStr = it }, label = { Text("b") }, modifier = Modifier.weight(1f))
                OutlinedTextField(value = cStr, onValueChange = { cStr = it }, label = { Text("c") }, modifier = Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    val a = aStr.toDoubleOrNull() ?: 1.0
                    val b = bStr.toDoubleOrNull() ?: 0.0
                    val c = cStr.toDoubleOrNull() ?: 0.0
                    if (a == 0.0) {
                        resultText = "Error: 'a' cannot be 0 in a quadratic equation."
                    } else {
                        val d = (b * b) - (4 * a * c)
                        if (d > 0) {
                            val r1 = (-b + sqrt(d)) / (2 * a)
                            val r2 = (-b - sqrt(d)) / (2 * a)
                            resultText = "Discriminant D = $d (>0: Two real distinct roots)\nx₁ = ${String.format("%.3f", r1)}\nx₂ = ${String.format("%.3f", r2)}"
                        } else if (d == 0.0) {
                            val r = -b / (2 * a)
                            resultText = "Discriminant D = 0 (Two equal real roots)\nx = ${String.format("%.3f", r)}"
                        } else {
                            resultText = "Discriminant D = $d (<0: No real roots / Complex conjugates)"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate Roots & Discriminant")
            }

            if (resultText.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = resultText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun OhmsLawCalculatorCard() {
    var vStr by remember { mutableStateOf("12") }
    var rStr by remember { mutableStateOf("4") }
    var currentResult by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Ohm's Law: V = I · R (Calculate Current)", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = vStr, onValueChange = { vStr = it }, label = { Text("Voltage V (Volts)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = rStr, onValueChange = { rStr = it }, label = { Text("Resistance R (Ohms)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    val v = vStr.toDoubleOrNull() ?: 0.0
                    val r = rStr.toDoubleOrNull() ?: 1.0
                    if (r <= 0) {
                        currentResult = "Resistance must be greater than 0."
                    } else {
                        val i = v / r
                        val p = v * i
                        currentResult = "Current I = V / R = ${String.format("%.3f", i)} Amperes\nPower P = V · I = ${String.format("%.3f", p)} Watts"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Compute Current & Power")
            }

            if (currentResult.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = currentResult,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun KineticEnergyCalculatorCard() {
    var massStr by remember { mutableStateOf("5") }
    var velStr by remember { mutableStateOf("10") }
    var keResult by remember { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Kinetic Energy: KE = ½ · m · v²", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(value = massStr, onValueChange = { massStr = it }, label = { Text("Mass m (kg)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = velStr, onValueChange = { velStr = it }, label = { Text("Velocity v (m/s)") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = {
                    val m = massStr.toDoubleOrNull() ?: 0.0
                    val v = velStr.toDoubleOrNull() ?: 0.0
                    val ke = 0.5 * m * v * v
                    keResult = "Kinetic Energy KE = ${String.format("%.2f", ke)} Joules (J)"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Calculate Kinetic Energy")
            }

            if (keResult.isNotBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    color = MaterialTheme.colorScheme.primaryContainer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = keResult,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun StudyTimerTab(viewModel: EduViewModel) {
    val secondsLeft by viewModel.studyTimerSecondsRemaining.collectAsState()
    val isRunning by viewModel.isStudyTimerRunning.collectAsState()
    val activeAmbient by viewModel.activeAmbientSound.collectAsState()
    val mins = secondsLeft / 60
    val secs = secondsLeft % 60

    val presets = listOf(
        Pair("25m Pomodoro", 25),
        Pair("45m Deep Work", 45),
        Pair("15m Quick Drill", 15)
    )

    val ambientSounds = listOf(
        "Library Ambiance 📚",
        "Rain Shower 🌧️",
        "White Noise 💨",
        "Alpha Waves 🧠"
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text("Pomodoro Study Focus Timer", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text("Scientifically proven interval learning for maximum retention", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Timer Preset Chips
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                presets.forEach { (label, duration) ->
                    val isCurrent = viewModel.studyTimerMinutes.value == duration
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isCurrent) EduPrimary else MaterialTheme.colorScheme.surfaceVariant,
                        modifier = Modifier.clickable { viewModel.resetStudyTimer(duration) }
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCurrent) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Timer Dial
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .clip(CircleShape)
                    .background(if (isRunning) EduPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = String.format("%02d:%02d", mins, secs),
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace,
                        color = if (isRunning) EduPrimary else MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (isRunning) "Deep Focus Active" else "Ready to Begin",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Control Buttons
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(
                    onClick = { viewModel.toggleStudyTimer() },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isRunning) Color.Red else EduPrimary),
                    modifier = Modifier.width(140.dp)
                ) {
                    Icon(if (isRunning) Icons.Default.Pause else Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isRunning) "Pause" else "Start")
                }

                OutlinedButton(
                    onClick = { viewModel.resetStudyTimer(viewModel.studyTimerMinutes.value) },
                    modifier = Modifier.width(120.dp)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Reset")
                }
            }
        }

        // Ambient Background Soundscape Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("Ambient Study Soundscape", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Enhance concentration with non-distracting background sound", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        ambientSounds.forEach { sound ->
                            val isSelected = activeAmbient == sound
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSelected) EduSecondary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier
                                    .weight(1f)
                                    .clickable { viewModel.activeAmbientSound.value = sound }
                            ) {
                                Text(
                                    text = sound,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                                    maxLines = 1
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
fun RevisionPlannerTab() {
    val checklist = remember {
        mutableStateListOf(
            Pair("Review Light reflection ray diagrams", true),
            Pair("Practice 10 quadratic formula equations", true),
            Pair("Memorize Ohm's law and resistivity definitions", false),
            Pair("Solve 1 Mock Test on Science", false),
            Pair("Revise Chemical Reactions oxidation & rancidity", false)
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text("Weekly Revision Planner", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("Track syllabus milestones before tests and exams", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(6.dp))
        }

        items(checklist.indices.toList()) { idx ->
            val (task, isDone) = checklist[idx]
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = isDone,
                        onCheckedChange = { checked ->
                            checklist[idx] = Pair(task, checked)
                        }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = task,
                        fontSize = 13.sp,
                        fontWeight = if (isDone) FontWeight.Normal else FontWeight.SemiBold,
                        color = if (isDone) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// ==========================================
// 3. SAVED ITEMS & BOOKMARKS SCREEN
// ==========================================
@Composable
fun SavedItemsScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    val savedNotes by viewModel.savedNotes.collectAsState()
    var tab by remember { mutableStateOf(0) } // 0: Bookmarks, 1: Saved Notes

    Column(modifier = modifier.fillMaxSize().testTag("saved_items_screen")) {
        ScrollableTabRow(
            selectedTabIndex = tab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(selected = tab == 0, onClick = { tab = 0 }, text = { Text("Bookmarks (${bookmarks.size})") })
            Tab(selected = tab == 1, onClick = { tab = 1 }, text = { Text("Notes (${savedNotes.size})") })
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (tab == 0) {
                if (bookmarks.isEmpty()) {
                    item { Text("No bookmarks yet. Tap the bookmark icon on questions or chapters to pin them!", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    items(bookmarks) { b ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Surface(shape = RoundedCornerShape(4.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                                        Text(text = b.itemType, fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    IconButton(
                                        onClick = { viewModel.toggleBookmark(b.id, b.itemType, b.title, b.subtitle, b.contentSnippet) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = b.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = b.contentSnippet, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            } else {
                if (savedNotes.isEmpty()) {
                    item { Text("No saved notes yet. You can write your own notes inside chapter views or save from AI Study Assistant!", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                } else {
                    items(savedNotes) { note ->
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    IconButton(onClick = { viewModel.deleteNote(note.id) }, modifier = Modifier.size(24.dp)) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Text(text = "Author: ${note.authorName} (${note.authorRole})", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = note.content, fontSize = 13.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. TEACHER & ADMIN PORTAL
// ==========================================
@Composable
fun TeacherAdminScreen(
    viewModel: EduViewModel,
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsState()
    var portalTab by remember { mutableStateOf(0) } // 0: Teacher Actions, 1: Admin Panel

    Column(modifier = modifier.fillMaxSize().testTag("teacher_admin_screen")) {
        ScrollableTabRow(
            selectedTabIndex = portalTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            Tab(selected = portalTab == 0, onClick = { portalTab = 0 }, text = { Text("Teacher Portal") })
            Tab(selected = portalTab == 1, onClick = { portalTab = 1 }, text = { Text("Admin Panel") })
        }

        if (portalTab == 0) {
            TeacherPortalContent(viewModel)
        } else {
            AdminPanelContent(viewModel)
        }
    }
}

@Composable
fun TeacherPortalContent(viewModel: EduViewModel) {
    var annTitle by remember { mutableStateOf("") }
    var annMessage by remember { mutableStateOf("") }
    var statusMsg by remember { mutableStateOf("") }

    var qSubject by remember { mutableStateOf("physics") }
    var qText by remember { mutableStateOf("") }
    var qOptA by remember { mutableStateOf("") }
    var qOptB by remember { mutableStateOf("") }
    var qOptC by remember { mutableStateOf("") }
    var qOptD by remember { mutableStateOf("") }
    var qCorrectIdx by remember { mutableStateOf("0") }
    var qExplanation by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Teacher Control Center", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text("Upload study notes, create custom questions, share announcements, and monitor student test scores.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
                }
            }
        }

        // Post Announcement
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Broadcast Class Announcement", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = annTitle, onValueChange = { annTitle = it }, label = { Text("Announcement Title") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = annMessage, onValueChange = { annMessage = it }, label = { Text("Message / Instructions") }, modifier = Modifier.fillMaxWidth().height(90.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (annTitle.isNotBlank() && annMessage.isNotBlank()) {
                                viewModel.postTeacherAnnouncement(annTitle, annMessage, 10)
                                annTitle = ""
                                annMessage = ""
                                statusMsg = "Announcement published to Class 10!"
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Post Announcement")
                    }
                    if (statusMsg.isNotBlank()) {
                        Text(text = statusMsg, color = EduSecondary, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            }
        }

        // Add Custom Question
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Add Question to Question Bank", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = qText, onValueChange = { qText = it }, label = { Text("Question Text") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = qOptA, onValueChange = { qOptA = it }, label = { Text("Option A") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = qOptB, onValueChange = { qOptB = it }, label = { Text("Option B") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = qOptC, onValueChange = { qOptC = it }, label = { Text("Option C") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(value = qOptD, onValueChange = { qOptD = it }, label = { Text("Option D") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = qCorrectIdx, onValueChange = { qCorrectIdx = it }, label = { Text("Correct Option (0 for A, 1 for B, 2 for C, 3 for D)") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(value = qExplanation, onValueChange = { qExplanation = it }, label = { Text("Explanation") }, modifier = Modifier.fillMaxWidth())
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = {
                            if (qText.isNotBlank() && qOptA.isNotBlank()) {
                                viewModel.addTeacherQuestion(
                                    subjectId = qSubject,
                                    chapter = "Chapter Drill",
                                    questionText = qText,
                                    optA = qOptA,
                                    optB = qOptB,
                                    optC = qOptC,
                                    optD = qOptD,
                                    correctIdx = qCorrectIdx.toIntOrNull() ?: 0,
                                    explanation = qExplanation,
                                    difficulty = "Medium"
                                )
                                qText = ""
                                qOptA = ""
                                qOptB = ""
                                qOptC = ""
                                qOptD = ""
                                qExplanation = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Add Question")
                    }
                }
            }
        }
    }
}

@Composable
fun AdminPanelContent(viewModel: EduViewModel) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Edu Card Platform Administration", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatItem("Total Classes", "7 (6 to 12)", EduPrimary)
                        StatItem("Subjects", "8 Fields", EduSecondary)
                        StatItem("Active Students", "1,240", EduTertiary)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        StatItem("Total Questions", "120+ Bank", EduPrimary)
                        StatItem("Mock Tests", "8 Active", EduSecondary)
                        StatItem("Platform Uptime", "99.9%", EduTertiary)
                    }
                }
            }
        }

        item {
            Text("Curriculum Management", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("• All 8 Disciplines active across grades 6 to 12.", fontSize = 12.sp)
                    Text("• Chapter database sync status: OK (Local Room Persistence)", fontSize = 12.sp)
                    Text("• AI Study Assistant status: ONLINE (Gemini 3.5 Flash)", fontSize = 12.sp)
                }
            }
        }
    }
}

// ==========================================
// 5. PROFILE & AUTH SCREEN
// ==========================================
@Composable
fun ProfileAuthScreen(
    viewModel: EduViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val user by viewModel.user.collectAsState()
    var nameInput by remember { mutableStateOf(user?.displayName ?: "Aarav Sharma") }
    var selectedRole by remember { mutableStateOf(user?.role ?: "STUDENT") }
    var isSavedConfirm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize().padding(16.dp).testTag("profile_auth_screen"),
        contentPadding = PaddingValues(bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text("Profile & Authentication", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Switch roles seamlessly (Student, Teacher, Admin)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(modifier = Modifier.height(14.dp))

                    Text("Switch Active Role", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("STUDENT", "TEACHER", "ADMIN").forEach { role ->
                            val isSelected = selectedRole == role
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) EduPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { selectedRole = role }
                            ) {
                                Text(
                                    text = role,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Display Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.switchRole(selectedRole, nameInput)
                            isSavedConfirm = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Update Profile / Switch Role")
                    }

                    if (isSavedConfirm) {
                        Text("Profile updated successfully!", color = EduSecondary, fontSize = 12.sp, modifier = Modifier.padding(top = 6.dp))
                    }
                }
            }
        }

        // About Edu Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("About Edu Card", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Edu Card is an all-in-one educational platform designed for students in Classes 6 to 12. Tagline: 'Learn • Practice • Improve'. Offering high-yield notes, interactive flashcards, MCQ practice with instant explanations, timed mock tests, and an intelligent AI Study Assistant.",
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Privacy Policy & Terms
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Privacy & Student Protection Policy", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Edu Card does not collect unnecessary personal information from students. Leaderboard rankings display privacy-friendly aliases. All test scores and notes are stored securely on-device with Room database persistence.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Terms & Conditions: Free academic resource platform for students, teachers, and schools.", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        // Creator Attribution
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "made by Praveen Kumar",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Edu Card • All-in-One Learning Platform",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
