package cn.super12138.todo.logic.database

import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.PrimaryKey
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.model.Priority
import kotlinx.serialization.Serializable
import kotlin.time.Instant

@Serializable
@Entity(tableName = Constants.DB_TABLE_NAME)
data class TaskEntity(
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "category") val category: String = "",
    @ColumnInfo(name = "completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "pinned") val isPinned: Boolean = false,
    @ColumnInfo(name = "priority") val priority: Priority = Priority.Default,
    @ColumnInfo(name = "due_date") val dueDate: Instant? = null,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0
)
