import SwiftUI
import MapKit

struct MapPreviewCard: View {
    let latitude: Double
    let longitude: Double
    let address: String

    @State private var region: MKCoordinateRegion

    init(latitude: Double, longitude: Double, address: String) {
        self.latitude = latitude
        self.longitude = longitude
        self.address = address
        _region = State(initialValue: MKCoordinateRegion(
            center: CLLocationCoordinate2D(latitude: latitude, longitude: longitude),
            span: MKCoordinateSpan(latitudeDelta: 0.005, longitudeDelta: 0.005)
        ))
    }

    var body: some View {
        AppCard(padding: Spacing.none) {
            VStack(alignment: .leading, spacing: Spacing.none) {
                // Map view
                Map(coordinateRegion: $region, annotationItems: [MapPin(coordinate: CLLocationCoordinate2D(latitude: latitude, longitude: longitude))]) { pin in
                    MapMarker(coordinate: pin.coordinate, tint: .appPrimary)
                }
                .frame(height: Dimens.mapPreviewHeight)
                .cornerRadius(Radius.lg, corners: [.topLeft, .topRight])
                .disabled(true)

                // Address row
                HStack(spacing: Spacing.m) {
                    Image(systemName: "mappin.circle.fill")
                        .font(.system(size: Dimens.iconCompact))
                        .foregroundColor(.appPrimary)
                    VStack(alignment: .leading, spacing: Spacing.xxs) {
                        AppCaptionText(text: "Location")
                        AppLabelText(text: address, maxLines: 2)
                    }
                    Spacer()
                    Button(action: openInMaps) {
                        AppCaptionText(text: "Open", color: .appPrimary)
                            .padding(.horizontal, Spacing.m)
                            .padding(.vertical, Spacing.tiny)
                            .background(Color.appPrimaryLight)
                            .cornerRadius(Radius.pill)
                    }
                }
                .padding(Spacing.l)
            }
        }
    }

    private func openInMaps() {
        let coordinate = CLLocationCoordinate2D(latitude: latitude, longitude: longitude)
        let placemark = MKPlacemark(coordinate: coordinate)
        let mapItem = MKMapItem(placemark: placemark)
        mapItem.name = address
        mapItem.openInMaps(launchOptions: [MKLaunchOptionsDirectionsModeKey: MKLaunchOptionsDirectionsModeDriving])
    }
}

private struct MapPin: Identifiable {
    let id = UUID()
    let coordinate: CLLocationCoordinate2D
}

extension View {
    func cornerRadius(_ radius: CGFloat, corners: UIRectCorner) -> some View {
        clipShape(RoundedCorner(radius: radius, corners: corners))
    }
}

struct RoundedCorner: Shape {
    var radius: CGFloat
    var corners: UIRectCorner

    func path(in rect: CGRect) -> Path {
        let path = UIBezierPath(
            roundedRect: rect,
            byRoundingCorners: corners,
            cornerRadii: CGSize(width: radius, height: radius)
        )
        return Path(path.cgPath)
    }
}
