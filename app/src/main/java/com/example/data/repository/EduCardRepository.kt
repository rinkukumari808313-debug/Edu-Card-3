package com.example.data.repository

import com.example.data.ai.EduCardAiService
import com.example.data.curriculum.CurriculumData
import com.example.data.local.BookmarkEntity
import com.example.data.local.CustomQuestionEntity
import com.example.data.local.EduDao
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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow

class EduCardRepository(
    private val dao: EduDao,
    private val aiService: EduCardAiService = EduCardAiService()
) {

    suspend fun initializeDefaultDataIfNeeded() {
        val existingUser = dao.getUser().firstOrNull()
        if (existingUser == null) {
            dao.insertOrUpdateUser(
                UserEntity(
                    id = "active_user",
                    username = "aarav_10",
                    displayName = "Aarav Sharma",
                    role = "STUDENT",
                    classNum = 10,
                    streakDays = 7,
                    points = 1450,
                    completedChaptersCount = 8,
                    testsCompletedCount = 14
                )
            )

            // Seed initial flashcards
            CurriculumData.DEFAULT_FLASHCARDS.forEach { (front, back) ->
                dao.insertFlashcard(
                    FlashcardEntity(
                        subject = "Physics / Math",
                        topic = "Key Formulas",
                        front = front,
                        back = back,
                        masteryLevel = 1
                    )
                )
            }

            // Seed initial test history
            dao.insertTestHistory(
                TestHistoryEntity(
                    testTitle = "Class 10 CBSE Science Model Test",
                    subject = "Physics",
                    classNum = 10,
                    score = 83,
                    totalQuestions = 6,
                    correctCount = 5,
                    incorrectCount = 1,
                    durationSeconds = 480,
                    examType = "Board Exam",
                    timestamp = System.currentTimeMillis() - 86400000L
                )
            )
            dao.insertTestHistory(
                TestHistoryEntity(
                    testTitle = "Mathematics Quadratic Equations Drill",
                    subject = "Mathematics",
                    classNum = 10,
                    score = 100,
                    totalQuestions = 5,
                    correctCount = 5,
                    incorrectCount = 0,
                    durationSeconds = 620,
                    examType = "Chapter Quick Test",
                    timestamp = System.currentTimeMillis() - (86400000L * 2)
                )
            )

            // Seed announcements
            dao.insertAnnouncement(
                TeacherAnnouncementEntity(
                    teacherName = "Dr. Vikram Sen",
                    title = "Mid-Term Physics & Chemistry Revision Test",
                    message = "All Class 10 students should review Light and Chemical Reactions chapters. Mock test scheduled for this Friday.",
                    targetClass = 10
                )
            )

            // Seed initial activity
            dao.insertActivity(
                StudyActivityEntity(
                    title = "Completed 100% Score in Math Drill",
                    description = "Solved 5 questions on Quadratic Equations",
                    category = "TEST"
                )
            )
        }
    }

    // User & Profile
    fun getUser(): Flow<UserEntity?> = dao.getUser()

    suspend fun updateUser(user: UserEntity) = dao.insertOrUpdateUser(user)

    suspend fun updateSelectedClass(classNum: Int) {
        val current = dao.getUser().firstOrNull() ?: UserEntity()
        dao.insertOrUpdateUser(current.copy(classNum = classNum))
    }

    suspend fun switchRole(role: String, name: String) {
        val current = dao.getUser().firstOrNull() ?: UserEntity()
        dao.insertOrUpdateUser(current.copy(role = role, displayName = name))
    }

    // Curriculum & Chapters
    fun getSubjects(classNum: Int): List<SubjectInfo> = CurriculumData.getSubjectsForClass(classNum)

    fun getChapters(classNum: Int, subjectId: String): List<Chapter> =
        CurriculumData.getChapters(classNum, subjectId)

    fun getChapterById(chapterId: String): Chapter? {
        val parts = chapterId.split("_")
        if (parts.size >= 2) {
            val classNum = parts[0].toIntOrNull() ?: 10
            val subjectId = parts[1]
            val chapters = getChapters(classNum, subjectId)
            return chapters.find { it.id == chapterId }
        }
        return null
    }

    // Question Bank & Custom Questions
    suspend fun getQuestionsForChapter(chapterId: String, classNum: Int, subjectId: String): List<Question> {
        val baseQuestions = CurriculumData.QUESTIONS.filter { it.chapterId == chapterId || it.subjectId == subjectId }
        return baseQuestions
    }

    fun getAllQuestions(): List<Question> = CurriculumData.QUESTIONS

    // Mock Tests
    fun getMockTests(classNum: Int): List<MockTestInfo> {
        return CurriculumData.MOCK_TESTS.filter { it.classNum == classNum }
    }

    // Bookmarks
    fun getAllBookmarks(): Flow<List<BookmarkEntity>> = dao.getAllBookmarks()

    fun isBookmarked(id: String): Flow<Boolean> = dao.isBookmarked(id)

    suspend fun toggleBookmark(bookmark: BookmarkEntity) {
        val isSaved = dao.isBookmarked(bookmark.id).firstOrNull() ?: false
        if (isSaved) {
            dao.deleteBookmark(bookmark.id)
        } else {
            dao.insertBookmark(bookmark)
        }
    }

    // Saved Notes
    fun getAllSavedNotes(): Flow<List<SavedNoteEntity>> = dao.getAllSavedNotes()

    suspend fun saveNote(title: String, subject: String, classNum: Int, content: String, authorRole: String = "STUDENT", authorName: String = "Self") {
        dao.insertSavedNote(
            SavedNoteEntity(
                title = title,
                subject = subject,
                classNum = classNum,
                content = content,
                authorRole = authorRole,
                authorName = authorName
            )
        )
        dao.insertActivity(
            StudyActivityEntity(
                title = "Saved new study note: $title",
                description = "Subject: $subject (Class $classNum)",
                category = "NOTE"
            )
        )
    }

    suspend fun deleteSavedNote(id: Long) = dao.deleteSavedNote(id)

    // Test History & Results
    fun getTestHistory(): Flow<List<TestHistoryEntity>> = dao.getAllTestHistory()

    suspend fun recordTestResult(
        testTitle: String,
        subject: String,
        classNum: Int,
        score: Int,
        totalQuestions: Int,
        correctCount: Int,
        incorrectCount: Int,
        durationSeconds: Int,
        examType: String
    ) {
        dao.insertTestHistory(
            TestHistoryEntity(
                testTitle = testTitle,
                subject = subject,
                classNum = classNum,
                score = score,
                totalQuestions = totalQuestions,
                correctCount = correctCount,
                incorrectCount = incorrectCount,
                durationSeconds = durationSeconds,
                examType = examType
            )
        )

        // Update User points & test counts
        val user = dao.getUser().firstOrNull()
        if (user != null) {
            val gainedPoints = (score * 2) + 20
            dao.insertOrUpdateUser(
                user.copy(
                    points = user.points + gainedPoints,
                    testsCompletedCount = user.testsCompletedCount + 1
                )
            )
        }

        dao.insertActivity(
            StudyActivityEntity(
                title = "Completed Test: $testTitle",
                description = "Scored $score% ($correctCount/$totalQuestions correct)",
                category = "TEST"
            )
        )
    }

    // Flashcards
    fun getFlashcards(): Flow<List<FlashcardEntity>> = dao.getAllFlashcards()

    suspend fun addFlashcard(subject: String, topic: String, front: String, back: String) {
        dao.insertFlashcard(
            FlashcardEntity(
                subject = subject,
                topic = topic,
                front = front,
                back = back,
                masteryLevel = 0,
                isCustom = true
            )
        )
    }

    suspend fun updateFlashcard(flashcard: FlashcardEntity) = dao.updateFlashcard(flashcard)

    suspend fun deleteFlashcard(id: Long) = dao.deleteFlashcard(id)

    // Activities
    fun getRecentActivities(): Flow<List<StudyActivityEntity>> = dao.getRecentActivities()

    // Announcements
    fun getAnnouncements(): Flow<List<TeacherAnnouncementEntity>> = dao.getAnnouncements()

    suspend fun postAnnouncement(teacherName: String, title: String, message: String, targetClass: Int) {
        dao.insertAnnouncement(
            TeacherAnnouncementEntity(
                teacherName = teacherName,
                title = title,
                message = message,
                targetClass = targetClass
            )
        )
    }

    // Custom Questions by Teacher
    fun getCustomQuestions(): Flow<List<CustomQuestionEntity>> = dao.getAllCustomQuestions()

    suspend fun addCustomQuestion(question: CustomQuestionEntity) {
        dao.insertCustomQuestion(question)
        dao.insertActivity(
            StudyActivityEntity(
                title = "Teacher added new question",
                description = question.questionText.take(40) + "...",
                category = "QUESTION"
            )
        )
    }

    // AI Study Assistant
    suspend fun askAiAssistant(query: String, subject: String): String =
        aiService.askAssistant(query, subject)

    // Global Search across all resources
    fun performSearch(query: String, classNum: Int): List<SearchResult> {
        val q = query.trim().lowercase()
        if (q.isBlank()) return emptyList()

        val results = mutableListOf<SearchResult>()

        // Search Subjects
        CurriculumData.SUBJECTS.forEach { subj ->
            if (subj.name.lowercase().contains(q) || subj.hindiName.contains(q) || subj.description.lowercase().contains(q)) {
                results.add(SearchResult("Subject", subj.name, subj.description, "subject", subj.id))
            }
        }

        // Search Chapters
        CurriculumData.SUBJECTS.forEach { subj ->
            val chapters = getChapters(classNum, subj.id)
            chapters.forEach { ch ->
                if (ch.title.lowercase().contains(q) || ch.overview.lowercase().contains(q) || ch.notes.lowercase().contains(q)) {
                    results.add(SearchResult("Chapter", ch.title, ch.overview, "chapter", ch.id))
                }
                // Search Definitions & Formulas inside chapter
                ch.definitions.forEach { def ->
                    if (def.term.lowercase().contains(q) || def.definition.lowercase().contains(q)) {
                        results.add(SearchResult("Definition", def.term, def.definition, "chapter", ch.id))
                    }
                }
                ch.formulas.forEach { f ->
                    if (f.name.lowercase().contains(q) || f.formula.lowercase().contains(q)) {
                        results.add(SearchResult("Formula", f.name, "${f.formula} - ${f.explanation}", "chapter", ch.id))
                    }
                }
            }
        }

        // Search Questions
        CurriculumData.QUESTIONS.forEach { question ->
            if (question.questionText.lowercase().contains(q) || question.explanation.lowercase().contains(q)) {
                results.add(SearchResult("Question (${question.category})", question.questionText, question.explanation, "question", question.id))
            }
        }

        // Search Tests
        CurriculumData.MOCK_TESTS.forEach { test ->
            if (test.title.lowercase().contains(q) || test.examType.lowercase().contains(q)) {
                results.add(SearchResult("Mock Test", test.title, "${test.durationMinutes} mins • ${test.totalQuestions} questions", "test", test.id))
            }
        }

        return results
    }

    // Leaderboard
    fun getLeaderboard(): List<LeaderboardUser> {
        return listOf(
            LeaderboardUser(1, "Sneha P.", 2840, 26, 19, 6, 0xFF3B82F6),
            LeaderboardUser(2, "Rohan V.", 2610, 24, 14, 5, 0xFF10B981),
            LeaderboardUser(3, "Aarav Sharma (You)", 1450, 14, 7, 4, 0xFFF59E0B),
            LeaderboardUser(4, "Priya K.", 1380, 12, 6, 4, 0xFF8B5CF6),
            LeaderboardUser(5, "Ananya D.", 1240, 11, 5, 3, 0xFFEC4899),
            LeaderboardUser(6, "Kabir M.", 1120, 10, 4, 3, 0xFF06B6D4),
            LeaderboardUser(7, "Tanvi S.", 980, 8, 3, 2, 0xFF14B8A6),
            LeaderboardUser(8, "Ritik J.", 890, 7, 2, 2, 0xFF6366F1)
        )
    }

    // Badges & Achievements
    fun getBadges(): List<EduBadge> {
        return listOf(
            EduBadge("b_first_test", "First Test", "Completed your very first mock test or practice quiz.", "CheckCircle", true),
            EduBadge("b_streak_7", "7-Day Streak", "Studied consistently for 7 consecutive days.", "LocalFireDepartment", true),
            EduBadge("b_100_q", "100 Questions Solved", "Answered 100 questions across question banks.", "Psychology", true),
            EduBadge("b_perfect", "Perfect Score", "Achieved 100% accuracy in a timed mock test.", "Grade", true),
            EduBadge("b_ch_master", "Chapter Master", "Finished all notes and questions of a chapter.", "WorkspacePremium", false, "80%"),
            EduBadge("b_consistent", "Consistent Learner", "Logged in and completed daily practice for 3 weeks.", "MilitaryTech", false, "60%")
        )
    }

    // Notifications
    fun getNotifications(): List<EduNotification> {
        return listOf(
            EduNotification("notif_1", "Upcoming Mock Test", "Class 10 CBSE Science Model Test is scheduled for today.", "2 hours ago", "TEST"),
            EduNotification("notif_2", "Daily Practice Reminder", "Complete your daily 5-question MCQ streak to earn +50 points.", "5 hours ago", "PRACTICE"),
            EduNotification("notif_3", "New Study Notes Added", "Teacher Dr. Vikram Sen uploaded new summary for Chemical Reactions.", "1 day ago", "MATERIAL"),
            EduNotification("notif_4", "Revision Alert", "It's time to review your saved Physics Light formulas.", "2 days ago", "REVISION")
        )
    }
}

data class SearchResult(
    val category: String,
    val title: String,
    val description: String,
    val targetType: String,
    val targetId: String
)
