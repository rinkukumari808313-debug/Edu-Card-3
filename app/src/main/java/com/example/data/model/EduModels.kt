package com.example.data.model

data class SubjectInfo(
    val id: String,
    val name: String,
    val hindiName: String = "",
    val description: String,
    val iconName: String,
    val primaryColorHex: Long,
    val totalChapters: Int = 12
)

data class DefinitionItem(
    val term: String,
    val definition: String
)

data class FormulaItem(
    val name: String,
    val formula: String,
    val explanation: String
)

data class StudyQuestionItem(
    val question: String,
    val answer: String,
    val marks: Int = 3
)

data class DiagramItem(
    val title: String,
    val description: String,
    val asciiArtOrLabel: String
)

data class Chapter(
    val id: String,
    val classNum: Int,
    val subjectId: String,
    val chapterNum: Int,
    val title: String,
    val overview: String,
    val notes: String,
    val definitions: List<DefinitionItem> = emptyList(),
    val formulas: List<FormulaItem> = emptyList(),
    val keyPoints: List<String> = emptyList(),
    val diagrams: List<DiagramItem> = emptyList(),
    val summary: String,
    val importantQuestions: List<StudyQuestionItem> = emptyList()
)

data class Question(
    val id: String,
    val chapterId: String,
    val classNum: Int,
    val subjectId: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctIndex: Int, // 0..3
    val explanation: String,
    val difficulty: String, // "Easy", "Medium", "Hard"
    val category: String, // "MCQ", "PYQ", "HOTS", "ASSERTION_REASON", "CASE_BASED", "OLYMPIAD"
    val isBookmarked: Boolean = false
)

data class MockTestInfo(
    val id: String,
    val title: String,
    val classNum: Int,
    val subjectId: String,
    val durationMinutes: Int,
    val totalQuestions: Int,
    val isCompetitive: Boolean = false,
    val examType: String = "Board Exam", // "Board Exam", "JEE Main", "NEET", "Olympiad", "Chapter Quick Test"
    val questions: List<Question> = emptyList()
)

data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val points: Int,
    val testsCount: Int,
    val streak: Int,
    val badgesCount: Int,
    val avatarColorHex: Long
)

data class EduBadge(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val progress: String = "100%"
)

data class EduNotification(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: String // "TEST", "PRACTICE", "MATERIAL", "REVISION"
)

enum class UserRole {
    STUDENT,
    TEACHER,
    ADMIN
}
