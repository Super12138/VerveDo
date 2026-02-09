package cn.super12138.todo.ui.pages.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cn.super12138.todo.ui.VerveDoDefaults

@Composable
fun SettingsContainer(
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(VerveDoDefaults.settingsItemPadding),
        state = state,
        modifier = modifier
    ) {
        item {
            Spacer(modifier = Modifier.size(VerveDoDefaults.screenVerticalPadding))
        }

        content()

        item {
            Spacer(modifier = Modifier.size(VerveDoDefaults.screenVerticalPadding))
        }
    }
}