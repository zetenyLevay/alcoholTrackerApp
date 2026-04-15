package com.example.alcoholtracker.ui.components.detailitemcomponents

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun DetailRow(){
     Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.height(32.dp)
    ) {
        Box(
            modifier = Modifier
                .width(8.dp)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(12.dp))
        )
        Text("Location & Context",
            fontSize = MaterialTheme.typography.titleMedium.fontSize,
            fontStyle = MaterialTheme.typography.titleMedium.fontStyle,
            fontWeight = FontWeight.Bold
        )
            }
}