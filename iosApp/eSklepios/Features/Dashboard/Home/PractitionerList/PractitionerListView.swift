import SwiftUI
import shared

struct PractitionerListView: View {
    @Environment(\.dismiss) private var dismiss
    @StateObject private var viewModel = HomeViewModelWrapper()
    @State private var selectedPractitionerId: String? = nil
    @State private var navigateToPractitioner = false
    @Namespace private var heroTransition

    private var dateFilters: [(key: String, label: String)] {
        [
            ("All",          NSLocalizedString("home_filter_all",   value: "All",           comment: "")),
            ("Today",        NSLocalizedString("home_filter_today", value: "Today",         comment: "")),
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
            PractitionerDateFilterPicker(
                filters: dateFilters,
                selectedFilter: viewModel.selectedDateFilter,
                onFilterSelected: { viewModel.setDateFilter($0) }
            )

            // "Open to New Patients" toggle pill
            HStack {
                PractitionerNewPatientsTogglePill(
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
                                },
                                namespace: heroTransition
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
                PractitionerDetailView(practitionerId: id, namespace: heroTransition)
            }
        }
    }
}
