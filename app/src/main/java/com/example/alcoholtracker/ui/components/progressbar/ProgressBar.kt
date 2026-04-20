package com.example.alcoholtracker.ui.components.progressbar

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.AlcoholTrackerTheme

@Composable
fun ProgressBar(
    progress: Float,
    primaryText: String,
    secondaryText: String,
    onEditClick: () -> Unit,
){

    Box(
        modifier = Modifier.fillMaxWidth()
    ){
        IconButton(
            onClick = onEditClick
        ){
            Icon(
                Icons.Default.Edit,
                contentDescription = "Edit",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = primaryText,
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = secondaryText,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            ProgressBarGradient(
                progress = progress,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }

}

@Composable
fun ProgressBarGradient(
    progress: Float,
    modifier: Modifier = Modifier
){
    val safeProgress = progress.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp)
            .clip(RoundedCornerShape(50.dp))
            .border(
                width = 4.dp,
                brush = Brush.linearGradient(
                    listOf(
                        Color(0xFFFFD54F),
                        Color(0xFFF57C00),
                        Color(0xFFB71C1C)
                    )
                ),
                shape = RoundedCornerShape(50.dp)
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.horizontalGradient(
                        listOf(
                            Color(0xFFFFD54F),
                            Color(0xFFF57C00),
                            Color(0xFFB71C1C)
                        )
                    )
                )
        )
        if (safeProgress < 1f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(1f - safeProgress)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .align(Alignment.CenterEnd)
            )
        }
    }
}
