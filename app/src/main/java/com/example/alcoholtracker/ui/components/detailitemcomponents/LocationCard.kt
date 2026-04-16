package com.example.alcoholtracker.ui.components.detailitemcomponents

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NoteAlt
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.R
import com.example.compose.AlcoholTrackerTheme

@Composable
fun LocationCard(
    location: String,
    notes: String,
    recipient: String){

    val iconSize = 24.dp
    val iconIndent = 16.dp
    val total = iconIndent+iconSize

    Card(
        modifier = Modifier
            .fillMaxWidth()
            ,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )

    ) {
        Column(modifier = Modifier
            .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
            ) {
                Icon(Icons.Filled.LocationOn,
                    contentDescription = "Location",
                    modifier = Modifier
                        .size(iconSize))
                Spacer(modifier = Modifier.size(iconIndent))
                Text(text = location.ifEmpty { "No Location" },
                    fontWeight = if(location.isNotEmpty()){
                        FontWeight.Bold}
                    else{
                        FontWeight.Thin
                        },
                    fontStyle = if (location.isEmpty()) {
                        FontStyle.Italic
                    } else {
                        MaterialTheme.typography.headlineSmall.fontStyle
                    },
                )
            }
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier
                    .padding(start = total)
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                Image(
                painter = painterResource(R.drawable.map_placeholder),
                contentDescription = "Map",
                    contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
            )
            }
            Row(
            ) {
                Icon(Icons.Filled.People,
                    contentDescription = "Recipient",
                    modifier = Modifier
                        .size(iconSize))
                Spacer(modifier = Modifier.size(iconIndent))

                


                    Text(recipient.ifEmpty { "None" },
                    fontWeight = if(recipient.isNotEmpty()){
                        FontWeight.Bold}
                    else{
                        FontWeight.Thin
                        },
                    fontStyle = if (recipient.isEmpty()) {
                        FontStyle.Italic
                    } else {
                        MaterialTheme.typography.headlineSmall.fontStyle
                    },
                        fontSize = MaterialTheme.typography.labelLarge.fontSize
                )


            }
            HorizontalDivider(modifier = Modifier
                .padding(start = total))
            Row(
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Notes,
                    contentDescription = "Notes",
                    modifier = Modifier
                        .size(iconSize))
                Spacer(modifier = Modifier.size(iconIndent))

                Text(
                    text = notes.ifEmpty { "No notes" },
                    fontStyle = if (notes.isEmpty()) {
                        FontStyle.Italic
                    } else {
                        MaterialTheme.typography.bodyMedium.fontStyle
                    },
                )
            }
        }
    }
}

@Preview
@Composable
fun Preview(){
    AlcoholTrackerTheme {
        LocationCard(
            location = "Shamrock Irish Pub",
            notes = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum",
            recipient = "Paizs Levi"
        )
    }
}
