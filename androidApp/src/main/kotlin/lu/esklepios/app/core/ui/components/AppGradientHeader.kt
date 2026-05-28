package lu.esklepios.app.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import lu.esklepios.app.R
import lu.esklepios.app.core.ui.theme.Dimens
import lu.esklepios.app.core.ui.theme.PrimaryDark
import lu.esklepios.app.core.ui.theme.TealAccent

// ── Action slots ──────────────────────────────────────────────────────────────

sealed class HeaderAction {
    data class IconButtonAction(
        val icon: ImageVector,
        val contentDescription: String,
        val tint: Color = Color.White,
        val onClick: () -> Unit,
    ) : HeaderAction()

    data class TitleAction(
        val text: String,
        val style: TextStyle = TextStyle.Default,
    ) : HeaderAction()

    object None : HeaderAction()
}

// ── Profile section ───────────────────────────────────────────────────────────

sealed class HeaderProfile {
    /** Avatar on left, name/subtitle/caption stacked on right (PractitionerDetail) */
    data class Inline(
        val initials: String,
        val avatarSize: Dp = Dimens.avatarSizeLg,
        val name: String,
        val subtitle: String? = null,
        val caption: String? = null,
    ) : HeaderProfile()

    /** Centered avatar with ring + camera badge, name + email below (Profile) */
    data class Centered(
        val initials: String,
        val avatarSize: Dp = Dimens.avatarSizeXl,
        val name: String,
        val email: String? = null,
        val onCameraClick: (() -> Unit)? = null,
    ) : HeaderProfile()
}

// ── Supporting data classes ───────────────────────────────────────────────────

data class HeaderTextBlock(
    val overline: String? = null,
    val title: String? = null,
)

data class HeaderSearch(
    val searchQuery: String,
    val onSearchQueryChange: (String) -> Unit,
    val locationQuery: String,
    val onLocationQueryChange: (String) -> Unit,
    val onSearchClick: () -> Unit,
)

// ── Main composable ───────────────────────────────────────────────────────────

@Composable
fun AppGradientHeader(
    modifier: Modifier = Modifier,
    roundedBottom: Boolean = false,
    leadingAction: HeaderAction = HeaderAction.None,
    centerAction: HeaderAction = HeaderAction.None,
    trailingAction: HeaderAction = HeaderAction.None,
    textBlock: HeaderTextBlock? = null,
    profile: HeaderProfile? = null,
    search: HeaderSearch? = null,
) {
    val compact = textBlock == null && profile == null && search == null
    GradientHeader(
        modifier = modifier,
        roundedBottom = roundedBottom,
        topPadding = Dimens.paddingS,
        bottomPadding = if (compact) Dimens.paddingS else Dimens.paddingXXL,
    ) {
        // Toolbar row — three-position Box so center title is always pixel-centred
        Box(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(Dimens.iconButtonSize),
        ) {
            HeaderActionSlot(leadingAction, Modifier.align(Alignment.CenterStart))
            HeaderActionSlot(centerAction, Modifier.align(Alignment.Center))
            HeaderActionSlot(trailingAction, Modifier.align(Alignment.CenterEnd))
        }

        // Optional text block
        textBlock?.let { tb ->
            Spacer(Modifier.height(Dimens.paddingS))
            tb.overline?.let {
                AppBodyText(
                    text = it,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.padding(horizontal = Dimens.paddingXXL),
                )
            }
            tb.title?.let {
                AppTitleText(
                    text = it,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = Dimens.paddingXXL),
                )
            }
        }

        // Optional search card
        if (search != null) {
            Column(
                modifier =
                    Modifier.padding(
                        horizontal = Dimens.paddingM,
                        vertical = Dimens.paddingS,
                    ),
            ) {
                Text(
                    text =
                        buildAnnotatedString {
                            withStyle(SpanStyle(color = Color.White, fontWeight = FontWeight.ExtraBold)) {
                                append(stringResource(R.string.landing_hero_prefix))
                            }
                            withStyle(SpanStyle(color = TealAccent, fontWeight = FontWeight.ExtraBold)) {
                                append(stringResource(R.string.landing_hero_accent))
                            }
                        },
                    style = MaterialTheme.typography.displaySmall,
                )
                Spacer(Modifier.height(Dimens.paddingM))
                Surface(
                    modifier =
                        modifier
                            .fillMaxWidth(),
                    shape = RoundedCornerShape(Dimens.paddingXXXL),
                    color = Color.White,
                    shadowElevation = Dimens.elevationNone,
                    tonalElevation = Dimens.elevationNone,
                ) {
                    SearchCard(
                        searchQuery = search.searchQuery,
                        onSearchQueryChange = search.onSearchQueryChange,
                        locationQuery = search.locationQuery,
                        onLocationQueryChange = search.onLocationQueryChange,
                        onSearchClick = search.onSearchClick,
                        modifier = Modifier.padding(horizontal = Dimens.paddingM, vertical = Dimens.paddingXL),
                    )
                }
            }
        }

        // Optional profile section
        if (profile != null) {
            Spacer(Modifier.height(Dimens.paddingM))
            when (profile) {
                is HeaderProfile.Inline -> InlineProfile(profile)
                is HeaderProfile.Centered -> CenteredProfile(profile)
            }
            Spacer(Modifier.height(Dimens.paddingM))
        }
    }
}

// ── Private rendering helpers ─────────────────────────────────────────────────

@Composable
private fun HeaderActionSlot(
    action: HeaderAction,
    modifier: Modifier = Modifier,
) {
    when (action) {
        is HeaderAction.IconButtonAction -> {
            IconButton(onClick = action.onClick, modifier = modifier) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.contentDescription,
                    tint = action.tint,
                )
            }
        }

        is HeaderAction.TitleAction -> {
            val resolvedStyle =
                if (action.style == TextStyle.Default) {
                    MaterialTheme.typography.headlineSmall
                } else {
                    action.style
                }
            // AppTitleText doesn't support custom TextStyle, so we inline Text here
            // for the TitleAction slot which requires arbitrary style injection from callers.
            // This is a documented exception: HeaderAction.TitleAction carries a caller-supplied
            // TextStyle that cannot be expressed through App*Text components.
            Text(
                text = action.text,
                color = Color.White,
                style = resolvedStyle,
                textAlign = TextAlign.Center,
                modifier = modifier,
            )
        }

        HeaderAction.None -> {
            Box(modifier = modifier.size(Dimens.iconButtonSize))
        }
    }
}

@Composable
private fun InlineProfile(profile: HeaderProfile.Inline) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AvatarCircle(
            initials = profile.initials,
            size = profile.avatarSize,
        )
        Spacer(Modifier.width(Dimens.paddingL))
        Column {
            AppTitleText(text = profile.name, color = Color.White)
            profile.subtitle?.let {
                AppBodyText(
                    text = it,
                    color = Color.White.copy(alpha = 0.85f),
                )
            }
            profile.caption?.let {
                AppCaptionText(
                    text = it,
                    color = Color.White.copy(alpha = 0.7f),
                )
            }
        }
    }
}

@Composable
private fun CenteredProfile(profile: HeaderProfile.Centered) {
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.wrapContentSize()) {
            Box(
                modifier =
                    Modifier
                        .size(profile.avatarSize + Dimens.paddingXS)
                        .align(Alignment.Center)
                        .border(Dimens.borderStrong, Color.White.copy(alpha = 0.45f), CircleShape),
            )
            Box(modifier = Modifier.align(Alignment.Center)) {
                AvatarCircle(
                    initials = profile.initials,
                    size = profile.avatarSize,
                )
            }
            Box(
                modifier =
                    Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = Dimens.offsetIconShadow, y = Dimens.offsetIconShadow)
                        .size(Dimens.cameraBadgeSize)
                        .background(PrimaryDark, CircleShape)
                        .border(Dimens.borderMedium, Color.White, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.CameraAlt,
                    // a11y: decorative — labelled by adjacent Text
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(Dimens.iconSizeCamera),
                )
            }
        }
    }
    Spacer(Modifier.height(Dimens.paddingM))
    AppTitleText(
        text = profile.name,
        color = Color.White,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(),
    )
    profile.email?.let { email ->
        AppCaptionText(
            text = email,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
