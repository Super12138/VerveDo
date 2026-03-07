package cn.super12138.todo.logic.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import cn.super12138.todo.constants.Constants
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = Constants.DB_TABLE_NAME)
data class TaskEntity(
    @ColumnInfo(name = "content") val content: String,
    @ColumnInfo(name = "category") val category: String = "",
    @ColumnInfo(name = "completed") val isCompleted: Boolean = false,
    @ColumnInfo(name = "priority") val priority: Float,
    @ColumnInfo(name = "due_date") val dueDate: Long? = null,
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") val id: Int = 0,
)
