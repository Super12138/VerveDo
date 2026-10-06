package cn.super12138.todo.ui.pages.settings.components.appearance.palette

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import cn.super12138.todo.R
import cn.super12138.todo.logic.model.ContrastLevel
import cn.super12138.todo.logic.model.DarkMode
import cn.super12138.todo.logic.model.PaletteStyle
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.pages.settings.components.LazyRowSettingsItem
import cn.super12138.todo.utils.isDark

@Composable
fun PalettePicker(
    currentPalette: PaletteStyle,
    onPaletteChange: (paletteStyle: PaletteStyle) -> Unit,
    isDynamicColor: Boolean,
    darkMode: DarkMode,
    pureBlackMode: Boolean,
    contrastLevel: ContrastLevel,
    modifier: Modifier = Modifier
) {
    LazyRowSettingsItem(
        title = stringResource(R.string.pref_palette_style),
        description = stringResource(R.string.pref_palette_style_desc),
        horizontalArrangement = Arrangement.spacedBy(VerveDoDefaults.contentPadding / 2),
        modifier = modifier
    ) {
        items(items = PaletteStyle.entries, key = { it.id }) {
            PaletteItem(
                isDynamicColor = isDynamicColor,
                isDark = darkMode.isDark(),
                paletteStyle = it,
                selected = currentPalette == it,
                contrastLevel = contrastLevel,
                pureBlackMode = pureBlackMode,
                onSelect = { onPaletteChange(it) }
            )
        }
    }
}