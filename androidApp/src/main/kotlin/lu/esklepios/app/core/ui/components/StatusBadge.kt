package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import lu.esklepios.app.core.ui.theme.Danger
import lu.esklepios.app.core.ui.theme.DangerBg
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.PrimaryLight
import lu.esklepios.app.core.ui.theme.Success
import lu.esklepios.app.core.ui.theme.SuccessBg
import lu.esklepios.app.core.ui.theme.Warning
import lu.esklepios.app.core.ui.theme.WarningBg
import lu.esklepios.app.domain.model.AppointmentStatus
import lu.esklepios.app.util.AppointmentStatusColorScheme
import lu.esklepios.app.util.colorScheme
import lu.esklepios.app.utils.labelStringRes

/** Maps the shared [AppointmentStatusColorScheme] to concrete platform colour tokens. */
@Composable
internal fun colorsForScheme(scheme: AppointmentStatusColorScheme): Pair<Color, Color> =
    when (scheme) {
        AppointmentStatusColorScheme.SUCCESS -> SuccessBg to Success
        AppointmentStatusColorScheme.WARNING -> WarningBg to Warning
        AppointmentStatusColorScheme.DANGER -> DangerBg to Danger
        AppointmentStatusColorScheme.PRIMARY -> PrimaryLight to Primary
    }

@Composable
fun StatusBadge(status: AppointmentStatus) {
    val (bgColor, textColor) = colorsForScheme(status.colorScheme())
    Box(
        modifier =
            Modifier
                .background(color = bgColor, shape = RoundedCornerShape(Dimens.radiusPill))
                .padding(horizontal = Dimens.paddingM, vertical = Dimens.paddingXS),
    ) {
        AppCaptionText(
            text = stringResource(status.labelStringRes()),
            color = textColor,
        )
    }
}
