import SwiftUI

struct BookAppointmentView: View {
    let practitionerId: String
    let slotId: String
    @StateObject private var viewModel: BookAppointmentViewModelWrapper
    @Environment(\.dismiss) var dismiss
    @State private var consultationReason = ""
    @State private var messageToDoctor = ""
    @State private var navigateToSuccess = false
    @State private var navigateToLogin = false
    @State private var showError = false

    init(practitionerId: String, slotId: String) {
        self.practitionerId = practitionerId
        self.slotId = slotId
        _viewModel = StateObject(wrappedValue: BookAppointmentViewModelWrapper(practitionerId: practitionerId, slotId: slotId))
    }

    var body: some View {
        ScrollView {
            VStack(spacing: 0) {
                AppToolbar(title: "Book Appointment", onBack: { dismiss() })

                VStack(spacing: Dimens.paddingL) {
                    // Booking summary card
                    AppCard {
                        VStack(alignment: .leading, spacing: Dimens.paddingM) {
                            Text("Appointment Summary")
                                .font(.system(size: 14, weight: .semibold))
                                .foregroundColor(.appPrimary)

                            let state = viewModel.uiState
                            if let p = state.practitioner {
                                InfoRow(icon: "person", label: "Practitioner", value: p.fullName)
                                InfoRow(icon: "stethoscope", label: "Specialty", value: p.specialty)
                                InfoRow(icon: "building.2", label: "Clinic", value: p.clinicName)
                            }
                            if let slot = state.selectedSlot {
                                InfoRow(icon: "calendar", label: "Date & Time", value: slot.dateTime)
                            }
                        }
                    }

                    // Cancellation policy
                    HStack(spacing: 10) {
                        Image(systemName: "info.circle").foregroundColor(.appWarning).font(.system(size: 16))
                        Text("Cancellations must be made at least 24 hours in advance.")
                            .font(.system(size: 13))
                            .foregroundColor(.appWarning)
                    }
                    .padding(12)
                    .frame(maxWidth: .infinity, alignment: .leading)
                    .background(Color.appWarningBg)
                    .cornerRadius(Dimens.radiusMd)

                    // Consultation reason
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Reason for consultation *")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.appTextPrimary)
                        TextField("Briefly describe the reason…", text: $consultationReason)
                            .padding(12)
                            .background(Color.appSurface)
                            .cornerRadius(Dimens.radiusMd)
                            .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
                    }

                    // Message to doctor
                    VStack(alignment: .leading, spacing: 6) {
                        Text("Message to doctor (optional)")
                            .font(.system(size: 13, weight: .medium))
                            .foregroundColor(.appTextPrimary)
                        TextEditor(text: $messageToDoctor)
                            .frame(height: 100)
                            .padding(8)
                            .background(Color.appSurface)
                            .cornerRadius(Dimens.radiusMd)
                            .overlay(RoundedRectangle(cornerRadius: Dimens.radiusMd).stroke(Color.appTextHint.opacity(0.4), lineWidth: 1))
                    }

                    // Auth required banner
                    if !viewModel.uiState.isAuthenticated {
                        HStack(spacing: 10) {
                            Image(systemName: "lock.fill").foregroundColor(.appPrimary).font(.system(size: 16))
                            VStack(alignment: .leading, spacing: 2) {
                                Text("Sign in required").font(.system(size: 14, weight: .semibold)).foregroundColor(.appPrimary)
                                Text("You need an account to book appointments.").font(.system(size: 12)).foregroundColor(.appTextSecondary)
                            }
                        }
                        .padding(14)
                        .frame(maxWidth: .infinity, alignment: .leading)
                        .background(Color.appPrimaryLight)
                        .cornerRadius(Dimens.radiusMd)
                    }

                    PrimaryButton(
                        title: viewModel.uiState.isAuthenticated ? "Confirm Booking" : "Sign In to Book",
                        isLoading: viewModel.uiState.isLoading
                    ) {
                        if !viewModel.uiState.isAuthenticated {
                            navigateToLogin = true
                        } else {
                            viewModel.confirmBooking(message: messageToDoctor, reason: consultationReason)
                        }
                    }
                    .frame(maxWidth: .infinity)
                }
                .padding(.horizontal, Dimens.paddingL)
                .padding(.top, Dimens.paddingL)
                .padding(.bottom, Dimens.paddingXXXL)
            }
        }
        .background(Color.appBackground)
        .navigationBarHidden(true)
        .alert("Error", isPresented: $showError) {
            Button("OK") { viewModel.clearError() }
        } message: {
            Text(viewModel.uiState.error ?? "")
        }
        .onChange(of: viewModel.uiState.isConfirmed) { confirmed in
            if confirmed { navigateToSuccess = true }
        }
        .onChange(of: viewModel.uiState.error) { error in
            showError = error != nil
        }
        .navigationDestination(isPresented: $navigateToSuccess) {
            AppointmentSuccessView()
        }
        .navigationDestination(isPresented: $navigateToLogin) {
            LoginView()
        }
    }
}
