package com.example.hangmangame.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.hangmangame.theme.NeonIndigo
import com.example.hangmangame.theme.NeonRed
import com.example.hangmangame.theme.DarkBorder

@Composable
fun HangmanCanvas(
    incorrectGuessesCount: Int,
    modifier: Modifier = Modifier
) {
    // Animatable progress for each hangman part
    val animHead = remember { Animatable(0f) }
    val animBody = remember { Animatable(0f) }
    val animLeftArm = remember { Animatable(0f) }
    val animRightArm = remember { Animatable(0f) }
    val animLeftLeg = remember { Animatable(0f) }
    val animRightLeg = remember { Animatable(0f) }

    LaunchedEffect(incorrectGuessesCount) {
        if (incorrectGuessesCount == 0) {
            animHead.snapTo(0f)
            animBody.snapTo(0f)
            animLeftArm.snapTo(0f)
            animRightArm.snapTo(0f)
            animLeftLeg.snapTo(0f)
            animRightLeg.snapTo(0f)
        } else {
            if (incorrectGuessesCount >= 1 && animHead.value == 0f) animHead.animateTo(1f, tween(350))
            if (incorrectGuessesCount >= 2 && animBody.value == 0f) animBody.animateTo(1f, tween(350))
            if (incorrectGuessesCount >= 3 && animLeftArm.value == 0f) animLeftArm.animateTo(1f, tween(350))
            if (incorrectGuessesCount >= 4 && animRightArm.value == 0f) animRightArm.animateTo(1f, tween(350))
            if (incorrectGuessesCount >= 5 && animLeftLeg.value == 0f) animLeftLeg.animateTo(1f, tween(350))
            if (incorrectGuessesCount >= 6 && animRightLeg.value == 0f) animRightLeg.animateTo(1f, tween(350))
        }
    }

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(260.dp)
    ) {
        val w = size.width
        val h = size.height

        val strokeWidthGallows = 8.dp.toPx()
        val strokeWidthMan = 6.dp.toPx()

        // Gallows dimensions
        val baseXStart = w * 0.15f
        val baseXEnd = w * 0.55f
        val baseY = h * 0.9f

        val poleX = w * 0.32f
        val poleYStart = h * 0.08f

        val beamXEnd = w * 0.70f
        val ropeYEnd = h * 0.22f

        // Draw Gallows (persistent background)
        // 1. Base
        drawLine(
            color = DarkBorder,
            start = Offset(baseXStart, baseY),
            end = Offset(baseXEnd, baseY),
            strokeWidth = strokeWidthGallows,
            cap = StrokeCap.Round
        )

        // 2. Vertical Pole
        drawLine(
            color = NeonIndigo,
            start = Offset(poleX, baseY),
            end = Offset(poleX, poleYStart),
            strokeWidth = strokeWidthGallows,
            cap = StrokeCap.Round
        )

        // 3. Top Horizontal Beam
        drawLine(
            color = NeonIndigo,
            start = Offset(poleX - (strokeWidthGallows / 2), poleYStart),
            end = Offset(beamXEnd, poleYStart),
            strokeWidth = strokeWidthGallows,
            cap = StrokeCap.Round
        )

        // 4. Diagonal Support Brace
        drawLine(
            color = NeonIndigo,
            start = Offset(poleX, poleYStart + h * 0.18f),
            end = Offset(poleX + w * 0.12f, poleYStart),
            strokeWidth = strokeWidthGallows,
            cap = StrokeCap.Round
        )

        // 5. Rope
        drawLine(
            color = NeonIndigo,
            start = Offset(beamXEnd, poleYStart),
            end = Offset(beamXEnd, ropeYEnd),
            strokeWidth = 4.dp.toPx(),
            cap = StrokeCap.Round
        )

        // Hangman Dimensions (Centered on beamXEnd)
        val manX = beamXEnd
        val headRadius = h * 0.07f
        val headCenterY = ropeYEnd + headRadius
        val bodyStartY = ropeYEnd + (headRadius * 2)
        val bodyLength = h * 0.22f
        val bodyEndY = bodyStartY + bodyLength

        val armStartY = bodyStartY + (bodyLength * 0.2f)
        val armWidth = w * 0.10f
        val armHeight = h * 0.08f

        val legWidth = w * 0.09f
        val legHeight = h * 0.14f

        // Draw Hangman parts sequentially
        // 1. Head (sweep arc drawing)
        if (animHead.value > 0f) {
            drawArc(
                color = NeonRed,
                startAngle = -90f,
                sweepAngle = 360f * animHead.value,
                useCenter = false,
                topLeft = Offset(manX - headRadius, ropeYEnd),
                size = Size(headRadius * 2, headRadius * 2),
                style = Stroke(width = strokeWidthMan, cap = StrokeCap.Round)
            )
        }

        // 2. Torso/Body (line drawing)
        if (animBody.value > 0f) {
            drawLine(
                color = NeonRed,
                start = Offset(manX, bodyStartY),
                end = Offset(manX, bodyStartY + (bodyLength * animBody.value)),
                strokeWidth = strokeWidthMan,
                cap = StrokeCap.Round
            )
        }

        // 3. Left Arm
        if (animLeftArm.value > 0f) {
            val progress = animLeftArm.value
            drawLine(
                color = NeonRed,
                start = Offset(manX, armStartY),
                end = Offset(manX - (armWidth * progress), armStartY + (armHeight * progress)),
                strokeWidth = strokeWidthMan,
                cap = StrokeCap.Round
            )
        }

        // 4. Right Arm
        if (animRightArm.value > 0f) {
            val progress = animRightArm.value
            drawLine(
                color = NeonRed,
                start = Offset(manX, armStartY),
                end = Offset(manX + (armWidth * progress), armStartY + (armHeight * progress)),
                strokeWidth = strokeWidthMan,
                cap = StrokeCap.Round
            )
        }

        // 5. Left Leg
        if (animLeftLeg.value > 0f) {
            val progress = animLeftLeg.value
            drawLine(
                color = NeonRed,
                start = Offset(manX, bodyEndY),
                end = Offset(manX - (legWidth * progress), bodyEndY + (legHeight * progress)),
                strokeWidth = strokeWidthMan,
                cap = StrokeCap.Round
            )
        }

        // 6. Right Leg
        if (animRightLeg.value > 0f) {
            val progress = animRightLeg.value
            drawLine(
                color = NeonRed,
                start = Offset(manX, bodyEndY),
                end = Offset(manX + (legWidth * progress), bodyEndY + (legHeight * progress)),
                strokeWidth = strokeWidthMan,
                cap = StrokeCap.Round
            )
        }
    }
}
