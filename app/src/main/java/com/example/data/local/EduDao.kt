package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EduDao {

    // User Operations
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    fun getUser(userId: String = "active_user"): Flow<UserEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    // Bookmarks
    @Query("SELECT * FROM bookmarks ORDER BY timestamp DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarks WHERE id = :id)")
    fun isBookmarked(id: String): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE id = :id")
    suspend fun deleteBookmark(id: String)

    // Saved Notes
    @Query("SELECT * FROM saved_notes ORDER BY timestamp DESC")
    fun getAllSavedNotes(): Flow<List<SavedNoteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedNote(note: SavedNoteEntity): Long

    @Query("DELETE FROM saved_notes WHERE id = :id")
    suspend fun deleteSavedNote(id: Long)

    // Test History
    @Query("SELECT * FROM test_history ORDER BY timestamp DESC")
    fun getAllTestHistory(): Flow<List<TestHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTestHistory(result: TestHistoryEntity): Long

    // Flashcards
    @Query("SELECT * FROM flashcards ORDER BY id DESC")
    fun getAllFlashcards(): Flow<List<FlashcardEntity>>

    @Query("SELECT * FROM flashcards WHERE subject = :subject ORDER BY id DESC")
    fun getFlashcardsBySubject(subject: String): Flow<List<FlashcardEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcard(flashcard: FlashcardEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFlashcards(flashcards: List<FlashcardEntity>)

    @Update
    suspend fun updateFlashcard(flashcard: FlashcardEntity)

    @Query("DELETE FROM flashcards WHERE id = :id")
    suspend fun deleteFlashcard(id: Long)

    // Study Activities
    @Query("SELECT * FROM study_activities ORDER BY timestamp DESC LIMIT 20")
    fun getRecentActivities(): Flow<List<StudyActivityEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: StudyActivityEntity)

    // Chapter Progress
    @Query("SELECT * FROM chapter_progress WHERE classNum = :classNum")
    fun getChapterProgressForClass(classNum: Int): Flow<List<ChapterProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateChapterProgress(progress: ChapterProgressEntity)

    // Announcements
    @Query("SELECT * FROM teacher_announcements ORDER BY timestamp DESC")
    fun getAnnouncements(): Flow<List<TeacherAnnouncementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnnouncement(announcement: TeacherAnnouncementEntity)

    // Custom Questions
    @Query("SELECT * FROM custom_questions ORDER BY id DESC")
    fun getAllCustomQuestions(): Flow<List<CustomQuestionEntity>>

    @Query("SELECT * FROM custom_questions WHERE classNum = :classNum AND subjectId = :subjectId")
    fun getCustomQuestions(classNum: Int, subjectId: String): Flow<List<CustomQuestionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomQuestion(question: CustomQuestionEntity): Long
}
