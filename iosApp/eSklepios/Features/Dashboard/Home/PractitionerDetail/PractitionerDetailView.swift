import SwiftUI
import shared

struct PractitionerDetailView: View {
    let practitionerId: String
    var namespace: Namespace.ID? = nil
    @StateObject private var viewModel: PractitionerDetailViewModelWrapper
    @Environment(\.dismiss) var dismiss
    @Environment(\.openURL) var openURL

    init(practitionerId: String, namespace: Namespace.ID? = nil) {
        self.practitionerId = practitionerId
        self.namespace = namespace
        _viewModel = StateObject(wrappedValue: PractitionerDetailViewModelWrapper(practitionerId: practitionerId))
    }

    var body: some View {
        Group {
            if viewModel.uiState.isLoading {
                LoadingView(message: "Loading practitioner…")
            } else if let error = viewModel.uiState.error {
                ErrorView(message: error) { viewModel.loadDetail() }
            } else if let p = viewModel.uiState.practitioner {
                detailContent(p)
            }
        }
        .navigationBarHidden(true)
        .onAppear { viewModel.loadDetail() }
    }

    // MARK: - Main scroll content

    @ViewBuilder
    private func detailContent(_ p: Practitioner) -> some View {
        ScrollView {
            VStack(spacing: Spacing.none) {
                AppGradientHeaderView(
                    roundedBottom: false,
                    leading: .iconButton(
                        systemName: "chevron.left",
                        accessibilityLabel: NSLocalizedString("cd_back", value: "Back", comment: ""),
                        action: { dismiss() }
                    ),
                    profile: .inline(
                        initials: p.initials,
                        avatarSize: Dimens.detailAvatarSize,
                        name: p.fullName,
                        subtitle: p.specialty,
                        caption: nil
                    ),
                    heroNamespace: namespace,
                    heroId: practitionerId
                )

                VStack(spacing: Spacing.m) {
                    contactCard(p)
                    schedulesCard(p)
                    paymentsCard(p)
                    presentationCard(p)
                }
                .padding(.horizontal, Spacing.m)
                .padding(.top, Spacing.m)
                .padding(.bottom, Spacing.xxl)
            }
        }
        .background(Color.appBackground)
    }

    // MARK: - Card 1: Contact

    @ViewBuilder
    private func contactCard(_ p: Practitioner) -> some View {
        let addressLine2 = p.postalCode.isEmpty ? p.city : "\(p.postalCode) \(p.city)"
        AppCard(padding: Spacing.none, cornerRadius: Dimens.radiusCard) {
            VStack(alignment: .leading, spacing: Spacing.none) {
                cardTitle("Contact details")
                    .padding([.horizontal, .top], Spacing.l)
                    .padding(.bottom, Spacing.m)
                HStack(alignment: .top, spacing: Spacing.m) {
                    VStack(alignment: .leading, spacing: Spacing.m) {
                        contactRow(icon: "building.2.fill",
                                   text: p.clinicName)
                        contactRow(icon: "mappin.and.ellipse",
                                   text: "\(p.address)\n\(addressLine2)")
                        Button(action: { callPhone(p.phone) }) {
                            contactRow(icon: "phone.fill", text: p.phone)
                        }
                        Button(action: { emailTo(p.email) }) {
                            contactRow(icon: "envelope", text: p.email)
                        }
                    }
                    RoundedRectangle(cornerRadius: Dimens.radiusSm)
                        .fill(Color.appPrimaryLight)
                        .frame(maxWidth: .infinity)
                        .frame(height: Dimens.detailMapHeight)
                        .overlay(
                            Image(systemName: "map")
                                .foregroundColor(.appPrimary)
                                .font(.system(size: Dimens.iconXl))
                                .accessibilityLabel("Map placeholder")
                        )
                }
                .padding([.horizontal, .bottom], Spacing.l)
            }
        }
    }

    // MARK: - Card 2: Schedules

    @ViewBuilder
    private func schedulesCard(_ p: Practitioner) -> some View {
        AppCard(padding: Spacing.none, cornerRadius: Dimens.radiusCard) {
            VStack(alignment: .leading, spacing: Spacing.none) {
                AppCaptionText(text: "SCHEDULES", color: .appTextSecondary)
                    .fontWeight(.bold)
                    .padding([.horizontal, .top], Spacing.l)
                    .padding(.bottom, Spacing.m)
                HStack(alignment: .top, spacing: Spacing.m) {
                    VStack(alignment: .leading, spacing: Spacing.xs) {
                        ForEach(p.schedule, id: \.day) { entry in
                            HStack(alignment: .top, spacing: Spacing.xs) {
                                AppBodyText(text: entry.day, color: .appTextPrimary)
                                    .frame(width: Dimens.scheduleDayLabelWidth,
                                           alignment: .leading)
                                AppBodyText(text: entry.hours, color: .appTextSecondary)
                                    .fixedSize(horizontal: false, vertical: true)
                            }
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)

                    VStack(alignment: .leading, spacing: Spacing.s) {
                        HStack(spacing: Spacing.xs) {
                            Image(systemName: "cross.circle.fill")
                                .foregroundColor(.appDanger)
                                .font(.system(size: Dimens.iconMicro))
                            AppCaptionText(text: "Emergency?", color: .appDanger)
                        }
                        (Text("Call ")
                            .foregroundColor(Color.appTextPrimary)
                            + Text("112")
                            .fontWeight(.bold)
                            .foregroundColor(Color.appDanger))
                        .font(.callout)
                        Button(action: { openURL(URL(string: "https://112.lu")!) }) {
                            Text("112.lu")
                                .font(.callout)
                                .foregroundColor(.appPrimary)
                                .underline()
                        }
                    }
                    .frame(maxWidth: .infinity, alignment: .leading)
                }
                .padding([.horizontal, .bottom], Spacing.l)
            }
        }
    }

    // MARK: - Card 3: Payments

    @ViewBuilder
    private func paymentsCard(_ p: Practitioner) -> some View {
        if !p.paymentMethods.isEmpty {
            AppCard(padding: Spacing.none, cornerRadius: Dimens.radiusCard) {
                VStack(alignment: .leading, spacing: Spacing.none) {
                    cardTitle("Payments")
                        .padding([.horizontal, .top], Spacing.l)
                        .padding(.bottom, Spacing.xs)
                    AppBodyText(text: "Means of payment", color: .appTextSecondary)
                        .padding(.horizontal, Spacing.l)
                        .padding(.bottom, Spacing.m)
                    VStack(alignment: .leading, spacing: Spacing.s) {
                        ForEach(p.paymentMethods, id: \.self) { method in
                            HStack(spacing: Spacing.s) {
                                Circle()
                                    .fill(Color.appTextSecondary)
                                    .frame(width: Dimens.paddingTiny,
                                           height: Dimens.paddingTiny)
                                AppBodyText(text: method, color: .appTextPrimary)
                            }
                        }
                    }
                    .padding(.horizontal, Spacing.l)
                    .padding(.bottom, Spacing.l)
                }
            }
        }
    }

    // MARK: - Card 4: Presentation

    @ViewBuilder
    private func presentationCard(_ p: Practitioner) -> some View {
        if !p.diplomas.isEmpty || !p.presentation.isEmpty {
            AppCard(padding: Spacing.none, cornerRadius: Dimens.radiusCard) {
                VStack(alignment: .leading, spacing: Spacing.none) {
                    cardTitle("Presentation")
                        .padding([.horizontal, .top], Spacing.l)
                        .padding(.bottom, Spacing.m)
                    if !p.diplomas.isEmpty {
                        VStack(alignment: .leading, spacing: Spacing.s) {
                            ForEach(p.diplomas, id: \.self) { diploma in
                                HStack(spacing: Spacing.s) {
                                    Circle()
                                        .fill(Color.appTextSecondary)
                                        .frame(width: Dimens.paddingTiny,
                                               height: Dimens.paddingTiny)
                                    AppBodyText(text: diploma, color: .appTextPrimary)
                                }
                            }
                        }
                        .padding(.horizontal, Spacing.l)
                        .padding(.bottom, p.presentation.isEmpty ? Spacing.l : Spacing.m)
                    }
                    if !p.presentation.isEmpty {
                        AppBodyText(text: p.presentation, color: .appTextPrimary)
                            .padding(.horizontal, Spacing.l)
                            .padding(.bottom, Spacing.l)
                    }
                }
            }
        }
    }

    // MARK: - Helpers

    private func cardTitle(_ text: String) -> some View {
        AppLabelText(text: text, color: .appTextPrimary)
    }

    private func contactRow(icon: String, text: String) -> some View {
        HStack(alignment: .top, spacing: Spacing.s) {
            Image(systemName: icon)
                .foregroundColor(.appPrimary)
                .font(.system(size: Dimens.iconMicro))
                .frame(width: Dimens.iconMd, alignment: .center)
            AppBodyText(text: text, color: .appTextPrimary)
        }
    }

    private func callPhone(_ phone: String) {
        let cleaned = phone
            .replacingOccurrences(of: " ", with: "")
            .replacingOccurrences(of: "(", with: "")
            .replacingOccurrences(of: ")", with: "")
            .replacingOccurrences(of: "+", with: "")
        if let url = URL(string: "tel://\(cleaned)") {
            openURL(url)
        }
    }

    private func emailTo(_ email: String) {
        if let url = URL(string: "mailto:\(email)") {
            openURL(url)
        }
    }
}
