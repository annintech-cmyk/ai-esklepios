import SwiftUI

struct SplashView: View {
    @StateObject private var viewModel = SplashViewModelWrapper()
    @Binding var isAuthenticated: Bool
    @Binding var isReady: Bool

    var body: some View {
        ZStack {
            AppGradient.primaryVertical
                .ignoresSafeArea()

            VStack(spacing: 12) {
                Text(String(localized: "app_name"))
                    .font(.system(size: 46, weight: .bold))
                    .foregroundColor(.white)
                Text(String(localized: "splash_tagline"))
                    .font(.system(size: 15))
                    .foregroundColor(.white.opacity(0.75))
                Spacer().frame(height: 48)
                ProgressView()
                    .tint(.white)
                    .scaleEffect(1.2)
            }
        }
        // Dark colour scheme → system status bar shows light (white) icons on the gradient
        .preferredColorScheme(.dark)
        .onAppear {
            viewModel.checkAuth()
        }
        .onChange(of: viewModel.uiState.isLoading) { loading in
            if !loading {
                isAuthenticated = viewModel.uiState.isAuthenticated
                isReady = true
            }
        }
    }
}
