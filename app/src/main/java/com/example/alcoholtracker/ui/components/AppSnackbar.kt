package com.example.alcoholtracker.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.alcoholtracker.SnackbarType

// Carries the snackbar type through SnackbarHostState so the host can style it; timing is handled by the caller
class AppSnackbarVisuals(
    override val message: String,
    override val actionLabel: String?,
    val type: SnackbarType,
) : SnackbarVisuals {
    override val withDismissAction: Boolean = false
    override val duration: SnackbarDuration = SnackbarDuration.Indefinite
}

private data class SnackbarStyle(
    val container: Color,
    val content: Color,
    val action: Color,
    val icon: ImageVector?,
)

@Composable
private fun SnackbarType.style(): SnackbarStyle {
    val colors = MaterialTheme.colorScheme
    return when (this) {
        SnackbarType.SUCCESS -> SnackbarStyle(
            container = colors.primaryContainer,
            content = colors.onPrimaryContainer,
            action = colors.primary,
            icon = Icons.Filled.CheckCircle
        )

        SnackbarType.ERROR -> SnackbarStyle(
            container = colors.errorContainer,
            content = colors.onErrorContainer,
            action = colors.error,
            icon = Icons.Filled.ErrorOutline
        )

        SnackbarType.INFO -> SnackbarStyle(
            container = colors.inverseSurface,
            content = colors.inverseOnSurface,
            action = colors.inversePrimary,
            icon = null
        )
    }
}

@Composable
fun AppSnackbar(data: SnackbarData) {
    val type = (data.visuals as? AppSnackbarVisuals)?.type ?: SnackbarType.INFO
    val style = type.style()

    Snackbar(
        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
        shape = RoundedCornerShape(12.dp),
        containerColor = style.container,
        contentColor = style.content,
        action = data.visuals.actionLabel?.let { label ->
            {
                TextButton(onClick = { data.performAction() }) {
                    Text(label, color = style.action, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            style.icon?.let {
                Icon(it, contentDescription = null, modifier = Modifier.size(20.dp))
            }
            Text(data.visuals.message)
        }
    }
}
