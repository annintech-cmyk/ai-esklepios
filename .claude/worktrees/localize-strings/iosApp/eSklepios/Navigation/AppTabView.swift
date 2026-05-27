import SwiftUI

struct AppTabView: View {
    @State private var selectedTab = 0

    var body: some View {
        TabView(selection: $selectedTab) {
            HomeView()
                .tabItem {
                    Label(String(localized: "nav_home"), systemImage: "house.fill")
                }
                .tag(0)

            MyAppointmentsView()
                .tabItem {
                    Label(String(localized: "nav_appointments"), systemImage: "calendar")
                }
                .tag(1)

            ProfileView()
                .tabItem {
                    Label(String(localized: "nav_profile"), systemImage: "person.fill")
                }
                .tag(2)
        }
        .accentColor(.appPrimary)
    }
}

#Preview {
    AppTabView()
}
