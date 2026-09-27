package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NoteAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.curriculum.CurriculumData
import com.example.data.model.Chapter
import com.example.data.model.SubjectInfo
import com.example.ui.EduViewModel
import com.example.ui.theme.EduPrimary
import com.example.ui.theme.EduSecondary
import com.example.util.EduTtsManager

@Composable
fun ClassesAndSubjectsScreen(
    viewModel: EduViewModel,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentClass by viewModel.selectedClass.collectAsState()
    val selectedSubjectId by viewModel.selectedSubjectId.collectAsState()
    val subjects = viewModel.getSubjects()
    val chapters = viewModel.getChapters(selectedSubjectId)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("classes_subjects_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Class Selector Row (Classes 6 to 12)
        item {
            Column {
                Text(
                    text = "Select Class Grade",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items((6..12).toList()) { grade ->
                        val isSelected = grade == currentClass
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (isSelected) EduPrimary else MaterialTheme.colorScheme.surfaceVariant,
                            modifier = Modifier
                                .clickable { viewModel.setClass(grade) }
                                .testTag("class_tab_$grade")
                        ) {
                            Text(
                                text = "Class $grade",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }
        }

        // Subjects Grid / Row
        item {
            Column {
                Text(
                    text = "Subjects (Class $currentClass)",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(10.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(subjects) { subj ->
                        val isSelected = subj.id == selectedSubjectId
                        Card(
                            modifier = Modifier
                                .width(150.dp)
                                .clickable { viewModel.setSubject(subj.id) }
                                .testTag("subject_card_${subj.id}"),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 4.dp else 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(Color(subj.primaryColorHex).copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.MenuBook,
                                        contentDescription = subj.name,
                                        tint = Color(subj.primaryColorHex),
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = subj.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = subj.hindiName,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Chapters List Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val currentSubjName = subjects.find { it.id == selectedSubjectId }?.name ?: "Subject"
                Text(
                    text = "Chapters • $currentSubjName",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${chapters.size} Chapters",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Chapters List
        items(chapters) { ch ->
            ChapterItemCard(
                chapter = ch,
                onOpenChapter = { viewModel.openChapter(ch.id) },
                onStartQuiz = {
                    viewModel.startMcqPractice(subjectId = ch.subjectId, chapterId = ch.id)
                }
            )
        }
    }
}

@Composable
fun ChapterItemCard(
    chapter: Chapter,
    onOpenChapter: () -> Unit,
    onStartQuiz: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenChapter() }
            .testTag("chapter_item_${chapter.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "Ch ${chapter.chapterNum}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = chapter.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = chapter.overview,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    SubjectResourceBadge(text = "${chapter.definitions.size} Defs")
                    SubjectResourceBadge(text = "${chapter.formulas.size} Formulas")
                    SubjectResourceBadge(text = "${chapter.importantQuestions.size} Qs")
                }
                TextButton(
                    onClick = onStartQuiz,
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Practice MCQs", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun SubjectResourceBadge(text: String) {
    Surface(
        shape = RoundedCornerShape(6.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Text(
            text = text,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun ChapterDetailScreen(
    viewModel: EduViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val chapter = viewModel.getCurrentChapter()
    val bookmarks by viewModel.bookmarks.collectAsState()
    var selectedTab by remember { mutableStateOf(0) }
    var showNoteDialog by remember { mutableStateOf(false) }
    var noteInput by remember { mutableStateOf("") }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val ttsManager = remember { EduTtsManager(context) }

    DisposableEffect(Unit) {
        onDispose {
            ttsManager.shutdown()
        }
    }

    if (chapter == null) {
        Column(
            modifier = modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Chapter not found.")
            Button(onClick = onBack) { Text("Go Back") }
        }
        return
    }

    val isBookmarked = bookmarks.any { it.id == chapter.id }
    val tabTitles = listOf("Notes", "Definitions", "Formulas", "Key Points", "Diagrams", "Summary", "Questions")

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("chapter_detail_screen")
    ) {
        // Chapter Header Bar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 2.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                    Text(
                        text = "Class ${chapter.classNum} • ${chapter.subjectId.replaceFirstChar { it.uppercase() }}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            if (ttsManager.isSpeaking.value) {
                                ttsManager.stop()
                            } else {
                                val textToRead = when (selectedTab) {
                                    0 -> chapter.notes
                                    5 -> chapter.summary
                                    else -> "${chapter.title}. ${chapter.overview}"
                                }
                                ttsManager.speak(textToRead)
                            }
                        }) {
                            Icon(
                                imageVector = if (ttsManager.isSpeaking.value) Icons.Default.Stop else Icons.Default.VolumeUp,
                                contentDescription = "Audio Listen",
                                tint = if (ttsManager.isSpeaking.value) Color.Red else EduPrimary
                            )
                        }
                        IconButton(onClick = {
                            viewModel.toggleBookmark(
                                id = chapter.id,
                                type = "NOTE",
                                title = chapter.title,
                                subtitle = "Class ${chapter.classNum} ${chapter.subjectId}",
                                snippet = chapter.overview
                            )
                        }) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) EduPrimary else MaterialTheme.colorScheme.onSurface
                            )
                        }
                        IconButton(onClick = { showNoteDialog = true }) {
                            Icon(Icons.Default.NoteAdd, contentDescription = "Save Note")
                        }
                    }
                }
                Text(
                    text = chapter.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Horizontal Scrollable Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 16.dp,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) }
                )
            }
        }

        // Tab Content Display
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            when (selectedTab) {
                // 1. Comprehensive Notes
                0 -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Comprehensive Study Notes", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EduPrimary)
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = chapter.notes,
                                    fontSize = 14.sp,
                                    lineHeight = 22.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                    item {
                        Button(
                            onClick = { viewModel.startMcqPractice(subjectId = chapter.subjectId, chapterId = chapter.id) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = EduPrimary)
                        ) {
                            Icon(Icons.Default.Quiz, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Test Your Knowledge: Practice MCQs")
                        }
                    }
                }

                // 2. Definitions
                1 -> {
                    items(chapter.definitions) { def ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(text = def.term, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = def.definition, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                // 3. Formulas
                2 -> {
                    items(chapter.formulas) { f ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = f.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(f.formula))
                                            Toast.makeText(context, "Formula copied to clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.ContentCopy, contentDescription = "Copy formula", tint = EduPrimary, modifier = Modifier.size(16.dp))
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = f.formula,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        fontFamily = FontFamily.Monospace,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = f.explanation, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }

                // 4. Key Points
                3 -> {
                    items(chapter.keyPoints) { point ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
                                Text(text = "•", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = EduPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = point, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                // 5. Diagrams / Schematics
                4 -> {
                    items(chapter.diagrams) { diag ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = diag.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.primary)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = diag.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(10.dp))
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = diag.asciiArtOrLabel,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // 6. Summary
                5 -> {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(text = "Chapter Summary", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = EduPrimary)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = chapter.summary, fontSize = 14.sp, lineHeight = 22.sp)
                            }
                        }
                    }
                }

                // 7. Important Questions
                6 -> {
                    items(chapter.importantQuestions) { q ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = "Important Question",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = EduPrimary
                                    )
                                    Text(
                                        text = "${q.marks} Marks",
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 11.sp,
                                        color = EduSecondary
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = q.question, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(text = "Answer:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = q.answer, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }
        }
    }

    if (showNoteDialog) {
        Dialog(onDismissRequest = { showNoteDialog = false }) {
            Card(
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Save Personal Study Note", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = noteInput,
                        onValueChange = { noteInput = it },
                        label = { Text("Write your key takeaways or doubt notes...") },
                        modifier = Modifier.fillMaxWidth().height(140.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = { showNoteDialog = false }) { Text("Cancel") }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(onClick = {
                            if (noteInput.isNotBlank()) {
                                viewModel.saveNote(
                                    title = "Note on ${chapter.title}",
                                    content = noteInput
                                )
                                showNoteDialog = false
                                noteInput = ""
                            }
                        }) {
                            Text("Save Note")
                        }
                    }
                }
            }
        }
    }
}
