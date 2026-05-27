import SwiftUI

struct ProfileView: View {
    @StateObject private var viewModel = ProfileViewModelWrapper()
    @State private var navigateToEditProfile = false
    @State private var navigateToChangeEmail = false
    @State private var navigateToChangePassword = false
    @State private var navigateToLanding = false
    @State private var showLogoutAlert = false
    @State private var notificationsEnabled = true

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 0) {
                    // Gradient header with avatar
                    GradientHeader(minHeight: 180) {
                        HStack(spacing: Dimens.paddingL) {
                            AvatarCircle(initials: viewModel.uiState.user?.initials ?? "?", size: Dimens.avatarXl)
                            VStack(alignment: .leading, spacing: 4) {
                                Text(viewModel.uiState.user?.fullName ?? "Loading…")
                                    .font(.system(size: 20, weight: .bold))
                                    .foregroundColor(.white)
                                Text(viewModel.uiState.user?.email ?? "")
                                    .font(.system(size: 13))
                                    .foregroundColor(.white.opacity(0.75))
                                if let pt = viewModel.uiState.user?.profileType {
                                    Text(pt.name.capitalized)
                                        .font(.system(size: 12))
                                        .foregroundColor(.white.opacity(0.6))
                                }
                            }
                        }
                    }

                    VStack(spacing: 16) {
                        // Personal info section
                        profileSection(header: "Personal Information") {
                            profileItem(icon: "person.fill", title: "Edit Profile", subtitle: "Update your personal details") {
                                navigateToEditProfile = true
                            }
                        }

                        // Security section
                        profileSection(header: "Security") {
                            profileItem(icon: "envelope.fill", title: "Change Email", subtitle: viewModel.uiState.user?.email ?? "") {
                                navigateToChangeEmail = true
                            }
                            Divider().padding(.horizontal, Dimens.paddingL)
                            profileItem(icon: "lock.fill", title: "Change Password", subtitle: "Update your password") {
                                navigateToChangePassword = true
                            }
                        }

                        // Preferences section
                        profileSection(header: "Preferences") {
                            HStack {
                                HStack(spacing: 12) {
                                    ZStack {
                                        RoundedRectangle(cornerRadius: 10)
                                            .fill(Color.appPrimaryLight)
                                            .frame(width: 40, height: 40)
                                        Image(systemName: "bell.fill")
                                            .font(.system(size: 18))
                                            .foregroundColor(.appPrimary)
                                    }
                                    VStack(alignment: .leading, spacing: 2) {
                                        Text("Notifications").font(.system(size: 14, weight: .medium)).foregroundColor(.appTextPrimary)
                                        Text("Appointment reminders").font(.system(size: 12)).foregroundColor(.appTextSecondary)
                                    }
                                }
                                Spacer()
                                Toggle("", isOn: $notificationsEnabled)
                                    .tint(.appPrimary)
                            }
                            .padding(.horizontal, Dimens.paddingL)
                            .padding(.vertical, Dimens.paddingM)
                        }

                        // Legal section
                        profileSection(header: "Legal") {
                            profileItem(icon: "info.circle.fill", title: "About eSklepios", subtitle: "Version 1.0.0") {}
                            Divider().padding(.horizontal, Dimens.paddingL)
                            profileItem(icon: "doc.text.fill", title: "Terms of Service", subtitle: "") {}
                            Divider().padding(.horizontal, Dimens.paddingL)
                            profileItem(icon: "hand.raised.fill", title: "Privacy Policy", subtitle: "") {}
                        }

                        // Logout
                        Button(action: { showLogoutAlert = true }) {
                            HStack(spacing: 8) {
                                Image(systemName: "rectangle.portrait.and.arrow.right")
                                    .font(.system(size: 16))
                                Text("Sign Out")
                                    .font(.system(size: 15, weight: .semibold))
                            }
                            .frame(maxWidth: .infinity)
                            .frame(height: Dimens.buttonHeight)
                            .background(Color.appDangerBg)
                            .foregroundColor(.appDanger)
                            .cornerRadius(Dimens.radiusPill)
                        }
                        .padding(.horizontal, Dimens.paddingL)
                        .padding(.bottom, Dimens.paddingXXXL)
                    }
                    .padding(.top, Dimens.paddingL)
                }
            }
            .background(Color.appBackground)
            .navigationBarHidden(true)
            .alert("Sign Out", isPresented: $showLogoutAlert) {
                Button("Cancel", role: .cancel) {}
                Button("Sign Out", role: .destructive) { viewModel.logout() }
            } message: {
                Text("Are you sure you want to sign out?")
            }
            .onChange(of: viewModel.uiState.isLoggedOut) { loggedOut in
                if loggedOut { navigateToLanding = true }
            }
            .navigationDestination(isPresented: $navigateToEditProfile) { EditProfileView() }
            .navigationDestination(isPresented: $navigateToChangeEmail) { ChangeEmailView() }
            .navigationDestination(isPresented: $navigateToChangePassword) { ChangePasswordView() }
            .navigationDestination(isPresented: $navigateToLanding) { LandingView() }
        }
    }

    private func profileSection<Content: View>(header: String, @ViewBuilder content: () -> Content) -> some View {
        VStack(alignment: .leading, spacing: 0) {
            Text(header)
                .font(.system(size: 12, weight: .semibold))
                .foregroundColor(.appTextSecondary)
                .padding(.horizontal, Dimens.paddingXXL)
                .padding(.bottom, 6)

            AppCard(padding: 0) {
                content()
            }
            .padding(.horizontal, Dimens.paddingL)
        }
    }

    private func profileItem(icon: String, title: String, subtitle: String, action: @escaping () -> Void) -> some View {
        Button(action: action) {
            HStack(spacing: 14) {
                ZStack {
                    RoundedRectangle(cornerRadius: 10)
                        .fill(Color.appPrimaryLight)
                        .frame(width: 40, height: 40)
                    Image(systemName: icon)
                        .font(.system(size: 18))
                        .foregroundColor(.appPrimary)
                }
                VStack(alignment: .leading, spacing: 2) {
                    Text(title).font(.system(size: 14, weight: .medium)).foregroundColor(.appTextPrimary)
                    if !subtitle.isEmpty {
                        Text(subtitle).font(.system(size: 12)).foregroundColor(.appTextSecondary).lineLimit(1)
                    }
                }
                Spacer()
                Image(systemName: "chevron.right")
                    .font(.system(size: 13))
                    .foregroundColor(.appTextHint)
            }
            .padding(.horizontal, Dimens.paddingL)
            .padding(.vertical, Dimens.paddingM)
        }
    }
}
