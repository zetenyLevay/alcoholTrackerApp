package com.example.alcoholtracker.ui.components.logComponents

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.ui.text.input.KeyboardType
import com.example.alcoholtracker.utils.NumberInputSpec


class NumberInputTransformation(private val spec: NumberInputSpec) : InputTransformation {

    override val keyboardOptions: KeyboardOptions = KeyboardOptions(
        keyboardType = if (spec.maxDecimals > 0) KeyboardType.Decimal else KeyboardType.Number
    )

    private val pattern = if (spec.maxDecimals > 0) {
        Regex("^\\d{0,${spec.maxIntegerDigits}}(\\.\\d{0,${spec.maxDecimals}})?$")
    } else {
        Regex("^\\d{0,${spec.maxIntegerDigits}}$")
    }

    override fun TextFieldBuffer.transformInput() {
        for (i in 0 until length) {
            if (charAt(i) == ',') replace(i, i + 1, ".")
        }

        if (!pattern.matches(asCharSequence())) {
            revertAllChanges()
            return
        }

        if (length > 1 && charAt(0) == '0' && charAt(1).isDigit()) replace(0, 1, "")
        if (length > 0 && charAt(0) == '.') replace(0, 0, "0")

        val maxValue = spec.maxValue
        val value = asCharSequence().toString().toDoubleOrNull()
        if (maxValue != null && value != null && value > maxValue) revertAllChanges()
    }
}
