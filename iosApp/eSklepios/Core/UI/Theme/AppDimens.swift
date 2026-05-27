import SwiftUI

/**
 Design-system dimension tokens.

 RULE: Never introduce hardcoded CGFloat literals in UI code.
       Always reference a token from this file. If no token fits, add one
       here with a semantic name (e.g. `iconChevron`) — not a numeric one.
 */
enum Dimens {
    // MARK: - Spacing
    static let paddingNone: CGFloat = 0
    static let paddingXXS: CGFloat = 2
    static let paddingXS: CGFloat = 4
    static let paddingTiny: CGFloat = 6
    static let paddingS: CGFloat = 8
    static let paddingCompact: CGFloat = 10
    static let paddingM: CGFloat = 12
    static let paddingPlus: CGFloat = 14
    static let paddingL: CGFloat = 16
    static let paddingXL: CGFloat = 20
    static let paddingXXL: CGFloat = 24
    static let paddingXXXL: CGFloat = 32

    // MARK: - Corner radii
    static let cornerNone: CGFloat = 0
    static let radiusSm: CGFloat = 8
    static let radiusInput: CGFloat = 10
    static let radiusMd: CGFloat = 12
    static let radiusAction: CGFloat = 14
    static let radiusLg: CGFloat = 16
    static let radiusCard: CGFloat = 18
    static let radiusXl: CGFloat = 20
    static let radiusPill: CGFloat = 50

    // MARK: - Borders / strokes
    static let strokeNone: CGFloat = 0
    static let strokeThin: CGFloat = 1
    static let strokeMedium: CGFloat = 1.5
    static let strokeThick: CGFloat = 3
    static let dividerThickness: CGFloat = 1

    // MARK: - Component heights & widths
    static let buttonHeight: CGFloat = 52
    static let buttonHeightSm: CGFloat = 48
    static let editButtonHeight: CGFloat = 28
    static let inputHeight: CGFloat = 52
    static let inputHeightSmall: CGFloat = 44
    static let toolbarHeight: CGFloat = 56
    static let toolbarSlot: CGFloat = 40         // ← AppToolbar back/title slots
    static let stepIndicatorSize: CGFloat = 32

    // MARK: - Avatars
    static let avatarSm: CGFloat = 36
    static let avatarMd: CGFloat = 48
    static let avatarLg: CGFloat = 64
    static let avatarXl: CGFloat = 84
    static let detailAvatarSize: CGFloat = 56    // avatar in PractitionerDetailView gradient header

    // MARK: - Icons
    static let iconXs: CGFloat = 13
    static let iconChevron: CGFloat = 13
    static let iconXxs: CGFloat = 10
    static let iconMicro: CGFloat = 14
    static let iconCamera: CGFloat = 15
    static let iconSm: CGFloat = 16
    static let iconCompact: CGFloat = 18
    static let iconMd: CGFloat = 20
    static let iconLgInner: CGFloat = 22
    static let iconLg: CGFloat = 24
    static let iconBoxSm: CGFloat = 24           // ← Profile leading icon column
    static let iconXl: CGFloat = 32
    static let iconButtonSize: CGFloat = 40
    static let iconDrawerItem: CGFloat = 22
    static let iconLgVisual: CGFloat = 18        // ← Profile leading icon point-size

    // MARK: - Shadows
    static let shadowRadius: CGFloat = 8
    static let shadowOpacity: CGFloat = 0.08
    static let shadowY: CGFloat = 2
    static let cardElevation: CGFloat = 4

    // MARK: - Profile / decorative
    static let cameraBadgeSize: CGFloat = 30
    static let cameraBadgeOffsetY: CGFloat = 4
    static let profileIconBox: CGFloat = 40      // ← ProfileView leading-icon colored square
    static let offsetIconShadow: CGFloat = 2     // ← icon double-stacking offset

    // MARK: - Layout heights / widths
    static let drawerWidth: CGFloat = 300
    static let drawerHeaderTop: CGFloat = 100
    static let mapPreviewHeight: CGFloat = 140
    static let detailMapHeight: CGFloat = 160            // map placeholder in contact card
    static let scheduleDayLabelWidth: CGFloat = 80       // fixed day-name column in schedule table
    static let timeSlotGridHeight: CGFloat = 120
    static let messageEditorHeight: CGFloat = 100
    static let progressBarHeight: CGFloat = 4
    static let progressBarHeightThick: CGFloat = 6
    static let progressBarRadius: CGFloat = 2
    static let progressBarRadiusThick: CGFloat = 3
    static let countryCodeWidth: CGFloat = 100
    static let infoRowIconColumn: CGFloat = 22

    // MARK: - AppGradientHeader computed-height constants
    static let headerTextBlockHeight: CGFloat = 60
    static let headerSearchHeight: CGFloat = 120
    static let headerInlineProfileHeight: CGFloat = 80
    static let headerCenteredProfileHeight: CGFloat = 150
    static let headerMinHeightRounded: CGFloat = 200
    static let headerMinHeight: CGFloat = 160

    // MARK: - GradientHeader decorative orbs
    static let orbXs: CGFloat = 40
    static let orbSm: CGFloat = 80
    static let orbMd: CGFloat = 120
    static let orbLg: CGFloat = 180
    static let orbXl: CGFloat = 200
    static let orbOffsetMainNeg: CGFloat = -40   // ← top-left orb offset
    static let orbOffsetSecondaryTop: CGFloat = 20
    static let orbOffsetSecondaryRight: CGFloat = 80
    static let orbOffsetTertiaryRight: CGFloat = 30
    static let orbOffsetTertiaryBottom: CGFloat = 40
    static let orbOffsetLandingNeg: CGFloat = -50

    // MARK: - Schedule strip
    static let scheduleDayCircleSize: CGFloat = 26

    // MARK: - Empty state icon sizes
    static let emptyIconSize: CGFloat = 80
    static let emptyIconSmSize: CGFloat = 56
    static let appointmentSuccessIcon: CGFloat = 120
    static let cardOverlap: CGFloat = 28
}

/// Compact alias namespaces that group tokens by category for ergonomic call-sites.
/// Implementation is a typealias-style facade over `Dimens` — single source of truth.
enum Spacing {
    static let none = Dimens.paddingNone
    static let xxs  = Dimens.paddingXXS
    static let xs   = Dimens.paddingXS
    static let tiny = Dimens.paddingTiny
    static let s    = Dimens.paddingS
    static let compact = Dimens.paddingCompact
    static let m    = Dimens.paddingM
    static let plus = Dimens.paddingPlus
    static let l    = Dimens.paddingL
    static let xl   = Dimens.paddingXL
    static let xxl  = Dimens.paddingXXL
    static let xxxl = Dimens.paddingXXXL
}

enum Radius {
    static let none   = Dimens.cornerNone
    static let sm     = Dimens.radiusSm
    static let input  = Dimens.radiusInput
    static let md     = Dimens.radiusMd
    static let action = Dimens.radiusAction
    static let lg     = Dimens.radiusLg
    static let card   = Dimens.radiusCard
    static let xl     = Dimens.radiusXl
    static let pill   = Dimens.radiusPill
}

enum Sizing {
    static let iconXxs      = Dimens.iconXxs
    static let iconXs       = Dimens.iconXs
    static let iconChevron  = Dimens.iconChevron
    static let iconMicro    = Dimens.iconMicro
    static let iconSm       = Dimens.iconSm
    static let iconCompact  = Dimens.iconCompact
    static let iconMd       = Dimens.iconMd
    static let iconLg       = Dimens.iconLg
    static let iconXl       = Dimens.iconXl
    static let iconButton   = Dimens.iconButtonSize
    static let toolbarSlot  = Dimens.toolbarSlot
    static let avatarSm     = Dimens.avatarSm
    static let avatarMd     = Dimens.avatarMd
    static let avatarLg     = Dimens.avatarLg
    static let avatarXl     = Dimens.avatarXl
    static let buttonHeight = Dimens.buttonHeight
    static let inputHeight  = Dimens.inputHeight
}
