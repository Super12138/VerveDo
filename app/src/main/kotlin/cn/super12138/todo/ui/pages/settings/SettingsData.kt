package cn.super12138.todo.ui.pages.settings

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.super12138.todo.R
import cn.super12138.todo.ui.components.ConfirmDialog
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.settings.components.SettingsCategory
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SettingsItem
import cn.super12138.todo.utils.SystemUtils
import cn.super12138.todo.utils.restartApp
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsData(
    modifier: Modifier = Modifier,
    toCategoryManager: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: SettingsDataViewModel = koinViewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val tipBackupSuccess = stringResource(R.string.tip_backup_success)
    val tipBackupFailed = stringResource(R.string.tip_backup_failed)
    val tipExportSuccess = stringResource(R.string.tip_export_success)
    val tipExportFailed = stringResource(R.string.tip_export_failed)
    val tipRestoreFailed = stringResource(R.string.tip_restore_failed)

    val backupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip"),
        onResult = {
            if (it != null) {
                viewModel.backupDataInZipFile(
                    uri = it,
                    context = context,
                    onResult = { success ->
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) tipBackupSuccess else tipBackupFailed)
                        }
                    }
                )
            }
        }
    )

    val exportCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv"),
        onResult = {
            if (it != null) {
                viewModel.backupDataInCsvFile(
                    uri = it,
                    context = context,
                    onResult = { success ->
                        scope.launch {
                            snackbarHostState.showSnackbar(if (success) tipExportSuccess else tipExportFailed)
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
                            viewModel.showRestoreDialog()
                        } else {
                            scope.launch {
                                snackbarHostState.showSnackbar(tipRestoreFailed)
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
                        backupLauncher.launch("VerveDo-backup-${SystemUtils.getFormattedCurrentTime()}.zip")
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
                SettingsItem(
                    leadingIconRes = R.drawable.ic_export_notes,
                    title = stringResource(R.string.pref_export_task),
                    description = stringResource(R.string.pref_export_task_desc),
                    onClick = {
                        exportCsvLauncher.launch("VerveDo-backup-${SystemUtils.getFormattedCurrentTime()}.csv")
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
        visible = uiState.showRestoreDialog,
        iconRes = R.drawable.ic_restart_alt,
        title = stringResource(R.string.tip_tips),
        text = stringResource(R.string.tip_restore_success),
        showDismissButton = false,
        onConfirm = { context.restartApp() },
        onDismiss = viewModel::hideRestoreDialog,
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    )

    /*FormatSelectorDialog(
        visible = uiState.showBackupFormatDialog,
        options = BackupFormat.entries,
        onSelect = {
            when (it) {
                BackupFormat.Zip -> backupZipLauncher.launch("VerveDo-backup-${SystemUtils.getTime()}.zip")
                BackupFormat.Csv -> backupCsvLauncher.launch("VerveDo-backup-${SystemUtils.getTime()}.csv")
            }
        },
        onDismiss = viewModel::hideBackupFormatDialog
    )*/
}
/*
enum class BackupFormat {
    Zip,
    Csv
}


@Composable
private fun FormatSelectorDialog(
    visible: Boolean,
    options: List<BackupFormat>,
    onSelect: (BackupFormat) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SettingsDialog(
        visible = visible,
        title = stringResource(R.string.title_choose_backup_format),
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier
                    .selectableGroup()
                    .verticalScroll(rememberScrollState())
            ) {
                options.forEach {
                    FormatItem(
                        text = it.name,
                        onClick = {
                            onSelect(it)
                            onDismiss()
                        }
                    )
                }
            }
        },
        onDismissRequest = onDismiss,
        modifier = modifier
    )
}

@Composable
private fun FormatItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val view = LocalView.current
    val pressed by interactionSource.collectIsPressedAsState()

    Row(
        modifier
            .fillMaxWidth()
            .height(56.dp)
            .clip(
                shapeByInteraction(
                    shapes = VerveDoDefaults.shapes,
                    pressed = pressed,
                    animationSpec = VerveDoDefaults.shapesDefaultAnimationSpec
                )
            )
            .selectable(
                interactionSource = interactionSource,
                selected = false,
                onClick = {
                    VibrationUtils.performHapticFeedback(view)
                    onClick()
                },
                role = Role.RadioButton
            )
            .padding(horizontal = VerveDoDefaults.screenHorizontalPadding),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}*/
