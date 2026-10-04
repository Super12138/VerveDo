package cn.super12138.todo.ui.pages.tasks.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import cn.super12138.todo.R
import cn.super12138.todo.utils.VibrationUtils

@Composable
fun TaskSearchTextField(
    value: String,
    onValueChange: (String) -> Unit,
    onExitSearchMode: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isTrailingIconVisible by remember { derivedStateOf { value.isNotEmpty() } }

    TextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = { LeadingIcon(onClick = onExitSearchMode) },
        placeholder = { Text(stringResource(R.string.action_search)) },
        trailingIcon = {
            AnimatedVisibility(
                visible = isTrailingIconVisible,
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                TrailingIcon(onClick = { onValueChange("") })
            }
        },
        maxLines = 1,
        shape = CircleShape,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        modifier = modifier
    )
}

@Composable
private fun LeadingIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    IconButton(
        modifier = modifier,
        onClick = {
            VibrationUtils.performHapticFeedback(view)
            onClick()
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_arrow_back),
            contentDescription = stringResource(R.string.action_back)
        )
    }
}

@Composable
private fun TrailingIcon(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    IconButton(
        modifier = modifier,
        onClick = {
            VibrationUtils.performHapticFeedback(view)
            onClick()
        }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_close),
            contentDescription = stringResource(R.string.action_clear)
        )
    }
}
