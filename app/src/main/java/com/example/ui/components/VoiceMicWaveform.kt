package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.ExecutiveBlue
import com.example.ui.theme.ExecutiveBlueLight

@Composable
fun VoiceMicWaveform(
    isListening: Boolean,
    rmsLevel: Float,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 84.dp
) {
    val rippleScale1 = remember { Animatable(1f) }
    val rippleScale2 = remember { Animatable(1f) }

    LaunchedEffect(isListening) {
        if (isListening) {
            rippleScale1.animateTo(
                targetValue = 1.6f,
                animationSpec = infiniteRepeatable(
                    animation = tween(1200, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            rippleScale1.snapTo(1f)
        }
    }

    LaunchedEffect(isListening) {
        if (isListening) {
            rippleScale2.animateTo(
                targetValue = 1.35f,
                animationSpec = infiniteRepeatable(
                    animation = tween(900, delayMillis = 300, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                )
            )
        } else {
            rippleScale2.snapTo(1f)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .testTag("voice_mic_button")
            .size(size * 1.8f)
    ) {
        // Outer pulsing ripple ring
        if (isListening) {
            val audioBoost = (rmsLevel * 0.4f)
            Box(
                modifier = Modifier
                    .size(size)
                    .scale(rippleScale1.value + audioBoost)
                    .clip(CircleShape)
                    .background(ExecutiveBlueLight.copy(alpha = 0.18f))
            )

            // Inner pulsing ripple ring
            Box(
                modifier = Modifier
                    .size(size)
                    .scale(rippleScale2.value + audioBoost * 0.7f)
                    .clip(CircleShape)
                    .background(ExecutiveBlue.copy(alpha = 0.28f))
            )
        }

        // Main circular button
        val buttonColor = if (isListening) Color(0xFFDC2626) else ExecutiveBlue
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(buttonColor)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick
                )
        ) {
            Icon(
                imageVector = if (isListening) Icons.Default.Mic else Icons.Default.Mic,
                contentDescription = if (isListening) "Stop Listening" else "Start Speaking",
                tint = Color.White,
                modifier = Modifier.size(size * 0.48f)
            )
        }
    }
}
