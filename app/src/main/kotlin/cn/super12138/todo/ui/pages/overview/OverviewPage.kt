package cn.super12138.todo.ui.pages.overview

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import cn.super12138.todo.R
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.components.TopAppBarScaffold
import cn.super12138.todo.ui.pages.overview.components.ListCard
import cn.super12138.todo.ui.pages.overview.components.ProgressCard
import cn.super12138.todo.ui.pages.overview.components.RoundedCornerCardLarge
import cn.super12138.todo.utils.blend
import cn.super12138.todo.utils.isDark
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OverviewPage(
    modifier: Modifier = Modifier,
    viewModel: OverviewViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    TopAppBarScaffold(
        title = stringResource(R.string.page_overview),
        modifier = modifier
    ) {
        LazyVerticalStaggeredGrid(
            modifier = Modifier.fillMaxSize(),
            columns = StaggeredGridCells.Adaptive(160.dp),
            horizontalArrangement = Arrangement.spacedBy(VerveDoDefaults.contentPadding),
            verticalItemSpacing = VerveDoDefaults.contentPadding
        ) {
            item(key = 1, span = StaggeredGridItemSpan.SingleLane) {
                AnimatedVisibility(
                    visible = uiState.pinnedTasks.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    ListCard(
                        title = stringResource(R.string.title_pinned_task),
                        list = uiState.pinnedTasks,
                        containerColor = VerveDoDefaults.Colors.Container.blend(
                            color = MaterialTheme.colorScheme.errorContainer,
                            fraction = if (uiState.darkMode.isDark()) 0.4f else 0.3f
                        )
                    )
                }
            }
            item(key = 2) {
                RoundedCornerCardLarge(
                    iconRes = R.drawable.ic_apps,
                    title = stringResource(R.string.title_all_task),
                    count = uiState.totalTasks
                )
            }
            item(key = 3) {
                RoundedCornerCardLarge(
                    iconRes = R.drawable.ic_check_circle,
                    title = stringResource(R.string.title_completed_task),
                    count = uiState.completedTasks,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            }
            item(key = 4) {
                RoundedCornerCardLarge(
                    iconRes = R.drawable.ic_pending,
                    title = stringResource(R.string.title_pending_task),
                    count = uiState.totalTasks - uiState.completedTasks,
                    colors = CardDefaults.cardColors(
                        containerColor = VerveDoDefaults.Colors.Container.blend(
                            color = MaterialTheme.colorScheme.tertiaryContainer,
                            fraction = if (uiState.darkMode.isDark()) 0.85f else 1f
                        ),
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                )
            }
            item(key = 5) {
                ProgressCard(
                    title = stringResource(R.string.title_today_task),
                    total = uiState.todayTasks.size,
                    completed = uiState.todayTasks.count { it.isCompleted }
                )
            }
            item(key = 6) {
                ListCard(
                    title = stringResource(R.string.title_upcoming_task),
                    list = uiState.nextWeekTasks
                )
            }
        }
    }
}