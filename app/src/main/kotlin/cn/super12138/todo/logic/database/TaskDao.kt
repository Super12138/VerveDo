package cn.super12138.todo.logic.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import cn.super12138.todo.constants.Constants
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Query("SELECT * FROM ${Constants.DB_TABLE_NAME}")
    fun getAll(): Flow<List<TaskEntity>>

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("DELETE FROM ${Constants.DB_TABLE_NAME} WHERE id in (:taskIds)")
    suspend fun deleteFromIds(taskIds: Set<Int>)

    /*@Query("DELETE FROM todo")
    suspend fun deleteAllTodo()*/
}