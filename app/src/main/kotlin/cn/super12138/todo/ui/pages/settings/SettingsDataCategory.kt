package cn.super12138.todo.ui.pages.settings

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.super12138.todo.R
import cn.super12138.todo.ui.components.EmptyTip
import cn.super12138.todo.ui.components.EmptyTipType
import cn.super12138.todo.ui.components.TodoFloatingActionButton
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.settings.components.SettingsContainer
import cn.super12138.todo.ui.pages.settings.components.SettingsItem
import cn.super12138.todo.ui.pages.settings.components.category.CategoryPromptDialog
import cn.super12138.todo.ui.theme.fadeScale
import cn.super12138.todo.utils.VibrationUtils
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SettingsDataCategory(
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.dataUiState.collectAsStateWithLifecycle()

    // TODO: 本页及其相关组件重组性能检查优化
    val view = LocalView.current
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()

    var initialCategory by rememberSaveable { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }

    val isExpanded by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    val transitionSpec = fadeScale()

    TopAppBarScaffold(
        title = stringResource(R.string.pref_category_category_management),
        onBack = onNavigateUp,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            TodoFloatingActionButton(
                iconRes = R.drawable.ic_add,
                text = stringResource(R.string.action_add_category),
                expanded = isExpanded,
                onClick = {
                    initialCategory = ""
                    showDialog = true
                }
            )
        },
        modifier = modifier,
    ) {
        AnimatedContent(
            targetState = uiState.categories.isEmpty(),
            transitionSpec = { transitionSpec }
        ) {
            if (it) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    EmptyTip(
                        type = EmptyTipType.List,
                        size = 96.dp
                    )

                    Text(
                        text = stringResource(R.string.tip_no_category_page),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                SettingsContainer(Modifier.fillMaxSize()) {
                    items(
                        items = uiState.categories,
                        key = { category -> category }
                    ) { category ->
                        SettingsItem(
                            headlineContent = {
                                Text(
                                    text = category,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.basicMarquee()
                                )
                            },
                            trailingContent = {
                                FilledTonalIconButton(
                                    shapes = IconButtonDefaults.shapes(),
                                    onClick = {
                                        VibrationUtils.performHapticFeedback(view)
                                        viewModel.setCategories(uiState.categories - category)
                                    }
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.ic_delete),
                                        contentDescription = stringResource(R.string.action_delete)
                                    )
                                }
                            },
                            onClick = {
                                initialCategory = category
                                showDialog = true
                            },
                            modifier = Modifier.animateItem(
                                fadeInSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
                                placementSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
                                fadeOutSpec = MaterialTheme.motionScheme.defaultEffectsSpec()
                            )
                        )
                    }
                }
            }
        }

        CategoryPromptDialog(
            visible = showDialog,
            initialCategory = initialCategory,
            onSave = { oldCategory, newCategory ->
                if (oldCategory.isEmpty()) {
                    if (!uiState.categories.contains(newCategory)) {
                        viewModel.setCategories(uiState.categories + newCategory)
                    } else {
                        // 调换分类位置
                        val tempList = uiState.categories - newCategory
                        viewModel.setCategories(tempList + newCategory)
                    }
                } else {
                    if (oldCategory != newCategory) {
                        val tempList = uiState.categories - oldCategory
                        viewModel.setCategories(tempList + newCategory)
                    }
                }
            },
            onDismiss = { showDialog = false }
        )
    }
}