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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuPopup
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import cn.super12138.todo.R
import cn.super12138.todo.logic.model.SortingDirection
import cn.super12138.todo.logic.model.SortingOption
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.theme.fadeScale
import cn.super12138.todo.utils.VibrationUtils

@Composable
fun TasksTopAppBar(
    inSearchMode: Boolean,
    inSelectionMode: Boolean,
    selectedTasksIds: Set<Int>,
    sortingOption: SortingOption,
    sortingDirection: SortingDirection,
    onEnterSearchMode: () -> Unit,
    onSelectAll: () -> Unit,
    onExitSelectMode: () -> Unit,
    onDeleteSelectedTask: () -> Unit,
    onOptionChange: (SortingOption) -> Unit,
    onOrderChange: (SortingDirection) -> Unit,
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
                            sortingDirection = sortingDirection,
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
    sortingDirection: SortingDirection,
    onOptionChange: (SortingOption) -> Unit,
    onOrderChange: (SortingDirection) -> Unit,
    onSearchClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    SortingButtonWithMenu(
        sortingOption = sortingOption,
        sortingDirection = sortingDirection,
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
private fun SortingButtonWithMenu(
    sortingOption: SortingOption,
    sortingDirection: SortingDirection,
    modifier: Modifier = Modifier,
    onOptionChange: (SortingOption) -> Unit = {},
    onOrderChange: (SortingDirection) -> Unit = {},
    groupInteractionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    scrollState: ScrollState = rememberScrollState()
) {
    val view = LocalView.current
    val allOption = SortingOption.entries
    val allDirection = SortingDirection.entries

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
                contentDescription = stringResource(R.string.label_sort_by)
            )
        }

        DropdownMenuPopup(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShape(0, 2),
                interactionSource = groupInteractionSource,
            ) {
                MenuDefaults.DropdownMenuGroupLabel {
                    GroupLabelText(stringResource(R.string.label_sort_by))
                }
                allOption.forEachIndexed { index, option ->
                    DropdownMenuItem(
                        selected = option == sortingOption,
                        text = stringResource(option.labelRes),
                        onClick = {
                            onOptionChange(option)
                            expanded = false
                        },
                        index = index,
                        count = allOption.size
                    )
                }
            }
            Spacer(Modifier.height(MenuDefaults.GroupSpacing))
            DropdownMenuGroup(
                shapes = MenuDefaults.groupShape(1, 2),
                interactionSource = groupInteractionSource,
            ) {
                MenuDefaults.DropdownMenuGroupLabel {
                    GroupLabelText(stringResource(R.string.label_sorting_direction))
                }
                allDirection.forEachIndexed { index, direction ->
                    DropdownMenuItem(
                        selected = direction == sortingDirection,
                        text = stringResource(direction.labelRes),
                        onClick = {
                            onOrderChange(direction)
                            expanded = false
                        },
                        index = index,
                        count = allDirection.size
                    )
                }
            }
        }
    }
}

@Composable
private fun ColumnScope.GroupLabelText(
    label: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier
    )
}

@Composable
private fun DropdownMenuItem(
    selected: Boolean,
    text: String,
    onClick: () -> Unit,
    index: Int,
    count: Int,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    SelectableDropdownMenuItem(
        selected = selected,
        onClick = {
            VibrationUtils.performHapticFeedback(view)
            onClick()
        },
        text = {
            Text(
                text = text,
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
            count = count
        ),
        modifier = modifier
    )
}