package cn.super12138.todo.ui.pages.settings

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.TaskRepository
import cn.super12138.todo.utils.FileUtils
import com.jsoizo.kotlincsv.writer.CsvWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

class SettingsDataViewModel(
    private val taskRepository: TaskRepository,
    private val csvWriter: CsvWriter
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsDataUiState())
    val uiState = _uiState.asStateFlow()

    /**
     * 备份应用数据
     *
     * @param uri 备份文件路径的 URI
     * @param context 应用 Context
     */
    suspend fun backupDataInZipFile(uri: Uri, context: Context) = withContext(Dispatchers.IO) {
        runCatching {
            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                ZipOutputStream(BufferedOutputStream(outputStream)).use { zipOutStream ->
                    getBackupFiles(context).forEach { file ->
                        FileUtils.addFileToZip(file, file.name, zipOutStream)
                    }
                }
            } ?: error("Failed to open output stream.")
        }
    }

    /**
     * 恢复应用数据
     *
     * @param uri 选择的恢复文件的 URI
     * @param context 应用 Context
     */
    suspend fun restoreAppData(uri: Uri, context: Context): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    ZipInputStream(BufferedInputStream(inputStream)).use { zipInputStream ->
                        extractZipEntries(zipInputStream, context)
                    }
                } ?: error("Failed to open input stream.")
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
        val dbPath = context.getDatabasePath(Constants.DB_NAME)
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

    // 考虑重新设计API
    suspend fun backupDataInCsvFile(uri: Uri, context: Context) = withContext(Dispatchers.IO) {
        runCatching {
            val tasks = taskRepository.getAllTasks().first()

            val header =
                listOf("content", "category", "isCompleted", "priority", "dueDateMillis", "id")

            val rows = tasks.map { task ->
                with(task) {
                    listOf(
                        content,
                        category,
                        isCompleted.toString(),
                        priority.toString(),
                        dueDate?.toString() ?: "",
                        id.toString()
                    )
                }
            }

            context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                outputStream.write(
                    csvWriter.writeAll(listOf(header) + rows).toByteArray(Charsets.UTF_8)
                )
            } ?: error("Failed to open output stream when export CSV.")
        }
    }

    fun showRestoreDialog() = _uiState.update { it.copy(showRestoreDialog = true) }
    fun hideRestoreDialog() = _uiState.update { it.copy(showRestoreDialog = false) }
    // fun showBackupFormatDialog() = _uiState.update { it.copy(showBackupFormatDialog = true) }
    // fun hideBackupFormatDialog() = _uiState.update { it.copy(showBackupFormatDialog = false) }
}
