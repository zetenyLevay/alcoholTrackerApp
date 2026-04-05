package com.example.alcoholtracker.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun DrinkBanner(drinkCount: Int){
    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .height(40.dp),
                        verticalAlignment = Alignment.CenterVertically

                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(
                                "Tonight's Drinks ($drinkCount)",
                                color = MaterialTheme.colorScheme.onPrimary
                            )

                        }

                    }
}

@Preview
@Composable
fun PreviewDrinkBanner(){
    DrinkBanner(4)
}