import SwiftUI

struct MyAppointmentsView: View {
    @StateObject private var viewModel = MyAppointmentsViewModelWrapper()
    @State private var selectedTab = 0
    @State private var cancelTargetId: String? = nil
    @State private var showCancelAlert = false

    var body: some View {
        VStack(spacing: 0) {
            GradientHeader(minHeight: 140) {
                Text("My Appointments")
                    .font(.system(size: 22, weight: .bold))
                    .foregroundColor(.white)
            }

            // Tab selector
            Picker("Tab", selection: $selectedTab) {
                Text("Upcoming").tag(0)
                Text("Past").tag(1)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal, Dimens.paddingL)
            .padding(.vertical, 10)
            .background(Color.appSurface)

            if viewModel.uiState.isLoading {
                LoadingView(message: "Loading appointments…")
            } else if let error = viewModel.uiState.error {
                ErrorView(message: error) { viewModel.refresh() }
            } else {
                let appointments = selectedTab == 0
                    ? viewModel.uiState.upcomingAppointments
                    : viewModel.uiState.pastAppointments

                if appointments.isEmpty {
                    EmptyStateView(
                        icon: "calendar.badge.exclamationmark",
                        title: selectedTab == 0 ? "No upcoming appointments" : "No past appointments",
                        message: selectedTab == 0 ? "Book your first appointment to get started" : "Your history will appear here",
                        actionLabel: selectedTab == 0 ? "Find Practitioners" : nil,
                        onAction: selectedTab == 0 ? {} : nil
                    )
                } else {
                    ScrollView {
                        LazyVStack(spacing: 12) {
                            ForEach(appointments, id: \.id) { appt in
                                AppointmentCard(
                                    practitionerName: appt.practitionerName,
                                    specialty: appt.specialty,
                                    dateTime: appt.dateTime,
                                    clinicName: appt.clinicName,
                                    status: AppointmentStatusDisplay.from(string: appt.status.name),
                                    isUpcoming: selectedTab == 0,
                                    onModify: selectedTab == 0 ? {} : nil,
                                    onCancel: selectedTab == 0 ? {
                                        cancelTargetId = appt.id
                                        showCancelAlert = true
                                    } : nil
                                )
                            }
                        }
                        .padding(.horizontal, Dimens.paddingL)
                        .padding(.vertical, 12)
                    }
                    .background(Color.appBackground)
                }
            }
        }
        .background(Color.appBackground)
        .navigationBarHidden(true)
        .alert("Cancel Appointment", isPresented: $showCancelAlert) {
            Button("Keep It", role: .cancel) { cancelTargetId = nil }
            Button("Yes, Cancel", role: .destructive) {
                if let id = cancelTargetId {
                    viewModel.cancelAppointment(appointmentId: id)
                }
                cancelTargetId = nil
            }
        } message: {
            Text("Are you sure you want to cancel this appointment?")
        }
    }
}
