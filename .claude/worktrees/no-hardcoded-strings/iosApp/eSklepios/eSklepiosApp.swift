import SwiftUI

@main
struct eSklepiosApp: App {
    init() {
        KoinHelper.shared.start(enableLogging: false)
    }

    var body: some Scene {
        WindowGroup {
            RootView()
        }
    }
}
