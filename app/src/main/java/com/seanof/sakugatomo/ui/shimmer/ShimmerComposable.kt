package com.seanof.sakugatomo.ui.shimmer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun ShimmerItem(
    brush: Brush
) {
    Column(modifier = Modifier.padding(2.dp)) {
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .size(170.dp)
                .background(brush = brush)
        )
    }
}