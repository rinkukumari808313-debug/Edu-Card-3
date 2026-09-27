package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [
        UserEntity::class,
        BookmarkEntity::class,
        SavedNoteEntity::class,
        TestHistoryEntity::class,
        FlashcardEntity::class,
        StudyActivityEntity::class,
        ChapterProgressEntity::class,
        TeacherAnnouncementEntity::class,
        CustomQuestionEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class EduDatabase : RoomDatabase() {

    abstract fun eduDao(): EduDao

    companion object {
        @Volatile
        private var INSTANCE: EduDatabase? = null

        fun getDatabase(context: Context): EduDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    EduDatabase::class.java,
                    "educard_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
