package cn.super12138.todo.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.super12138.todo.R
import cn.super12138.todo.logic.model.ColorSpecVersion
import cn.super12138.todo.logic.model.DynamicSchemePlatform
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SettingsItem
import cn.super12138.todo.ui.pages.settings.components.SettingsRadioDialog
import cn.super12138.todo.ui.pages.settings.components.SettingsRadioOptions
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsDeveloperOptions(
    modifier: Modifier = Modifier,
    toPaddingPage: () -> Unit,
    onNavigateUp: () -> Unit,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.devUiState.collectAsStateWithLifecycle()

    var specVersionDialog by rememberSaveable { mutableStateOf(false) }
    var schemePlatformDialog by rememberSaveable { mutableStateOf(false) }

    TopAppBarScaffold(
        title = stringResource(R.string.pref_developer_options),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        SettingsContainer(Modifier.fillMaxSize()) {
            item(key = 1) {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_padding,
                    title = stringResource(R.string.pref_padding),
                    onClick = toPaddingPage
                )
            }
            item(key = 2) {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_colorize,
                    title = stringResource(R.string.pref_color_spec_version),
                    onClick = { specVersionDialog = true }
                )
            }
            item(key = 3) {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_devices_wearables,
                    title = stringResource(R.string.pref_dynamic_scheme_platform),
                    onClick = { schemePlatformDialog = true }
                )
            }
        }

        val specVersionOptions = remember {
            ColorSpecVersion.entries.map {
                SettingsRadioOptions(
                    id = it.id,
                    text = it.name
                )
            }
        }
        SettingsRadioDialog(
            visible = specVersionDialog,
            title = stringResource(R.string.pref_color_spec_version),
            currentOptions = SettingsRadioOptions(
                id = uiState.colorSpecVersion.id,
                text = uiState.colorSpecVersion.name
            ),
            options = specVersionOptions,
            onSelect = { viewModel.setColorSpecVersion(it) },
            onDismiss = { specVersionDialog = false }
        )

        val schemePlatformOptions = remember {
            DynamicSchemePlatform.entries.map {
                SettingsRadioOptions(
                    id = it.id,
                    text = it.name
                )
            }
        }
        SettingsRadioDialog(
            visible = schemePlatformDialog,
            title = stringResource(R.string.pref_dynamic_scheme_platform),
            currentOptions = SettingsRadioOptions(
                id = uiState.dynamicSchemePlatform.id,
                text = uiState.dynamicSchemePlatform.name
            ),
            options = schemePlatformOptions,
            onSelect = { viewModel.setDynamicSchemePlatform(it) },
            onDismiss = { schemePlatformDialog = false }
        )
    }
}