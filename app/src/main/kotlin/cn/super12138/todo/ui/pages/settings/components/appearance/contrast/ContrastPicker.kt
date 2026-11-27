package cn.super12138.todo.ui.pages.settings.components.appearance.contrast

import androidx.compose.foundation.layout.FlexAlignContent
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexDirection
import androidx.compose.foundation.layout.FlexJustifyContent
import androidx.compose.foundation.layout.FlexWrap
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import cn.super12138.todo.R
import cn.super12138.todo.logic.model.ContrastLevel
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.pages.settings.components.MoreContentSettingsItem
import cn.super12138.todo.utils.VibrationUtils
import cn.super12138.todo.utils.toggleButtonShapesIn

@Composable
fun ContrastPicker(
    currentContrast: ContrastLevel,
    onContrastChange: (ContrastLevel) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val contrastLevelList = ContrastLevel.entries

    MoreContentSettingsItem(
        title = stringResource(R.string.pref_contrast_level),
        description = stringResource(R.string.pref_contrast_level_desc),
        modifier = modifier
    ) {
        FlexBox(
            modifier = Modifier.fillMaxWidth(),
            config = {
                direction(FlexDirection.Row)
                justifyContent(FlexJustifyContent.Start)
                alignContent(FlexAlignContent.Start)
                wrap(FlexWrap.Wrap)
                rowGap(ButtonGroupDefaults.ConnectedSpaceBetween)
                columnGap(VerveDoDefaults.contentPadding / 4)
            }
        ) {
            contrastLevelList.forEachIndexed { index, contrastLevel ->
                ToggleButton(
                    content = { Text(stringResource(contrastLevel.nameRes)) },
                    checked = currentContrast == contrastLevel,
                    onCheckedChange = {
                        onContrastChange(contrastLevel)
                        VibrationUtils.performHapticFeedback(view)
                    },
                    shapes = index toggleButtonShapesIn contrastLevelList,
                    colors = VerveDoDefaults.toggleButtonColors,
                    modifier = Modifier.semantics { role = Role.RadioButton }
                )
            }
        }
    }
}
