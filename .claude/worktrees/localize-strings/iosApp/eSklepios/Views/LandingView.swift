import SwiftUI

struct LandingView: View {
    @State private var searchQuery   = ""
    @State private var locationQuery = "Luxembourg"
    @State private var navigateToHome    = false
    @State private var navigateToLogin   = false
    @State private var navigateToRegister = false

    // Teal/cyan accent that sits on the blue gradient
    private let landingTeal = Color(hex: "4DD0E1")
    private let fieldBg     = Color(hex: "F3F4F6")

    var body: some View {
        NavigationStack {
            ZStack {
                // Light blue visible behind / below the white card
                Color.appPrimaryLight
                    .ignoresSafeArea()

                ScrollView(showsIndicators: false) {
                    VStack(spacing: -28) {          // negative spacing creates the overlap
                        heroSection
                        searchCard
                    }
                }
                .ignoresSafeArea(edges: .top)
            }
            .preferredColorScheme(.dark)            // white status-bar icons on blue bg
            .navigationBarHidden(true)
            .navigationDestination(isPresented: $navigateToHome)     { HomeView() }
            .navigationDestination(isPresented: $navigateToLogin)    { LoginView() }
            .navigationDestination(isPresented: $navigateToRegister) { RegisterView() }
        }
    }

    // MARK: - Hero section (gradient background)

    private var heroSection: some View {
        ZStack(alignment: .topLeading) {
            // Gradient fills all the way behind the status bar
            AppGradient.primary
                .ignoresSafeArea(edges: .top)

            // Subtle decorative orbs (consistent with other screens)
            Circle()
                .fill(Color.white.opacity(0.05))
                .frame(width: 200)
                .offset(x: -50, y: -50)
            Circle()
                .fill(Color.white.opacity(0.04))
                .frame(width: 130)
                .offset(x: UIScreen.main.bounds.width - 60, y: 10)

            VStack(alignment: .leading, spacing: 0) {

                // "Connect" pill button – top right
                HStack {
                    Spacer()
                    Button(action: { navigateToLogin = true }) {
                        Text(String(localized: "landing_connect"))
                            .font(.system(size: 15, weight: .semibold))
                            .foregroundColor(.white)
                            .padding(.horizontal, 22)
                            .padding(.vertical, 10)
                            .background(Color.white.opacity(0.18), in: Capsule())
                    }
                }
                .padding(.top, 12)
                .padding(.bottom, 36)
                .padding(.horizontal, 24)

                // Brand: eSklepios.
                HStack(alignment: .bottom, spacing: 1) {
                    Text(String(localized: "app_name"))
                        .font(.system(size: 26, weight: .bold))
                        .foregroundColor(.white)
                    Text(".")
                        .font(.system(size: 32, weight: .black))
                        .foregroundColor(landingTeal)
                }
                .padding(.horizontal, 24)

                Spacer().frame(height: 20)

                // Hero headline – white + teal mixed using Text concatenation
                Group {
                    Text(String(localized: "landing_hero_prefix") + " ")
                        .foregroundColor(.white)
                    + Text(String(localized: "landing_hero_accent"))
                        .foregroundColor(landingTeal)
                }
                .font(.system(size: 30, weight: .heavy))
                .lineSpacing(4)
                .fixedSize(horizontal: false, vertical: true)
                .padding(.horizontal, 24)

                Spacer().frame(height: 48)
            }
        }
    }

    // MARK: - Search card (white, rounded top)

    private var searchCard: some View {
        VStack(alignment: .leading, spacing: 0) {
            VStack(spacing: 12) {

                // Research / specialty field
                landingField(
                    systemIcon: "stethoscope",
                    iconColor: .appTextHint,
                    placeholder: String(localized: "landing_search_research"),
                    text: $searchQuery
                )

                // Location field
                landingField(
                    systemIcon: "mappin.and.ellipse",
                    iconColor: .appPrimaryMid,
                    placeholder: String(localized: "landing_location_hint"),
                    text: $locationQuery
                )

                // Search →  button
                Button(action: { navigateToHome = true }) {
                    HStack(spacing: 8) {
                        Text(String(localized: "landing_search_button"))
                            .font(.system(size: 16, weight: .semibold))
                        Image(systemName: "arrow.right")
                            .font(.system(size: 14, weight: .semibold))
                    }
                    .foregroundColor(.white)
                    .frame(maxWidth: .infinity)
                    .frame(height: 52)
                    .background(Color.appPrimary)
                    .cornerRadius(14)
                }
            }

            Spacer().frame(height: 20)
            Divider()
            Spacer().frame(height: 20)

            // Trust items – vertical list
            VStack(alignment: .leading, spacing: 12) {
                landingTrustRow(systemIcon: "checkmark.shield",      label: String(localized: "landing_trust_verified"))
                landingTrustRow(systemIcon: "location.north.fill",   label: String(localized: "landing_trust_closest"))
                landingTrustRow(systemIcon: "lock.fill",             label: String(localized: "landing_trust_data_secured"))
            }

            Spacer().frame(height: 48)
        }
        .padding(.horizontal, 24)
        .padding(.top, 28)
        // White background with large rounded top corners
        .background(Color.white)
        .clipShape(UnevenRoundedRectangle(topLeadingRadius: 32, topTrailingRadius: 32))
    }

    // MARK: - Helpers

    private func landingField(
        systemIcon: String,
        iconColor: Color,
        placeholder: String,
        text: Binding<String>
    ) -> some View {
        HStack(spacing: 10) {
            Image(systemName: systemIcon)
                .font(.system(size: 16))
                .foregroundColor(iconColor)
                .frame(width: 22)
            TextField(placeholder, text: text)
                .font(.system(size: 15))
                .foregroundColor(.appTextPrimary)
                .autocorrectionDisabled()
                .autocapitalization(.none)
        }
        .padding(.horizontal, 16)
        .frame(height: 52)
        .background(fieldBg)
        .cornerRadius(14)
    }

    private func landingTrustRow(systemIcon: String, label: String) -> some View {
        HStack(spacing: 10) {
            Image(systemName: systemIcon)
                .font(.system(size: 15))
                .foregroundColor(.appTextSecondary)
                .frame(width: 20)
            Text(label)
                .font(.system(size: 14))
                .foregroundColor(.appTextSecondary)
        }
    }
}
