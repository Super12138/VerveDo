package cn.super12138.todo.ui.pages.tasks.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import cn.super12138.todo.R
import cn.super12138.todo.logic.model.Priority
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.theme.shapeByInteraction
import cn.super12138.todo.utils.VibrationUtils
import cn.super12138.todo.utils.containerColor
import cn.super12138.todo.utils.disabledContainerColor
import cn.super12138.todo.utils.disabledContentColor
import cn.super12138.todo.utils.toRelativeTimeString
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

@Composable
fun TaskCard(
    content: String,
    category: String,
    completed: Boolean,
    dueDateInstant: Instant?,
    priority: Priority,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {},
    onChecked: () -> Unit = {},
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: CardColors = VerveDoDefaults.listColor,
    shapes: ButtonShapes = VerveDoDefaults.shapes
) {
    val view = LocalView.current

    val enterTransition = fadeIn(MaterialTheme.motionScheme.fastSpatialSpec()) + expandHorizontally(
        MaterialTheme.motionScheme.fastSpatialSpec()
    )
    val exitTransition = fadeOut(MaterialTheme.motionScheme.fastSpatialSpec()) + shrinkHorizontally(
        MaterialTheme.motionScheme.fastSpatialSpec()
    )

    val pressed by interactionSource.collectIsPressedAsState()
    val animatedShape = shapeByInteraction(
        shapes = shapes,
        pressed = if (selected) true else pressed,
        animationSpec = VerveDoDefaults.shapesDefaultAnimationSpec
    )

    val containerColor by animateColorAsState( // @ChatGPT
        targetValue = when {
            selected -> MaterialTheme.colorScheme.secondaryContainer
            completed -> colors.disabledContainerColor
            else -> colors.containerColor
        },
        label = "containerColor"
    )

    val contentColor by animateColorAsState(
        targetValue = if (completed) colors.disabledContentColor else colors.contentColor,
        label = "contentColor"
    )

    val badgeColor by animateColorAsState(
        targetValue = if (completed) disabledContainerColor() else MaterialTheme.colorScheme.primary,
        label = "badgeColor"
    )

    val dateColor by animateColorAsState(
        targetValue = if (completed) colors.disabledContentColor else MaterialTheme.colorScheme.onSurface,
        label = "contentColor"
    )

    val relativeDateColor by animateColorAsState(
        targetValue = if (completed) colors.disabledContentColor else MaterialTheme.colorScheme.onSurfaceVariant,
        label = "contentColor"
    )

    val priorityColor by animateColorAsState(
        targetValue = if (completed) disabledContentColor() else priority.containerColor(),
        label = "priorityColor"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .animateContentSize()
            //.heightIn(min = VerveDoDefaults.Sizes.taskCardHeight)
            //.wrapContentHeight()
            .clip(animatedShape)
            .combinedClickable(
                interactionSource = interactionSource,
                onClick = {
                    VibrationUtils.performHapticFeedback(view)
                    onClick()
                },
                // 不再需要使用：VibrationUtils.performHapticFeedback(view, HapticFeedbackConstants.LONG_PRESS)
                // 因为 combinedClickable 在更新的 Compose 里已经处理好了触感反馈
                onLongClick = onLongClick
            )
            .drawBehind { drawRect(containerColor) }
    ) {
        AnimatedVisibility(
            visible = selected,
            enter = enterTransition,
            exit = exitTransition
        ) { SelectedIcon(Modifier.padding(start = VerveDoDefaults.contentPadding * 2)) }

        CompositionLocalProvider(LocalContentColor provides contentColor) {
            Column(
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(
                        horizontal = VerveDoDefaults.contentPadding * 2,
                        vertical = VerveDoDefaults.contentPadding * 2
                    )
                    .weight(1f)
                    .fillMaxSize()
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = content,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 3,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .wrapContentHeight()
                            .weight(1f)
                    )

                    dueDateInstant?.let {
                        DueDatePresenter(
                            dueDateInstant = it,
                            dateColor = dateColor,
                            relativeDateColor = relativeDateColor,
                            modifier = Modifier.padding(start = VerveDoDefaults.contentPadding)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(VerveDoDefaults.contentPadding / 2),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CategoryBadge(
                        category = category,
                        containerColor = badgeColor,
                        modifier = Modifier.weight(weight = 1f, fill = false)
                    )

                    Text(
                        text = stringResource(priority.nameRes),
                        style = MaterialTheme.typography.labelMedium,
                        color = priorityColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.wrapContentWidth()
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = !selected && !completed,
            enter = enterTransition,
            exit = exitTransition
        ) {
            CheckButton(
                onChecked = onChecked, modifier = Modifier
                    .fillMaxHeight()
                    .animateContentSize()
            )
        }
    }
}

@Composable
private fun SelectedIcon(modifier: Modifier = Modifier) {
    Box(
        modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondary)
            .padding(VerveDoDefaults.contentPadding / 2)
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            tint = contentColorFor(MaterialTheme.colorScheme.secondary),
            contentDescription = stringResource(R.string.tip_selected)
        )
    }
}

@Composable
private fun DueDatePresenter(
    dueDateInstant: Instant,
    modifier: Modifier = Modifier,
    dateColor: Color = MaterialTheme.colorScheme.onSurface,
    relativeDateColor: Color = MaterialTheme.colorScheme.onSurfaceVariant
) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.End,
        modifier = modifier
    ) {
        val dueDateText = remember(dueDateInstant) {
            dueDateInstant.toLocalDateTime(TimeZone.UTC)
                .format(VerveDoDefaults.defaultDateFormatter)
        }
        Text(
            text = dueDateText,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = dateColor
        )

        val relativeTimeString =
            remember(dueDateInstant) { dueDateInstant.toRelativeTimeString(context) }
        Text(
            text = relativeTimeString,
            style = MaterialTheme.typography.labelSmall,
            color = relativeDateColor
        )
    }
}

@Composable
fun CategoryBadge(
    category: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primary
) {
    Badge(
        containerColor = containerColor,
        modifier = modifier
    ) {
        Text(
            text = category.ifEmpty { stringResource(R.string.tip_default_category) },
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun CheckButton(
    modifier: Modifier = Modifier,
    onChecked: () -> Unit = {}
) {
    val view = LocalView.current
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(VerveDoDefaults.Colors.Green)
            .clickable {
                VibrationUtils.performHapticFeedback(view)
                onChecked()
            }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_check),
            tint = Color.White,
            contentDescription = null,
            modifier = Modifier.padding(VerveDoDefaults.screenHorizontalPadding)
        )
    }
}

/*
@Preview
@Composable
private fun LongTaskCardPreview() {
    TaskCard(
        content = "这里有一条很长长长长长长长长长长长长长长长长长长长长长长的任务",
        category = "这里有一条很长长长长长长长长长长长长长长长长长长长长长长的分类",
        completed = false,
        dueDateInstant = 1787616000000,
        priority = Priority.NotImportant,
        selected = false
    )
}

@Preview
@Composable
private fun TaskCardPreview() {
    TaskCard(
        content = "这是一个任务",
        category = "它的分类",
        completed = true,
        dueDateInstant = 1787616000000,
        priority = Priority.NotImportant,
        selected = false
    )
}
*/
