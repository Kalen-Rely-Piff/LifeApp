package com.lifeapp.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lifeapp.data.dao.*
import com.lifeapp.data.entity.*

@Database(
    entities = [
        Todo::class,
        Memo::class,
        Project::class,
        DevTask::class,
        CodeSnippet::class,
        TechNote::class,
        Inspiration::class,
        ScriptDraft::class,
        PublishPlan::class,
        Material::class,
        Entertainment::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun todoDao(): TodoDao
    abstract fun memoDao(): MemoDao
    abstract fun projectDao(): ProjectDao
    abstract fun devTaskDao(): DevTaskDao
    abstract fun codeSnippetDao(): CodeSnippetDao
    abstract fun techNoteDao(): TechNoteDao
    abstract fun inspirationDao(): InspirationDao
    abstract fun scriptDraftDao(): ScriptDraftDao
    abstract fun publishPlanDao(): PublishPlanDao
    abstract fun materialDao(): MaterialDao
    abstract fun entertainmentDao(): EntertainmentDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "life_app.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }

        fun getDatabasePath(context: Context): String {
            return context.getDatabasePath("life_app.db").absolutePath
        }
    }
}
