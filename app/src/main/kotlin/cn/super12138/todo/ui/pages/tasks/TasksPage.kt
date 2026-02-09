package cn.super12138.todo.ui.pages.tasks

import androidx.activity.compose.BackHandler
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.animateFloatingActionButton
import androidx.compose.runtime.Composable
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
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import cn.super12138.todo.R
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.database.TodoEntity
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.logic.model.Priority
import cn.super12138.todo.ui.TodoDefaults
import cn.super12138.todo.ui.components.ConfirmDialog
import cn.super12138.todo.ui.components.TodoFloatingActionButton
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.tasks.components.TodoCard
import cn.super12138.todo.ui.pages.tasks.components.TodoTopAppBar
import cn.super12138.todo.ui.viewmodels.MainViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.TasksPage(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    toTodoAddPage: () -> Unit,
    toTodoEditPage: (TodoEntity) -> Unit,
) {
    val animatedVisibilityScope = LocalNavAnimatedContentScope.current

    val selectedTodos = viewModel.selectedTodoIds.collectAsState()
    val showCompleted by DataStoreManager.showCompletedFlow.collectAsState(initial = Constants.PREF_SHOW_COMPLETED_DEFAULT)

    val listState = rememberLazyListState()
    var showDeleteConfirmDialog by rememberSaveable { mutableStateOf(false) }

    val selectedTodoIds by remember { derivedStateOf { selectedTodos.value } }
    val inSelectedMode by remember { derivedStateOf { !selectedTodoIds.isEmpty() } }
    val toDoList by viewModel.sortedTodos.collectAsState(initial = emptyList())
    val filteredTodoList =
        if (showCompleted) toDoList else toDoList.filter { item -> !item.isCompleted }
    val expandedFab by remember { derivedStateOf { listState.firstVisibleItemIndex == 0 } }

    // 当按下返回键（或进行返回操作）时清空选择，仅在非选择模式下生效
    BackHandler(inSelectedMode) { viewModel.clearAllTodoSelection() }

    TopAppBarScaffold(
        topBar = {
            TodoTopAppBar(
                selectedTodoIds = selectedTodoIds,
                selectedMode = inSelectedMode,
                onCancelSelect = { viewModel.clearAllTodoSelection() },
                onSelectAll = { viewModel.selectAllTodos() },
                onDeleteSelectedTodo = { showDeleteConfirmDialog = true }
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
        LazyColumn(
            state = listState,
            verticalArrangement = Arrangement.spacedBy(TodoDefaults.settingsItemPadding),
            modifier = Modifier.fillMaxSize()
        ) {
            item {
                Spacer(modifier = Modifier.size(TodoDefaults.screenVerticalPadding))
            }

            if (filteredTodoList.isEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.tip_no_task),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            } else {
                items(
                    items = filteredTodoList,
                    key = { it.id }
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
    ConfirmDialog(
        visible = showDeleteConfirmDialog,
        iconRes = R.drawable.ic_delete,
        text = stringResource(R.string.tip_delete_task, selectedTodoIds.size),
        onConfirm = { viewModel.deleteSelectedTodo() },
        onDismiss = { showDeleteConfirmDialog = false }
    )
}