import SwiftUI

struct LandingView: View {
    @State private var searchQuery = ""
    @State private var locationQuery = ""
    @State private var navigateToHome = false
    @State private var navigateToLogin = false
    @State private var navigateToRegister = false

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 0) {
                    // Gradient hero header
                    GradientHeader(minHeight: 260) {
                        VStack(alignment: .leading, spacing: 8) {
                            Text("eSklepios")
                                .font(.system(size: 34, weight: .bold))
                                .foregroundColor(.white)
                            Text("Your Health, Connected")
                                .font(.system(size: 16, weight: .medium))
                                .foregroundColor(.white.opacity(0.85))
                            Text("Find practitioners, book appointments, manage your healthcare")
                                .font(.system(size: 13))
                                .foregroundColor(.white.opacity(0.7))
                                .fixedSize(horizontal: false, vertical: true)
                        }
                    }

                    // Search card - overlapping header
                    AppCard {
                        VStack(spacing: 12) {
                            HStack {
                                Image(systemName: "magnifyingglass")
                                    .foregroundColor(.appPrimaryMid)
                                TextField("Search specialty, doctor name…", text: $searchQuery)
                                    .font(.system(size: 15))
                            }
                            .padding(12)
                            .background(Color.appBackground)
                            .cornerRadius(Dimens.radiusMd)

                            HStack {
                                Image(systemName: "location")
                                    .foregroundColor(.appPrimaryMid)
                                TextField("City or postal code", text: $locationQuery)
                                    .font(.system(size: 15))
                            }
                            .padding(12)
                            .background(Color.appBackground)
                            .cornerRadius(Dimens.radiusMd)

                            PrimaryButton(title: "Find Practitioners") {
                                navigateToHome = true
                            }
                        }
                    }
                    .padding(.horizontal, Dimens.paddingL)
                    .padding(.top, -20)

                    // Trust indicators
                    HStack(spacing: Dimens.paddingXXL) {
                        TrustItem(icon: "stethoscope", label: "Licensed")
                        TrustItem(icon: "lock.shield", label: "Secure")
                        TrustItem(icon: "heart", label: "Free")
                    }
                    .padding(.horizontal, Dimens.paddingXXL)
                    .padding(.top, Dimens.paddingXXL)

                    // Auth buttons
                    VStack(spacing: 12) {
                        GhostButton(title: "Sign In") { navigateToLogin = true }
                            .frame(maxWidth: .infinity)

                        Button(action: { navigateToRegister = true }) {
                            Text("Create Account")
                                .font(.system(size: 14))
                                .foregroundColor(.appTextSecondary)
                        }
                    }
                    .padding(.horizontal, Dimens.paddingXXL)
                    .padding(.top, Dimens.paddingXL)
                    .padding(.bottom, Dimens.paddingXXXL)
                }
            }
            .background(Color.appBackground)
            .navigationBarHidden(true)
            .navigationDestination(isPresented: $navigateToHome) {
                HomeView()
            }
            .navigationDestination(isPresented: $navigateToLogin) {
                LoginView()
            }
            .navigationDestination(isPresented: $navigateToRegister) {
                RegisterView()
            }
        }
    }
}

private struct TrustItem: View {
    let icon: String
    let label: String

    var body: some View {
        VStack(spacing: 8) {
            ZStack {
                RoundedRectangle(cornerRadius: 12)
                    .fill(Color.appPrimaryLight)
                    .frame(width: 48, height: 48)
                Image(systemName: icon)
                    .font(.system(size: 22))
                    .foregroundColor(.appPrimary)
            }
            Text(label)
                .font(.system(size: 12, weight: .medium))
                .foregroundColor(.appTextSecondary)
        }
    }
}
