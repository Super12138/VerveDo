package cn.super12138.todo.logic.database

import androidx.room3.ColumnTypeConverters
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import cn.super12138.todo.constants.Constants

@Database(entities = [TaskEntity::class], version = 6)
@ColumnTypeConverters(Converters::class)
abstract class TaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    companion object {
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override suspend fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE ${Constants.DB_TABLE_NAME} ADD COLUMN custom_subject TEXT NOT NULL DEFAULT ''")
            }
        }

        // 为自定义学科功能进行迁移
        val MIGRATION_3_4 = object : Migration(3, 4) {
            override suspend fun migrate(connection: SQLiteConnection) {
                // 创建一个新表，其中不含有subject，并且有一个新的category字段（由custom_subject迁移而来）
                connection.execSQL("CREATE TABLE IF NOT EXISTS ${Constants.DB_TABLE_NEW_NAME} (content TEXT NOT NULL, category TEXT NOT NULL DEFAULT '', completed INTEGER NOT NULL, priority REAL NOT NULL, id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
                // 将旧表中的数据迁移到新表中
                connection.execSQL(
                    """
                        INSERT INTO ${Constants.DB_TABLE_NEW_NAME} (content, category, completed, priority, id) 
                        SELECT
                            content,
                            COALESCE(NULLIF(custom_subject, ''), '') AS category,
                            completed,
                            priority,
                            id
                        FROM ${Constants.DB_TABLE_NAME}
                    """.trimIndent()
                )
                // 删除旧表
                connection.execSQL("DROP TABLE ${Constants.DB_TABLE_NAME}")
                // 重命名新表
                connection.execSQL("ALTER TABLE ${Constants.DB_TABLE_NEW_NAME} RENAME TO ${Constants.DB_TABLE_NAME}")
            }
        }

        val MIGRATION_4_5 = object : Migration(4, 5) {
            override suspend fun migrate(connection: SQLiteConnection) {
                connection.execSQL("ALTER TABLE ${Constants.DB_TABLE_NAME} ADD COLUMN due_date INTEGER")
            }
        }

        val MIGRATION_5_6 = object : Migration(5, 6) {
            override suspend fun migrate(connection: SQLiteConnection) {
                // 创建一个新表，其中的priority字段数据类型由REAL变为TEXT
                connection.execSQL("CREATE TABLE IF NOT EXISTS ${Constants.DB_TABLE_NEW_NAME} (content TEXT NOT NULL, category TEXT NOT NULL, completed INTEGER NOT NULL, priority TEXT NOT NULL DEFAULT 'Default', due_date INTEGER, id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
                // 将旧表数据迁移到新表，其中对priority字段进行值映射
                connection.execSQL(
                    """
                        INSERT INTO ${Constants.DB_TABLE_NEW_NAME} (content, category, completed, priority, due_date, id) 
                        SELECT
                            content,
                            category,
                            completed,
                            CASE priority
                                WHEN -2 THEN "NotUrgent"
                                WHEN -1 THEN "NotImportant"
                                WHEN 0 THEN "Default"
                                WHEN 1 THEN "Important"
                                WHEN 2 THEN "Urgent"
                            END AS priority,
                            due_date,
                            id
                        FROM ${Constants.DB_TABLE_NAME}
                    """.trimIndent()
                )
                // 删除旧表
                connection.execSQL("DROP TABLE ${Constants.DB_TABLE_NAME}")
                // 重命名新表
                connection.execSQL("ALTER TABLE ${Constants.DB_TABLE_NEW_NAME} RENAME TO ${Constants.DB_TABLE_NAME}")
                // 新增列
                connection.execSQL("ALTER TABLE ${Constants.DB_TABLE_NAME} ADD COLUMN pinned completed INTEGER NOT NULL DEFAULT 0")
            }
        }
    }
}