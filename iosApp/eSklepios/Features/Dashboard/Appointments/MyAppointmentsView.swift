import SwiftUI

struct MyAppointmentsView: View {
    @StateObject private var viewModel = MyAppointmentsViewModelWrapper()
    @State private var cancelTargetId: String? = nil
    @State private var showCancelAlert = false

    var body: some View {
        VStack(spacing: Spacing.none) {
            AppGradientHeaderView(
                center: .title(text: NSLocalizedString("appointments_title", value: "My Appointments", comment: ""))
            )

            Picker(NSLocalizedString("appointments_title", value: "My Appointments", comment: ""), selection: Binding(
                get: { viewModel.selectedTab },
                set: { viewModel.selectTab($0) }
            )) {
                // SwiftUI system API requires raw Text for Picker items with .tag()
                Text(NSLocalizedString("appointments_upcoming", value: "Upcoming", comment: "")).tag(0)
                Text(NSLocalizedString("appointments_past", value: "Past", comment: "")).tag(1)
            }
            .pickerStyle(.segmented)
            .padding(.horizontal, Dimens.paddingL)
            .padding(.vertical, Dimens.paddingM)
            .background(Color.appSurface)

            if viewModel.uiState.isLoading {
                LoadingView(message: NSLocalizedString("appointments_loading", value: "Loading appointments…", comment: ""))
            } else if let error = viewModel.uiState.error {
                ErrorView(message: error) { viewModel.refresh() }
            } else {
                let isUpcoming = viewModel.selectedTab == 0
                let appointments = isUpcoming
                    ? viewModel.uiState.upcomingAppointments
                    : viewModel.uiState.pastAppointments

                if appointments.isEmpty {
                    EmptyStateView(
                        icon: "calendar.badge.exclamationmark",
                        title: isUpcoming
                            ? NSLocalizedString("appointments_empty_upcoming_title", value: "No upcoming appointments", comment: "")
                            : NSLocalizedString("appointments_empty_past_title", value: "No past appointments", comment: ""),
                        message: isUpcoming
                            ? NSLocalizedString("appointments_empty_upcoming_subtitle", value: "Book your first appointment to get started", comment: "")
                            : NSLocalizedString("appointments_empty_past_subtitle", value: "Your appointment history will appear here", comment: ""),
                        actionLabel: isUpcoming
                            ? NSLocalizedString("appointments_find_practitioners", value: "Find Practitioners", comment: "")
                            : nil,
                        onAction: isUpcoming ? {} : nil
                    )
                } else {
                    ScrollView {
                        LazyVStack(spacing: Dimens.paddingM) {
                            ForEach(appointments, id: \.id) { appt in
                                AppointmentCard(
                                    practitionerName: appt.practitionerName,
                                    specialty: appt.specialty,
                                    dateTime: DateUtil.formatAppointmentDateTime(appt.dateTime),
                                    clinicName: appt.clinicName,
                                    status: AppointmentStatusDisplay.from(string: appt.status.name),
                                    isUpcoming: isUpcoming,
                                    onModify: isUpcoming ? {} : nil,
                                    onCancel: isUpcoming ? {
                                        cancelTargetId = appt.id
                                        showCancelAlert = true
                                    } : nil
                                )
                            }
                        }
                        .padding(.horizontal, Dimens.paddingL)
                        .padding(.vertical, Dimens.paddingM)
                    }
                    .background(Color.appBackground)
                }
            }
        }
        .background(Color.appBackground)
        .navigationBarHidden(true)
        .task { viewModel.loadAppointments() }
        .alert(
            NSLocalizedString("appointments_cancel_title", value: "Cancel Appointment", comment: ""),
            isPresented: $showCancelAlert
        ) {
            Button(
                NSLocalizedString("appointments_keep", value: "Keep It", comment: ""),
                role: .cancel
            ) { cancelTargetId = nil }
            Button(
                NSLocalizedString("appointments_cancel_yes", value: "Yes, Cancel", comment: ""),
                role: .destructive
            ) {
                if let id = cancelTargetId {
                    viewModel.cancelAppointment(appointmentId: id)
                }
                cancelTargetId = nil
            }
        } message: {
            AppBodyText(text: NSLocalizedString("appointments_cancel_confirm", value: "Are you sure you want to cancel this appointment?", comment: ""))
        }
    }
}