package lu.esklepios.app.view.dashboard.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import lu.esklepios.app.core.ui.components.AppCaptionText
import lu.esklepios.app.core.ui.components.AppLabelText
import lu.esklepios.app.core.ui.theme.BorderColor
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.Surface
import lu.esklepios.app.core.ui.theme.TextHint
import lu.esklepios.app.core.ui.theme.TextPrimary
import lu.esklepios.app.core.ui.theme.TextSecondary

@Composable
fun PractitionerDateFilterRow(
    filters: List<Pair<String, String>>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(Surface)
                .padding(horizontal = Dimens.paddingL, vertical = Dimens.paddingM),
    ) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            filters.forEachIndexed { index, (key, label) ->
                SegmentedButton(
                    selected = selectedFilter == key,
                    onClick = { onFilterSelected(key) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = filters.size),
                    colors =
                        SegmentedButtonDefaults.colors(
                            activeContainerColor = Primary,
                            activeContentColor = Color.White,
                            activeBorderColor = Primary,
                            inactiveContainerColor = Surface,
                            inactiveContentColor = TextSecondary,
                            inactiveBorderColor = BorderColor,
                        ),
                    icon = {},
                ) {
                    AppLabelText(text = label, color = Color.Unspecified)
                }
            }
        }
    }
}

@Composable
fun PractitionerNewPatientsToggle(
    checked: Boolean,
    label: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.clickable(onClick = onToggle),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { onToggle() },
            colors =
                CheckboxDefaults.colors(
                    checkedColor = Primary,
                    uncheckedColor = TextHint,
                    checkmarkColor = Color.White,
                ),
        )
        AppCaptionText(
            text = label,
            color = if (checked) TextPrimary else TextSecondary,
        )
    }
}
