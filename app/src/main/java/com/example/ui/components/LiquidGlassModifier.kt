package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassTealBloom
import com.example.ui.theme.InterFont
import com.example.ui.theme.Primary
import com.example.ui.theme.Secondary

val GlassBorderBrush = Brush.linearGradient(
    0.0f to Color(0x38FFFFFF),
    0.4f to Color(0x12FFFFFF),
    0.85f to Color(0x287EE0D2),
    1.0f to Color(0x15CCBEFF),
    start = Offset(0f, 0f),
    end = Offset(400f, 600f)
)

val ElevatedGlassBorderBrush = Brush.linearGradient(
    0.0f to Color(0x55FFFFFF),
    0.35f to Color(0x18FFFFFF),
    0.75f to Color(0x407EE0D2),
    1.0f to Color(0x25CCBEFF)
)

val LuminousPillBorderBrush = Brush.linearGradient(
    listOf(
        Color(0x907EE0D2),
        Color(0x40FFFFFF),
        Color(0x707EE0D2)
    )
)

fun Modifier.liquidGlass(
    shape: Shape = RoundedCornerShape(24.dp),
    backgroundColor: Color = Color(0x80131B2A),
    borderBrush: Brush = GlassBorderBrush,
    borderWidth: Dp = 1.dp
): Modifier = this
    .clip(shape)
    .background(backgroundColor, shape)
    .border(borderWidth, borderBrush, shape)
    .drawBehind {
        // Subtle top specular highlight refraction
        drawLine(
            brush = Brush.horizontalGradient(
                0.0f to Color.Transparent,
                0.2f to Color(0x35FFFFFF),
                0.5f to Color(0x50FFFFFF),
                0.8f to Color(0x35FFFFFF),
                1.0f to Color.Transparent
            ),
            start = Offset(16.dp.toPx(), 1.dp.toPx()),
            end = Offset(size.width - 16.dp.toPx(), 1.dp.toPx()),
            strokeWidth = 1.5f
        )
    }

fun Modifier.liquidElevatedGlass(
    shape: Shape = RoundedCornerShape(28.dp),
    backgroundColor: Color = Color(0xB3182438)
): Modifier = this.liquidGlass(
    shape = shape,
    backgroundColor = backgroundColor,
    borderBrush = ElevatedGlassBorderBrush,
    borderWidth = 1.2.dp
)

@Composable
fun LiquidPillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isPrimary: Boolean = true,
    testTag: String = "liquid_pill_button",
    leadingIcon: (@Composable () -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val glowAlpha by animateFloatAsState(
        targetValue = if (isPressed) 0.5f else 0.22f,
        animationSpec = tween(200),
        label = "pill_glow"
    )

    val shape = RoundedCornerShape(9999.dp)
    val baseBg = if (isPrimary) {
        Color(0x337EE0D2) // 20% teal
    } else {
        Color(0x20FFFFFF) // 12% white
    }
    val contentColor = if (isPrimary) Color(0xFFE6FFFA) else Color(0xFFC4B5FD)

    Box(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minHeight = 48.dp)
            .clip(shape)
            .drawBehind {
                if (isPrimary) {
                    drawCircle(
                        color = GlassTealBloom.copy(alpha = glowAlpha),
                        radius = size.maxDimension * 0.7f,
                        center = center
                    )
                }
            }
            .background(baseBg, shape)
            .border(
                1.dp,
                if (isPrimary) LuminousPillBorderBrush else Brush.linearGradient(listOf(Color(0x30FFFFFF), Color(0x15FFFFFF))),
                shape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 24.dp, vertical = 13.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            leadingIcon?.invoke()
            Text(
                text = text,
                color = contentColor,
                fontFamily = InterFont,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                letterSpacing = 0.05.sp
            )
        }
    }
}

@Composable
fun LiquidChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    testTag: String = "liquid_chip"
) {
    val shape = RoundedCornerShape(9999.dp)
    val bg = if (selected) Color(0x38C4B5FD) else Color(0x14FFFFFF)
    val borderBrush = if (selected) {
        Brush.horizontalGradient(listOf(Secondary, Primary))
    } else {
        Brush.horizontalGradient(listOf(Color(0x20FFFFFF), Color(0x10FFFFFF)))
    }
    val textColor = if (selected) Primary else Color(0xFFBDC9C6)

    Box(
        modifier = modifier
            .testTag(testTag)
            .defaultMinSize(minHeight = 40.dp)
            .clip(shape)
            .background(bg, shape)
            .border(1.dp, borderBrush, shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 9.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontFamily = InterFont,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            fontSize = 13.sp,
            letterSpacing = 0.04.sp
        )
    }
}
