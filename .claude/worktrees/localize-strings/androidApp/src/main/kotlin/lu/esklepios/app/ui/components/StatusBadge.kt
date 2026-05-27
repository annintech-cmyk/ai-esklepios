package lu.esklepios.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lu.esklepios.app.R
import lu.esklepios.app.ui.theme.*

enum class AppointmentStatus {
    CONFIRMED,
    RESERVED,
    CANCELLED
}

@Composable
fun StatusBadge(status: AppointmentStatus) {
    val (bgColor, textColor, labelRes) = when (status) {
        AppointmentStatus.CONFIRMED -> Triple(SuccessBg, Success, R.string.status_confirmed)
        AppointmentStatus.RESERVED -> Triple(WarningBg, Warning, R.string.status_reserved)
        AppointmentStatus.CANCELLED -> Triple(DangerBg, Danger, R.string.status_cancelled)
    }

    Box(
        modifier = Modifier
            .background(color = bgColor, shape = RoundedCornerShape(Dimens.radiusPill))
            .padding(horizontal = Dimens.paddingM, vertical = Dimens.paddingXS)
    ) {
        Text(
            text = stringResource(labelRes),
            style = MaterialTheme.typography.labelSmall.copy(
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp
            )
        )
    }
}
