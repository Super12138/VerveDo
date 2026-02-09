package cn.super12138.todo.ui.pages.overview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.super12138.todo.R
import cn.super12138.todo.logic.database.TodoEntity
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.overview.components.ListCard
import cn.super12138.todo.ui.pages.overview.components.ProgressCard
import cn.super12138.todo.ui.pages.overview.components.RoundedCornerCardLarge
import cn.super12138.todo.ui.viewmodels.MainViewModel
import cn.super12138.todo.utils.SystemUtils

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OverviewPage(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val toDos by viewModel.sortedTodos.collectAsState(initial = emptyList())
    val totalTasks by remember { derivedStateOf { toDos.size } }
    val completedTasks by remember { derivedStateOf { toDos.count { it.isCompleted } } }

    val todayMillis = SystemUtils.getTodayEightAM()
    val dayMillis = 24L * 60 * 60 * 1000

    val todayTodo by remember {
        derivedStateOf {
            toDos.filter { todo ->
                val due = todo.dueDate ?: return@filter false
                due == todayMillis
            }
        }
    }

    val nextWeekTodo by remember {
        derivedStateOf {
            val weekFromToday = todayMillis + 7 * dayMillis
            toDos
                .filter { todo ->
                    val due = todo.dueDate ?: return@filter false
                    due in todayMillis..weekFromToday && !todo.isCompleted
                }
                .sortedWith(
                    comparator = compareBy<TodoEntity> { it.dueDate }
                        .thenBy { it.category }
                        .thenByDescending { it.priority }
                )
        }
    }


    TopAppBarScaffold(
        title = stringResource(R.string.page_overview),
        modifier = modifier
    ) {
        Column {
            LazyVerticalStaggeredGrid(
                modifier = Modifier.fillMaxSize(),
                columns = StaggeredGridCells.Adaptive(160.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalItemSpacing = 10.dp
            ) {
                item {
                    RoundedCornerCardLarge(
                        iconRes = R.drawable.ic_apps,
                        title = stringResource(R.string.title_all_task),
                        count = totalTasks
                    )
                }
                item {
                    RoundedCornerCardLarge(
                        iconRes = R.drawable.ic_check_circle,
                        title = stringResource(R.string.title_completed_task),
                        count = completedTasks,
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                }
                item {
                    RoundedCornerCardLarge(
                        iconRes = R.drawable.ic_pending,
                        title = stringResource(R.string.title_pending_task),
                        count = totalTasks - completedTasks,
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                }
                item {
                    ProgressCard(
                        title = stringResource(R.string.title_today_task),
                        total = todayTodo.size,
                        completed = todayTodo.count { it.isCompleted }
                    )
                }

                item {
                    ListCard(
                        title = stringResource(R.string.title_upcoming_task),
                        list = nextWeekTodo
                    )
                }
            }
        }
    }
}