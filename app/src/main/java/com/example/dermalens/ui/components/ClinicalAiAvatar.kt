package com.example.dermalens.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.dermalens.R

/**
 * DermaLens Clinical AI Avatar using custom vector drawable ic_ai_avatar.
 * Features the mint-to-green gradient orb with lens ring and optional online status dot.
 */
@Composable
fun ClinicalAiAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 34.dp,
    showOnlineDot: Boolean = false
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_ai_avatar),
            contentDescription = "Clinical AI Avatar",
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
        )

        // Active Clinical Online status dot
        if (showOnlineDot) {
            Box(
                modifier = Modifier
                    .size(size * 0.30f)
                    .align(Alignment.BottomEnd)
                    .background(Color(0xFF00E676), CircleShape)
                    .border(1.5.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
        }
    }
}
