package cn.super12138.todo.ui.pages.tasks.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import cn.super12138.todo.R
import cn.super12138.todo.utils.VibrationUtils

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TodoSearchTextField(
    searchMode: Boolean,
    onSearchModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    textFieldState: TextFieldState,
) {
    val view = LocalView.current
    TextField(
        modifier = modifier.fillMaxWidth(),
        state = textFieldState,
        shape = CircleShape,
        placeholder = { Text(stringResource(R.string.action_search)) },
        lineLimits = TextFieldLineLimits.SingleLine,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
        leadingIcon = {
            IconButton(
                onClick = {
                VibrationUtils.performHapticFeedback(view)
                onSearchModeChange(!searchMode)
            }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_back),
                    contentDescription = stringResource(R.string.action_back)
                )
            }
        },
        trailingIcon = {
            AnimatedVisibility(
                visible = textFieldState.text.isNotBlank(),
                enter = fadeIn() + scaleIn(),
                exit = fadeOut() + scaleOut(),
            ) {
                IconButton(onClick = { textFieldState.setTextAndPlaceCursorAtEnd("") }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_close),
                        contentDescription = stringResource(R.string.action_clear)
                    )
                }
            }
        }
    )
}