package com.example.alcoholtracker.ui.components.detailitemcomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.R
import com.example.compose.AlcoholTrackerTheme

@Composable
fun ImageCard(
    src: Int,
    name: String,
    description: String,
    abv: Double,
    category: String

    ) {

    ElevatedCard(
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 12.dp
        )
    ) {
        Box() {
            Image(
                painter = painterResource(id = src),
                contentDescription = description,
                modifier = Modifier.fillMaxWidth()
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart).padding(start = 12.dp, bottom = 8.dp)
            ) {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ){
                    TagLabel(
                        text = category,
                        backgroundColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    TagLabel(
                        text = "$abv% ABV",
                        backgroundColor = MaterialTheme.colorScheme.tertiaryContainer,
                        contentColor = MaterialTheme.colorScheme.onTertiaryContainer)
                }

                 Box {

                    Text(
                        text = name,
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            drawStyle = Stroke(
                                miter = 10f,
                                width = 4f,
                                join = StrokeJoin.Round 
                            )
                        ),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = name,
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}


@Composable
fun TagLabel(text: String, backgroundColor: Color, contentColor: Color) {
    Box(
        modifier = Modifier
            .background(color = backgroundColor, shape = RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text.uppercase(),
            color = contentColor,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview
@Composable
fun ImageCardPreview() {
    AlcoholTrackerTheme {
        ImageCard(
            src = R.drawable.beer_icon,
            name = "Green Diamonds",
            description = "A glass of beer",
            abv = 5.5,
            category = "Beer"
        )
    }
}

