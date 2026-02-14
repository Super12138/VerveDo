package cn.super12138.todo.ui.pages.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import cn.super12138.todo.R
import cn.super12138.todo.ui.components.BasicDialog
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SettingsItem
import cn.super12138.todo.utils.VibrationUtils
import com.mikepenz.aboutlibraries.ui.compose.android.produceLibraries
import com.mikepenz.aboutlibraries.ui.compose.util.author
import com.mikepenz.aboutlibraries.ui.compose.util.htmlReadyLicenseContent

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsAboutLicence(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    val libraries by produceLibraries(R.raw.aboutlibraries)
    val view = LocalView.current
    val uriHandler = LocalUriHandler.current

    TopAppBarScaffold(
        title = stringResource(R.string.pref_licence),
        onBack = onNavigateUp,
        modifier = modifier
    ) {
        SettingsContainer(Modifier.fillMaxSize()) {
            items(
                items = libraries?.libraries ?: listOf(),
                key = { it.artifactId }
            ) { library ->
                var openDialog by remember { mutableStateOf(false) }
                SettingsItem(
                    headlineContent = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = library.name,
                                modifier = Modifier.weight(1f),
                                style = MaterialTheme.typography.titleLarge,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            val version = library.artifactVersion
                            if (version != null) {
                                Text(
                                    text = version,
                                    style = MaterialTheme.typography.bodyMedium,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                        val author = library.author
                        if (author.isNotBlank()) {
                            Text(
                                text = author,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    supportingContent = {
                        if (library.licenses.isNotEmpty()) {
                            FlowRow {
                                library.licenses.forEach {
                                    Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                        Text(
                                            maxLines = 1,
                                            text = it.name,
                                            // style = MaterialTheme.typography.labelSmall,
                                            textAlign = TextAlign.Center,
                                            overflow = TextOverflow.Ellipsis,
                                        )
                                    }
                                }
                            }
                        }
                    },
                    onClick = {
                        val license = library.licenses.firstOrNull()
                        if (!license?.htmlReadyLicenseContent.isNullOrBlank()) {
                            openDialog = true
                        } else if (!license?.url.isNullOrBlank()) {
                            license.url?.also {
                                try {
                                    uriHandler.openUri(it)
                                } catch (t: Throwable) {
                                    throw Exception("Failed to open licence URL: $it", t)
                                }
                            }
                        }
                    }
                )

                BasicDialog(
                    visible = openDialog,
                    title = { Text(library.name) },
                    text = {
                        library.licenses.firstOrNull()?.licenseContent?.let {
                            Column(Modifier.verticalScroll(rememberScrollState())) {
                                SelectionContainer { Text(text = it) }
                            }
                        }
                    },
                    confirmButton = {
                        FilledTonalButton(
                            onClick = {
                                openDialog = false
                                VibrationUtils.performHapticFeedback(view)
                            },
                            shapes = ButtonDefaults.shapes()
                        ) { Text(stringResource(R.string.action_confirm)) }
                    },
                    onDismissRequest = { openDialog = false }
                )
            }
        }
    }
}