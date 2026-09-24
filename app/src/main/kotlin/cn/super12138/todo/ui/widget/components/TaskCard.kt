package cn.super12138.todo.ui.widget.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.glance.ColorFilter
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.action
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.wrapContentHeight
import androidx.glance.text.Text
import androidx.glance.unit.ColorProvider
import cn.super12138.todo.R
import cn.super12138.todo.logic.model.Priority
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.utils.GlanceTypography
import cn.super12138.todo.utils.glanceContainerColor
import cn.super12138.todo.utils.toColorProvider
import cn.super12138.todo.utils.toFormattedDate
import cn.super12138.todo.utils.toRelativeTimeString

@Composable
fun GlanceTaskCard(
    content: String,
    category: String,
    isCompleted: Boolean,
    priority: Priority,
    dueDateMillis: Long?,
    modifier: GlanceModifier = GlanceModifier,
    showDueDate: Boolean = true,
    onChecked: () -> Unit = {}
) {
    val context = LocalContext.current

    val priorityText = remember(priority) { context.getString(priority.nameRes) }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(vertical = VerveDoDefaults.contentPadding / 2)
    ) {
        Column(
            verticalAlignment = Alignment.CenterVertically,
            modifier = GlanceModifier.defaultWeight()
        ) {
            Text(
                text = content,
                style = GlanceTypography.titleMedium,
                maxLines = 1
            )
            if (!isCompleted) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = GlanceModifier.fillMaxWidth()
                ) {
                    Text(
                        text = category,
                        style = GlanceTypography.labelMedium.copy(
                            color = GlanceTheme.colors.onSurfaceVariant
                        ),
                        maxLines = 1
                    )
                    Spacer(GlanceModifier.size(VerveDoDefaults.contentPadding))
                    Text(
                        text = priorityText,
                        style = GlanceTypography.labelMedium.copy(
                            color = priority.glanceContainerColor()
                        ),
                        maxLines = 1
                    )
                }
                if (showDueDate) {
                    dueDateMillis?.let {
                        DueDatePresenter(
                            dueDateMillis = it,
                            modifier = GlanceModifier.fillMaxWidth()
                        )
                    }
                }
            }
        }

        if (!isCompleted) {
            CheckButton { onChecked() }
        }
    }
}

@Composable
fun DueDatePresenter(
    dueDateMillis: Long?,
    modifier: GlanceModifier = GlanceModifier
) {
    val context = LocalContext.current

    val dueDate = remember(dueDateMillis) { dueDateMillis?.toFormattedDate() ?: "" }
    val relativeDueDate =
        remember(dueDateMillis) { dueDateMillis?.toRelativeTimeString(context) ?: "" }
    val combinedText = remember(dueDateMillis) {
        context.getString(R.string.label_slot_date_with_relative, dueDate, relativeDueDate)
    }

    Text(
        text = combinedText,
        style = GlanceTypography.labelMedium.copy(
            color = GlanceTheme.colors.onSurfaceVariant
        ),
        maxLines = 1,
        modifier = modifier
    )
}

@Composable
fun CheckButton(
    modifier: GlanceModifier = GlanceModifier,
    contentColor: ColorProvider = Color.White.toColorProvider(),
    backgroundColor: Color = VerveDoDefaults.Colors.Green,
    onChecked: () -> Unit = {}
) {
    val context = LocalContext.current
    val description = remember(context) { context.getString(R.string.tip_mark_completed) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .background(backgroundColor)
            .size(40.dp)
            .cornerRadius(100.dp)
            .padding(VerveDoDefaults.contentPadding / 2)
            .clickable(action(block = onChecked))
    ) {
        Image(
            provider = ImageProvider(R.drawable.ic_check),
            contentDescription = description,
            colorFilter = ColorFilter.tint(contentColor)
        )
    }
}