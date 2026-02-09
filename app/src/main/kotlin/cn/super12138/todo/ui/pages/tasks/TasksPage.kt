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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import cn.super12138.todo.R
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.database.TodoEntity
import cn.super12138.todo.logic.model.Priority
import cn.super12138.todo.ui.TodoDefaults
import cn.super12138.todo.ui.components.ConfirmDialog
import cn.super12138.todo.ui.components.EmptyTip
import cn.super12138.todo.ui.components.EmptyTipType
import cn.super12138.todo.ui.components.TodoFloatingActionButton
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.tasks.components.TodoCard
import cn.super12138.todo.ui.pages.tasks.components.TodoSearchTextField
import cn.super12138.todo.ui.pages.tasks.components.TodoTopAppBar
import cn.super12138.todo.ui.theme.fadeScale
import cn.super12138.todo.ui.viewmodels.MainViewModel
import cn.super12138.todo.utils.toLocalDateString

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.TasksPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    toTodoAddPage: () -> Unit,
    toTodoEditPage: (TodoEntity) -> Unit,
) {
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current

    val toDoList by viewModel.sortedTodos.collectAsState(initial = emptyList())
    val selectedTodos = viewModel.selectedTodoIds.collectAsState()

    // 状态持久化
    val searchFieldState = viewModel.searchFieldState
    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val selectedTodoIds by remember { derivedStateOf { selectedTodos.value } }
    val inSelectedMode by remember { derivedStateOf { !selectedTodoIds.isEmpty() } }
    val expandedFab by remember { derivedStateOf { viewModel.toDoListState.firstVisibleItemIndex == 0 } }

    val filteredTodoList = if (viewModel.searchMode.value) toDoList.filter {
        it.content.contains(searchFieldState.text, ignoreCase = true) ||
                it.category.contains(searchFieldState.text, ignoreCase = true) ||
                it.dueDate?.toLocalDateString()
                    ?.contains(searchFieldState.text, ignoreCase = true) == true
    } else toDoList

    val transitionSpec = fadeScale()

    // 当按下返回键（或进行返回操作）时清空选择，仅在非选择模式下生效
    BackHandler(inSelectedMode) { viewModel.clearAllTodoSelection() }

    // 选择时自动退出搜索模式
    LaunchedEffect(inSelectedMode) { if (inSelectedMode) viewModel.searchMode.value = false }

    TopAppBarScaffold(
        topBar = {
            TodoTopAppBar(
                selectedTodoIds = selectedTodoIds,
                selectedMode = inSelectedMode,
                onCancelSelect = { viewModel.clearAllTodoSelection() },
                onSelectAll = { viewModel.selectAllTodos() },
                onDeleteSelectedTodo = { showDeleteConfirmDialog = true },
                onSearchModeChange = { viewModel.searchMode.value = it },
                searchMode = viewModel.searchMode.value,
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
                        visible = !inSelectedMode,
                        alignment = Alignment.BottomEnd,
                    )
            )
        },
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        modifier = modifier
    ) {
        Column {
            AnimatedVisibility(
                visible = viewModel.searchMode.value,
                enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()) + expandVertically(
                    MaterialTheme.motionScheme.fastSpatialSpec()
                ),
                exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()) + shrinkVertically(
                    MaterialTheme.motionScheme.fastSpatialSpec()
                ),
            ) {
                TodoSearchTextField(
                    searchMode = viewModel.searchMode.value,
                    onSearchModeChange = { viewModel.searchMode.value = it },
                    textFieldState = searchFieldState
                )
            }
            AnimatedContent(
                targetState = filteredTodoList.isEmpty(),
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
                            type = if (viewModel.searchMode.value) EmptyTipType.Search else EmptyTipType.TaskCompleted,
                            size = 96.dp
                        )

                        Text(
                            text = stringResource(if (viewModel.searchMode.value) R.string.tip_search_task_not_found else R.string.tip_no_task),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    LazyColumn(
                        state = viewModel.toDoListState,
                        verticalArrangement = Arrangement.spacedBy(TodoDefaults.settingsItemPadding),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        item {
                            Spacer(modifier = Modifier.size(TodoDefaults.screenVerticalPadding))
                        }

                        items(
                            items = filteredTodoList,
                            key = { task -> task.id }
                        ) { task ->
                            TodoCard(
                                // id = item.id,
                                content = task.content,
                                category = task.category,
                                completed = task.isCompleted,
                                dueDate = task.dueDate,
                                priority = Priority.fromFloat(task.priority),
                                selected = selectedTodoIds.contains(task.id),
                                onCardClick = {
                                    if (inSelectedMode) {
                                        viewModel.toggleTodoSelection(task)
                                    } else {
                                        toTodoEditPage(task)
                                    }
                                },
                                onCardLongClick = { viewModel.toggleTodoSelection(task) },
                                onChecked = {
                                    viewModel.updateTodo(task.copy(isCompleted = true))
                                    viewModel.playConfetti()
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
                            Spacer(modifier = Modifier.size(TodoDefaults.screenVerticalPadding))
                        }
                    }
                }
            }
        }
        ConfirmDialog(
            visible = showDeleteConfirmDialog,
            iconRes = R.drawable.ic_delete,
            text = stringResource(R.string.tip_delete_task, selectedTodoIds.size),
            onConfirm = { viewModel.deleteSelectedTodo() },
            onDismiss = { showDeleteConfirmDialog = false }
        )
    }
}