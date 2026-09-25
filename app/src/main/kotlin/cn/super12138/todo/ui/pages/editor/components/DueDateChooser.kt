package cn.super12138.todo.ui.pages.editor.components

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExposedDropdownMenu
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.SelectableDropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.setDisplayedMonth
import androidx.compose.material3.setSelectedDate
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cn.super12138.todo.R
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.utils.SystemUtils
import cn.super12138.todo.utils.VibrationUtils
import cn.super12138.todo.utils.toFormattedDate
import cn.super12138.todo.utils.toInstant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toJavaMonth
import kotlinx.datetime.toLocalDateTime
import java.time.LocalDate
import java.time.YearMonth
import kotlin.time.Duration.Companion.days
import kotlin.time.Instant

enum class DueDateSelection(@StringRes val labelRes: Int) {
    None(R.string.label_none),
    Today(R.string.time_today),
    Tomorrow(R.string.time_tomorrow),
    NextWeek(R.string.time_next_week),
    Customization(R.string.label_customization)
}

@Composable
fun DueDateChooser(
    instant: Instant?,
    onChange: (Instant?) -> Unit
) {
    val view = LocalView.current

    val datePickerState = rememberDatePickerState()
    val dueDateItems = DueDateSelection.entries.map { it }

    var openDialog by remember { mutableStateOf(false) }
    var menuExpanded by remember { mutableStateOf(false) }

    var setSelectedDate by rememberSaveable { mutableStateOf(false) }
    var selectedItem by rememberSaveable { mutableStateOf(DueDateSelection.None) }

    val confirmEnabled by remember { derivedStateOf { datePickerState.selectedDateMillis != null } }

    SideEffect(instant) {
        // @DeepSeek
        val today = SystemUtils.startOfUTCToday()
        val newSelection = when (instant) {
            null -> DueDateSelection.None
            else -> {
                when (instant) {
                    today -> DueDateSelection.Today
                    today + 1.days -> DueDateSelection.Tomorrow
                    today + 7.days -> DueDateSelection.NextWeek
                    else -> DueDateSelection.Customization
                }
            }
        }
        if (selectedItem != newSelection) {
            selectedItem = newSelection
        }
    }

    SideEffect(instant) {
        if (setSelectedDate || instant == null) return@SideEffect
        val date = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        datePickerState.setDisplayedMonth(YearMonth.of(date.year, date.month.toJavaMonth()))
        datePickerState.setSelectedDate(LocalDate.of(date.year, date.month.toJavaMonth(), date.day))
        setSelectedDate = true
    }

    ExposedDropdownMenu(
        expanded = menuExpanded,
        onExpandedChange = { menuExpanded = it },
        items = dueDateItems,
        selectedItem = selectedItem,
        onSelectedItemChange = {
            selectedItem = it
            val today = SystemUtils.startOfUTCToday()
            when (it) {
                DueDateSelection.None -> onChange(null)
                DueDateSelection.Today -> onChange(today)
                DueDateSelection.Tomorrow -> onChange((today + 1.days))
                DueDateSelection.NextWeek -> onChange((today + 7.days))
                DueDateSelection.Customization -> openDialog = true
            }
        },
        specificInstant = instant
    )

    if (openDialog) {
        DatePickerDialog(
            content = {
                DatePicker(
                    state = datePickerState,
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                )
            },
            confirmButton = {
                FilledTonalButton(
                    enabled = confirmEnabled,
                    onClick = {
                        VibrationUtils.performHapticFeedback(view)
                        onChange(datePickerState.selectedDateMillis?.toInstant())
                        openDialog = false
                    },
                    shapes = ButtonDefaults.shapes(),
                ) {
                    Text(stringResource(R.string.action_confirm))
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(VerveDoDefaults.contentPadding)) {
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
                            openDialog = false
                        },
                        shapes = ButtonDefaults.shapes()
                    ) {
                        Text(stringResource(R.string.action_cancel))
                    }
                }
            },
            onDismissRequest = { openDialog = false }
        )
    }
}

@Composable
private fun ExposedDropdownMenu(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    items: List<DueDateSelection>,
    selectedItem: DueDateSelection,
    onSelectedItemChange: (DueDateSelection) -> Unit,
    modifier: Modifier = Modifier,
    specificInstant: Instant? = null,
) {
    val view = LocalView.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            onExpandedChange(it)
            if (it) VibrationUtils.performHapticFeedback(view)
        },
        modifier = modifier
    ) {
        val selectedText = buildString {
            append(stringResource(selectedItem.labelRes))

            if (selectedItem == DueDateSelection.Customization) {
                specificInstant?.let {
                    append(" ")
                    append(it.toLocalDateTime(TimeZone.UTC).toFormattedDate())
                }
            }
        }
        TextField(
            value = selectedText,
            onValueChange = {},
            label = { Text(stringResource(R.string.label_due_date)) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            readOnly = true,
            singleLine = true,
            colors = ExposedDropdownMenuDefaults.textFieldColors(),
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            containerColor = MenuDefaults.groupStandardContainerColor,
            shape = MenuDefaults.standaloneGroupShape,
        ) {
            val optionCount = items.size
            items.forEachIndexed { index, option ->
                SelectableDropdownMenuItem(
                    shapes = MenuDefaults.itemShape(index, optionCount),
                    text = {
                        Text(
                            stringResource(option.labelRes),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    },
                    selected = option == selectedItem,
                    onClick = {
                        onExpandedChange(false)
                        onSelectedItemChange(option)
                        VibrationUtils.performHapticFeedback(view)
                    },
                    selectedLeadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.ic_check),
                            modifier = Modifier.size(MenuDefaults.LeadingIconSize),
                            contentDescription = null,
                        )
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
}
