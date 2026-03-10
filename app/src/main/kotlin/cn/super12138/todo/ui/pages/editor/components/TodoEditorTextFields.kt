package cn.super12138.todo.ui.pages.editor.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cn.super12138.todo.R

@Composable
fun TodoContentTextField(
    state: TextFieldState,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    TextField(
        state = state,
        label = { Text(stringResource(R.string.placeholder_add_todo)) },
        lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 3),
        isError = isError,
        supportingText = {
            AnimatedVisibility(
                visible = isError,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Text(stringResource(R.string.error_no_content_entered))
            }
        },
        modifier = modifier
    )
}

@Composable
fun TodoCategoryTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    supportingText: String = stringResource(R.string.tip_short_category),
    modifier: Modifier = Modifier
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(R.string.label_enter_category_name)) },
        isError = isError,
        supportingText = { Text(supportingText) },
        maxLines = 1,
        modifier = modifier
    )
}