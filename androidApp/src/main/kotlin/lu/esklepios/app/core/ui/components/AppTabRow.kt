package lu.esklepios.app.core.ui.components

import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.Surface
import lu.esklepios.app.core.ui.theme.TextSecondary

@Composable
fun AppTabRow(
    selectedIndex: Int,
    tabs: List<AppTabItem>,
    modifier: Modifier = Modifier,
) {
    TabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier,
        containerColor = Surface,
        contentColor = Primary,
    ) {
        tabs.forEachIndexed { index, tab ->
            Tab(
                selected = selectedIndex == index,
                onClick = tab.onClick,
                text = {
                    AppCaptionText(
                        text = tab.label,
                        color = if (selectedIndex == index) Primary else TextSecondary,
                    )
                },
            )
        }
    }
}

data class AppTabItem(val label: String, val onClick: () -> Unit)