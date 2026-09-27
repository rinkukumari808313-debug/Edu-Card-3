package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val id: String = "active_user",
    val username: String = "student_alpha",
    val displayName: String = "Aarav Sharma",
    val role: String = "STUDENT", // "STUDENT", "TEACHER", "ADMIN"
    val classNum: Int = 10,
    val streakDays: Int = 7,
    val points: Int = 1450,
    val completedChaptersCount: Int = 8,
    val testsCompletedCount: Int = 14,
    val lastActiveTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val id: String, // e.g. questionId or "note_ch1"
    val itemType: String, // "QUESTION", "NOTE", "FORMULA"
    val title: String,
    val subtitle: String,
    val subject: String,
    val classNum: Int,
    val contentSnippet: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_notes")
data class SavedNoteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val subject: String,
    val classNum: Int,
    val content: String,
    val authorRole: String = "STUDENT", // or "TEACHER"
    val authorName: String = "Self",
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "test_history")
data class TestHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val testTitle: String,
    val subject: String,
    val classNum: Int,
    val score: Int,
    val totalQuestions: Int,
    val correctCount: Int,
    val incorrectCount: Int,
    val durationSeconds: Int,
    val examType: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "flashcards")
data class FlashcardEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val subject: String,
    val topic: String,
    val front: String,
    val back: String,
    val masteryLevel: Int = 0, // 0 = New, 1 = Learning, 2 = Mastered
    val isCustom: Boolean = false
)

@Entity(tableName = "study_activities")
data class StudyActivityEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // "TEST", "MCQ", "NOTE", "FLASHCARD"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "chapter_progress")
data class ChapterProgressEntity(
    @PrimaryKey val chapterId: String,
    val classNum: Int,
    val subjectId: String,
    val isCompleted: Boolean = false,
    val completionPercent: Int = 0,
    val lastAccessed: Long = System.currentTimeMillis()
)

@Entity(tableName = "teacher_announcements")
data class TeacherAnnouncementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val teacherName: String,
    val title: String,
    val message: String,
    val targetClass: Int = 10,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_questions")
data class CustomQuestionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val classNum: Int,
    val subjectId: String,
    val chapterName: String,
    val questionText: String,
    val optionA: String,
    val optionB: String,
    val optionC: String,
    val optionD: String,
    val correctIndex: Int,
    val explanation: String,
    val difficulty: String = "Medium",
    val category: String = "MCQ",
    val creatorName: String = "Teacher"
)
