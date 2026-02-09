package cn.super12138.todo.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.super12138.todo.ui.TodoDefaults

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RoundedScreenContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.padding(horizontal = TodoDefaults.screenHorizontalPadding),
        color = TodoDefaults.Colors.Background,
        shape = TodoDefaults.ScreenContainerShape,
        content = content
    )
}