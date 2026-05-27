import SwiftUI
import shared

struct ProfileView: View {
    @StateObject private var viewModel = ProfileViewModelWrapper()
    @State private var notificationsEnabled = true
    @State private var selectedLanguage = "fr"
    @State private var showLogoutAlert = false
    @State private var showMenuSheet = false

    private let languages: [(String, String)] = [
        ("fr", "🇫🇷  French"),
        ("en", "🇬🇧  English"),
        ("de", "🇩🇪  German"),
        ("lb", "🇱🇺  Luxembourgish")
    ]

    private var languageLabel: String {
        languages.first { $0.0 == selectedLanguage }?.1 ?? "🇫🇷  French"
    }

    // Icon palette
    private let iconBgBlue   = Color(hex: "EEF0FD")
    private let iconBgPink   = Color(hex: "FFEBF0")
    private let iconBgOrange = Color(hex: "FFF3E0")
    private let iconBgTeal   = Color(hex: "E0F7FA")
    private let iconBgGreen  = Color(hex: "E8F5E9")
    private let iconTintPink   = Color(hex: "E91E63")
    private let iconTintOrange = Color(hex: "FF9800")
    private let iconTintTeal   = Color(hex: "00ACC1")
    private let iconTintGreen  = Color(hex: "43A047")

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: 0) {
                    profileHeader
                    contentSections
                }
            }
            .background(Color.appBackground)
            .ignoresSafeArea(edges: .top)
            .navigationBarHidden(true)
            .alert(String(localized: "profile_sign_out"), isPresented: $showLogoutAlert) {
                Button(String(localized: "action_cancel"), role: .cancel) {}
                Button(String(localized: "profile_sign_out"), role: .destructive) { viewModel.logout() }
            } message: {
                Text(String(localized: "profile_logout_confirm"))
            }
            .sheet(isPresented: $showMenuSheet) {
                menuSheet
            }
            .onChange(of: viewModel.uiState.isLoggedOut) { loggedOut in
                if loggedOut { /* handled by parent navigation */ }
            }
            .onAppear {
                if let lang = viewModel.uiState.user?.language, !lang.isEmpty {
                    selectedLanguage = lang
                }
            }
        }
    }

    // MARK: - Profile Header

    private var profileHeader: some View {
        ZStack(alignment: .top) {
            // Gradient background (extends into safe area)
            AppGradient.primary.ignoresSafeArea(edges: .top)

            // Decorative orbs
            Circle().fill(Color.white.opacity(0.05)).frame(width: 180).offset(x: -40, y: -40)
            Circle().fill(Color.white.opacity(0.04)).frame(width: 120).offset(x: UIScreen.main.bounds.width - 60, y: 20)

            VStack(spacing: 0) {
                // Toolbar row: ☰ | Profile | logout
                HStack {
                    Button { showMenuSheet = true } label: {
                        Image(systemName: "line.3.horizontal")
                            .font(.system(size: 18, weight: .medium))
                            .foregroundColor(.white)
                            .frame(width: 40, height: 40)
                            .background(Color.white.opacity(0.18), in: Circle())
                    }
                    Spacer()
                    Text(String(localized: "nav_profile"))
                        .font(.system(size: 18, weight: .bold))
                        .foregroundColor(.white)
                    Spacer()
                    Button { showLogoutAlert = true } label: {
                        Image(systemName: "rectangle.portrait.and.arrow.right")
                            .font(.system(size: 18, weight: .medium))
                            .foregroundColor(.white)
                            .frame(width: 40, height: 40)
                            .background(Color.white.opacity(0.18), in: Circle())
                    }
                }
                .padding(.horizontal, 20)
                .frame(height: 56)

                Spacer().frame(height: 12)

                // Avatar with white ring and camera badge
                ZStack(alignment: .bottomTrailing) {
                    AvatarCircle(initials: viewModel.uiState.user?.initials ?? "?", size: Dimens.avatarXl)
                        // White ring around avatar (mirrors Android AvatarCircle's border)
                        .overlay(Circle().stroke(Color.white.opacity(0.45), lineWidth: 3))
                    // Camera badge — dark navy background, white icon
                    ZStack {
                        Circle().fill(Color.appPrimaryDark).frame(width: 30, height: 30)
                        Circle().stroke(Color.white, lineWidth: 1.5).frame(width: 30, height: 30)
                        Image(systemName: "camera.fill")
                            .font(.system(size: 13))
                            .foregroundColor(.white)
                    }
                    .offset(x: 2, y: 4)
                }

                Spacer().frame(height: 10)

                Text(viewModel.uiState.user?.fullName ?? String(localized: "profile_loading_name"))
                    .font(.system(size: 20, weight: .bold))
                    .foregroundColor(.white)
                    .multilineTextAlignment(.center)

                Text(viewModel.uiState.user?.email ?? "")
                    .font(.system(size: 13))
                    .foregroundColor(.white.opacity(0.8))
                    .multilineTextAlignment(.center)

                Spacer().frame(height: 24)
            }
        }
        .clipShape(UnevenRoundedRectangle(bottomLeadingRadius: 30, bottomTrailingRadius: 30))
    }

    // MARK: - Content

    private var contentSections: some View {
        VStack(spacing: 12) {
            Spacer().frame(height: 16)

            // Personal Information card
            profileCard {
                // Header with Edit button
                HStack(spacing: 6) {
                    Image(systemName: "person.fill")
                        .font(.system(size: 14))
                        .foregroundColor(.appPrimary)
                    Text(String(localized: "profile_personal_info_label"))
                        .font(.system(size: 12, weight: .bold))
                        .foregroundColor(.appPrimary)
                        .tracking(0.5)
                    Spacer()
                    NavigationLink(destination: EditProfileView()) {
                        HStack(spacing: 4) {
                            Image(systemName: "pencil").font(.system(size: 11))
                            Text(String(localized: "profile_edit_button")).font(.system(size: 12))
                        }
                        .foregroundColor(.appPrimary)
                        .padding(.horizontal, 10)
                        .frame(height: 28)
                        .overlay(RoundedRectangle(cornerRadius: 14).stroke(Color.appPrimary, lineWidth: 1))
                    }
                }
                .padding(.horizontal, 16)
                .padding(.vertical, 12)

                Divider()

                profileInfoRow(iconName: "person.fill", iconTint: .appPrimary, iconBg: iconBgBlue,
                               label: String(localized: "profile_full_name_label"), value: viewModel.uiState.user?.fullName ?? "—")
                Divider().padding(.horizontal, 16)
                profileInfoRow(iconName: "face.smiling.fill", iconTint: iconTintPink, iconBg: iconBgPink,
                               label: String(localized: "profile_gender_label"), value: genderDisplay)
                Divider().padding(.horizontal, 16)
                profileInfoRow(iconName: "birthday.cake.fill", iconTint: iconTintOrange, iconBg: iconBgOrange,
                               label: String(localized: "profile_dob_label"), value: dobDisplay)
                Divider().padding(.horizontal, 16)
                profileInfoRow(iconName: "phone.fill", iconTint: iconTintTeal, iconBg: iconBgTeal,
                               label: String(localized: "profile_phone_label"), value: viewModel.uiState.user?.phone.isEmpty == false ? viewModel.uiState.user!.phone : "—")
                Divider().padding(.horizontal, 16)
                profileInfoRow(iconName: "creditcard.fill", iconTint: iconTintGreen, iconBg: iconBgGreen,
                               label: String(localized: "profile_cns_label"), value: maskedCns)
            }

            // Preferences card
            profileCard {
                HStack(spacing: 6) {
                    Image(systemName: "bell.fill").font(.system(size: 14)).foregroundColor(.appPrimary)
                    Text(String(localized: "profile_preferences_label")).font(.system(size: 12, weight: .bold)).foregroundColor(.appPrimary).tracking(0.5)
                    Spacer()
                }
                .padding(.horizontal, 16).padding(.vertical, 12)

                Divider()

                // Notifications toggle
                HStack(spacing: 14) {
                    iconBox(iconName: "envelope.fill", iconTint: iconTintTeal, iconBg: iconBgTeal)
                    VStack(alignment: .leading, spacing: 2) {
                        Text(String(localized: "profile_notifications_label")).font(.system(size: 11)).foregroundColor(.appTextSecondary).tracking(0.5)
                        Text(String(localized: "profile_email_notifications")).font(.system(size: 14, weight: .medium)).foregroundColor(.appTextPrimary)
                    }
                    Spacer()
                    Toggle("", isOn: $notificationsEnabled).tint(.appPrimary).labelsHidden()
                }
                .padding(.horizontal, 16).padding(.vertical, 10)

                Divider().padding(.horizontal, 16)

                // Language picker
                VStack(alignment: .leading, spacing: 8) {
                    Text(String(localized: "profile_language_label")).font(.system(size: 11)).foregroundColor(.appTextSecondary).tracking(0.5)
                    Menu {
                        ForEach(languages, id: \.0) { code, label in
                            Button(label) { selectedLanguage = code }
                        }
                    } label: {
                        HStack {
                            Text(languageLabel).font(.system(size: 14)).foregroundColor(.appTextPrimary)
                            Spacer()
                            Image(systemName: "chevron.up.chevron.down")
                                .font(.system(size: 12))
                                .foregroundColor(.appTextSecondary)
                        }
                        .padding(.horizontal, 14)
                        .frame(height: 48)
                        .background(Color.appBackground)
                        .cornerRadius(10)
                        .overlay(RoundedRectangle(cornerRadius: 10).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
                    }
                }
                .padding(.horizontal, 16).padding(.bottom, 16)
            }

            // Login & Security card
            profileCard {
                HStack(spacing: 6) {
                    Image(systemName: "shield.fill").font(.system(size: 14)).foregroundColor(.appPrimary)
                    Text(String(localized: "profile_login_security")).font(.system(size: 12, weight: .bold)).foregroundColor(.appPrimary).tracking(0.5)
                    Spacer()
                }
                .padding(.horizontal, 16).padding(.vertical, 12)

                Divider()

                NavigationLink(destination: ChangeEmailView()) {
                    securityRow(iconName: "at", iconTint: iconTintTeal, iconBg: iconBgTeal,
                                label: String(localized: "profile_login_email_label"),
                                value: viewModel.uiState.user?.email ?? "—")
                }
                .buttonStyle(.plain)

                Divider().padding(.horizontal, 16)

                NavigationLink(destination: ChangePasswordView()) {
                    securityRow(iconName: "key.fill", iconTint: iconTintPink, iconBg: iconBgPink,
                                label: String(localized: "profile_security_label"),
                                value: String(localized: "profile_change_password"))
                }
                .buttonStyle(.plain)
            }

            // Sign Out
            Button {
                showLogoutAlert = true
            } label: {
                HStack(spacing: 8) {
                    Image(systemName: "rectangle.portrait.and.arrow.right").font(.system(size: 18))
                    Text(String(localized: "profile_sign_out")).font(.system(size: 15, weight: .semibold))
                }
                .foregroundColor(.appDanger)
                .frame(maxWidth: .infinity)
                .frame(height: 52)
                .background(Color.appDangerBg)
                .cornerRadius(12)
            }
            .padding(.horizontal, 16)

            Spacer().frame(height: 32)
        }
        .padding(.horizontal, 16)
    }

    // MARK: - Derived values

    private var genderDisplay: String {
        switch viewModel.uiState.user?.gender.lowercased() {
        case "male", "man": return String(localized: "gender_male")
        case "female", "woman": return String(localized: "gender_female")
        case "other": return String(localized: "gender_other")
        default: return viewModel.uiState.user?.gender.isEmpty == false ? viewModel.uiState.user!.gender : "—"
        }
    }

    private var dobDisplay: String {
        guard let dob = viewModel.uiState.user?.dateOfBirth, !dob.isEmpty else { return "—" }
        let inputFormatter = DateFormatter()
        inputFormatter.dateFormat = "yyyy-MM-dd"
        let outputFormatter = DateFormatter()
        outputFormatter.dateStyle = .long
        outputFormatter.locale = Locale.current
        return inputFormatter.date(from: dob).map { outputFormatter.string(from: $0) } ?? dob
    }

    private var maskedCns: String {
        guard let cns = viewModel.uiState.user?.cnsNumber, !cns.isEmpty else { return "—" }
        return cns.count > 9 ? "\(cns.prefix(9)) ••••" : cns
    }

    // MARK: - Helpers

    private func profileCard<Content: View>(@ViewBuilder content: () -> Content) -> some View {
        VStack(spacing: 0) {
            content()
        }
        .background(Color.appSurface)
        .cornerRadius(16)
        .shadow(color: Color.black.opacity(0.06), radius: 8, y: 2)
    }

    private func profileInfoRow(iconName: String, iconTint: Color, iconBg: Color, label: String, value: String) -> some View {
        HStack(spacing: 14) {
            iconBox(iconName: iconName, iconTint: iconTint, iconBg: iconBg)
            VStack(alignment: .leading, spacing: 2) {
                Text(label).font(.system(size: 11)).foregroundColor(.appTextSecondary).tracking(0.5)
                Text(value).font(.system(size: 14, weight: .medium)).foregroundColor(.appTextPrimary)
            }
            Spacer()
            Image(systemName: "chevron.right").font(.system(size: 13)).foregroundColor(.appTextHint)
        }
        .padding(.horizontal, 16).padding(.vertical, 12)
    }

    private func securityRow(iconName: String, iconTint: Color, iconBg: Color, label: String, value: String) -> some View {
        HStack(spacing: 14) {
            iconBox(iconName: iconName, iconTint: iconTint, iconBg: iconBg)
            VStack(alignment: .leading, spacing: 2) {
                Text(label).font(.system(size: 11)).foregroundColor(.appTextSecondary).tracking(0.5)
                Text(value).font(.system(size: 14, weight: .medium)).foregroundColor(.appTextPrimary)
            }
            Spacer()
            Image(systemName: "chevron.right").font(.system(size: 13)).foregroundColor(.appTextHint)
        }
        .padding(.horizontal, 16).padding(.vertical, 12)
    }

    private func iconBox(iconName: String, iconTint: Color, iconBg: Color) -> some View {
        ZStack {
            RoundedRectangle(cornerRadius: 10).fill(iconBg).frame(width: 40, height: 40)
            Image(systemName: iconName).font(.system(size: 18)).foregroundColor(iconTint)
        }
    }

    private var menuSheet: some View {
        VStack(spacing: 0) {
            HStack {
                Text(String(localized: "drawer_menu_title")).font(.system(size: 18, weight: .bold)).foregroundColor(.appTextPrimary)
                Spacer()
                Button { showMenuSheet = false } label: {
                    Image(systemName: "xmark").font(.system(size: 16, weight: .medium)).foregroundColor(.appTextSecondary)
                }
            }
            .padding()
            Divider()
            Group {
                menuItem(icon: "info.circle", label: String(localized: "drawer_who_are_we"))
                menuItem(icon: "doc.text", label: String(localized: "drawer_terms"))
                menuItem(icon: "hand.raised", label: String(localized: "drawer_privacy_policy"))
                menuItem(icon: "envelope", label: String(localized: "drawer_contact_us"))
                Divider().padding(.horizontal)
                menuItem(icon: "cross.case", label: String(localized: "drawer_emergencies"))
                menuItem(icon: "pills", label: String(localized: "drawer_pharmacy_on_call"))
                menuItem(icon: "building.columns", label: String(localized: "drawer_national_health_fund"))
                menuItem(icon: "building.2", label: String(localized: "drawer_ministry_of_health"))
            }
            Spacer()
        }
        .background(Color.appSurface)
    }

    private func menuItem(icon: String, label: String) -> some View {
        Button {
            showMenuSheet = false
        } label: {
            HStack(spacing: 14) {
                Image(systemName: icon).font(.system(size: 18)).foregroundColor(.appPrimary).frame(width: 24)
                Text(label).font(.system(size: 15)).foregroundColor(.appTextPrimary)
                Spacer()
            }
            .padding(.horizontal).padding(.vertical, 14)
        }
    }
}
