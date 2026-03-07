package cn.super12138.todo.logic

import cn.super12138.todo.logic.database.TaskEntity
import kotlinx.coroutines.flow.Flow

interface IRepository {
    suspend fun insertTask(task: TaskEntity)

    fun getAllTasks(): Flow<List<TaskEntity>>

    suspend fun updateTask(task: TaskEntity)

    suspend fun deleteTask(task: TaskEntity)

    suspend fun deleteTaskFromIds(tasks: List<Int>)

    // suspend fun deleteAllTodo()
}