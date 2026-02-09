package cn.super12138.todo.ui.pages.settings.components.appearance.darkmode

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.theme.shapeByInteraction
import cn.super12138.todo.utils.VibrationUtils

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun DarkModeItem(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    name: String,
    contentColor: Color,
    containerColor: Color,
    selected: Boolean,
    onSelect: () -> Unit,
    shapes: ButtonShapes = VerveDoDefaults.shapes()
) {
    val view = LocalView.current

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val animatedShape = shapeByInteraction(shapes, pressed, VerveDoDefaults.shapesDefaultAnimationSpec)

    val borderWidth by animateDpAsState(if (selected) 3.dp else (-1).dp)
    Column(
        modifier = modifier
            .clip(animatedShape)
            .clickable(
                interactionSource = interactionSource,
                role = Role.Button,
                onClick = {
                    VibrationUtils.performHapticFeedback(view)
                    onSelect()
                }
            )
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(MaterialTheme.shapes.large)
                .background(containerColor)
                .border(
                    width = borderWidth,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.large
                ),
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .size(30.dp)
                    .align(Alignment.Center)
            )
        }

        Spacer(Modifier.size(8.dp))

        Text(
            text = name,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}