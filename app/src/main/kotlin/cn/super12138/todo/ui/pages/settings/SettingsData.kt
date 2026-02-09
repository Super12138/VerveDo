package cn.super12138.todo.ui.pages.settings

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import cn.super12138.todo.R
import cn.super12138.todo.ui.activities.MainActivity
import cn.super12138.todo.ui.components.ConfirmDialog
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.settings.components.SettingsCategory
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SettingsItem
import cn.super12138.todo.ui.viewmodels.MainViewModel
import cn.super12138.todo.utils.SystemUtils
import kotlinx.coroutines.launch
import kotlin.system.exitProcess

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsData(
    viewModel: MainViewModel,
    toCategoryManager: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showRestoreDialog by rememberSaveable { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip"),
        onResult = {
            if (it != null) {
                viewModel.backupAppData(
                    uri = it,
                    context = context,
                    onResult = { success ->
                        if (success) {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.tip_backup_success)
                                )
                            }
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.tip_backup_failed)
                                )
                            }
                        }
                    }
                )
            }
        }
    )

    val restoreLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = {
            if (it != null) {
                viewModel.restoreAppData(
                    uri = it,
                    context = context,
                    onResult = { success ->
                        if (success) {
                            showRestoreDialog = true
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(
                                    context.getString(R.string.tip_restore_failed)
                                )
                            }
                        }
                    }
                )
            }
        }
    )

    TopAppBarScaffold(
        title = stringResource(R.string.pref_data),
        onBack = onNavigateUp,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = modifier,
    ) {
        SettingsContainer(Modifier.fillMaxSize()) {
            item {
                SettingsCategory(
                    title = stringResource(R.string.pref_category_data_management),
                    first = true
                )
                SettingsItem(
                    leadingIconRes = R.drawable.ic_download,
                    title = stringResource(R.string.pref_backup),
                    description = stringResource(R.string.pref_backup_desc),
                    onClick = {
                        backupLauncher.launch("VerveDo-backup-${SystemUtils.getTime()}.zip")
                    }
                )
            }
            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_upload,
                    title = stringResource(R.string.pref_restore),
                    description = stringResource(R.string.pref_restore_desc),
                    onClick = {
                        restoreLauncher.launch(arrayOf("application/zip"))
                    }
                )
            }

            item {
                SettingsCategory(stringResource(R.string.pref_category_category_management))
                SettingsItem(
                    leadingIconRes = R.drawable.ic_category,
                    title = stringResource(R.string.pref_category_category_management),
                    description = stringResource(R.string.pref_category_management_desc),
                    onClick = toCategoryManager
                )
            }
        }
    }
    ConfirmDialog(
        visible = showRestoreDialog,
        iconRes = R.drawable.ic_restart_alt,
        title = stringResource(R.string.tip_tips),
        text = stringResource(R.string.tip_restore_success),
        showDismissButton = false,
        onConfirm = { restartApp(context) },
        onDismiss = { showRestoreDialog = false },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )
}

/**
 * 重启应用
 * @param context 上下文
 */
private fun restartApp(context: Context) {
    val intent = Intent(
        context,
        MainActivity::class.java
    ).apply {
        flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }
    context.startActivity(intent)
    exitProcess(0)
}