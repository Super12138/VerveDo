package cn.super12138.todo.ui.pages.tasks.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SelectableDropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import cn.super12138.todo.R
import cn.super12138.todo.logic.model.SortingOption
import cn.super12138.todo.logic.model.SortingOrder
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.theme.fadeScale
import cn.super12138.todo.utils.VibrationUtils

@Composable
fun TasksTopAppBar(
    inSearchMode: Boolean,
    inSelectionMode: Boolean,
    selectedTasksIds: Set<Int>,
    sortingOption: SortingOption,
    sortingOrder: SortingOrder,
    onEnterSearchMode: () -> Unit,
    onSelectAll: () -> Unit,
    onExitSelectMode: () -> Unit,
    onDeleteSelectedTask: () -> Unit,
    onOptionChange: (SortingOption) -> Unit,
    onOrderChange: (SortingOrder) -> Unit,
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

    val actionEnterTransition = fadeIn(
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
    ) + scaleIn(
        initialScale = 0.92f,
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )

    val actionExitTransition = fadeOut(
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
    )

    val defaultTransitionSpec = fadeScale()

    val view = LocalView.current
    val animatedContainerColor by animateColorAsState(
        animationSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
        targetValue = if (inSelectionMode) {
            MaterialTheme.colorScheme.surfaceContainerHighest
        } else {
            VerveDoDefaults.Colors.Background
        }
    )

    TopAppBar(
        navigationIcon = {
            AnimatedVisibility(
                visible = inSelectionMode,
                enter = navIconEnterTransition,
                exit = navIconExitTransition
            ) {
                IconButton(
                    shapes = IconButtonDefaults.shapes(),
                    onClick = {
                        VibrationUtils.performHapticFeedback(view)
                        onExitSelectMode()
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
                targetState = inSelectionMode,
                transitionSpec = { defaultTransitionSpec }
            ) {
                if (it) {
                    Text(
                        text = stringResource(
                            R.string.title_selected_count,
                            selectedTasksIds.size
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                } else {
                    Text(
                        text = stringResource(R.string.page_tasks),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        },
        actions = {
            val selectionMode = 0
            val searchMode = 1
            val elseMode = 2

            AnimatedContent(
                targetState = when {
                    inSelectionMode -> selectionMode
                    inSearchMode -> searchMode
                    else -> elseMode
                },
                transitionSpec = { actionEnterTransition togetherWith actionExitTransition }
            ) {
                Row {
                    when (it) {
                        selectionMode -> {
                            MultipleSelectionAction(
                                onSelectAll = onSelectAll,
                                onDeleteSelectedTodo = onDeleteSelectedTask
                            )
                        }

                        searchMode -> {}
                        elseMode -> DefaultAction(
                            sortingOption = sortingOption,
                            sortingOrder = sortingOrder,
                            onOptionChange = onOptionChange,
                            onOrderChange = onOrderChange,
                            onSearchClick = onEnterSearchMode
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors().copy(containerColor = Color.Transparent),
        modifier = modifier.drawBehind { drawRect(animatedContainerColor) }
    )
}

@Composable
private fun RowScope.MultipleSelectionAction(
    onSelectAll: () -> Unit,
    onDeleteSelectedTodo: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
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

@Composable
private fun RowScope.DefaultAction(
    sortingOption: SortingOption,
    sortingOrder: SortingOrder,
    onOptionChange: (SortingOption) -> Unit,
    onOrderChange: (SortingOrder) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    SortingButtonWithMenu(
        sortingOption = sortingOption,
        sortingOrder = sortingOrder,
        onOptionChange = onOptionChange,
        onOrderChange = onOrderChange
    )

    IconButton(
        shapes = IconButtonDefaults.shapes(),
        onClick = {
            VibrationUtils.performHapticFeedback(view)
            onSearchClick()
        },
        modifier = modifier
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_search),
            contentDescription = stringResource(R.string.action_search)
        )
    }
}

@Composable
fun SortingButtonWithMenu(
    sortingOption: SortingOption,
    sortingOrder: SortingOrder,
    modifier: Modifier = Modifier,
    onOptionChange: (SortingOption) -> Unit = {},
    onOrderChange: (SortingOrder) -> Unit = {},
    scrollState: ScrollState = rememberScrollState()
) {
    val view = LocalView.current

    var expanded by remember { mutableStateOf(false) }

    Box(modifier) {
        IconButton(
            shapes = IconButtonDefaults.shapes(),
            onClick = {
                VibrationUtils.performHapticFeedback(view)
                expanded = true
            }
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_sort),
                contentDescription = stringResource(R.string.label_sorting_method)
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            scrollState = scrollState,
            containerColor = MenuDefaults.groupStandardContainerColor,
            shape = MenuDefaults.standaloneGroupShape
        ) {
            SortingOption.entries.forEachIndexed { index, option ->
                SelectableDropdownMenuItem(
                    selected = option == sortingOption,
                    onClick = {
                        onOptionChange(option)
                        VibrationUtils.performHapticFeedback(view)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = stringResource(option.labelRes),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    selectedLeadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_check),
                            modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                            contentDescription = null,
                        )
                    },
                    shapes = MenuDefaults.itemShape(
                        index = index,
                        count = SortingOption.entries.size
                    )
                )
            }

            HorizontalDivider(Modifier.padding(vertical = VerveDoDefaults.contentPadding / 2))

            SortingOrder.entries.forEachIndexed { index, order ->
                SelectableDropdownMenuItem(
                    selected = order == sortingOrder,
                    onClick = {
                        onOrderChange(order)
                        VibrationUtils.performHapticFeedback(view)
                        expanded = false
                    },
                    text = {
                        Text(
                            text = stringResource(order.labelRes),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    selectedLeadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_check),
                            modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                            contentDescription = null,
                        )
                    },
                    shapes = MenuDefaults.itemShape(
                        index = index,
                        count = SortingOrder.entries.size
                    )
                )
            }
        }
    }
}
