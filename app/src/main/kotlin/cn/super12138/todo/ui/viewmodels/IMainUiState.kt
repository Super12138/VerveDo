package cn.super12138.todo.ui.viewmodels

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.input.TextFieldState
import androidx.navigation3.runtime.NavKey
import cn.super12138.todo.logic.database.TaskEntity
import cn.super12138.todo.logic.model.ScreenMode
import cn.super12138.todo.ui.navigation.TopLevelBackStack

interface IMainUiState {
    val backStack: TopLevelBackStack<NavKey>

    val taskList: List<TaskEntity>
    val taskListState: LazyListState

    var screenMode: ScreenMode
    val selectedTaskIds: Set<Int>
    val searchTextState: TextFieldState

    var showConfetti: Boolean


}