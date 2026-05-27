import SwiftUI

struct HomeView: View {
    @StateObject private var viewModel = HomeViewModelWrapper()
    @State private var searchText = ""
    @State private var locationText = ""
    @State private var selectedFilters: Set<String> = []
    @State private var selectedPractitionerId: String? = nil
    @State private var navigateToPractitioner = false

    let filters = ["All", "Today", "Within 3 days", "Open to new patients", "Cardiology", "Dermatology"]

    var body: some View {
        NavigationStack {
            VStack(spacing: 0) {
                // Gradient header with search
                GradientHeader(minHeight: 180) {
                    VStack(alignment: .leading, spacing: 10) {
                        Text("Find a practitioner")
                            .font(.system(size: 22, weight: .bold))
                            .foregroundColor(.white)

                        HStack {
                            Image(systemName: "magnifyingglass").foregroundColor(.white.opacity(0.7))
                            TextField("", text: $searchText)
                                .placeholder(when: searchText.isEmpty) {
                                    Text("Search specialty, doctor…").foregroundColor(.white.opacity(0.5))
                                }
                                .foregroundColor(.white)
                                .autocapitalization(.none)
                                .onSubmit {
                                    viewModel.search(query: searchText, location: locationText)
                                }
                        }
                        .padding(10)
                        .background(Color.white.opacity(0.15))
                        .cornerRadius(Dimens.radiusMd)
                    }
                }

                // Filter chips
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 8) {
                        ForEach(filters, id: \.self) { filter in
                            let isSelected = filter == "All"
                                ? selectedFilters.isEmpty
                                : selectedFilters.contains(filter)
                            FilterChip(label: filter, isSelected: isSelected) {
                                if filter == "All" {
                                    selectedFilters.removeAll()
                                } else if selectedFilters.contains(filter) {
                                    selectedFilters.remove(filter)
                                } else {
                                    selectedFilters.insert(filter)
                                }
                            }
                        }
                    }
                    .padding(.horizontal, Dimens.paddingL)
                    .padding(.vertical, 10)
                }
                .background(Color.appSurface)

                // Content
                if viewModel.uiState.isLoading {
                    LoadingView(message: "Finding practitioners…")
                } else if let error = viewModel.uiState.error {
                    ErrorView(message: error) {
                        viewModel.refresh()
                    }
                } else if viewModel.uiState.practitioners.isEmpty {
                    EmptyStateView(
                        icon: "person.2.slash",
                        title: "No practitioners found",
                        message: "Try adjusting your search or filters",
                        actionLabel: "Clear Filters"
                    ) {
                        selectedFilters.removeAll()
                        viewModel.refresh()
                    }
                } else {
                    ScrollView {
                        LazyVStack(spacing: 12) {
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
                                        nextSlots: p.availableSlots.filter { $0.available }.prefix(4).map { $0.dateTime }
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
                                        viewModel.applyFilter(p.id)
                                    }
                                )
                            }
                        }
                        .padding(.horizontal, Dimens.paddingL)
                        .padding(.vertical, 12)
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
}

extension View {
    func placeholder<Content: View>(when shouldShow: Bool, @ViewBuilder placeholder: () -> Content) -> some View {
        ZStack(alignment: .leading) {
            if shouldShow { placeholder() }
            self
        }
    }
}
