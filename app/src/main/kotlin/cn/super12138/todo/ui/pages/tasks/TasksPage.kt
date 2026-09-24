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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.material3.toShape
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
import cn.super12138.todo.ui.pages.tasks.components.TaskCard
import cn.super12138.todo.ui.pages.tasks.components.TaskSearchTextField
import cn.super12138.todo.ui.pages.tasks.components.TasksTopAppBar
import cn.super12138.todo.ui.theme.fadeScale
import cn.super12138.todo.utils.toFormattedDate
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.TasksPage(
    modifier: Modifier = Modifier,
    toTaskAddPage: () -> Unit,
    toTaskEditPage: (TaskEntity) -> Unit,
    viewModel: TaskViewModel = koinViewModel()
) {
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current
    val fadeScaleTransition = fadeScale()
    val listEnterTransition = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()) +
            expandVertically(MaterialTheme.motionScheme.fastSpatialSpec())
    val listExitTransition =
        fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()) +
                shrinkVertically(MaterialTheme.motionScheme.fastSpatialSpec())

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val taskListState = rememberLazyStaggeredGridState()
    val taskList = remember(uiState.originalTaskList, uiState.searchQuery) {
        if (uiState.searchQuery.isEmpty()) {
            uiState.originalTaskList
        } else {
            uiState.originalTaskList.filter { task ->
                listOf(
                    task.content,
                    task.category,
                    task.dueDateMillis?.toFormattedDate() ?: ""
                ).any {
                    it.contains(uiState.searchQuery, ignoreCase = true)
                }
            }
        }
    }

    val expandedFab by remember { derivedStateOf { taskListState.firstVisibleItemIndex == 0 } }

    BackHandler {
        if (uiState.inSelectionMode) {
            viewModel.exitMultiSelectMode()
        } else if (uiState.inSearchMode) {
            viewModel.exitSearchMode()
        }
    }

    TopAppBarScaffold(
        topBar = {
            TasksTopAppBar(
                inSearchMode = uiState.inSearchMode,
                inSelectionMode = uiState.inSelectionMode,
                selectedTasksIds = uiState.selectedTaskIds,
                onExitSelectMode = viewModel::exitMultiSelectMode,
                onSelectAll = { viewModel.selectVisibleAllTask(taskList) },
                onDeleteSelectedTask = viewModel::showDeleteConfirmDialog,
                onEnterSearchMode = viewModel::enterSearchMode
            )
        },
        floatingActionButton = {
            TodoFloatingActionButton(
                text = stringResource(R.string.action_add_task),
                iconRes = R.drawable.ic_add,
                expanded = expandedFab,
                onClick = toTaskAddPage,
                modifier = Modifier
                    .sharedBounds(
                        sharedContentState = rememberSharedContentState(key = Constants.KEY_TODO_FAB_TRANSITION),
                        animatedVisibilityScope = animatedVisibilityScope,
                        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
                    )
                    .animateFloatingActionButton(
                        visible = !uiState.inSelectionMode,
                        alignment = Alignment.BottomEnd,
                    )
            )
        },
        modifier = modifier
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(VerveDoDefaults.settingsItemPadding)) {
            AnimatedVisibility(
                visible = uiState.inSearchMode && !uiState.inSelectionMode,
                enter = listEnterTransition,
                exit = listExitTransition
            ) {
                TaskSearchTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    onExitSearchMode = viewModel::exitSearchMode,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            AnimatedContent(
                targetState = taskList.isEmpty(),
                transitionSpec = { fadeScaleTransition }
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
                            type = if (uiState.inSearchMode) EmptyTipType.Search else EmptyTipType.TaskCompleted,
                            size = VerveDoDefaults.Sizes.EmptyTip.large,
                            shape = MaterialShapes.Cookie6Sided.toShape()
                        )

                        Text(
                            text = stringResource(if (uiState.inSearchMode) R.string.tip_search_task_not_found else R.string.tip_no_task),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    LazyVerticalStaggeredGrid(
                        state = taskListState,
                        columns = StaggeredGridCells.Adaptive(minSize = 250.dp),
                        contentPadding = PaddingValues(vertical = VerveDoDefaults.contentPadding / 4),
                        verticalItemSpacing = VerveDoDefaults.contentPadding / 2,
                        horizontalArrangement = Arrangement.spacedBy(VerveDoDefaults.contentPadding / 2),
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(VerveDoDefaults.ScreenContainerShape)
                    ) {
                        item {
                            Spacer(modifier = Modifier.size(VerveDoDefaults.screenVerticalPadding / 2))
                        }

                        items(
                            items = taskList,
                            key = { task -> task.id }
                        ) { task ->
                            val priority =
                                remember(task.priority) { Priority.fromFloat(task.priority) }
                            val selected by remember(task.id, uiState.selectedTaskIds) {
                                derivedStateOf {
                                    task.id in uiState.selectedTaskIds
                                }
                            }
                            TaskCard(
                                content = task.content,
                                category = task.category,
                                completed = task.isCompleted,
                                dueDateMillis = task.dueDateMillis,
                                priority = priority,
                                selected = selected,
                                onClick = {
                                    if (uiState.inSelectionMode) {
                                        viewModel.toggleTaskSelection(task)
                                    } else {
                                        toTaskEditPage(task)
                                    }
                                },
                                onLongClick = {
                                    if (uiState.inSelectionMode) {
                                        viewModel.toggleTaskSelection(task)
                                    } else {
                                        viewModel.enterMultiSelectMode(task.id)
                                    }
                                },
                                onChecked = {
                                    viewModel.updateTask(task.copy(isCompleted = true))
                                    viewModel.setConfettiVisibility(true)
                                },
                                modifier = Modifier
                                    .sharedBounds(
                                        sharedContentState = rememberSharedContentState(key = "${Constants.KEY_TODO_ITEM_TRANSITION}_${task.id}"),
                                        animatedVisibilityScope = animatedVisibilityScope,
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
            onConfirm = {
                viewModel.deleteSelectedTask()
                viewModel.exitMultiSelectMode()
            },
            onDismiss = viewModel::hideDeleteConfirmDialog
        )
    }
}
