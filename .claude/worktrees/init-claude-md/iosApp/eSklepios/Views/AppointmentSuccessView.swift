import SwiftUI

struct AppointmentSuccessView: View {
    @Environment(\.dismiss) var dismiss
    @State private var navigateToAppointments = false
    @State private var navigateToHome = false
    @State private var checkmarkScale: CGFloat = 0.5

    var body: some View {
        NavigationStack {
            ZStack {
                Color.appBackground.ignoresSafeArea()

                VStack(spacing: Dimens.paddingXXL) {
                    Spacer()

                    // Animated success circle
                    ZStack {
                        Circle()
                            .fill(Color.appSuccessBg)
                            .frame(width: 120, height: 120)
                        Image(systemName: "checkmark.circle.fill")
                            .font(.system(size: 72))
                            .foregroundColor(.appSuccess)
                            .scaleEffect(checkmarkScale)
                    }
                    .onAppear {
                        withAnimation(.spring(response: 0.5, dampingFraction: 0.6)) {
                            checkmarkScale = 1.0
                        }
                    }

                    VStack(spacing: 10) {
                        Text("Appointment Confirmed!")
                            .font(.system(size: 24, weight: .bold))
                            .foregroundColor(.appTextPrimary)
                            .multilineTextAlignment(.center)
                        Text("Your appointment has been successfully booked.")
                            .font(.system(size: 15))
                            .foregroundColor(.appTextSecondary)
                            .multilineTextAlignment(.center)
                    }

                    // Confirmed status indicator
                    HStack(spacing: 6) {
                        Image(systemName: "checkmark.circle.fill")
                            .foregroundColor(.appSuccess)
                            .font(.system(size: 15))
                        Text("CONFIRMED")
                            .font(.system(size: 13, weight: .bold))
                            .foregroundColor(.appSuccess)
                    }
                    .padding(.horizontal, 20)
                    .padding(.vertical, 10)
                    .background(Color.appSuccessBg)
                    .cornerRadius(Dimens.radiusPill)

                    Spacer()

                    // Action buttons
                    VStack(spacing: 12) {
                        PrimaryButton(title: "View My Appointments") {
                            navigateToAppointments = true
                        }
                        .frame(maxWidth: .infinity)

                        GhostButton(title: "Back to Home") {
                            navigateToHome = true
                        }
                        .frame(maxWidth: .infinity)
                    }
                    .padding(.horizontal, Dimens.paddingXXL)
                    .padding(.bottom, Dimens.paddingXXXL)
                }
            }
            .navigationBarHidden(true)
            .navigationDestination(isPresented: $navigateToAppointments) {
                MyAppointmentsView()
            }
            .navigationDestination(isPresented: $navigateToHome) {
                HomeView()
            }
        }
    }
}
