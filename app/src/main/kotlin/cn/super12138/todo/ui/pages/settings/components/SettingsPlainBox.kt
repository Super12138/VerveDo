package cn.super12138.todo.ui.pages.settings.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import cn.super12138.todo.R
import cn.super12138.todo.ui.VerveDoDefaults

@Composable
fun SettingsPlainBox(
    text: String,
    modifier: Modifier = Modifier
) {
    val tipText = stringResource(R.string.tip_tips)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(
                vertical = VerveDoDefaults.settingsItemVerticalPadding,
                horizontal = VerveDoDefaults.settingsItemHorizontalPadding / 2
            ),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_info),
            contentDescription = null
        )
        Spacer(Modifier.size(20.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier
                .clearAndSetSemantics { contentDescription = "$tipText $text" }
        )
    }
}