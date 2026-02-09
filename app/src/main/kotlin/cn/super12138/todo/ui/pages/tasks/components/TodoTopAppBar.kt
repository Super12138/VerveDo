package cn.super12138.todo.ui.pages.tasks.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkOut
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import cn.super12138.todo.R
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.theme.fadeScale
import cn.super12138.todo.utils.VibrationUtils

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TodoTopAppBar(
    searchMode: Boolean,
    selectedMode: Boolean,
    selectedTodoIds: List<Int>,
    onSearchModeChange: (Boolean) -> Unit,
    onCancelSelect: () -> Unit,
    onSelectAll: () -> Unit,
    onDeleteSelectedTodo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val navIconEnterTransition = fadeIn(
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
    ) + expandIn(
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        expandFrom = Alignment.CenterStart
    )

    val navIconExitTransition = fadeOut(
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
    ) + shrinkOut(
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
        shrinkTowards = Alignment.CenterStart
    )

    val defaultTransitionSpec = fadeScale()

    val view = LocalView.current
    val animatedContainerColor by animateColorAsState(
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        targetValue = if (selectedMode) {
            MaterialTheme.colorScheme.surfaceContainerHighest
        } else {
            VerveDoDefaults.Colors.Background
        }
    )

    TopAppBar(
        navigationIcon = {
            AnimatedVisibility(
                visible = selectedMode,
                enter = navIconEnterTransition,
                exit = navIconExitTransition
            ) {
                IconButton(
                    shapes = IconButtonDefaults.shapes(),
                    onClick = {
                        VibrationUtils.performHapticFeedback(view)
                        onCancelSelect()
                    }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.tip_clear_selected_items)
                    )
                }
            }
        },
        title = {
            AnimatedContent(
                targetState = !selectedMode,
                transitionSpec = { defaultTransitionSpec }
            ) {
                if (it) {
                    Text(
                        text = stringResource(R.string.page_tasks),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = stringResource(
                            R.string.title_selected_count,
                            selectedTodoIds.size
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        actions = {
            AnimatedContent(
                targetState = selectedMode,
                transitionSpec = { defaultTransitionSpec }
            ) {
                if (it) {
                    ActionMultipleSelection(
                        onSelectAll = onSelectAll,
                        onDeleteSelectedTodo = onDeleteSelectedTodo
                    )
                } else {
                    ActionSearch(
                        searchMode = searchMode,
                        onSearchModeChange = onSearchModeChange,
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent),
        modifier = modifier.drawBehind {
            drawRect(animatedContainerColor)
        }
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun ActionSearch(
    searchMode: Boolean,
    onSearchModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    BackHandler(enabled = searchMode) { onSearchModeChange(false) }

    AnimatedVisibility(
        visible = !searchMode,
        enter = fadeIn(MaterialTheme.motionScheme.fastEffectsSpec()) + scaleIn(MaterialTheme.motionScheme.fastSpatialSpec()),
        exit = fadeOut(MaterialTheme.motionScheme.fastEffectsSpec()) + scaleOut(MaterialTheme.motionScheme.fastSpatialSpec()),
    ) {
        IconButton(
            shapes = IconButtonDefaults.shapes(),
            onClick = {
                VibrationUtils.performHapticFeedback(view)
                onSearchModeChange(!searchMode)
            },
            modifier = modifier
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_search),
                contentDescription = stringResource(R.string.action_search)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ActionMultipleSelection(
    onSelectAll: () -> Unit,
    onDeleteSelectedTodo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = modifier
    ) {
        IconButton(
            shapes = IconButtonDefaults.shapes(),
            onClick = {
                VibrationUtils.performHapticFeedback(view)
                onSelectAll()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_select_all),
                contentDescription = stringResource(R.string.tip_select_all)
            )
        }
        IconButton(
            shapes = IconButtonDefaults.shapes(),
            onClick = {
                VibrationUtils.performHapticFeedback(view)
                onDeleteSelectedTodo()
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = stringResource(R.string.action_delete)
            )
        }
    }
}

@Preview(locale = "zh-rCN", showBackground = true)
@Composable
private fun TodoTopAppBarPreview() {
    val selectedMode = remember { mutableStateOf(false) }
    TodoTopAppBar(
        searchMode = true,
        onSearchModeChange = {},
        selectedTodoIds = (1..10).toList(),
        selectedMode = selectedMode.value,
        onCancelSelect = { selectedMode.value = !selectedMode.value },
        onSelectAll = { },
        onDeleteSelectedTodo = { }
    )
}