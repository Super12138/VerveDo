package cn.super12138.todo.logic

import cn.super12138.todo.VerveDoApp
import cn.super12138.todo.logic.database.TodoEntity
import kotlinx.coroutines.flow.Flow

object Repository : IRepository {
    private val db get() = VerveDoApp.db
    private val toDoDao = db.toDoDao()

    override suspend fun insertTodo(toDo: TodoEntity) {
        toDoDao.insert(toDo)
    }

    override fun getAllTodos(): Flow<List<TodoEntity>> = toDoDao.getAll()

    override suspend fun updateTodo(toDo: TodoEntity) {
        toDoDao.update(toDo)
    }

    override suspend fun deleteTodo(toDo: TodoEntity) {
        toDoDao.delete(toDo)
    }

    override suspend fun deleteTodoFromIds(toDoItems: List<Int>) {
        toDoDao.deleteFromIds(toDoItems.toSet())
    }

    /*override suspend fun deleteAllTodo() {
        toDoDao.deleteAllTodo()
    }*/
}