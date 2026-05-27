import SwiftUI

struct SplashView: View {
    @StateObject private var viewModel = SplashViewModelWrapper()
    @Binding var isAuthenticated: Bool
    @Binding var isReady: Bool

    var body: some View {
        ZStack {
            AppGradient.primaryVertical
                .ignoresSafeArea()

            VStack(spacing: Dimens.paddingM) {
                AppTitleText(text: NSLocalizedString("app_name", value: "eSklepios", comment: ""), color: .white)
                AppBodyText(text: NSLocalizedString("landing_tagline", value: "Your Health, Connected", comment: ""), color: .white.opacity(0.75))
                Spacer().frame(height: Dimens.paddingXXXL + Dimens.paddingL)
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
