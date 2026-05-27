import SwiftUI
import shared

struct ProfileView: View {
    @StateObject private var viewModel = ProfileViewModelWrapper()
    @State private var notificationsEnabled = true
    @State private var selectedLanguage = "fr"
    @State private var showLogoutAlert = false
    @State private var showMenuSheet = false
    @Environment(\.openURL) private var openURL

    /// Display tuples for the language picker — assembled from the shared
    /// `supportedLanguages` source of truth (Rule A-13). Flag emoji comes from
    /// the shared model; the localized language name comes from Twine via
    /// `NSLocalizedString` keyed `language_<code>`.
    private var languages: [(code: String, label: String)] {
        LocalesKt.supportedLanguages.map { lang in
            let twineKey = "language_\(lang.code)"
            let localized = NSLocalizedString(twineKey, value: lang.englishName, comment: "")
            return (code: lang.code, label: "\(lang.flagEmoji)  \(localized)")
        }
    }

    private var languageLabel: String {
        languages.first { $0.code == selectedLanguage }?.label
            ?? languages.first?.label
            ?? ""
    }

    // Icon palette — resolved from AppColors tokens
    private let iconBgBlue   = Color.appIconBgBlue
    private let iconBgPink   = Color.appIconBgPink
    private let iconBgOrange = Color.appIconBgOrange
    private let iconBgTeal   = Color.appIconBgTeal
    private let iconBgGreen  = Color.appIconBgGreen
    private let iconTintPink   = Color.appIconTintPink
    private let iconTintOrange = Color.appIconTintOrange
    private let iconTintTeal   = Color.appIconTintTeal
    private let iconTintGreen  = Color.appIconTintGreen

    var body: some View {
        NavigationStack {
            ScrollView {
                VStack(spacing: Spacing.none) {
                    AppGradientHeaderView(
                        roundedBottom: true,
                        leading: .iconButton(
                            systemName: "line.3.horizontal",
                            accessibilityLabel: NSLocalizedString("cd_menu", value: "Menu", comment: ""),
                            action: { showMenuSheet = true }
                        ),
                        center: .title(text: NSLocalizedString("screen_profile", value: "Profile", comment: "")),
                        trailing: .iconButton(
                            systemName: "rectangle.portrait.and.arrow.right",
                            accessibilityLabel: NSLocalizedString("cd_sign_out", value: "Sign out", comment: ""),
                            action: { showLogoutAlert = true }
                        ),
                        profile: .centered(
                            initials: viewModel.uiState.user?.initials ?? "?",
                            avatarSize: Sizing.avatarXl,
                            name: viewModel.uiState.user?.fullName
                                ?? NSLocalizedString("profile_loading_name", value: "Loading…", comment: ""),
                            email: viewModel.uiState.user?.email
                        )
                    )
                    contentSections
                }
            }
            .background(Color.appBackground)
            .ignoresSafeArea(edges: .top)
            .navigationBarHidden(true)
            .alert(NSLocalizedString("profile_logout_alert_title", value: "Sign Out", comment: ""),
                   isPresented: $showLogoutAlert) {
                Button(NSLocalizedString("action_cancel", value: "Cancel", comment: ""), role: .cancel) {}
                Button(NSLocalizedString("action_logout", value: "Sign Out", comment: ""),
                       role: .destructive) { viewModel.logout() }
            } message: {
                // UI-14 exemption: SwiftUI .alert message: slot — system slot, can't use AppBodyText wrapper here.
                Text(NSLocalizedString("profile_logout_confirm",
                                       value: "Are you sure you want to sign out?",
                                       comment: ""))
            }
            .sheet(isPresented: $showMenuSheet) { menuSheet }
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

    // MARK: - Content

    private var contentSections: some View {
        VStack(spacing: Spacing.m) {
            Spacer().frame(height: Spacing.l)

            // Personal Information card
            profileCard {
                HStack(spacing: Spacing.tiny) {
                    AppIcon(systemName: "person.fill", tint: .appPrimary, size: Dimens.iconMicro)
                    AppCaptionText(
                        text: NSLocalizedString("profile_section_personal",
                                                value: "PERSONAL INFORMATION", comment: ""),
                        color: .appPrimary
                    )
                    Spacer()
                    NavigationLink(destination: EditProfileView()) {
                        HStack(spacing: Spacing.xs) {
                            AppIcon(systemName: "pencil", tint: .appPrimary, size: Dimens.iconXs)
                            AppCaptionText(
                                text: NSLocalizedString("action_edit", value: "Edit", comment: ""),
                                color: .appPrimary
                            )
                        }
                        .foregroundColor(.appPrimary)
                        .padding(.horizontal, Spacing.compact)
                        .frame(height: Dimens.editButtonHeight)
                        .overlay(
                            RoundedRectangle(cornerRadius: Radius.action)
                                .stroke(Color.appPrimary, lineWidth: Dimens.strokeThin)
                        )
                    }
                }
                .padding(.horizontal, Spacing.l)
                .padding(.vertical, Spacing.m)

                Divider()

                profileInfoRow(
                    iconName: "person.fill", iconTint: .appPrimary, iconBg: iconBgBlue,
                    label: NSLocalizedString("profile_label_full_name", value: "FULL NAME", comment: ""),
                    value: viewModel.uiState.user?.fullName ?? "—"
                )
                Divider().padding(.horizontal, Spacing.l)
                profileInfoRow(
                    iconName: "face.smiling.fill", iconTint: iconTintPink, iconBg: iconBgPink,
                    label: NSLocalizedString("profile_label_gender", value: "GENDER", comment: ""),
                    value: genderDisplay
                )
                Divider().padding(.horizontal, Spacing.l)
                profileInfoRow(
                    iconName: "birthday.cake.fill", iconTint: iconTintOrange, iconBg: iconBgOrange,
                    label: NSLocalizedString("profile_label_dob", value: "DATE OF BIRTH", comment: ""),
                    value: dobDisplay
                )
                Divider().padding(.horizontal, Spacing.l)
                profileInfoRow(
                    iconName: "phone.fill", iconTint: iconTintTeal, iconBg: iconBgTeal,
                    label: NSLocalizedString("profile_label_phone", value: "PHONE NUMBER", comment: ""),
                    value: (viewModel.uiState.user?.phone.isEmpty == false) ? viewModel.uiState.user!.phone : "—"
                )
                Divider().padding(.horizontal, Spacing.l)
                profileInfoRow(
                    iconName: "creditcard.fill", iconTint: iconTintGreen, iconBg: iconBgGreen,
                    label: NSLocalizedString("profile_label_cns", value: "CNS NUMBER", comment: ""),
                    value: maskedCns
                )
            }

            // Preferences card
            profileCard {
                HStack(spacing: Spacing.tiny) {
                    AppIcon(systemName: "bell.fill", tint: .appPrimary, size: Dimens.iconMicro)
                    AppCaptionText(
                        text: NSLocalizedString("profile_section_preferences",
                                                value: "PREFERENCES", comment: ""),
                        color: .appPrimary
                    )
                    Spacer()
                }
                .padding(.horizontal, Spacing.l).padding(.vertical, Spacing.m)

                Divider()

                // Notifications toggle
                HStack(spacing: Spacing.plus) {
                    iconBox(iconName: "envelope.fill", iconTint: iconTintTeal, iconBg: iconBgTeal)
                    VStack(alignment: .leading, spacing: Spacing.xxs) {
                        AppCaptionText(
                            text: NSLocalizedString("profile_label_notifications",
                                                    value: "NOTIFICATIONS", comment: "")
                        )
                        AppBodyText(
                            text: NSLocalizedString("profile_label_notifications_email",
                                                    value: "Email Notifications", comment: ""),
                            color: .appTextPrimary
                        )
                    }
                    Spacer()
                    Toggle("", isOn: $notificationsEnabled).tint(.appPrimary).labelsHidden()
                }
                .padding(.horizontal, Spacing.l).padding(.vertical, Spacing.compact)

                Divider().padding(.horizontal, Spacing.l)

                // Language picker
                VStack(alignment: .leading, spacing: Spacing.s) {
                    AppCaptionText(
                        text: NSLocalizedString("profile_label_language", value: "LANGUAGE", comment: "")
                    )
                    Menu {
                        ForEach(languages, id: \.code) { lang in
                            Button(lang.label) { selectedLanguage = lang.code }
                        }
                    } label: {
                        HStack {
                            AppBodyText(text: languageLabel, color: .appTextPrimary)
                            Spacer()
                            AppIcon(
                                systemName: "chevron.up.chevron.down",
                                tint: .appTextSecondary,
                                size: Dimens.iconXs
                            )
                        }
                        .padding(.horizontal, Spacing.plus)
                        .frame(height: Dimens.buttonHeightSm)
                        .background(Color.appBackground)
                        .cornerRadius(Radius.input)
                        .overlay(
                            RoundedRectangle(cornerRadius: Radius.input)
                                .stroke(Color.appTextHint.opacity(0.4), lineWidth: Dimens.strokeThin)
                        )
                    }
                }
                .padding(.horizontal, Spacing.l).padding(.bottom, Spacing.l)
            }

            // Login & Security card
            profileCard {
                HStack(spacing: Spacing.tiny) {
                    AppIcon(systemName: "shield.fill", tint: .appPrimary, size: Dimens.iconMicro)
                    AppCaptionText(
                        text: NSLocalizedString("profile_section_security",
                                                value: "LOGIN & SECURITY", comment: ""),
                        color: .appPrimary
                    )
                    Spacer()
                }
                .padding(.horizontal, Spacing.l).padding(.vertical, Spacing.m)

                Divider()

                NavigationLink(destination: ChangeEmailView()) {
                    securityRow(
                        iconName: "at", iconTint: iconTintTeal, iconBg: iconBgTeal,
                        label: NSLocalizedString("profile_label_login_email",
                                                 value: "LOGIN EMAIL ADDRESS", comment: ""),
                        value: viewModel.uiState.user?.email ?? "—"
                    )
                }
                .buttonStyle(.plain)

                Divider().padding(.horizontal, Spacing.l)

                NavigationLink(destination: ChangePasswordView()) {
                    securityRow(
                        iconName: "key.fill", iconTint: iconTintPink, iconBg: iconBgPink,
                        label: NSLocalizedString("profile_label_security", value: "SECURITY", comment: ""),
                        value: NSLocalizedString("profile_label_change_password",
                                                 value: "Change Password", comment: "")
                    )
                }
                .buttonStyle(.plain)
            }

            // Sign Out
            Button {
                showLogoutAlert = true
            } label: {
                HStack(spacing: Spacing.s) {
                    AppIcon(
                        systemName: "rectangle.portrait.and.arrow.right",
                        tint: .appDanger,
                        size: Dimens.iconCompact
                    )
                    AppLabelText(
                        text: NSLocalizedString("action_logout", value: "Sign Out", comment: ""),
                        color: .appDanger
                    )
                }
                .frame(maxWidth: .infinity)
                .frame(height: Sizing.buttonHeight)
                .background(Color.appDangerBg)
                .cornerRadius(Radius.md)
            }
            .padding(.horizontal, Spacing.l)

            Spacer().frame(height: Spacing.xxxl)
        }
        .padding(.horizontal, Spacing.l)
    }

    // MARK: - Derived values

    /// Localized gender label — resolves via shared `Gender.fromApiString` (Rule A-13),
    /// then maps each enum's `labelKey` to its Twine resource.
    private var genderDisplay: String {
        let raw = viewModel.uiState.user?.gender ?? ""
        guard !raw.isEmpty else { return "—" }
        let gender = Gender.companion.fromApiString(value: raw)
        switch gender {
        case .male:   return NSLocalizedString("gender_male",   value: "Male",   comment: "")
        case .female: return NSLocalizedString("gender_female", value: "Female", comment: "")
        case .other:  return NSLocalizedString("gender_other",  value: "Other",  comment: "")
        default:      return NSLocalizedString("gender_other",  value: "Other",  comment: "")
        }
    }

    private var dobDisplay: String {
        guard let dob = viewModel.uiState.user?.dateOfBirth, !dob.isEmpty else { return "—" }
        return DateUtil.formatIsoDate(dob, pattern: DateUtil.PATTERN_DOB)
    }

    /// CNS number masked through shared `CnsFormatter` (Rule A-13).
    private var maskedCns: String {
        guard let cns = viewModel.uiState.user?.cnsNumber, !cns.isEmpty else { return "—" }
        return CnsFormatter.shared.mask(cns: cns)
    }

    // MARK: - Helpers

    private func profileCard<Content: View>(@ViewBuilder content: () -> Content) -> some View {
        VStack(spacing: Spacing.none) {
            content()
        }
        .background(Color.appSurface)
        .cornerRadius(Radius.lg)
        .shadow(color: Color.black.opacity(0.06), radius: Dimens.shadowRadius, y: Dimens.shadowY)
    }

    private func profileInfoRow(iconName: String, iconTint: Color, iconBg: Color,
                                label: String, value: String) -> some View {
        HStack(spacing: Spacing.plus) {
            iconBox(iconName: iconName, iconTint: iconTint, iconBg: iconBg)
            VStack(alignment: .leading, spacing: Spacing.xxs) {
                AppCaptionText(text: label)
                AppBodyText(text: value, color: .appTextPrimary)
            }
            Spacer()
            AppIcon(systemName: "chevron.right", tint: .appTextHint, size: Dimens.iconChevron)
        }
        .padding(.horizontal, Spacing.l).padding(.vertical, Spacing.m)
    }

    private func securityRow(iconName: String, iconTint: Color, iconBg: Color,
                             label: String, value: String) -> some View {
        HStack(spacing: Spacing.plus) {
            iconBox(iconName: iconName, iconTint: iconTint, iconBg: iconBg)
            VStack(alignment: .leading, spacing: Spacing.xxs) {
                AppCaptionText(text: label)
                AppBodyText(text: value, color: .appTextPrimary)
            }
            Spacer()
            AppIcon(systemName: "chevron.right", tint: .appTextHint, size: Dimens.iconChevron)
        }
        .padding(.horizontal, Spacing.l).padding(.vertical, Spacing.m)
    }

    private func iconBox(iconName: String, iconTint: Color, iconBg: Color) -> some View {
        ZStack {
            RoundedRectangle(cornerRadius: Radius.input)
                .fill(iconBg)
                .frame(width: Dimens.profileIconBox, height: Dimens.profileIconBox)
            AppIcon(systemName: iconName, tint: iconTint, size: Dimens.iconLgVisual)
        }
    }

    private var menuSheet: some View {
        VStack(spacing: Spacing.none) {
            HStack {
                AppSubtitleText(text: NSLocalizedString("profile_menu_title",
                                                       value: "Menu", comment: ""))
                Spacer()
                AppIconButton(
                    systemName: "xmark",
                    accessibilityLabel: NSLocalizedString("cd_close", value: "Close", comment: ""),
                    action: { showMenuSheet = false },
                    tint: .appTextSecondary,
                    iconSize: Dimens.iconSm
                )
            }
            .padding()
            Divider()
            Group {
                menuItem(icon: "info.circle",
                         labelKey: "profile_menu_who",  fallback: "Who are we?") {
                    if let url = URL(string: AppUrls.website) {
                        openURL(url)
                    }
                }
                menuItem(icon: "doc.text",
                         labelKey: "profile_menu_terms",       fallback: "Terms and Conditions") {
                    if let url = URL(string: AppUrls.termsAndConditions) {
                        openURL(url)
                    }
                }
                menuItem(icon: "hand.raised",
                         labelKey: "profile_menu_privacy",     fallback: "Privacy Policy") {
                    if let url = URL(string: AppUrls.privacyPolicy) {
                        openURL(url)
                    }
                }
                menuItem(icon: "envelope",
                         labelKey: "profile_menu_contact",     fallback: "Contact us") {
                    if let url = URL(string: AppUrls.contactEmail) {
                        openURL(url)
                    }
                }
                Divider().padding(.horizontal)
                menuItem(icon: "cross.case",
                         labelKey: "profile_menu_emergencies", fallback: "Emergencies") {
                    if let url = URL(string: AppUrls.emergencyServices) {
                        openURL(url)
                    }
                }
                menuItem(icon: "pills",
                         labelKey: "profile_menu_pharmacy",    fallback: "Pharmacy on call") {
                    if let url = URL(string: AppUrls.pharmacyServices) {
                        openURL(url)
                    }
                }
                menuItem(icon: "building.columns",
                         labelKey: "profile_menu_health_fund", fallback: "National Health Fund") {
                    if let url = URL(string: AppUrls.healthFund) {
                        openURL(url)
                    }
                }
                menuItem(icon: "building.2",
                         labelKey: "profile_menu_ministry",    fallback: "Ministry of Health") {
                    if let url = URL(string: AppUrls.ministry) {
                        openURL(url)
                    }
                }
            }
            Spacer()
        }
        .background(Color.appSurface)
    }

    private func menuItem(icon: String, labelKey: String, fallback: String, action: @escaping () -> Void = {}) -> some View {
        Button {
            showMenuSheet = false
            action()
        } label: {
            HStack(spacing: Spacing.plus) {
                AppIcon(systemName: icon, tint: .appPrimary, size: Dimens.iconCompact)
                    .frame(width: Dimens.iconBoxSm)
                AppBodyText(
                    text: NSLocalizedString(labelKey, value: fallback, comment: ""),
                    color: .appTextPrimary
                )
                Spacer()
            }
            .padding(.horizontal).padding(.vertical, Spacing.plus)
        }
    }
}
