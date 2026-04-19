package cn.super12138.todo.logic

import cn.super12138.todo.logic.database.TaskDao
import cn.super12138.todo.logic.database.TaskEntity
import kotlinx.coroutines.flow.Flow

class Repository(private val taskDao: TaskDao) : IRepository {
    override suspend fun insertTask(task: TaskEntity) {
        taskDao.insert(task)
    }

    override fun getAllTasks(): Flow<List<TaskEntity>> = taskDao.getAll()

    override suspend fun updateTask(task: TaskEntity) {
        taskDao.update(task)
    }

    override suspend fun deleteTask(task: TaskEntity) {
        taskDao.delete(task)
    }

    override suspend fun deleteTaskFromIds(tasks: List<Int>) {
        taskDao.deleteFromIds(tasks.toSet())
    }

    /*override suspend fun deleteAllTodo() {
        toDoDao.deleteAllTodo()
    }*/
}