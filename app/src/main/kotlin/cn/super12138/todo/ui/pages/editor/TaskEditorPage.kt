package cn.super12138.todo.ui.pages.editor

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import cn.super12138.todo.R
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.database.TodoEntity
import cn.super12138.todo.logic.datastore.DataStoreManager
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.components.ChipItem
import cn.super12138.todo.ui.components.ConfirmDialog
import cn.super12138.todo.ui.components.TodoFloatingActionButton
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.editor.components.TodoCategoryChip
import cn.super12138.todo.ui.pages.editor.components.TodoCategoryTextField
import cn.super12138.todo.ui.pages.editor.components.TodoContentTextField
import cn.super12138.todo.ui.pages.editor.components.TodoDueDateChooser
import cn.super12138.todo.ui.pages.editor.components.TodoMarkAsCompletedCheckbox
import cn.super12138.todo.ui.pages.editor.components.TodoPrioritySlider
import cn.super12138.todo.ui.pages.editor.state.rememberEditorState

@Composable
fun SharedTransitionScope.TaskAddPage(
    modifier: Modifier = Modifier,
    onSave: (TodoEntity) -> Unit,
    onNavigateUp: () -> Unit
) = TaskEditorPage(
    toDo = null,
    modifier = modifier
        .sharedBounds(
            sharedContentState = rememberSharedContentState(key = Constants.KEY_TODO_FAB_TRANSITION),
            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
        )
        .skipToLookaheadSize(), // 这个修饰符必须放后面
    onSave = onSave,
    onDelete = {},
    onNavigateUp = onNavigateUp
)

@Composable
fun SharedTransitionScope.TaskEditPage(
    modifier: Modifier = Modifier,
    toDo: TodoEntity,
    onSave: (TodoEntity) -> Unit,
    onDelete: () -> Unit,
    onNavigateUp: () -> Unit
) = TaskEditorPage(
    toDo = toDo,
    modifier = modifier.sharedBounds(
        sharedContentState = rememberSharedContentState(key = "${Constants.KEY_TODO_ITEM_TRANSITION}_${toDo.id}"),
        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
        resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
    ),
    //TODO: 没想好加不加 .skipToLookaheadSize(),
    onSave = onSave,
    onDelete = onDelete,
    onNavigateUp = onNavigateUp
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SharedTransitionScope.TaskEditorPage(
    modifier: Modifier = Modifier,
    toDo: TodoEntity? = null,
    onSave: (TodoEntity) -> Unit,
    onDelete: () -> Unit,
    onNavigateUp: () -> Unit
) {
    // TODO: 本页及其相关组件重组性能检查优化
    val uiState = rememberEditorState(initialTodo = toDo)

    val originalCategories by DataStoreManager.categoriesFlow.collectAsState(initial = emptyList())
    val categories = originalCategories
        .mapIndexed { index, category ->
            ChipItem(
                id = index,
                name = category
            )
        } + ChipItem(id = -1, name = stringResource(R.string.label_customization))

    var defaultIndex by remember { mutableIntStateOf(-1) }
    LaunchedEffect(originalCategories, toDo) {
        if (originalCategories.isEmpty()) return@LaunchedEffect
        if (toDo == null) {
            val index = if (categories.size == 1) -1 else 0
            defaultIndex = index
            uiState.selectedCategoryIndex = index
        } else {
            val index = categories.firstOrNull { it.name == toDo.category }?.id ?: -1
            defaultIndex = index
            uiState.selectedCategoryIndex = index
        }
    }

    val isCustomCategory by remember {
        derivedStateOf {
            uiState.selectedCategoryIndex == -1
        }
    }

    fun checkModifiedBeforeBack() {
        if (uiState.isModified()) {
            uiState.showExitConfirmDialog = true
        } else {
            onNavigateUp()
        }
    }

    BackHandler(onBack = ::checkModifiedBeforeBack)

    TopAppBarScaffold(
        title = stringResource(if (toDo != null) R.string.title_edit_task else R.string.action_add_task),
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.imePadding()
            ) {
                if (toDo !== null) {
                    TodoFloatingActionButton(
                        text = stringResource(R.string.action_delete),
                        iconRes = R.drawable.ic_delete,
                        expanded = true,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        onClick = { uiState.showDeleteConfirmDialog = true }
                    )
                }
                TodoFloatingActionButton(
                    text = stringResource(R.string.action_save),
                    iconRes = R.drawable.ic_save,
                    expanded = true,
                    onClick = {
                        if (uiState.setErrorIfNotValid()) {
                            return@TodoFloatingActionButton
                        } else {
                            uiState.clearError()
                            val newTodo = TodoEntity(
                                id = toDo?.id ?: 0,
                                content = uiState.toDoContent,
                                category = if (isCustomCategory) uiState.categoryContent else categories[uiState.selectedCategoryIndex].name,
                                priority = uiState.priorityState,
                                dueDate = uiState.dueDateState,
                                isCompleted = uiState.isCompleted
                            )
                            onSave(newTodo)
                        }
                    }
                )
            }
        },
        onBack = ::checkModifiedBeforeBack,
        modifier = modifier
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            item(key = 0) {
                Spacer(modifier = Modifier.size(VerveDoDefaults.screenVerticalPadding))
            }

            item(key = 1) {
                TodoContentTextField(
                    value = uiState.toDoContent,
                    onValueChange = { uiState.toDoContent = it },
                    isError = uiState.isErrorContent,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item(key = 2) {
                Text(
                    text = stringResource(R.string.label_category),
                    style = MaterialTheme.typography.titleMedium
                )

                TodoCategoryChip(
                    items = categories,
                    defaultSelectedItemIndex = defaultIndex,
                    isLoading = originalCategories.isEmpty(),
                    onCategorySelected = { uiState.selectedCategoryIndex = it },
                    modifier = Modifier.fillMaxWidth()
                )

                AnimatedVisibility(
                    visible = isCustomCategory,
                    enter = fadeIn(MaterialTheme.motionScheme.defaultEffectsSpec()) + expandVertically(
                        MaterialTheme.motionScheme.defaultEffectsSpec()
                    ),
                    exit = fadeOut(MaterialTheme.motionScheme.defaultEffectsSpec()) + shrinkVertically(
                        MaterialTheme.motionScheme.defaultEffectsSpec()
                    )
                ) {
                    TodoCategoryTextField(
                        value = uiState.categoryContent,
                        onValueChange = { uiState.categoryContent = it },
                        isError = uiState.isErrorCategory,
                        supportingText = stringResource(uiState.categorySupportingText),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item(key = 3) {
                Text(
                    text = stringResource(R.string.label_priority),
                    style = MaterialTheme.typography.titleMedium
                )

                TodoPrioritySlider(
                    value = { uiState.priorityState },
                    onValueChange = { uiState.priorityState = it },
                )
            }

            item(key = 4) {
                Text(
                    text = stringResource(R.string.label_more),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.size(4.dp))
                TodoDueDateChooser(
                    value = uiState.dueDateState,
                    onValueChange = { uiState.dueDateState = it },
                    modifier = Modifier.fillMaxWidth()
                )
                if (toDo != null) {
                    TodoMarkAsCompletedCheckbox(
                        checked = uiState.isCompleted,
                        onCheckedChange = { uiState.isCompleted = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            item(key = 5) {
                Spacer(modifier = Modifier.size(VerveDoDefaults.screenVerticalPadding))
            }
        }
    }

    ConfirmDialog(
        visible = uiState.showExitConfirmDialog,
        iconRes = R.drawable.ic_undo,
        text = stringResource(R.string.tip_discard_changes),
        onConfirm = {
            uiState.showExitConfirmDialog = false
            onNavigateUp()
        },
        onDismiss = { uiState.showExitConfirmDialog = false }
    )

    ConfirmDialog(
        visible = uiState.showDeleteConfirmDialog,
        iconRes = R.drawable.ic_delete,
        text = stringResource(R.string.tip_delete_task, 1),
        onConfirm = onDelete,
        onDismiss = { uiState.showDeleteConfirmDialog = false }
    )
}