package com.example.alcoholtracker.ui.components.progressbar

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Euro
import androidx.compose.material.icons.filled.LocalBar
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import java.math.BigDecimal

private data class GoalOption(
    val label: String,
    val icon: ImageVector,
    val prefix: String,
    val suffix: String,
    val presets: List<Double>,
    val allowDecimals: Boolean,
)

private fun ProgressBarType.option(): GoalOption = when (this) {
    ProgressBarType.MONEY -> GoalOption("Money", Icons.Filled.Euro, "€", "", listOf(20.0, 40.0, 60.0), true)
    ProgressBarType.COUNT -> GoalOption("Drinks", Icons.Filled.LocalBar, "", "drinks", listOf(3.0, 5.0, 8.0), false)
    ProgressBarType.AMOUNT -> GoalOption("Volume", Icons.Filled.WaterDrop, "", "ml", listOf(500.0, 1000.0, 1500.0), false)
}

private fun Double.toInput(): String =
    if (this <= 0.0) "" else BigDecimal.valueOf(this).stripTrailingZeros().toPlainString()

@Composable
fun ProgressBarEditDialog(
    currentType: ProgressBarType,
    currentTargets: Map<ProgressBarType, Double>,
    onDismiss: () -> Unit,
    onEvent: (ProgressBarType, Double) -> Unit
) {
    var selectedType by remember { mutableStateOf(currentType) }
    var input by remember { mutableStateOf(currentTargets[currentType]?.toInput().orEmpty()) }

    val option = selectedType.option()
    val target = input.toDoubleOrNull()
    val isValid = target != null && target > 0.0
    val save = {
        if (isValid) {
            onEvent(selectedType, target!!)
            onDismiss()
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            tonalElevation = 6.dp,
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Tonight's goal",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Pick what to track and set a limit",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.height(20.dp))

                SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                    ProgressBarType.entries.forEachIndexed { index, type ->
                        val typeOption = type.option()
                        SegmentedButton(
                            selected = selectedType == type,
                            onClick = {
                                selectedType = type
                                input = currentTargets[type]?.toInput().orEmpty()
                            },
                            shape = SegmentedButtonDefaults.itemShape(index, ProgressBarType.entries.size),
                            icon = {
                                SegmentedButtonDefaults.Icon(active = selectedType == type) {
                                    Icon(
                                        typeOption.icon,
                                        contentDescription = null,
                                        modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                                    )
                                }
                            },
                            label = { Text(typeOption.label, maxLines = 1) }
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                AnimatedContent(
                    targetState = selectedType,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "GoalInput"
                ) { type ->
                    val typeOption = type.option()
                    GoalInput(
                        value = input,
                        onValueChange = { new ->
                            val pattern = if (typeOption.allowDecimals) Regex("^\\d{0,6}(\\.\\d{0,2})?$") else Regex("^\\d{0,6}$")
                            if (new.matches(pattern)) input = new
                        },
                        prefix = typeOption.prefix,
                        suffix = typeOption.suffix,
                        allowDecimals = typeOption.allowDecimals,
                        onDone = save
                    )
                }

                Spacer(Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    option.presets.forEach { preset ->
                        val presetText = preset.toInput()
                        FilterChip(
                            selected = input == presetText,
                            onClick = { input = presetText },
                            label = {
                                Text(
                                    listOf(option.prefix + presetText, option.suffix)
                                        .filter { it.isNotEmpty() }
                                        .joinToString(" ")
                                )
                            }
                        )
                    }
                }

                Spacer(Modifier.height(28.dp))

                Button(
                    onClick = save,
                    enabled = isValid,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Save goal", fontWeight = FontWeight.SemiBold)
                }
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun GoalInput(
    value: String,
    onValueChange: (String) -> Unit,
    prefix: String,
    suffix: String,
    allowDecimals: Boolean,
    onDone: () -> Unit,
) {
    val numberStyle = MaterialTheme.typography.displayMedium.copy(
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        textAlign = TextAlign.Center
    )
    val unitStyle = MaterialTheme.typography.titleLarge
    val unitColor = MaterialTheme.colorScheme.onSurfaceVariant
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val fieldWidth = remember(value, numberStyle, density) {
        val measured = textMeasurer.measure(
            text = value.ifEmpty { "0" },
            style = numberStyle,
            maxLines = 1,
            softWrap = false
        )
        with(density) { measured.size.width.toDp() } + CursorSlack
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (prefix.isNotEmpty()) {
            Text(prefix, style = unitStyle, color = unitColor)
            Spacer(Modifier.width(6.dp))
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = numberStyle,
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            keyboardOptions = KeyboardOptions(
                keyboardType = if (allowDecimals) KeyboardType.Decimal else KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            modifier = Modifier.width(fieldWidth),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.Center) {
                    if (value.isEmpty()) {
                        Text("0", style = numberStyle, color = MaterialTheme.colorScheme.outlineVariant)
                    }
                    innerTextField()
                }
            }
        )
        if (suffix.isNotEmpty()) {
            Spacer(Modifier.width(6.dp))
            Text(suffix, style = unitStyle, color = unitColor)
        }
    }
}

private val CursorSlack = 6.dp
