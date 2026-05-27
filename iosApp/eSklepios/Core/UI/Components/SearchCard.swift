import SwiftUI

struct SearchCard: View {
    @Binding var searchQuery: String
    @Binding var locationQuery: String
    let onSearchTap: () -> Void
    var variant: SearchInputVariant = .light

    var body: some View {
        VStack(spacing: Dimens.paddingM) {
            SearchInputField(
                systemIcon: "stethoscope",
                placeholder: NSLocalizedString("landing_search_hint", value: "Search specialty, doctor name…", comment: ""),
                text: $searchQuery,
                iconColor: variant == .light ? .appTextHint : nil,
                variant: variant
            )
            SearchInputField(
                systemIcon: "mappin.and.ellipse",
                placeholder: NSLocalizedString("landing_location_hint", value: "City or postal code", comment: ""),
                text: $locationQuery,
                iconColor: variant == .light ? .appPrimaryMid : nil,
                variant: variant
            )
            PrimaryButton(
                title: NSLocalizedString("landing_find_practitioners", value: "Find Practitioners", comment: ""),
                icon: "arrow.right",
                action: onSearchTap
            )
        }
    }
}