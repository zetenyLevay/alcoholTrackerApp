package com.example.alcoholtracker.ui.screens


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.alcoholtracker.data.model.UserDrinkLog
import com.example.alcoholtracker.ui.components.AddButton
import com.example.alcoholtracker.ui.components.DrinkBanner
import com.example.alcoholtracker.ui.components.HomeTopBar
import com.example.alcoholtracker.ui.components.alcohollist.AlcoholListHome
import com.example.alcoholtracker.ui.components.progressbar.AmountProgressBar
import com.example.alcoholtracker.ui.components.progressbar.CountProgressBar
import com.example.alcoholtracker.ui.components.progressbar.MoneyProgressBar
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarEditDialog
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarInterface
import com.example.alcoholtracker.ui.components.progressbar.ProgressBarType
import com.example.alcoholtracker.ui.viewmodel.ProgressBarViewModel
import com.example.alcoholtracker.ui.viewmodel.DrinkLogFormViewModel

@Composable
fun HomeScreen(
    onFABClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    progressBarViewModel: ProgressBarViewModel = hiltViewModel(),

    userDrinkLogFormViewModel: DrinkLogFormViewModel = hiltViewModel(),
) {

    val twoDayDrinkLogs by userDrinkLogFormViewModel.twoDaySummary.collectAsState()
    val currentType by progressBarViewModel.currentType.collectAsState()


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
    if (twoDayDrinkLogs == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
    } else {
        val drinkCount = twoDayDrinkLogs!!.size
        HomeScreen(
            twoDayDrinkLogs!!,
            drinkCount,
            currentType,
            currentTargets,
            currentTarget!!,
            progressBar,
            onFABClick,
            onItemClick,
            { selectedType, selectedTarget ->
                progressBarViewModel.updateTarget(selectedTarget, selectedType)
            },
            { userDrinkLogFormViewModel.deleteDrink(it) }
        )
    }


}

@Composable
fun HomeScreen(
    twoDayDrinkLogs: List<UserDrinkLog>,
    drinkCount: Int,
    currentType: ProgressBarType,
    currentTargets: Map<ProgressBarType, Double>,
    currentTarget: Double,
    progressBar: ProgressBarInterface,
    onFABClick: () -> Unit,
    onItemClick: (Int) -> Unit,
    onTypeUpdated: (ProgressBarType, Double) -> Unit,
    onRemove: (UserDrinkLog) -> Unit

    ){

    var showDialog by remember { mutableStateOf(false) }


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
                    AlcoholListHome(
                        onEditClick = {},
                        onItemClick ={ onItemClick(it) },
                        onRemove = {onRemove(it)},
                        drinkLogs = twoDayDrinkLogs
                    )
                }

                if (showDialog) {
                    ProgressBarEditDialog(
                        currentType,
                        currentTargets,
                        onDismiss = { showDialog = false },
                        onConfirm = {  selectedType, selectedTarget ->
                            onTypeUpdated(selectedType, selectedTarget)
                            showDialog = false
                         })
                }

                progressBar.ProgressBarCard(
                    twoDayDrinkLogs,
                    target = currentTarget,
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
