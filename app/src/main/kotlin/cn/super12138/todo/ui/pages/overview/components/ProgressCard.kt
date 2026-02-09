package cn.super12138.todo.ui.pages.overview.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cn.super12138.todo.R
import cn.super12138.todo.ui.VerveDoDefaults
import cn.super12138.todo.ui.components.EmptyTip
import cn.super12138.todo.ui.components.EmptyTipType
import cn.super12138.todo.ui.theme.fadeScale

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ProgressCard(
    modifier: Modifier = Modifier,
    title: String,
    total: Int,
    completed: Int,
    containerColor: Color = VerveDoDefaults.Colors.Container,
    emptyTipContainerColor: Color = MaterialTheme.colorScheme.secondaryContainer
) {
    val progress = if (total == 0) 0f else completed / total.toFloat()

    Card(
        modifier = modifier.height(VerveDoDefaults.overviewCardHeight * 2),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        shape = VerveDoDefaults.defaultShape
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(VerveDoDefaults.screenHorizontalPadding),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge
            )
            val transitionSpec = fadeScale()
            AnimatedContent(
                targetState = total == 0,
                transitionSpec = { transitionSpec }
            ) {
                if (it) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f) // 占满剩余空间
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        EmptyTip(
                            type = EmptyTipType.List,
                            containerColor = emptyTipContainerColor
                        )

                        Text(
                            text = stringResource(R.string.tip_no_task_brief),
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val animatedProgress by animateFloatAsState(
                            targetValue = progress,
                            animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
                        )
                        val thickStrokeWidth = with(LocalDensity.current) { 5.dp.toPx() }
                        val thickStroke = remember(thickStrokeWidth) {
                            Stroke(width = thickStrokeWidth, cap = StrokeCap.Round)
                        }

                        CircularWavyProgressIndicator(
                            progress = { animatedProgress },
                            waveSpeed = 3.dp,
                            wavelength = 20.dp,
                            stroke = thickStroke,
                            trackStroke = thickStroke,
                            modifier = Modifier
                                .padding(VerveDoDefaults.screenHorizontalPadding)
                                .size(90.dp)
                        )

                        Text(
                            text = "$completed / $total",
                            style = MaterialTheme.typography.labelLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}