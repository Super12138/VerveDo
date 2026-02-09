package cn.super12138.todo.ui.pages.overview.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.ButtonShapes
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorProducer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.theme.shapeByInteraction

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RoundedCornerCardLarge(
    modifier: Modifier = Modifier,
    @DrawableRes iconRes: Int,
    title: String,
    count: Int,
    containerColor: Color = VerveDoDefaults.Colors.Container,
    shapes: ButtonShapes = VerveDoDefaults.shapes(),
    onClick: () -> Unit = {}
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val animatedShape = shapeByInteraction(shapes, pressed, VerveDoDefaults.shapesDefaultAnimationSpec)

    val cardColors = CardDefaults.cardColors(containerColor = containerColor)
    Card(
        modifier = modifier.height(VerveDoDefaults.overviewCardHeight),
        colors = cardColors,
        shape = animatedShape
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable(
                    enabled = true,
                    onClick = onClick,
                    interactionSource = interactionSource
                )
                .padding(VerveDoDefaults.screenHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.size(30.dp)
            )
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(vertical = 8.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium
                )

                BasicText(
                    text = count.toString(),
                    style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                    color = ColorProducer { cardColors.contentColor },
                    autoSize = TextAutoSize.StepBased(
                        MaterialTheme.typography.headlineSmall.fontSize,
                        MaterialTheme.typography.displayMedium.fontSize
                    ),
                )
            }
        }
    }
}