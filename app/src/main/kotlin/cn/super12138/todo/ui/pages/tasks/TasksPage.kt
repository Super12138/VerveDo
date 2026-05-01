package cn.super12138.todo.ui.pages.tasks

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import cn.super12138.todo.R
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.model.Priority
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.components.ConfirmDialog
import cn.super12138.todo.ui.components.EmptyTip
import cn.super12138.todo.ui.components.EmptyTipType
import cn.super12138.todo.ui.components.TodoFloatingActionButton
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.tasks.components.TasksTopAppBar
import cn.super12138.todo.ui.pages.tasks.components.TodoCard
import cn.super12138.todo.ui.pages.tasks.components.TodoSearchTextField
import cn.super12138.todo.ui.theme.fadeScale
import cn.super12138.todo.ui.viewmodels.MainViewModel
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.TasksPage(
    modifier: Modifier = Modifier,
    toTodoAddPage: () -> Unit,
    toTodoEditPage: (TaskEntity) -> Unit,
    viewModel: TaskViewModel = koinViewModel(),
    mainViewModel: MainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val taskListState = rememberLazyListState()
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current
    val expandedFab by remember { derivedStateOf { taskListState.firstVisibleItemIndex == 0 } }
    val transitionSpec = fadeScale()

    // 当按下返回键（或进行返回操作）时清空选择，仅在非选择模式下生效
    BackHandler(uiState.isInSelectionMode) { viewModel.exitMultiSelectMode() }
    BackHandler(uiState.isInSearchMode) { viewModel.exitSearchMode() }

    TopAppBarScaffold(
        topBar = {
            TasksTopAppBar(
                screenMode = uiState.screenMode,
                selectedTodoIds = uiState.selectedTaskIds,
                onCancelSelect = { viewModel.exitMultiSelectMode() },
                onSelectAll = { viewModel.selectAllTask() },
                onDeleteSelectedTodo = { viewModel.showDeleteConfirmDialog() },
                onSearchModeChange = { if (it) viewModel.enterSearchMode() else viewModel.exitSearchMode() },
            )
        },
        floatingActionButton = {
            TodoFloatingActionButton(
                text = stringResource(R.string.action_add_task),
                iconRes = R.drawable.ic_add,
                expanded = expandedFab,
                onClick = { toTodoAddPage() },
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = Constants.KEY_TODO_FAB_TRANSITION),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                    )
                    .animateFloatingActionButton(
                        visible = !uiState.isInSelectionMode,
                        alignment = Alignment.BottomEnd,
                    )
            )
        },
        // contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(VerveDoDefaults.settingsItemPadding)) {
            AnimatedVisibility(
                visible = uiState.isInSearchMode,
                enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()) + expandVertically(
                    MaterialTheme.motionScheme.fastSpatialSpec()
                ),
                exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()) + shrinkVertically(
                    MaterialTheme.motionScheme.fastSpatialSpec()
                ),
            ) {
                TodoSearchTextField(
                    searchMode = uiState.isInSearchMode,
                    onSearchModeChange = { viewModel.exitSearchMode() },
                    textFieldState = uiState.searchTextState
                )
            }
            AnimatedContent(
                targetState = uiState.taskList.isEmpty(),
                transitionSpec = { transitionSpec }
            ) {
                if (it) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f) // 占满剩余空间
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyTip(
                            type = if (uiState.isInSearchMode) EmptyTipType.Search else EmptyTipType.TaskCompleted,
                            size = VerveDoDefaults.Sizes.EmptyTip.large
                        )

                        Text(
                            text = stringResource(if (uiState.isInSearchMode) R.string.tip_search_task_not_found else R.string.tip_no_task),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    LazyColumn(
                        state = taskListState,
                        verticalArrangement = Arrangement.spacedBy(VerveDoDefaults.settingsItemPadding),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(VerveDoDefaults.ScreenContainerShape)
                    ) {
                        item {
                            Spacer(modifier = Modifier.size(VerveDoDefaults.screenVerticalPadding / 2))
                        }

                        items(
                            items = uiState.taskList,
                            key = { task -> task.id }
                        ) { task ->
                            TodoCard(
                                // id = item.id,
                                content = task.content,
                                category = task.category,
                                completed = task.isCompleted,
                                dueDate = task.dueDate,
                                priority = Priority.fromFloat(task.priority),
                                selected = uiState.selectedTaskIds.contains(task.id),
                                onCardClick = {
                                    if (uiState.isInSelectionMode) {
                                        viewModel.toggleTaskSelection(task)
                                    } else {
                                        toTodoEditPage(task)
                                    }
                                },
                                onCardLongClick = {
                                    if (!uiState.isInSelectionMode) {
                                        viewModel.enterMultiSelectMode(task.id)
                                    } else {
                                        viewModel.toggleTaskSelection(task)
                                    }
                                },
                                onChecked = {
                                    viewModel.updateTask(task.copy(isCompleted = true))
                                    mainViewModel.playConfetti()
                                },
                                modifier = Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "${Constants.KEY_TODO_ITEM_TRANSITION}_${task.id}"),
                                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                                    )
                                    .animateItem(
                                        fadeInSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
                                        placementSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
                                        fadeOutSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                                    )
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.size(VerveDoDefaults.screenVerticalPadding / 2))
                        }
                    }
                }
            }
        }
        ConfirmDialog(
            visible = uiState.showDeleteConfirmDialog,
            iconRes = R.drawable.ic_delete,
            text = stringResource(R.string.tip_delete_task, uiState.selectedTaskIds.size),
            onConfirm = { viewModel.deleteSelectedTask() },
            onDismiss = { viewModel.hideDeleteConfirmDialog() }
        )
    }
}