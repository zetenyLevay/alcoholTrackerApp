package com.example.alcoholtracker.ui.components.detailitemcomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.ElevatedCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.alcoholtracker.R

@Composable
fun ImageCard(
    src: Int,
    title: String,
    description: String,

    ) {
    ElevatedCard() {
        Column() {
            Image(
                painter = painterResource(id = src),
                contentDescription = description,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview
@Composable
fun ImageCardPreview() {
    ImageCard(
        src = R.drawable.beer_icon,
        title = "Beer",
        description = "A glass of beer"
    )
}

