import SwiftUI

struct SearchResultsView: View {
    let query: String

    var body: some View {
        HomeView()
            .onAppear {
                // HomeView will initialize HomeViewModelWrapper which auto-searches
                // The query is passed as a pre-filled search context
            }
    }
}
