package cn.super12138.todo.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import cn.super12138.todo.utils.blend
import nl.dionsegijn.konfetti.compose.KonfettiView
import nl.dionsegijn.konfetti.compose.OnParticleSystemUpdateListener
import nl.dionsegijn.konfetti.core.Angle
import nl.dionsegijn.konfetti.core.Party
import nl.dionsegijn.konfetti.core.PartySystem
import nl.dionsegijn.konfetti.core.Position
import nl.dionsegijn.konfetti.core.Spread
import nl.dionsegijn.konfetti.core.emitter.Emitter
import java.util.concurrent.TimeUnit

/**
 * 参照于：https://github.com/hushenghao/AndroidEasterEggs/blob/main/app/src/main/java/com/dede/android_eggs/views/main/compose/Konfetti.kt
 */

@Composable
fun Konfetti(
    modifier: Modifier = Modifier,
    state: MutableState<Boolean>,
    primary: Color = MaterialTheme.colorScheme.primary
) {
    var visible by state
    if (!visible) {
        return
    }
    val listener = remember(state) {
        object : OnParticleSystemUpdateListener {
            override fun onParticleSystemEnded(system: PartySystem, activeSystems: Int) {
                if (activeSystems == 0)
                    visible = false
            }
        }
    }
    KonfettiView(
        modifier = modifier
        /*.fillMaxSize()
        .then(modifier)*/,
        parties = remember { particles(primary.toArgb()) },
        updateListener = listener
    )
}

private val defaultColors = listOf(
    0xFCE18A,
    0x009688,
    0xFF726D,
    0xF4306D,
    0xB48DEF,
    0x95FF82,
    0x82ECFF,
    0xFF9800,
    0x0E008A,
)

private const val colorBlendFraction = 0.3f

private fun particles(primary: Int) = listOf(
    Party(
        speed = 0f,
        maxSpeed = 12f,
        damping = 0.9f,
        angle = Angle.BOTTOM,
        spread = Spread.ROUND,
        colors = defaultColors.map { it.blend(primary, colorBlendFraction) },
        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
        position = Position.Relative(0.0, 0.0).between(Position.Relative(1.0, 0.0)),
    ),
    Party(
        speed = 10f,
        maxSpeed = 30f,
        damping = 0.9f,
        angle = Angle.RIGHT - 55,
        spread = 60,
        colors = defaultColors.map { it.blend(primary, colorBlendFraction) },
        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
        position = Position.Relative(0.0, 1.0)
    ),
    Party(
        speed = 10f,
        maxSpeed = 30f,
        damping = 0.9f,
        angle = Angle.RIGHT - 125,
        spread = 60,
        colors = defaultColors.map { it.blend(primary, colorBlendFraction) },
        emitter = Emitter(duration = 2, TimeUnit.SECONDS).perSecond(100),
        position = Position.Relative(1.0, 1.0)
    )
)
