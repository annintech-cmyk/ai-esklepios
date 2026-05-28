package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.NearMe
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.Primary
import lu.esklepios.app.core.ui.theme.PrimaryMid
import lu.esklepios.app.core.ui.theme.TextHint
import lu.esklepios.app.core.ui.theme.TextSecondary

@Composable
fun SearchCard(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    locationQuery: String,
    onLocationQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    enabled: Boolean = true,
    variant: SearchInputVariant = SearchInputVariant.Light,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SearchInputField(
            value = searchQuery,
            onValueChange = onSearchQueryChange,
            placeholder = stringResource(R.string.landing_search_hint),
            leadingIcon = Icons.Outlined.MedicalServices,
            iconTint = TextHint,
            variant = variant,
        )
        VSpace(Dimens.paddingS)
        SearchInputField(
            value = locationQuery,
            onValueChange = onLocationQueryChange,
            placeholder = stringResource(R.string.landing_location_hint),
            leadingIcon = Icons.Outlined.LocationOn,
            iconTint = PrimaryMid,
            variant = variant,
        )
        VSpace(Dimens.paddingS)
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(Dimens.buttonHeight)
                    .clip(RoundedCornerShape(Dimens.radiusPill))
                    .background(if (enabled) Primary else Primary.copy(alpha = 0.5f))
                    .clickable(enabled = enabled, onClick = onSearchClick)
                    .padding(horizontal = Dimens.paddingL),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppIcon(
                imageVector = Icons.Filled.Search,
                // a11y: decorative — labelled by adjacent Text
                contentDescription = null,
                tint = Color.White,
                size = Dimens.iconSizeMd,
            )
            AppButtonText(
                text = stringResource(R.string.landing_find_practitioners),
                modifier = Modifier.weight(1f),
                color = Color.White,
                textAlign = TextAlign.Center,
            )
            AppIcon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                // a11y: decorative — labelled by adjacent Text
                contentDescription = null,
                tint = Color.White,
                size = Dimens.iconSizeMd,
            )
        }
    }
}

@Composable
fun LandingSearchCard(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    locationQuery: String,
    onLocationQueryChange: (String) -> Unit,
    onSearchClick: () -> Unit,
    enabled: Boolean = true,
    showPoints: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.paddingXXXL),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = Dimens.elevationNone,
        tonalElevation = Dimens.elevationNone,
    ) {
        Column(
            modifier =
                Modifier.padding(
                    horizontal = Dimens.paddingXXL,
                    vertical = Dimens.paddingXXL + Dimens.paddingS,
                ),
        ) {
            SearchCard(
                searchQuery = searchQuery,
                onSearchQueryChange = onSearchQueryChange,
                locationQuery = locationQuery,
                onLocationQueryChange = onLocationQueryChange,
                onSearchClick = onSearchClick,
                enabled = enabled,
            )
            if (showPoints) {
                VSpace(Dimens.paddingXL)
                LandingTrustRow(icon = Icons.Outlined.Shield, label = stringResource(R.string.landing_trust_licensed))
                VSpace(Dimens.paddingS)
                LandingTrustRow(icon = Icons.Outlined.Lock, label = stringResource(R.string.landing_trust_secure))
                VSpace(Dimens.paddingS)
                LandingTrustRow(icon = Icons.Outlined.NearMe, label = stringResource(R.string.landing_trust_free))
            }
        }
    }
}

@Composable
private fun LandingTrustRow(
    icon: ImageVector,
    label: String,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AppIcon(
            imageVector = icon,
            // a11y: decorative — labelled by adjacent Text
            contentDescription = null,
            tint = PrimaryMid,
            size = Dimens.iconSizeMd,
        )
        HSpace(Dimens.paddingS + Dimens.paddingXS)
        AppBodyText(text = label, color = TextSecondary)
    }
}
