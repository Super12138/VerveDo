package cn.super12138.todo.ui.pages.editor

import android.util.Log
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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import cn.super12138.todo.R
import cn.super12138.todo.constants.Constants
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.components.ConfirmDialog
import cn.super12138.todo.ui.components.TodoFloatingActionButton
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.editor.components.TodoCategoryChip
import cn.super12138.todo.ui.pages.editor.components.TodoCategoryTextField
import cn.super12138.todo.ui.pages.editor.components.TodoContentTextField
import cn.super12138.todo.ui.pages.editor.components.TodoDueDateChooser
import cn.super12138.todo.ui.pages.editor.components.TodoMarkAsCompletedCheckbox
import cn.super12138.todo.ui.pages.editor.components.TodoPrioritySlider
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SharedTransitionScope.TaskAddPage(
    modifier: Modifier = Modifier,
    onSave: (TaskEntity) -> Unit,
    onNavigateUp: () -> Unit
) = TaskEditorPage(
    task = null,
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
    task: TaskEntity,
    onSave: (TaskEntity) -> Unit,
    onDelete: () -> Unit,
    onNavigateUp: () -> Unit
) = TaskEditorPage(
    task = task,
    modifier = modifier
        .sharedBounds(
            sharedContentState = rememberSharedContentState(key = "${Constants.KEY_TODO_ITEM_TRANSITION}_${task.id}"),
            animatedVisibilityScope = LocalNavAnimatedContentScope.current,
            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds
        )
        .skipToLookaheadSize(),
    onSave = onSave,
    onDelete = onDelete,
    onNavigateUp = onNavigateUp
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TaskEditorPage(
    modifier: Modifier = Modifier,
    task: TaskEntity? = null,
    onNavigateUp: () -> Unit,
    onSave: (TaskEntity) -> Unit,
    onDelete: () -> Unit,
    viewModel: EditorViewModel = koinViewModel()
) {
    // TODO: 本页及其相关组件重组性能检查优化
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val isCustomCategory by remember { derivedStateOf { uiState.selectedCategoryIndex == -1 } }

    // 控制只有第一次进入界面才聚焦待办内容文本框
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    var isFocusedOnTextField by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(isFocusedOnTextField, uiState.isTextFieldAutoFocus) {
        Log.d(
            "Editor",
            "LaunchedEffect 01: isFocused $isFocusedOnTextField} - isAutoFocused ${uiState.isTextFieldAutoFocus}"
        )
        if (!isFocusedOnTextField && uiState.isTextFieldAutoFocus) {
            Log.d("Editor", "LaunchedEffect 01: Requesting focus for task content text field")
            withFrameNanos { }
            focusRequester.requestFocus()
            keyboardController?.show()
            isFocusedOnTextField = true
        }
    }

    var isInitializedTask by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(task?.id) {
        if (!isInitializedTask) {
            viewModel.uiState
                .map { it.categoryList.isNotEmpty() }
                .first { it }

            Log.d("Editor", "LaunchedEffect 02: taskId=${task?.id}")
            viewModel.setTaskEntity(task)
            isInitializedTask = true
        }
    }

    fun checkModifiedBeforeBack() {
        if (uiState.isModified()) {
            viewModel.showExitConfirmDialog()
        } else {
            onNavigateUp()
        }
    }
    BackHandler(onBack = ::checkModifiedBeforeBack)

    TopAppBarScaffold(
        title = stringResource(if (task != null) R.string.title_edit_task else R.string.action_add_task),
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.imePadding()
            ) {
                if (task !== null) {
                    TodoFloatingActionButton(
                        text = stringResource(R.string.action_delete),
                        iconRes = R.drawable.ic_delete,
                        expanded = true,
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        onClick = { viewModel.showDeleteConfirmDialog() }
                    )
                }
                TodoFloatingActionButton(
                    text = stringResource(R.string.action_save),
                    iconRes = R.drawable.ic_save,
                    expanded = true,
                    onClick = {
                        if (viewModel.setErrorIfNotValid()) {
                            return@TodoFloatingActionButton
                        } else {
                            viewModel.clearError()
                            val newTask = TaskEntity(
                                id = task?.id ?: 0,
                                content = uiState.taskContentState.text.toString(),
                                category = if (isCustomCategory) uiState.categoryContentState.text.toString() else uiState.categoryList[uiState.selectedCategoryIndex].name,
                                priority = uiState.priorityState,
                                dueDate = uiState.dueDateState,
                                isCompleted = uiState.isCompleted
                            )
                            Log.d(
                                "Editor",
                                "newTask category: ${newTask.category}, isCustomCategory: $isCustomCategory"
                            )
                            onSave(newTask)
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
                    state = uiState.taskContentState,
                    isError = uiState.isContentError,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                )
            }

            item(key = 2) {
                Text(
                    text = stringResource(R.string.label_category),
                    style = MaterialTheme.typography.titleMedium
                )

                TodoCategoryChip(
                    items = uiState.categoryList,
                    defaultSelectedItemIndex = uiState.selectedCategoryIndex,
                    isLoading = (uiState.categoryList.size - 1) == 0,
                    onCategorySelected = { viewModel.setSelectedCategory(it) },
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
                        state = uiState.categoryContentState,
                        isError = uiState.isCategoryError,
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
                    onValueChange = { viewModel.setPriority(it) },
                )
            }

            item(key = 4) {
                Text(
                    text = stringResource(R.string.label_more),
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.size(4.dp))
                TodoDueDateChooser(
                    value = { uiState.dueDateState },
                    onValueChange = { viewModel.setDueDate(it) },
                    modifier = Modifier.fillMaxWidth()
                )
                if (task != null) {
                    TodoMarkAsCompletedCheckbox(
                        checked = uiState.isCompleted,
                        onCheckedChange = { viewModel.setCompleted(it) },
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
            viewModel.hideExitConfirmDialog()
            onNavigateUp()
        },
        onDismiss = { viewModel.hideExitConfirmDialog() }
    )

    ConfirmDialog(
        visible = uiState.showDeleteConfirmDialog,
        iconRes = R.drawable.ic_delete,
        text = stringResource(R.string.tip_delete_task, 1),
        onConfirm = onDelete,
        onDismiss = { viewModel.hideDeleteConfirmDialog() }
    )
}