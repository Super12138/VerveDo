package cn.super12138.todo.ui.pages.editor.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import cn.super12138.todo.R

@Composable
fun TaskContentTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    isError: Boolean
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
                Text(
                    text = stringResource(R.string.error_no_content_entered),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        modifier = modifier
    )
}

@Composable
fun TaskCategoryTextField(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    isError: Boolean
) {
    val enterTransition = fadeIn(
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
    ) + scaleIn(
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )

    val exitTransition = fadeOut(
        animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
    ) + scaleOut(
        animationSpec = MaterialTheme.motionScheme.fastSpatialSpec()
    )

    TextField(
        state = state,
        label = { Text(stringResource(R.string.label_enter_category_name)) },
        isError = isError,
        supportingText = {
            AnimatedContent(
                targetState = isError,
                transitionSpec = { enterTransition togetherWith exitTransition }
            ) { error ->
                Text(
                    text = if (error) {
                        stringResource(R.string.error_no_content_entered)
                    } else {
                        stringResource(R.string.tip_short_category)
                    },
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        lineLimits = TextFieldLineLimits.SingleLine,
        modifier = modifier
    )
}