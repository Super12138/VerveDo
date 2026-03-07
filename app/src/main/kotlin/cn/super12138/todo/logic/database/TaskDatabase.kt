package cn.super12138.todo.logic.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import cn.super12138.todo.constants.Constants

@Database(entities = [TaskEntity::class], version = 5)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: TaskDatabase? = null
        fun getDatabase(context: Context): TaskDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskDatabase::class.java,
                    Constants.DB_NAME
                )
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .fallbackToDestructiveMigration(false)
                    .build()

                INSTANCE = instance
                return instance
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ${Constants.DB_TABLE_NAME} ADD COLUMN custom_subject TEXT NOT NULL DEFAULT ''")
            }
        }

        // 为自定义学科功能进行迁移
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 创建一个新表，其中不含有subject，并且有一个新的category字段（由custom_subject迁移而来）
                db.execSQL("CREATE TABLE IF NOT EXISTS todo_new (content TEXT NOT NULL, category TEXT NOT NULL DEFAULT '', completed INTEGER NOT NULL, priority REAL NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
                // 将旧表中的数据迁移到新表中
                db.execSQL("INSERT INTO todo_new (content, category, completed, priority, id) SELECT content, COALESCE(NULLIF(custom_subject, ''), '') AS category, completed, priority, id FROM todo")
                // 删除旧表
                db.execSQL("DROP TABLE todo")
                // 重命名新表
                db.execSQL("ALTER TABLE todo_new RENAME TO todo")
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE ${Constants.DB_TABLE_NAME} ADD COLUMN due_date INTEGER")
            }
        }
    }
}