import SwiftUI

struct PractitionerDetailView: View {
    let practitionerId: String
    @StateObject private var viewModel: PractitionerDetailViewModelWrapper
    @Environment(\.dismiss) var dismiss
    @State private var navigateToBook = false
    @State private var selectedSlotId = ""

    init(practitionerId: String) {
        self.practitionerId = practitionerId
        _viewModel = StateObject(wrappedValue: PractitionerDetailViewModelWrapper(practitionerId: practitionerId))
    }

    var body: some View {
        ZStack(alignment: .bottom) {
            if viewModel.uiState.isLoading {
                LoadingView(message: "Loading practitioner…")
            } else if let error = viewModel.uiState.error {
                ErrorView(message: error) { viewModel.loadDetail() }
            } else if let p = viewModel.uiState.practitioner {
                ScrollView {
                    VStack(spacing: 0) {
                        // Header
                        GradientHeader(
                            minHeight: 220,
                            onBack: { dismiss() },
                            trailingAction: { viewModel.toggleFavorite() },
                            trailingIcon: p.isFavorite ? "heart.fill" : "heart"
                        ) {
                            HStack(spacing: Dimens.paddingL) {
                                AvatarCircle(initials: p.initials, size: Dimens.avatarXl)
                                VStack(alignment: .leading, spacing: 4) {
                                    Text(p.fullName)
                                        .font(.system(size: 20, weight: .bold))
                                        .foregroundColor(.white)
                                    Text(p.specialty)
                                        .font(.system(size: 14))
                                        .foregroundColor(.white.opacity(0.85))
                                    Text(p.clinicName)
                                        .font(.system(size: 12))
                                        .foregroundColor(.white.opacity(0.7))
                                }
                            }
                        }

                        VStack(spacing: 12) {
                            // Contact card
                            AppCard {
                                VStack(alignment: .leading, spacing: Dimens.paddingM) {
                                    sectionHeader("Contact")
                                    InfoRow(icon: "phone", label: "Phone", value: p.phone)
                                    InfoRow(icon: "envelope", label: "Email", value: p.email)
                                    InfoRow(icon: "mappin", label: "Address", value: "\(p.address), \(p.city)")
                                    HStack(spacing: 10) {
                                        SecondaryButton(title: "Call", action: {})
                                        SecondaryButton(title: "Email", action: {})
                                    }
                                }
                            }

                            // Emergency banner
                            HStack(spacing: 10) {
                                Image(systemName: "exclamationmark.triangle.fill")
                                    .foregroundColor(.appDanger)
                                Text("For emergencies, call 112 immediately")
                                    .font(.system(size: 13, weight: .medium))
                                    .foregroundColor(.appDanger)
                            }
                            .padding(12)
                            .frame(maxWidth: .infinity, alignment: .leading)
                            .background(Color.appDangerBg)
                            .cornerRadius(Dimens.radiusMd)

                            // Schedule card
                            if !p.schedule.isEmpty {
                                AppCard {
                                    VStack(alignment: .leading, spacing: Dimens.paddingM) {
                                        sectionHeader("Schedule")
                                        ForEach(p.schedule, id: \.dayName) { day in
                                            HStack {
                                                Text(day.dayName)
                                                    .font(.system(size: 14, weight: day.isOpen ? .medium : .regular))
                                                    .foregroundColor(day.isOpen ? .appTextPrimary : .appTextSecondary)
                                                Spacer()
                                                Text(day.isOpen ? "\(day.openTime) – \(day.closeTime)" : "Closed")
                                                    .font(.system(size: 14))
                                                    .foregroundColor(day.isOpen ? .appTextPrimary : .appTextHint)
                                            }
                                        }
                                    }
                                }
                            }

                            // Map
                            MapPreviewCard(latitude: p.latitude, longitude: p.longitude, address: "\(p.address), \(p.city)")

                            // Payment methods
                            if !p.paymentMethods.isEmpty {
                                AppCard {
                                    VStack(alignment: .leading, spacing: Dimens.paddingM) {
                                        sectionHeader("Payment Methods")
                                        ScrollView(.horizontal, showsIndicators: false) {
                                            HStack(spacing: 8) {
                                                ForEach(p.paymentMethods, id: \.self) { method in
                                                    Text(method)
                                                        .font(.system(size: 12, weight: .medium))
                                                        .padding(.horizontal, 10)
                                                        .padding(.vertical, 4)
                                                        .background(Color.appPrimaryLight)
                                                        .foregroundColor(.appPrimary)
                                                        .cornerRadius(Dimens.radiusSm)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Diplomas
                            if !p.diplomas.isEmpty {
                                AppCard {
                                    VStack(alignment: .leading, spacing: Dimens.paddingM) {
                                        sectionHeader("Diplomas & Certifications")
                                        ForEach(p.diplomas, id: \.self) { diploma in
                                            HStack(spacing: 8) {
                                                Image(systemName: "checkmark.circle.fill")
                                                    .foregroundColor(.appSuccess)
                                                    .font(.system(size: 15))
                                                Text(diploma).font(.system(size: 14)).foregroundColor(.appTextPrimary)
                                            }
                                        }
                                    }
                                }
                            }

                            // Available slots
                            let slots = p.availableSlots.filter { $0.available }
                            if !slots.isEmpty {
                                AppCard {
                                    VStack(alignment: .leading, spacing: Dimens.paddingM) {
                                        sectionHeader("Available Slots")
                                        LazyVGrid(columns: [GridItem(.flexible()), GridItem(.flexible())], spacing: 8) {
                                            ForEach(slots.prefix(6), id: \.id) { slot in
                                                Button(action: {
                                                    viewModel.selectSlot(slot.id)
                                                    selectedSlotId = slot.id
                                                }) {
                                                    Text(slot.dateTime)
                                                        .font(.system(size: 12, weight: .medium))
                                                        .frame(maxWidth: .infinity)
                                                        .padding(.vertical, 8)
                                                        .background(viewModel.uiState.selectedSlot?.id == slot.id ? Color.appPrimary : Color.appPrimaryLight)
                                                        .foregroundColor(viewModel.uiState.selectedSlot?.id == slot.id ? .white : .appPrimary)
                                                        .cornerRadius(Dimens.radiusSm)
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer().frame(height: 100)
                        }
                        .padding(.horizontal, Dimens.paddingL)
                        .padding(.top, 12)
                    }
                }
                .background(Color.appBackground)

                // Floating book button
                VStack {
                    PrimaryButton(title: "Book an Appointment") {
                        let slot = viewModel.uiState.selectedSlot ?? p.availableSlots.first(where: { $0.available })
                        if let slot = slot {
                            selectedSlotId = slot.id
                            navigateToBook = true
                        }
                    }
                    .frame(maxWidth: .infinity)
                    .padding(.horizontal, Dimens.paddingL)
                    .padding(.vertical, Dimens.paddingM)
                    .background(.ultraThinMaterial)
                }
            }
        }
        .navigationBarHidden(true)
        .onAppear { viewModel.loadDetail() }
        .navigationDestination(isPresented: $navigateToBook) {
            BookAppointmentView(practitionerId: practitionerId, slotId: selectedSlotId)
        }
    }

    private func sectionHeader(_ title: String) -> some View {
        Text(title)
            .font(.system(size: 13, weight: .semibold))
            .foregroundColor(.appPrimary)
    }
}
