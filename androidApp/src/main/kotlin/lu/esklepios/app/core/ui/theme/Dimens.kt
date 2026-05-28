package lu.esklepios.app.core.ui.theme

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Design-system dimension tokens.
 *
 * RULE: Never introduce hardcoded dp/sp literals in UI code.
 *       Always reference a token from this file. If no token fits, add one
 *       here with a semantic name (e.g. `iconSizeChevron`) — not a numeric one.
 */
object Dimens {
    // ── Spacing ──────────────────────────────────────────────────────
    val paddingNone = 0.dp
    val paddingXXS = 2.dp
    val paddingXS = 4.dp
    val paddingTiny = 6.dp
    val paddingS = 8.dp
    val paddingM = 12.dp
    val paddingPlus = 14.dp
    val paddingL = 16.dp
    val paddingXL = 20.dp
    val paddingXXL = 24.dp
    val paddingXXXL = 32.dp

    // ── Corner radii ─────────────────────────────────────────────────
    val cornerNone = 0.dp
    val radiusXs = 6.dp
    val radiusSm = 8.dp
    val radiusMd = 12.dp
    val radiusAction = 14.dp // CTA buttons, "see all" outlined button
    val radiusLg = 16.dp
    val radiusCard = 18.dp
    val radiusXl = 20.dp // show-more pill
    val radiusPill = 50.dp

    // ── Borders & elevations ─────────────────────────────────────────
    val borderHairline = 0.5.dp
    val borderThin = 1.dp
    val borderAccent = 1.5.dp // slot buttons, key-action outlined borders
    val borderMedium = 2.dp
    val borderStrong = 3.dp
    val elevationNone = 0.dp
    val cardElevation = 2.dp

    // ── Component heights / fixed sizes ──────────────────────────────
    val buttonHeight = 52.dp
    val bottomNavHeight = 64.dp
    val appBarHeight = 56.dp
    val statusBadgeHeight = 24.dp
    val filterChipHeight = 32.dp
    val cardOverlap = 28.dp

    // ── Icons ────────────────────────────────────────────────────────
    val iconSizeXxs = 13.dp
    val iconSizeChevron = 13.dp // used for "size-3" chevrons / small inline icons
    val iconSizeMicro = 14.dp // smallest icon variant
    val iconSizeCamera = 15.dp
    val iconSizeSm = 16.dp
    val iconSizeCompact = 18.dp
    val iconSizeMd = 20.dp
    val iconSizeLgInner = 22.dp
    val iconSizeDrawerItem = 22.dp
    val iconSizeLg = 24.dp
    val iconButtonSize = 40.dp

    // ── Avatars ──────────────────────────────────────────────────────
    val avatarSizeSm = 36.dp
    val avatarSizeMd = 48.dp
    val avatarSizeLg = 64.dp
    val avatarSizeXl = 84.dp

    // ── Profile / decorative ─────────────────────────────────────────
    val cameraBadgeSize = 30.dp
    val offsetIconShadow = 2.dp

    // ── Layout dimensions ────────────────────────────────────────────
    val drawerWidth = 300.dp
    val drawerHeaderTop = 100.dp
    val drawerHeaderStart = 24.dp
    val drawerItemHorizontal = 20.dp
    val drawerItemVertical = 14.dp
    val drawerSectionHorizontal = 16.dp
    val drawerSectionVertical = 4.dp
    val drawerSectionTop = 16.dp
    val mapPreviewHeight = 140.dp
    val detailMapHeight = 160.dp // static map placeholder in detail contact card
    val detailAvatarSize = 56.dp // avatar in PractitionerDetailScreen gradient header
    val scheduleDayLabelWidth = 80.dp // fixed-width day name column in schedule table
    val timeSlotGridHeight = 120.dp
    val scheduleStripDayWidth = 52.dp
    val scheduleDayCircleSize = 20.dp // today-highlight circle in PractitionerCard
    val progressBarHeight = 4.dp
    val progressBarRadius = 2.dp
    val countryCodeWidth = 110.dp
    val emptyIconSize = 80.dp
    val emptyIconSmSize = 56.dp

    // ── GradientHeader decorative orbs ───────────────────────────────
    val orbXs = 40.dp
    val orbSm = 80.dp
    val orbMd = 120.dp
    val orbLg = 180.dp
    val orbXl = 200.dp
    val orbOffsetMainX = 200.dp
    val orbOffsetMainY = 20.dp
    val orbOffsetSecondaryX = 100.dp
    val orbOffsetSecondaryY = 80.dp

    // ── Font sizes (sp) ──────────────────────────────────────────────
    // Used only when overriding MaterialTheme typography. Prefer applying
    // a TextStyle directly when possible.
    val fontSizeTiny = 10.sp // slot buttons, show-more pill, day abbreviations
    val fontSizeXxs = 11.sp
    val fontSizeXs = 12.sp
    val fontSizeFootnote = 13.sp
    val fontSizeSm = 14.sp
    val fontSizeBase = 15.sp
    val fontSizeBody = 16.sp // empty-day dash, booking name
    val fontSizeMd = 18.sp
    val fontSizeLg = 22.sp
    val letterSpacingWide = 0.8.sp
}
