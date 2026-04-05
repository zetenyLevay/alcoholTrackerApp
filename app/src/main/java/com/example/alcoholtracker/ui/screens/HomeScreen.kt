package com.example.alcoholtracker.ui.screens


import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.ui.components.AddButton
import com.example.alcoholtracker.ui.components.AlcoholListType
import com.example.alcoholtracker.ui.components.DrinkBanner
import com.example.alcoholtracker.ui.components.HomeTopBar
import com.example.alcoholtracker.ui.components.alcohollist.AlcoholListComposable
import com.example.alcoholtracker.ui.components.progressbar.AmountProgressBar
import com.example.alcoholtracker.ui.components.progressbar.CountProgressBar
import com.example.alcoholtracker.ui.components.progressbar.MoneyProgressBar
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarEditDialog
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarInterface
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarType
import com.example.alcoholtracker.ui.viewmodel.AuthViewModel
import com.example.alcoholtracker.ui.viewmodel.ProgressBarViewModel
import com.example.alcoholtracker.ui.viewmodel.UserAndUserDrinkLogViewModel

@Composable
fun HomeScreen(
    onFABClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    progressBarViewModel: ProgressBarViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel(),
    userDrinkLogViewModel: UserAndUserDrinkLogViewModel = hiltViewModel(),
) {
    val userId by authViewModel.getUserID()
    // val userDrinkLogs by userDrinkLogViewModel.getDrinkLogs(userId!!).collectAsState()
    val twoDayDrinkLogs by userDrinkLogViewModel.getTwoDaySummary(userId!!).collectAsState()
    Log.d("HomeScreen", "I am being redrawn")
    var showDialog by remember { mutableStateOf(false) }
    val currentType by progressBarViewModel.currentType.collectAsState()
    val drinkCount = twoDayDrinkLogs.size

    val moneyTarget by progressBarViewModel.moneyTarget.collectAsState()
    val countTarget by progressBarViewModel.countTarget.collectAsState()
    val amountTarget by progressBarViewModel.amountTarget.collectAsState()
    val currentTargets =
        mapOf(
            ProgressBarType.MONEY to moneyTarget,
            ProgressBarType.COUNT to countTarget,
            ProgressBarType.AMOUNT to amountTarget
        )

    val currentTarget = currentTargets[currentType]

    val progressBar: ProgressBarInterface = when (currentType) {
        ProgressBarType.MONEY -> MoneyProgressBar()
        ProgressBarType.COUNT -> CountProgressBar()
        ProgressBarType.AMOUNT -> AmountProgressBar()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = { HomeTopBar() {} },
        floatingActionButton = { AddButton(onFABClick) },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {

            Column {

                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .height(260.dp)
                        .fillMaxWidth(),
                    colors = CardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                        contentColor = MaterialTheme.colorScheme.onSurface,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    DrinkBanner(drinkCount)
                    HorizontalDivider()
                    AlcoholListComposable(
                        AlcoholListType.HOME,
                        {},
                        { onItemClick(it) }
                    )
                }

                if (showDialog) {
                    ProgressBarEditDialog(
                        currentType,
                        currentTargets,
                        onDismiss = { showDialog = false },
                        onConfirm = { selectedType, selectedTarget ->
                            progressBarViewModel.updateType(selectedType.toString())
                            progressBarViewModel.updateTarget(selectedTarget, selectedType)
                            showDialog = false
                        })
                }

                progressBar.ProgressBarCard(
                    twoDayDrinkLogs,
                    target = currentTarget!!,
                    onEditClick = { showDialog = true }
                )
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun PreviewFunction() {

}
