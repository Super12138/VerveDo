package cn.super12138.todo.ui.pages.settings

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cn.super12138.todo.R
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.logic.model.DarkMode
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SwitchSettingsItem
import cn.super12138.todo.ui.pages.settings.components.appearance.darkmode.DarkModePicker
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsAppearanceDarkMode(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val darkMode by DataStoreManager.darkModeFlow.collectAsState(initial = Constants.PREF_DARK_MODE_DEFAULT)
    val pureBlackMode by DataStoreManager.pureBlackFlow.collectAsState(initial = Constants.PREF_PURE_BLACK_MODE_DEFAULT)

    val scope = rememberCoroutineScope()
    TopAppBarScaffold(
        title = stringResource(R.string.pref_dark_mode),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        SettingsContainer(Modifier.fillMaxSize()) {
            item(key = 1) {
                DarkModePicker(
                    currentDarkMode = { DarkMode.fromId(darkMode) },
                    onDarkModeChange = { scope.launch { DataStoreManager.setDarkMode(it.id) } }
                )
            }

            item(key = 2) {
                SwitchSettingsItem(
                    checked = pureBlackMode,
                    leadingIconRes = R.drawable.ic_wb_twilight,
                    title = stringResource(R.string.pref_pure_black_mode),
                    description = stringResource(R.string.pref_pure_black_mode_desc),
                    onCheckedChange = { scope.launch { DataStoreManager.setPureBlackMode(it) } }
                )
            }
        }
    }
}