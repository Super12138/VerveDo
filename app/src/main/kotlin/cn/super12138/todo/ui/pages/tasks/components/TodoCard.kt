package cn.super12138.todo.ui.pages.tasks.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import cn.super12138.todo.R
import cn.super12138.todo.logic.model.Priority
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.theme.shapeByInteraction
import cn.super12138.todo.utils.VibrationUtils
import cn.super12138.todo.utils.containerColor
import cn.super12138.todo.utils.disabledContainerColor
import cn.super12138.todo.utils.disabledContentColor
import cn.super12138.todo.utils.toLocalDateString
import cn.super12138.todo.utils.toRelativeTimeString

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TodoCard(
    modifier: Modifier = Modifier,
    // id: Int,
    content: String,
    category: String,
    completed: Boolean,
    dueDate: Long?,
    priority: Priority,
    selected: Boolean,
    onCardClick: () -> Unit = {},
    onCardLongClick: () -> Unit = {},
    onChecked: () -> Unit = {},
    shapes: ButtonShapes = VerveDoDefaults.shapes(),
) {
    val view = LocalView.current
    val context = LocalContext.current
    val cardColors = CardDefaults.cardColors(containerColor = VerveDoDefaults.Colors.Container)
    val animatedContainerColor by animateColorAsState(targetValue = if (selected) MaterialTheme.colorScheme.secondaryContainer else if (completed) cardColors.disabledContainerColor else cardColors.containerColor)

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val animatedShape = shapeByInteraction(
        shapes = shapes,
        pressed = if (selected) true else pressed,
        animationSpec = VerveDoDefaults.shapesDefaultAnimationSpec
    )

    val enterTransition = fadeIn(MaterialTheme.motionScheme.fastSpatialSpec()) + expandHorizontally(
        MaterialTheme.motionScheme.fastSpatialSpec()
    )
    val exitTransition = fadeOut(MaterialTheme.motionScheme.fastSpatialSpec()) + shrinkHorizontally(
        MaterialTheme.motionScheme.fastSpatialSpec()
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(VerveDoDefaults.toDoCardHeight)
            .clip(animatedShape)
            .combinedClickable(
                interactionSource = interactionSource,
                onClick = {
                    VibrationUtils.performHapticFeedback(view)
                    onCardClick()
                },
                // 不再需要使用：VibrationUtils.performHapticFeedback(view, HapticFeedbackConstants.LONG_PRESS)
                // 因为 combinedClickable 在更新的 Compose 里已经处理好了触感反馈
                onLongClick = onCardLongClick
            )
            .drawBehind {
                drawRect(animatedContainerColor)
            }
            .padding(start = VerveDoDefaults.screenHorizontalPadding)
    ) {
        AnimatedVisibility(
            visible = selected,
            enter = enterTransition,
            exit = exitTransition
        ) {
            Box(
                Modifier
                    .padding(end = 15.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondary)
                    .padding(5.dp)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_check),
                    tint = contentColorFor(MaterialTheme.colorScheme.secondary),
                    contentDescription = stringResource(R.string.tip_selected)
                )
            }
        }

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxSize()
        ) {
            CompositionLocalProvider(
                LocalContentColor provides if (completed) cardColors.disabledContentColor else cardColors.contentColor,
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = content,
                        style = MaterialTheme.typography.titleLarge,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(
                            start = VerveDoDefaults.screenVerticalPadding,
                            end = VerveDoDefaults.screenHorizontalPadding
                        )
                    ) {
                        Text(
                            text = dueDate.toLocalDateString(),
                            style = MaterialTheme.typography.labelLarge.copy(
                                color = if (completed) cardColors.disabledContentColor else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        )

                        Text(
                            text = dueDate.toRelativeTimeString(context),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (completed) cardColors.disabledContentColor else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Badge(containerColor = if (completed) disabledContainerColor() else MaterialTheme.colorScheme.primary) {
                        Text(
                            text = category.ifEmpty { stringResource(R.string.tip_default_category) },
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                    }

                    Text(
                        text = stringResource(priority.nameRes),
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (completed) disabledContentColor() else priority.containerColor()
                        ),
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = !selected && !completed,
            enter = enterTransition,
            exit = exitTransition
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxHeight()
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
    }
}

/*
@Preview(locale = "zh-rCN", showBackground = true)
@Composable
private fun TodoCardPreview() {
    TodoCard(
        content = "背《岳阳楼记》《出师表》《琵琶行》",
        subject = "语文",
        completed = false,
        priority = Priority.Important.value,
        selected = false,
        onCardClick = {},
        onCardLongClick = {},
        onChecked = {},
        sharedTransitionScope = ,
        animatedVisibilityScope =
    )
}*/
