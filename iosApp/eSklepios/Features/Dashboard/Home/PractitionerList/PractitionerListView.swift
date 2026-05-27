import SwiftUI
import shared

struct PractitionerListView: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var viewModel = HomeViewModelWrapper()
    @State private var selectedPractitionerId: String? = nil
    @State private var navigateToPractitioner = false

    private var dateFilters: [(key: String, label: String)] {
        [
            ("All", NSLocalizedString("home_filter_all", value: "All", comment: "")),
            ("Today", NSLocalizedString("home_filter_today", value: "Today", comment: "")),
            ("Within 3 days", NSLocalizedString("home_filter_3days", value: "Within 3 days", comment: ""))
        ]
    }

    var body: some View {
        VStack(spacing: Spacing.none) {
            AppGradientHeaderView(
                roundedBottom: true,
                leading: .iconButton(
                    systemName: "chevron.left",
                    accessibilityLabel: NSLocalizedString("cd_back", value: "Back", comment: ""),
                    action: { dismiss() }
                ),
                center: .title(
                    text: NSLocalizedString("screen_practitioner_list", value: "All Practitioners", comment: "")
                )
            )

            // Date filter segmented control
            Picker("", selection: Binding(
                get: { viewModel.selectedDateFilter },
                set: { viewModel.setDateFilter($0) }
            )) {
                ForEach(dateFilters, id: \.key) { filter in
                    Text(filter.label).tag(filter.key)
                }
            }
            .pickerStyle(.segmented)
            .padding(.horizontal, Dimens.paddingL)
            .padding(.vertical, Dimens.paddingM)
            .background(Color.appSurface)

            // "Open to New Patients" toggle pill
            HStack {
                PractitionerListNewPatientsToggle(
                    checked: viewModel.openToNewPatients,
                    label: NSLocalizedString("home_filter_new_patients", value: "Open to new patients", comment: ""),
                    onToggle: { viewModel.toggleNewPatientsFilter() }
                )
                Spacer()
            }
            .padding(.horizontal, Dimens.paddingL)
            .padding(.bottom, Dimens.paddingM)
            .background(Color.appSurface)

            if viewModel.uiState.isLoading {
                LoadingView(message: NSLocalizedString("home_loading", value: "Finding practitioners…", comment: ""))
            } else if let error = viewModel.uiState.error {
                ErrorView(message: error) {
                    viewModel.refresh()
                }
            } else if viewModel.uiState.practitioners.isEmpty {
                EmptyStateView(
                    icon: "person.2.slash",
                    title: NSLocalizedString("home_no_results_title", value: "No practitioners found", comment: ""),
                    message: NSLocalizedString("home_no_results_subtitle", value: "Try adjusting your search or filters", comment: ""),
                    actionLabel: NSLocalizedString("home_clear_filters", value: "Clear Filters", comment: "")
                ) {
                    viewModel.setDateFilter("All")
                    viewModel.refresh()
                }
            } else {
                ScrollView {
                    LazyVStack(spacing: Dimens.paddingM) {
                        // Section header
                        HStack {
                            AppSubtitleText(
                                text: NSLocalizedString("home_nearby_label", value: "Nearby Practitioners", comment: "")
                            )
                            Spacer()
                            AppCaptionText(
                                text: String(
                                    format: NSLocalizedString("home_results_count", value: "%d results", comment: ""),
                                    viewModel.practitioners.count
                                ),
                                color: .appTextSecondary
                            )
                        }
                        .padding(.horizontal, Dimens.paddingL)
                        .padding(.top, Dimens.paddingXS)

                        // All filtered practitioners
                        ForEach(viewModel.uiState.practitioners, id: \.id) { p in
                            PractitionerCard(
                                data: PractitionerCardData(
                                    id: p.id,
                                    firstName: p.firstName,
                                    lastName: p.lastName,
                                    specialty: p.specialty,
                                    clinicName: p.clinicName,
                                    address: p.address,
                                    city: p.city,
                                    acceptingNewPatients: p.acceptingNewPatients,
                                    isFavorite: p.isFavorite,
                                    availableSlotDays: slotDays(from: (p.availableSlots as? [AppointmentSlot]) ?? [])
                                ),
                                onSeeProfile: {
                                    selectedPractitionerId = p.id
                                    navigateToPractitioner = true
                                },
                                onBook: { _ in
                                    selectedPractitionerId = p.id
                                    navigateToPractitioner = true
                                },
                                onFavorite: {
                                    viewModel.toggleFavorite(id: p.id)
                                }
                            )
                            .padding(.horizontal, Dimens.paddingL)
                        }
                    }
                    .padding(.bottom, Dimens.paddingM)
                }
                .background(Color.appBackground)
            }
        }
        .background(Color.appBackground)
        .navigationBarHidden(true)
        .navigationDestination(isPresented: $navigateToPractitioner) {
            if let id = selectedPractitionerId {
                PractitionerDetailView(practitionerId: id)
            }
        }
    }
}

private func slotDays(from slots: [AppointmentSlot]) -> [SlotDayModel] {
    let available = slots.filter { $0.available }
    let grouped = Dictionary(grouping: available) { slot in
        String(slot.dateTime.prefix(10))
    }
    return grouped
        .map { (dateKey, daySlots) in
            SlotDayModel(
                id: dateKey,
                dateKey: dateKey,
                slots: daySlots
                    .map { SlotEntry(id: $0.id, time: String($0.dateTime.dropFirst(11).prefix(5))) }
                    .sorted { $0.time < $1.time }
            )
        }
        .sorted { $0.dateKey < $1.dateKey }
}

private struct PractitionerListNewPatientsToggle: View {
    let checked: Bool
    let label: String
    let onToggle: () -> Void

    var body: some View {
        HStack(spacing: Spacing.xs) {
            AppIcon(
                systemName: checked ? "checkmark.circle.fill" : "circle",
                tint: checked ? .appSuccess : .appTextSecondary,
                size: Dimens.iconSm
            )
            AppCaptionText(text: label, color: checked ? .appSuccess : .appTextSecondary)
        }
        .padding(.horizontal, Spacing.m)
        .padding(.vertical, Spacing.xs)
        .background(checked ? Color.appSuccessBg : Color.clear)
        .clipShape(Capsule())
        .overlay(Capsule().stroke(checked ? Color.appSuccess : Color.appTextHint.opacity(0.4), lineWidth: Dimens.strokeThin))
        .onTapGesture(perform: onToggle)
    }
}
