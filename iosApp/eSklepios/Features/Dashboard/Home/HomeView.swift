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
                    NewPatientsTogglePill(
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

// MARK: - Slot mapping helper

private func slotDays(from slots: [AppointmentSlot]) -> [SlotDayModel] {
    let available = slots.filter { $0.available }
    let grouped = Dictionary(grouping: available) { slot in
        DateUtil.dateFromDateTime(slot.dateTime)
    }
    return grouped
        .map { (dateKey, daySlots) in
            SlotDayModel(
                id: dateKey,
                dateKey: dateKey,
                slots: daySlots
                    .map { slot in
                        SlotEntry(
                            id: slot.id,
                            time: DateUtil.timeFromDateTime(slot.dateTime)
                        )
                    }
                    .sorted { $0.time < $1.time }
            )
        }
        .sorted { $0.dateKey < $1.dateKey }
}

private struct NewPatientsTogglePill: View {
    let checked: Bool
    let label: String
    let onToggle: () -> Void

    var body: some View {
        HStack(spacing: Spacing.xs) {
            Image(systemName: checked ? "checkmark.circle.fill" : "circle")
                .font(.system(size: Dimens.iconSm))
                .foregroundColor(checked ? .appSuccess : .appTextSecondary)
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
