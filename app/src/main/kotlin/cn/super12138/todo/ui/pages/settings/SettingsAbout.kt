package cn.super12138.todo.ui.pages.settings

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import cn.super12138.todo.R
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.icons.GitHubIcon
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SettingsItem
import cn.super12138.todo.utils.SystemUtils
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsAbout(
    //toSpecialPage: () -> Unit,
    toLicencePage: () -> Unit,
    toDevPage: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBarScaffold(
        title = stringResource(R.string.pref_about),
        onBack = onNavigateUp,
        modifier = modifier,
    ) {
        val context = LocalContext.current
        val uriHandler = LocalUriHandler.current
        var clickCount by remember { mutableIntStateOf(0) }
        var lastClickTime by remember { mutableLongStateOf(0L) }

        LaunchedEffect(clickCount) {
            if (clickCount > 0) {
                lastClickTime = System.currentTimeMillis()
                val currentClickTime = lastClickTime
                delay(300L)

                if (currentClickTime == lastClickTime) {
                    clickCount = 0
                }
            }
        }

        SettingsContainer(Modifier.fillMaxSize()) {
            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_numbers,
                    title = stringResource(R.string.pref_app_version),
                    description = SystemUtils.getAppVersion(context),
                    onClick = {
                        clickCount++
                        if (clickCount == 5) {
                            if ((System.currentTimeMillis() % 2) == 0.toLong()) {
                                Toast.makeText(context, "\uD83E\uDDE7", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "⛄", Toast.LENGTH_SHORT).show()
                            }
                            clickCount = 0
                        }
                    }
                )
            }
            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_person_4,
                    title = stringResource(R.string.pref_developer),
                    description = stringResource(R.string.developer_name),
                    onClick = { uriHandler.openUri(Constants.DEVELOPER_GITHUB) },
                )
            }
            item {
                SettingsItem(
                    leadingIcon = GitHubIcon,
                    title = stringResource(R.string.pref_view_on_github),
                    description = stringResource(R.string.pref_view_on_github_desc),
                    onClick = { uriHandler.openUri(Constants.GITHUB_REPO) }
                )
            }
            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_balance,
                    title = stringResource(R.string.pref_licence),
                    description = stringResource(R.string.pref_licence_desc),
                    onClick = toLicencePage
                )
            }
            item {
                SettingsItem(
                    leadingIconRes = R.drawable.ic_code_blocks,
                    title = stringResource(R.string.pref_developer_options),
                    description = stringResource(R.string.pref_developer_options_desc),
                    onClick = toDevPage
                )
            }
        }
    }
}