import SwiftUI

struct RootView: View {
    @State private var isAuthenticated = false
    @State private var isReady = false

    var body: some View {
        Group {
            if !isReady {
                SplashView(isAuthenticated: $isAuthenticated, isReady: $isReady)
            } else if isAuthenticated {
                AppTabView()
            } else {
                LandingView()
            }
        }
        .animation(.easeInOut(duration: 0.3), value: isReady)
        .animation(.easeInOut(duration: 0.3), value: isAuthenticated)
    }
}

#Preview {
    RootView()
}
