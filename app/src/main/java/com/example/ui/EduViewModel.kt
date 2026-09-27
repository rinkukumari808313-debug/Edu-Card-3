package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.curriculum.CurriculumData
import com.example.data.local.BookmarkEntity
import com.example.data.local.CustomQuestionEntity
import com.example.data.local.EduDatabase
import com.example.data.local.FlashcardEntity
import com.example.data.local.SavedNoteEntity
import com.example.data.local.StudyActivityEntity
import com.example.data.local.TeacherAnnouncementEntity
import com.example.data.local.TestHistoryEntity
import com.example.data.local.UserEntity
import com.example.data.model.Chapter
import com.example.data.model.EduBadge
import com.example.data.model.EduNotification
import com.example.data.model.LeaderboardUser
import com.example.data.model.MockTestInfo
import com.example.data.model.Question
import com.example.data.model.SubjectInfo
import com.example.data.repository.EduCardRepository
import com.example.data.repository.SearchResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class AiChatMessage(
    val sender: String, // "USER" or "AI"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

data class ActiveTestState(
    val testInfo: MockTestInfo? = null,
    val currentQuestionIndex: Int = 0,
    val selectedAnswers: Map<Int, Int> = emptyMap(), // questionIndex -> selectedOption
    val isSubmitted: Boolean = false,
    val remainingSeconds: Int = 0,
    val scorePercentage: Int = 0,
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val isTimerRunning: Boolean = false
)

data class ActiveMcqPracticeState(
    val questions: List<Question> = emptyList(),
    val currentIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val isAnswerRevealed: Boolean = false,
    val correctAnswersCount: Int = 0,
    val selectedDifficulty: String = "All", // "All", "Easy", "Medium", "Hard"
    val isFinished: Boolean = false
)

class EduViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EduCardRepository

    init {
        val dao = EduDatabase.getDatabase(application).eduDao()
        repository = EduCardRepository(dao)
        viewModelScope.launch {
            repository.initializeDefaultDataIfNeeded()
        }
    }

    // App Navigation & Current Selection
    val currentScreen = MutableStateFlow("HOME") // "HOME", "CLASSES", "SUBJECTS", "CHAPTER_DETAIL", "MCQ_PRACTICE", "MOCK_TESTS", "ACTIVE_TEST", "QUESTION_BANK", "AI_ASSISTANT", "DASHBOARD", "LEADERBOARD", "FLASHCARDS", "TOOLS", "TEACHER_ADMIN", "SAVED_ITEMS", "ABOUT_LEGAL"
    val selectedClass = MutableStateFlow(10) // 6 to 12
    val selectedSubjectId = MutableStateFlow("physics")
    val selectedChapterId = MutableStateFlow("10_phys_light")

    // Theme state
    val isDarkMode = MutableStateFlow(false)

    // Search
    val searchQuery = MutableStateFlow("")
    val searchResults = MutableStateFlow<List<SearchResult>>(emptyList())

    // User profile state
    val user: StateFlow<UserEntity?> = repository.getUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Data streams
    val bookmarks: StateFlow<List<BookmarkEntity>> = repository.getAllBookmarks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val savedNotes: StateFlow<List<SavedNoteEntity>> = repository.getAllSavedNotes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val testHistory: StateFlow<List<TestHistoryEntity>> = repository.getTestHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val flashcards: StateFlow<List<FlashcardEntity>> = repository.getFlashcards()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val recentActivities: StateFlow<List<StudyActivityEntity>> = repository.getRecentActivities()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val announcements: StateFlow<List<TeacherAnnouncementEntity>> = repository.getAnnouncements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Daily quote
    val dailyQuote = MutableStateFlow(CurriculumData.DAILY_QUOTES.first())

    // Active MCQ Practice State
    val mcqState = MutableStateFlow(ActiveMcqPracticeState())

    // Active Mock Test State
    val testState = MutableStateFlow(ActiveTestState())
    private var testTimerJob: Job? = null

    // AI Chat Assistant
    val aiMessages = MutableStateFlow<List<AiChatMessage>>(
        listOf(
            AiChatMessage(
                sender = "AI",
                message = "Hello! I am your Edu Card AI Study Assistant. 🎓\n\nAsk me any academic question, request step-by-step math solutions, chapter summaries, real-world examples, or custom practice questions!"
            )
        )
    )
    val isAiLoading = MutableStateFlow(false)

    // Study Timer (Pomodoro) State
    val studyTimerMinutes = MutableStateFlow(25)
    val studyTimerSecondsRemaining = MutableStateFlow(25 * 60)
    val isStudyTimerRunning = MutableStateFlow(false)
    val activeAmbientSound = MutableStateFlow("Library Ambiance") // "Library Ambiance", "Rain Shower", "White Noise", "Alpha Waves"
    private var studyTimerJob: Job? = null

    // Daily Streak Booster
    val isStreakClaimedToday = MutableStateFlow(false)

    // Test Review Filter
    val testReviewFilter = MutableStateFlow("ALL") // "ALL", "INCORRECT", "CORRECT"

    // Question Bank filter
    val questionBankCategory = MutableStateFlow("ALL") // "ALL", "PYQ", "HOTS", "ASSERTION_REASON", "CASE_BASED", "OLYMPIAD"

    fun claimDailyStreakXp() {
        if (isStreakClaimedToday.value) return
        isStreakClaimedToday.value = true
        viewModelScope.launch {
            val current = user.value
            if (current != null) {
                repository.updateUser(
                    current.copy(
                        points = current.points + 50,
                        streakDays = current.streakDays + 1
                    )
                )
            }
        }
    }

    data class ExamCountdown(val examName: String, val daysRemaining: Int, val dateStr: String, val targetGrade: String, val tip: String)

    fun getExamCountdowns(): List<ExamCountdown> = listOf(
        ExamCountdown("CBSE Class 10/12 Board Exams", 42, "Starting mid-Feb", "Class 10 & 12", "Focus on NCERT exemplars and last 5-year PYQs"),
        ExamCountdown("JEE Main 2026 Session 1", 28, "January Cycle", "Class 11 & 12", "Master high-weightage chapters in Physics & Mathematics"),
        ExamCountdown("NEET UG 2026", 92, "First Sunday of May", "Class 11 & 12", "Read Biology diagrams and NCERT line-by-line twice"),
        ExamCountdown("National Science Olympiad", 14, "December 15", "Class 6 to 10", "Practice HOTS & multi-concept analytical questions")
    )

    fun setScreen(screen: String) {
        currentScreen.value = screen
    }

    fun setClass(classNum: Int) {
        selectedClass.value = classNum
        viewModelScope.launch {
            repository.updateSelectedClass(classNum)
        }
    }

    fun setSubject(subjectId: String) {
        selectedSubjectId.value = subjectId
    }

    fun openChapter(chapterId: String) {
        selectedChapterId.value = chapterId
        currentScreen.value = "CHAPTER_DETAIL"
    }

    fun toggleDarkMode() {
        isDarkMode.value = !isDarkMode.value
    }

    fun onSearchQueryChange(query: String) {
        searchQuery.value = query
        if (query.length >= 2) {
            searchResults.value = repository.performSearch(query, selectedClass.value)
        } else {
            searchResults.value = emptyList()
        }
    }

    // Bookmarks & Notes
    fun toggleBookmark(id: String, type: String, title: String, subtitle: String, snippet: String) {
        viewModelScope.launch {
            repository.toggleBookmark(
                BookmarkEntity(
                    id = id,
                    itemType = type,
                    title = title,
                    subtitle = subtitle,
                    subject = selectedSubjectId.value,
                    classNum = selectedClass.value,
                    contentSnippet = snippet
                )
            )
        }
    }

    fun saveNote(title: String, content: String, authorRole: String = "STUDENT") {
        viewModelScope.launch {
            val userEntity = user.value
            val authorName = userEntity?.displayName ?: "Student"
            val subjectName = CurriculumData.SUBJECTS.find { it.id == selectedSubjectId.value }?.name ?: "General"
            repository.saveNote(
                title = title,
                subject = subjectName,
                classNum = selectedClass.value,
                content = content,
                authorRole = authorRole,
                authorName = authorName
            )
        }
    }

    fun deleteNote(id: Long) {
        viewModelScope.launch {
            repository.deleteSavedNote(id)
        }
    }

    // MCQ Practice
    fun startMcqPractice(subjectId: String? = null, chapterId: String? = null, difficulty: String = "All") {
        val allQ = repository.getAllQuestions()
        var filtered = allQ
        if (subjectId != null) {
            filtered = filtered.filter { it.subjectId == subjectId }
        }
        if (chapterId != null) {
            filtered = filtered.filter { it.chapterId == chapterId }
        }
        if (difficulty != "All") {
            filtered = filtered.filter { it.difficulty.equals(difficulty, ignoreCase = true) }
        }
        if (filtered.isEmpty()) {
            filtered = allQ
        }

        mcqState.value = ActiveMcqPracticeState(
            questions = filtered,
            currentIndex = 0,
            selectedOptionIndex = null,
            isAnswerRevealed = false,
            correctAnswersCount = 0,
            selectedDifficulty = difficulty,
            isFinished = false
        )
        currentScreen.value = "MCQ_PRACTICE"
    }

    fun selectMcqOption(optionIndex: Int) {
        val state = mcqState.value
        if (state.isAnswerRevealed || state.currentIndex >= state.questions.size) return
        val currentQ = state.questions[state.currentIndex]
        val isCorrect = optionIndex == currentQ.correctIndex

        mcqState.value = state.copy(
            selectedOptionIndex = optionIndex,
            isAnswerRevealed = true,
            correctAnswersCount = if (isCorrect) state.correctAnswersCount + 1 else state.correctAnswersCount
        )
    }

    fun nextMcqQuestion() {
        val state = mcqState.value
        if (state.currentIndex + 1 < state.questions.size) {
            mcqState.value = state.copy(
                currentIndex = state.currentIndex + 1,
                selectedOptionIndex = null,
                isAnswerRevealed = false
            )
        } else {
            mcqState.value = state.copy(isFinished = true)
        }
    }

    // Mock Tests
    fun startMockTest(testInfo: MockTestInfo) {
        testTimerJob?.cancel()
        val totalSecs = testInfo.durationMinutes * 60
        testState.value = ActiveTestState(
            testInfo = testInfo,
            currentQuestionIndex = 0,
            selectedAnswers = emptyMap(),
            isSubmitted = false,
            remainingSeconds = totalSecs,
            isTimerRunning = true
        )
        currentScreen.value = "ACTIVE_TEST"

        // Countdown timer
        testTimerJob = viewModelScope.launch {
            while (testState.value.remainingSeconds > 0 && !testState.value.isSubmitted) {
                delay(1000)
                testState.value = testState.value.copy(
                    remainingSeconds = testState.value.remainingSeconds - 1
                )
            }
            if (!testState.value.isSubmitted) {
                submitMockTest()
            }
        }
    }

    fun selectTestAnswer(questionIndex: Int, optionIndex: Int) {
        if (testState.value.isSubmitted) return
        val currentAnswers = testState.value.selectedAnswers.toMutableMap()
        currentAnswers[questionIndex] = optionIndex
        testState.value = testState.value.copy(selectedAnswers = currentAnswers)
    }

    fun nextTestQuestion() {
        val info = testState.value.testInfo ?: return
        if (testState.value.currentQuestionIndex + 1 < info.questions.size) {
            testState.value = testState.value.copy(
                currentQuestionIndex = testState.value.currentQuestionIndex + 1
            )
        }
    }

    fun prevTestQuestion() {
        if (testState.value.currentQuestionIndex > 0) {
            testState.value = testState.value.copy(
                currentQuestionIndex = testState.value.currentQuestionIndex - 1
            )
        }
    }

    fun submitMockTest() {
        testTimerJob?.cancel()
        val state = testState.value
        val info = state.testInfo ?: return
        var correct = 0
        var incorrect = 0

        info.questions.forEachIndexed { idx, q ->
            val userAns = state.selectedAnswers[idx]
            if (userAns != null) {
                if (userAns == q.correctIndex) {
                    correct++
                } else {
                    incorrect++
                }
            }
        }

        val total = info.questions.size
        val scorePercent = if (total > 0) (correct * 100) / total else 0
        val durationSpent = (info.durationMinutes * 60) - state.remainingSeconds

        testState.value = state.copy(
            isSubmitted = true,
            isTimerRunning = false,
            scorePercentage = scorePercent,
            correctCount = correct,
            incorrectCount = incorrect
        )

        viewModelScope.launch {
            repository.recordTestResult(
                testTitle = info.title,
                subject = info.subjectId,
                classNum = info.classNum,
                score = scorePercent,
                totalQuestions = total,
                correctCount = correct,
                incorrectCount = incorrect,
                durationSeconds = durationSpent,
                examType = info.examType
            )
        }
    }

    // AI Assistant
    fun sendAiPrompt(promptText: String) {
        if (promptText.isBlank() || isAiLoading.value) return
        val userMsg = AiChatMessage("USER", promptText)
        aiMessages.value = aiMessages.value + userMsg
        isAiLoading.value = true

        val subjName = CurriculumData.SUBJECTS.find { it.id == selectedSubjectId.value }?.name ?: "General"

        viewModelScope.launch {
            val response = repository.askAiAssistant(promptText, subjName)
            val aiMsg = AiChatMessage("AI", response)
            aiMessages.value = aiMessages.value + aiMsg
            isAiLoading.value = false
        }
    }

    // Flashcards
    fun createFlashcard(subject: String, topic: String, front: String, back: String) {
        viewModelScope.launch {
            repository.addFlashcard(subject, topic, front, back)
        }
    }

    fun updateFlashcardMastery(card: FlashcardEntity, newLevel: Int) {
        viewModelScope.launch {
            repository.updateFlashcard(card.copy(masteryLevel = newLevel))
        }
    }

    fun deleteFlashcard(cardId: Long) {
        viewModelScope.launch {
            repository.deleteFlashcard(cardId)
        }
    }

    // Study Focus Timer (Pomodoro)
    fun toggleStudyTimer() {
        if (isStudyTimerRunning.value) {
            studyTimerJob?.cancel()
            isStudyTimerRunning.value = false
        } else {
            isStudyTimerRunning.value = true
            studyTimerJob = viewModelScope.launch {
                while (studyTimerSecondsRemaining.value > 0 && isStudyTimerRunning.value) {
                    delay(1000)
                    studyTimerSecondsRemaining.value -= 1
                }
                isStudyTimerRunning.value = false
            }
        }
    }

    fun resetStudyTimer(minutes: Int = 25) {
        studyTimerJob?.cancel()
        studyTimerMinutes.value = minutes
        studyTimerSecondsRemaining.value = minutes * 60
        isStudyTimerRunning.value = false
    }

    // Teacher & Admin Management
    fun postTeacherAnnouncement(title: String, message: String, targetClass: Int) {
        viewModelScope.launch {
            val teacherName = user.value?.displayName ?: "Faculty Member"
            repository.postAnnouncement(teacherName, title, message, targetClass)
        }
    }

    fun addTeacherQuestion(
        subjectId: String,
        chapter: String,
        questionText: String,
        optA: String,
        optB: String,
        optC: String,
        optD: String,
        correctIdx: Int,
        explanation: String,
        difficulty: String
    ) {
        viewModelScope.launch {
            repository.addCustomQuestion(
                CustomQuestionEntity(
                    classNum = selectedClass.value,
                    subjectId = subjectId,
                    chapterName = chapter,
                    questionText = questionText,
                    optionA = optA,
                    optionB = optB,
                    optionC = optC,
                    optionD = optD,
                    correctIndex = correctIdx,
                    explanation = explanation,
                    difficulty = difficulty,
                    creatorName = user.value?.displayName ?: "Teacher"
                )
            )
        }
    }

    fun switchRole(role: String, name: String) {
        viewModelScope.launch {
            repository.switchRole(role, name)
        }
    }

    fun getSubjects(): List<SubjectInfo> = repository.getSubjects(selectedClass.value)

    fun getChapters(subjectId: String): List<Chapter> = repository.getChapters(selectedClass.value, subjectId)

    fun getCurrentChapter(): Chapter? = repository.getChapterById(selectedChapterId.value)

    fun getMockTests(): List<MockTestInfo> = repository.getMockTests(selectedClass.value)

    fun getLeaderboard(): List<LeaderboardUser> = repository.getLeaderboard()

    fun getBadges(): List<EduBadge> = repository.getBadges()

    fun getNotifications(): List<EduNotification> = repository.getNotifications()

    fun getAllQuestions(): List<Question> = repository.getAllQuestions()
}
