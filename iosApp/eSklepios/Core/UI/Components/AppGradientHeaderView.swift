import SwiftUI

// MARK: - Action slots

enum HeaderAction {
    case none
    case iconButton(systemName: String, accessibilityLabel: String, tintColor: Color = .white, action: () -> Void)
    case title(text: String, fontSize: CGFloat = 18)
}

// MARK: - Profile section

enum HeaderProfile {
    /// Avatar left, name/subtitle/caption stacked right (PractitionerDetail)
    case inline(initials: String, avatarSize: CGFloat = Dimens.avatarLg, name: String, subtitle: String? = nil, caption: String? = nil)
    /// Centered avatar with ring + camera badge, name + email below (Profile)
    case centered(initials: String, avatarSize: CGFloat = Dimens.avatarXl, name: String, email: String? = nil, onCameraTap: (() -> Void)? = nil)
}

// MARK: - Supporting types

struct HeaderTextBlock {
    var overline: String? = nil
    var title: String? = nil
}

struct HeaderSearchConfig {
    let searchQuery: String
    let locationQuery: String
    let onSearchQueryChange: (String) -> Void
    let onLocationQueryChange: (String) -> Void
    let onSearchTap: () -> Void
}

// MARK: - Main view

struct AppGradientHeaderView: View {
    var roundedBottom: Bool = false
    var leading: HeaderAction = .none
    var center: HeaderAction = .none
    var trailing: HeaderAction = .none
    var textBlock: HeaderTextBlock? = nil
    var profile: HeaderProfile? = nil
    var search: HeaderSearchConfig? = nil
    var heroNamespace: Namespace.ID? = nil
    var heroId: String? = nil

    @State private var localSearch: String = ""
    @State private var localLocation: String = ""

    var body: some View {
        ZStack(alignment: .topLeading) {
            AppGradient.primary
                .ignoresSafeArea(edges: .top)

            // Decorative orbs
            Circle().fill(Color.white.opacity(0.06)).frame(width: Dimens.orbLg).offset(x: Dimens.orbOffsetMainNeg, y: Dimens.orbOffsetMainNeg)
            Circle().fill(Color.white.opacity(0.04)).frame(width: Dimens.orbMd).offset(x: UIScreen.main.bounds.width - Dimens.orbSm, y: Dimens.orbOffsetSecondaryTop)
            Circle().fill(Color.white.opacity(0.05)).frame(width: Dimens.orbSm).offset(x: UIScreen.main.bounds.width - Dimens.orbOffsetTertiaryRight, y: resolvedMinHeight - Dimens.orbOffsetTertiaryBottom)

            VStack(alignment: .leading, spacing: Spacing.none) {
                // Toolbar row
                toolbarRow()
                    .padding(.top, Dimens.paddingL)

                // Text block
                if let tb = textBlock {
                    Spacer().frame(height: Dimens.paddingS)
                    if let overline = tb.overline {
                        AppBodyText(text: overline, color: .white.opacity(0.8))
                            .padding(.horizontal, Dimens.paddingXXL)
                    }
                    if let title = tb.title {
                        AppTitleText(text: title, color: .white)
                            .padding(.horizontal, Dimens.paddingXXL)
                    }
                }

                // Search card
                if let s = search {
                    Spacer().frame(height: Dimens.paddingL)
                    // Multi-color compound text — raw Text() + Text() concatenation required by SwiftUI (intentional exception)
                    (
                        Text(NSLocalizedString("landing_hero_prefix", value: "Make an appointment\nwith a ", comment: ""))
                            .foregroundColor(.white)
                        + Text(NSLocalizedString("landing_hero_accent", value: "health\nprofessional", comment: ""))
                            .foregroundColor(.appTealAccent)
                    )
                    .font(.heroDisplay)
                    .padding(.horizontal, Dimens.paddingM)

                    Spacer().frame(height: Dimens.paddingM)

                    SearchCard(
                        searchQuery: $localSearch,
                        locationQuery: $localLocation,
                        onSearchTap: s.onSearchTap,
                        variant: .light
                    )
                    .padding(.horizontal, Dimens.paddingM)
                    .padding(.vertical, Dimens.paddingXL)
                    .background(Color.white)
                    .cornerRadius(Radius.pill)
                    .onChange(of: localSearch) { s.onSearchQueryChange($0) }
                    .onChange(of: localLocation) { s.onLocationQueryChange($0) }
                    .onAppear {
                        localSearch = s.searchQuery
                        localLocation = s.locationQuery
                    }
                }

                // Profile section
                if let p = profile {
                    Spacer().frame(height: Dimens.paddingM)
                    profileSection(p)
                        .padding(.horizontal, Dimens.paddingXXL)
                    Spacer().frame(height: Dimens.paddingM)
                }

                Spacer().frame(height: Dimens.paddingXXL)
            }
        }
        .frame(minHeight: resolvedMinHeight)
        .if(roundedBottom) { view in
            view.clipShape(UnevenRoundedRectangle(
                topLeadingRadius: Radius.none,
                bottomLeadingRadius: Dimens.cameraBadgeSize,
                bottomTrailingRadius: Dimens.cameraBadgeSize,
                topTrailingRadius: Radius.none
            ))
        }
    }

    // MARK: - Toolbar

    @ViewBuilder
    private func toolbarRow() -> some View {
        HStack(spacing: Spacing.none) {
            actionView(leading)
                .frame(width: Dimens.iconButtonSize, height: Dimens.iconButtonSize)
            Spacer()
            actionView(center)
            Spacer()
            actionView(trailing)
                .frame(width: Dimens.iconButtonSize, height: Dimens.iconButtonSize)
        }
        .padding(.horizontal, Dimens.paddingL)
        .frame(height: CGFloat(Dimens.toolbarHeight))
    }

    @ViewBuilder
    private func actionView(_ action: HeaderAction) -> some View {
        switch action {
        case .none:
            Color.clear.frame(width: Dimens.iconButtonSize, height: Dimens.iconButtonSize)
        case .iconButton(let systemName, let label, let tintColor, let onTap):
            Button(action: onTap) {
                Image(systemName: systemName)
                    .font(.system(size: Dimens.iconMd, weight: .semibold))
                    .foregroundColor(tintColor)
                    .frame(width: Dimens.iconButtonSize, height: Dimens.iconButtonSize)
                    .background(Color.white.opacity(0.18), in: Circle())
            }
            .accessibilityLabel(label)
        case .title(let text, _):
            AppToolbarTitle(text: text, color: .white)
        }
    }

    // MARK: - Profile

    @ViewBuilder
    private func profileSection(_ profile: HeaderProfile) -> some View {
        switch profile {
        case .inline(let initials, let size, let name, let subtitle, let caption):
            HStack(spacing: Dimens.paddingL) {
                AvatarCircle(initials: initials, size: size)
                    .heroTransition(id: heroId.map { "\($0)-avatar" }, in: heroNamespace)
                VStack(alignment: .leading, spacing: Spacing.xs) {
                    AppSubtitleText(text: name, color: .white)
                        .heroTransition(id: heroId.map { "\($0)-name" }, in: heroNamespace)
                    if let sub = subtitle {
                        AppBodyText(text: sub, color: .white.opacity(0.85))
                    }
                    if let cap = caption {
                        AppCaptionText(text: cap, color: .white.opacity(0.7))
                    }
                }
            }
            .frame(maxWidth: .infinity, alignment: .leading)

        case .centered(let initials, let size, let name, let email, let onCameraTap):
            VStack(spacing: Spacing.none) {
                ZStack(alignment: .bottomTrailing) {
                    AvatarCircle(initials: initials, size: size)
                        .overlay(Circle().stroke(Color.white.opacity(0.45), lineWidth: Dimens.strokeThick))
                    ZStack {
                        Circle().fill(Color.appPrimaryDark).frame(width: Dimens.cameraBadgeSize, height: Dimens.cameraBadgeSize)
                        Circle().stroke(Color.white, lineWidth: Dimens.strokeMedium).frame(width: Dimens.cameraBadgeSize, height: Dimens.cameraBadgeSize)
                        Image(systemName: "camera.fill")
                            .font(.system(size: Dimens.iconXs))
                            .foregroundColor(.white)
                    }
                    .offset(x: Dimens.paddingXXS, y: Dimens.cameraBadgeOffsetY)
                    .onTapGesture { onCameraTap?() }
                }
                Spacer().frame(height: Dimens.paddingCompact)
                AppSubtitleText(text: name, color: .white, alignment: .center)
                if let mail = email {
                    AppLabelText(text: mail, color: .white.opacity(0.8), alignment: .center)
                }
            }
            .frame(maxWidth: .infinity)
        }
    }

    // MARK: - Height computation

    private var isCompact: Bool {
        textBlock == nil && profile == nil && search == nil
    }

    private var resolvedMinHeight: CGFloat {
        if isCompact {
            return CGFloat(Dimens.toolbarHeight) + Dimens.paddingL + Dimens.paddingS
        }
        var h: CGFloat = CGFloat(Dimens.toolbarHeight) + Dimens.paddingL + Dimens.paddingXXL
        if textBlock != nil { h += 60 }
        if search != nil { h += 120 }
        if let p = profile {
            switch p {
            case .inline: h += 80
            case .centered: h += 150
            }
        }
        return max(h, roundedBottom ? 200 : 160)
    }
}

// MARK: - Conditional modifier helper

private extension View {
    @ViewBuilder
    func `if`<T: View>(_ condition: Bool, transform: (Self) -> T) -> some View {
        if condition { transform(self) } else { self }
    }
}
