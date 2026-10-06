package cn.super12138.todo.ui.pages.settings.components.appearance.palette

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import cn.super12138.todo.logic.model.ContrastLevel
import cn.super12138.todo.logic.model.PaletteStyle
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.theme.animateColorScheme
import cn.super12138.todo.ui.theme.rememberDynamicColorScheme
import cn.super12138.todo.ui.theme.shapeByInteraction
import cn.super12138.todo.utils.VibrationUtils
import cn.super12138.todo.utils.keyColorBasedOnDynamicColor
import com.kyant.m3color.dynamiccolor.ColorSpec

@Composable
fun PaletteItem(
    isDynamicColor: Boolean,
    isDark: Boolean,
    pureBlackMode: Boolean,
    paletteStyle: PaletteStyle,
    contrastLevel: ContrastLevel,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onSelect: () -> Unit = {},
    shapes: ButtonShapes = VerveDoDefaults.shapes
) {
    val view = LocalView.current

    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()

    val animatedShape =
        shapeByInteraction(shapes, pressed, VerveDoDefaults.shapesDefaultAnimationSpec)
    val colorScheme = rememberDynamicColorScheme(
        keyColor = isDynamicColor.keyColorBasedOnDynamicColor(),
        isDark = isDark,
        contrastLevel = contrastLevel.value.toDouble(),
        pureBlack = pureBlackMode,
        style = paletteStyle,
        specVersion = ColorSpec.SpecVersion.SPEC_2025
    )
    val animatedColorScheme = animateColorScheme(colorScheme)

    val colors = listOf(
        animatedColorScheme.primary,
        animatedColorScheme.secondary,
        animatedColorScheme.tertiary,
        animatedColorScheme.tertiaryContainer,
        animatedColorScheme.secondaryContainer,
        animatedColorScheme.primaryContainer,
    )

    Surface(
        onClick = {
            VibrationUtils.performHapticFeedback(view)
            onSelect()
        },
        modifier = modifier.semantics { role = Role.Button },
        shape = animatedShape,
        color = VerveDoDefaults.Colors.Container,
        interactionSource = interactionSource,
    ) {
        Column(
            modifier = Modifier
                .width(90.dp)
                .padding(VerveDoDefaults.contentPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            ColorPreview(
                colors = colors,
                selected = selected,
                borderColor = animatedColorScheme.primary
            )

            Spacer(Modifier.size(VerveDoDefaults.contentPadding))

            Text(
                text = stringResource(paletteStyle.nameRes),
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun ColorPreview(
    colors: List<Color>,
    selected: Boolean,
    borderColor: Color,
    modifier: Modifier = Modifier
) {
    val borderWidth by animateDpAsState(if (selected) 3.dp else (-1).dp)
    Column(
        modifier = modifier
            .width(70.dp)
            .clip(MaterialTheme.shapes.large)
            .border(
                width = borderWidth,
                color = borderColor,
                shape = MaterialTheme.shapes.large
            ),
        verticalArrangement = Arrangement.spacedBy(VerveDoDefaults.contentPadding / 4)
    ) {
        colors.fastForEach {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(it)
            )
        }
    }
}