package cn.super12138.todo.ui.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation3.runtime.NavKey
import cn.super12138.todo.VerveDoApp
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.Repository
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.logic.model.SortingMethod
import cn.super12138.todo.ui.navigation.TopLevelBackStack
import cn.super12138.todo.ui.navigation.VerveDoScreen
import cn.super12138.todo.utils.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel : ViewModel() {
    val mainBackStack = TopLevelBackStack<NavKey>(VerveDoScreen.Overview)

    // 待办
    private val taskList: Flow<List<TaskEntity>> = Repository.getAllTasks()
    val sortedTaskList: StateFlow<List<TaskEntity>> = DataStoreManager.sortingMethodFlow
        .flatMapLatest { sortingMethod ->
            taskList.map { list ->
                when (SortingMethod.fromId(sortingMethod)) {
                    SortingMethod.Sequential -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted } // 必须先要按照是否完成排序
                            .thenBy { it.id }
                    )

                    SortingMethod.Category -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted }
                            .thenBy { it.category }
                    )

                    SortingMethod.Priority -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted }
                            .thenByDescending { it.priority }
                            .thenBy(nullsLast()) { it.dueDate }
                    ) // 优先级高的在前

                    SortingMethod.Completion -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted }
                            .thenBy { it.category }
                            .thenByDescending { it.priority }
                    ) // 未完成的在前
                    SortingMethod.AlphabeticalAscending -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted }
                            .thenBy { it.content }
                            .thenByDescending { it.priority }
                    )

                    SortingMethod.AlphabeticalDescending -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted }
                            .thenByDescending { it.content }
                            .thenByDescending { it.priority }
                    )

                    SortingMethod.DueDate -> list.sortedWith(
                        comparator = compareBy<TaskEntity> { it.isCompleted }
                            // 确保未设置截止日期的任务在最下头
                            .thenBy(nullsLast()) { it.dueDate }
                    )
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val showConfetti = mutableStateOf(false)

    val toDoListState = LazyListState()
    var searchMode by mutableStateOf(false)
        private set
    val searchFieldState = TextFieldState()

    // 多选逻辑参考：https://github.com/X1nto/Mauth
    private val _selectedTodoIds = MutableStateFlow(listOf<Int>())
    val selectedTodoIds = _selectedTodoIds.asStateFlow()

    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            Repository.insertTask(task)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            Repository.updateTask(task)
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            Repository.deleteTask(task)
        }
    }

    /*fun deleteAllTodo() {
        viewModelScope.launch {
            Repository.deleteAllTodo()
        }
    }*/

    /**
     * 切换待办的选择状态
     */
    fun toggleTaskSelection(task: TaskEntity) {
        _selectedTodoIds.update { idList ->
            if (idList.contains(task.id)) {
                // 若已经选择取消选择
                idList - task.id
            } else {
                // 若未选择添加到列表中，立即选中
                idList + task.id
            }
        }
    }

    /**
     * 切换是否全选
     */
    fun selectAllTask() {
        viewModelScope.launch {
            taskList.firstOrNull()?.let { tasks ->
                // 无论是否有选择都全选
                val allIds = tasks.map { it.id }
                _selectedTodoIds.value = allIds
            }
        }
    }

    /**
     * 清除全部已选择的待办
     */
    fun clearAllTaskSelection() {
        _selectedTodoIds.update { emptyList() }
    }

    /**
     * 删除选择的待办
     */
    fun deleteSelectedTask() {
        viewModelScope.launch {
            Repository.deleteTaskFromIds(selectedTodoIds.value)
            clearAllTaskSelection()
        }
    }

    fun playConfetti() {
        showConfetti.value = true
    }

    fun setSearchModeEnabled(enabled: Boolean) {
        searchMode = enabled
    }

    /**
     * 备份应用数据
     *
     * @param uri 备份文件路径的 URI
     * @param context 应用 Context
     * @param onResult 备份完成的回调函数
     */
    fun backupAppData(uri: Uri, context: Context, onResult: (completed: Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching {
                context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOutStream ->
                        getBackupFiles(context).forEach { file ->
                            FileUtils.addFileToZip(file, file.name, zipOutStream)
                        }
                    }
                }
            }.isSuccess
            withContext(Dispatchers.Main) { onResult(result) }
        }
    }

    /**
     * 恢复应用数据
     *
     * @param uri 选择的恢复文件的 URI
     * @param context 应用 Context
     * @param onResult 恢复完成的回调函数
     */
    fun restoreAppData(uri: Uri, context: Context, onResult: (completed: Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = runCatching {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    ZipInputStream(BufferedInputStream(inputStream)).use { zipInputStream ->
                        extractZipEntries(zipInputStream, context)
                    }
                }
            }.isSuccess
            withContext(Dispatchers.Main) { onResult(result) }
        }
    }

    /**
     * 获取要备份文件的文件列表
     * * 数据库文件
     * * 数据库的 wal 文件
     * * 数据库的 shm 文件
     * * DataStore Preferences 文件
     */
    private fun getBackupFiles(context: Context): List<File> {
        val dbPath = VerveDoApp.taskDatabase.openHelper.writableDatabase.path
        val prefPath = "${context.filesDir}/datastore"
        return listOf(
            context.getDatabasePath(Constants.DB_NAME), // 数据库
            File("$dbPath-wal"), // 数据库-wal
            File("$dbPath-shm"), // 数据库-shm
            File("$prefPath/${Constants.SP_NAME}.preferences_pb") // DataStore Preferences
        ).filter { it.exists() }
    }

    /**
     * 解压 zip 备份文件
     *
     * @param zipInputStream 备份文件中每个文件的输入流
     * @param context 应用 Context
     */
    private fun extractZipEntries(zipInputStream: ZipInputStream, context: Context) {
        val dbPath = context.getDatabasePath(Constants.DB_NAME).parent
        val prefPath = "${context.filesDir}/datastore/"
        generateSequence { zipInputStream.nextEntry }.forEach { zipEntry ->
            val outputFile = File(
                if (zipEntry.name.endsWith(".preferences_pb")) prefPath else dbPath,
                zipEntry.name
            )
            if (zipEntry.isDirectory) {
                outputFile.mkdirs()
            } else {
                outputFile.parentFile?.mkdirs()
                FileOutputStream(outputFile).use { zipInputStream.copyTo(it) }
            }
            zipInputStream.closeEntry()
        }
    }
}