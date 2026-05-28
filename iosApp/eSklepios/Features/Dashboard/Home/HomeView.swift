import SwiftUI
import shared

struct HomeView: View {
    @StateObject private var viewModel = HomeViewModelWrapper()
    @State private var searchText = ""
    @State private var locationText = ""
    @State private var selectedPractitionerId: String? = nil
    @State private var navigateToPractitioner = false
    @State private var navigateToPractitionerList = false
    @Namespace private var heroTransition

    private var dateFilters: [(key: String, label: String)] {
        [
            (DateFilter.all.apiKey,         NSLocalizedString("home_filter_all",   value: "All",           comment: "")),
            (DateFilter.today.apiKey,       NSLocalizedString("home_filter_today", value: "Today",         comment: "")),
            (DateFilter.within3Days.apiKey, NSLocalizedString("home_filter_3days", value: "Within 3 days", comment: ""))
        ]
    }

    var body: some View {
        NavigationStack {
            VStack(spacing: Spacing.none) {
                AppGradientHeaderView(
                    roundedBottom: true,
                    textBlock: HeaderTextBlock(
                        title: NSLocalizedString("home_find_practitioner", value: "Find a practitioner", comment: "")
                    ),
                    search: HeaderSearchConfig(
                        searchQuery: searchText,
                        locationQuery: locationText,
                        onSearchQueryChange: { searchText = $0 },
                        onLocationQueryChange: { locationText = $0 },
                        onSearchTap: { viewModel.search(query: searchText, location: locationText) }
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
                        viewModel.setDateFilter(DateFilter.all.apiKey)
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

                            // First 2 cards
                            ForEach(Array(viewModel.uiState.practitioners.prefix(2)), id: \.id) { p in
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

                            // "See all" text link — only when more than 2 results
                            if viewModel.uiState.practitioners.count > 2 {
                                AppTextLink(
                                    text: NSLocalizedString("home_see_all", value: "See all practitioners →", comment: ""),
                                    action: { navigateToPractitionerList = true }
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
            .navigationDestination(isPresented: $navigateToPractitionerList) {
                PractitionerListView()
            }
        }
    }
}
