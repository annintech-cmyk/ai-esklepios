import SwiftUI

struct LandingView: View {
    @State private var searchQuery = ""
    @State private var locationQuery = ""
    @State private var navigateToSearch = false
    @State private var navigateToLogin = false

    var body: some View {
        NavigationStack {
            GeometryReader { geo in
                ZStack {
                    Color.appPrimaryLight
                        .ignoresSafeArea()

                    ScrollView(showsIndicators: false) {
                        VStack(spacing: -(geo.size.height * 0.20)) {
                            LandingHeroSection(
                                heroHeight: geo.size.height * 0.65,
                                onSignInTap: { navigateToLogin = true }
                            )
                            LandingSearchCard(
                                searchQuery: $searchQuery,
                                locationQuery: $locationQuery,
                                onSearchTap: { navigateToSearch = true }
                            )
                            .zIndex(1)
                        }
                    }
                    .ignoresSafeArea(edges: .top)
                }
            }
            .navigationBarHidden(true)
            .navigationDestination(isPresented: $navigateToSearch) { SearchResultsView(query: searchQuery) }
            .navigationDestination(isPresented: $navigateToLogin) { LoginView() }
        }
    }
}

// MARK: - Hero Section

private struct LandingHeroSection: View {
    let heroHeight: CGFloat
    let onSignInTap: () -> Void

    var body: some View {
        ZStack(alignment: .topLeading) {
            AppGradient.primaryVertical
                .ignoresSafeArea(edges: .top)

            Circle()
                .fill(Color.white.opacity(0.05))
                .frame(width: Dimens.orbXl)
                .offset(x: Dimens.orbOffsetLandingNeg, y: Dimens.orbOffsetLandingNeg)

            VStack(alignment: .leading, spacing: Spacing.none) {
                HStack {
                    Spacer()
                    GlassButton(
                        text: NSLocalizedString("action_sign_in", value: "Sign In", comment: ""),
                        action: onSignInTap
                    )
                }
                .padding(.top, Dimens.paddingM)
                .padding(.bottom, Dimens.paddingXXXL)
                .padding(.horizontal, Dimens.paddingXXL)

                HStack(alignment: .bottom, spacing: Dimens.strokeThin) {
                    AppTitleText(text: NSLocalizedString("app_name", value: "eSklepios", comment: ""), color: .white)
                    // Brand accent dot uses a custom typeface size — raw Text() intentional (compound brand mark)
                    Text(".")
                        .font(.appSans(32, weight: .black))
                        .foregroundColor(.appTealAccent)
                }
                .padding(.horizontal, Dimens.paddingXXL)

                Spacer().frame(height: Dimens.paddingXL)

                // Multi-color compound text (white + teal accent) — raw Text() + Text() concatenation required by SwiftUI
                (
                    Text(NSLocalizedString("landing_hero_prefix", value: "Make an appointment\nwith a ", comment: ""))
                        .foregroundColor(.white)
                    + Text(NSLocalizedString("landing_hero_accent", value: "health\nprofessional", comment: ""))
                        .foregroundColor(.appTealAccent)
                )
                .font(.appSans(30, weight: .heavy))
                .lineSpacing(Dimens.paddingXS)
                .fixedSize(horizontal: false, vertical: true)
                .padding(.horizontal, Dimens.paddingXXL)

                Spacer()
            }
        }
        .frame(height: heroHeight)
        .clipShape(
            UnevenRoundedRectangle(
                topLeadingRadius: Radius.none,
                bottomLeadingRadius: Radius.pill,
                bottomTrailingRadius: Radius.pill,
                topTrailingRadius: Radius.none
            )
        )
    }
}

// MARK: - Search Card

private struct LandingSearchCard: View {
    @Binding var searchQuery: String
    @Binding var locationQuery: String
    let onSearchTap: () -> Void

    var body: some View {
        VStack(alignment: .leading, spacing: Spacing.none) {
            SearchCard(
                searchQuery: $searchQuery,
                locationQuery: $locationQuery,
                onSearchTap: onSearchTap
            )

            Spacer().frame(height: Dimens.paddingXL)
            Divider()
            Spacer().frame(height: Dimens.paddingXL)

            VStack(alignment: .leading, spacing: Dimens.paddingM) {
                LandingTrustRow(
                    systemIcon: "checkmark.shield",
                    label: NSLocalizedString("landing_trust_licensed", value: "Graduated and Verified practitioners", comment: "")
                )
                LandingTrustRow(
                    systemIcon: "lock.fill",
                    label: NSLocalizedString("landing_trust_secure", value: "Your personal data secured", comment: "")
                )
                LandingTrustRow(
                    systemIcon: "location.north.fill",
                    label: NSLocalizedString("landing_trust_free", value: "Closest to you", comment: "")
                )
            }

            Spacer().frame(height: Dimens.paddingXXL)
        }
        .padding(.horizontal, Dimens.paddingXXL)
        .padding(.top, Dimens.paddingXXL + Dimens.paddingS)
        .background(Color.appSurface)
    }
}

// MARK: - Trust Row

private struct LandingTrustRow: View {
    let systemIcon: String
    let label: String

    var body: some View {
        HStack(spacing: Dimens.paddingS + Dimens.paddingXS) {
            AppIcon(systemName: systemIcon, tint: .appTextSecondary, size: Dimens.iconMd)
                .frame(width: Dimens.iconMd)
            AppBodyText(text: label)
        }
    }
}