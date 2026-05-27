import SwiftUI

struct AppScreen<Content: View>: View {
    let title: String
    var onBack: (() -> Void)? = nil
    var error: String? = nil
    var onErrorDismissed: (() -> Void)? = nil
    @ViewBuilder let content: () -> Content

    @State private var showError = false

    var body: some View {
        ScrollView {
            VStack(spacing: Spacing.none) {
                AppToolbar(title: title, onBack: onBack)
                VStack(spacing: Dimens.paddingL) {
                    content()
                }
                .padding(.horizontal, Dimens.paddingXXL)
                .padding(.top, Dimens.paddingXXL)
                .padding(.bottom, Dimens.paddingXXXL)
            }
        }
        .background(Color.appBackground)
        .navigationBarHidden(true)
        .alert(NSLocalizedString("error_title", value: "Error", comment: ""), isPresented: $showError) {
            Button(NSLocalizedString("action_ok", value: "OK", comment: "")) { onErrorDismissed?() }
        } message: {
            AppBodyText(text: error ?? "")
        }
        .onChange(of: error) { newValue in
            showError = newValue != nil
        }
    }
}