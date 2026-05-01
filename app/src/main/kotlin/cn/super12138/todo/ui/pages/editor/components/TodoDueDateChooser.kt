package cn.super12138.todo.ui.pages.editor.components

import android.util.Log
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.setDisplayedMonth
import androidx.compose.material3.setSelectedDate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.super12138.todo.R
import cn.super12138.todo.utils.VibrationUtils
import cn.super12138.todo.utils.toLocalDate
import cn.super12138.todo.utils.toLocalDateString
import java.time.YearMonth

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun TodoDueDateChooser(
    dateMillis: Long?,
    onDateChange: (Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    val datePickerState = rememberDatePickerState()
    val openDialog = remember { mutableStateOf(false) }

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    LaunchedEffect(pressed) {
        if (pressed) {
            VibrationUtils.performHapticFeedback(view)
            openDialog.value = true
            // 时间戳转LocalDate
            val date = dateMillis?.toLocalDate()
            datePickerState.apply {
                setSelectedDate(date)
                date?.let { setDisplayedMonth(YearMonth.of(it.year, date.month)) }
            }
            Log.d(
                "Editor",
                "DatePicker: getTime: $dateMillis, stateTime: ${datePickerState.selectedDateMillis}"
            )
        }
    }

    TextField(
        value = dateMillis.toLocalDateString(),
        onValueChange = {},
        label = { Text(stringResource(R.string.label_due_date)) },
        readOnly = true,
        interactionSource = interactionSource,
        modifier = modifier
    )


    /*val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val animatedShape = shapeByInteraction(
        ButtonDefaults.shapes(),
        pressed,
        TodoDefaults.shapesDefaultAnimationSpec
    )*/
    /*Surface(
        modifier = modifier
            .semantics { role = Role.Button }
            .weight(1f),
        color = ButtonDefaults.filledTonalButtonColors().containerColor,
        shape = animatedShape
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(
                    minWidth = ButtonDefaults.MinWidth,
                    minHeight = ButtonDefaults.MinHeight,
                )
                .combinedClickable(
                    interactionSource = interactionSource,
                    onClick = {
                        VibrationUtils.performHapticFeedback(view)
                        openDialog = true
                    },
                    onLongClick = { onValueChange(null) }
                )
                .padding(ButtonDefaults.ContentPadding),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "选择",
                style = MaterialTheme.typography.labelLarge
            )
        }
    }*/


    if (openDialog.value) {
        DatePickerDialog(
            confirmButton = {
                FilledTonalButton(
                    onClick = {
                        VibrationUtils.performHapticFeedback(view)
                        onDateChange(datePickerState.selectedDateMillis)
                        openDialog.value = false
                    },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(
                        onClick = {
                            VibrationUtils.performHapticFeedback(view)
                            datePickerState.selectedDateMillis = null
                        },
                        shapes = ButtonDefaults.shapes()
                    ) {
                        Text(stringResource(R.string.action_clear))
                    }
                    TextButton(
                        onClick = {
                            VibrationUtils.performHapticFeedback(view)
                            openDialog.value = false
                        },
                        shapes = ButtonDefaults.shapes()
                    ) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            },
            onDismissRequest = {
                openDialog.value = false
            }
        ) {
            DatePicker(
                state = datePickerState,
                modifier = Modifier.verticalScroll(rememberScrollState()),
            )
        }
    }
}