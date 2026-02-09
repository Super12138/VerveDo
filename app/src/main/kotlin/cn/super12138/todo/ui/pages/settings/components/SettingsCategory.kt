package cn.super12138.todo.ui.pages.settings.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.super12138.todo.ui.VerveDoDefaults

@Composable
fun SettingsCategory(
    title: String,
    modifier: Modifier = Modifier,
    first: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                top = if (first) 0.dp else VerveDoDefaults.screenVerticalPadding,
                start = VerveDoDefaults.screenHorizontalPadding / 2,
                end = VerveDoDefaults.screenHorizontalPadding / 2,
                bottom = VerveDoDefaults.screenVerticalPadding
            )
    ) {
        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}