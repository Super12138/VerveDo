package cn.super12138.todo.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import cn.super12138.todo.logic.IRepository
import cn.super12138.todo.logic.database.TaskDatabase
import cn.super12138.todo.logic.datastore.DataStoreManager
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(
    private val database: TaskDatabase,
    private val repository: IRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    val showConfetti = mutableStateOf(false)

    fun playConfetti() {
        showConfetti.value = true
    }
}