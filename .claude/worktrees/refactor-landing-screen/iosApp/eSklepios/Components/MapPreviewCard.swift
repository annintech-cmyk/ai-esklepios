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
        AppCard(padding: 0) {
            VStack(alignment: .leading, spacing: 0) {
                // Map view
                Map(coordinateRegion: $region, annotationItems: [MapPin(coordinate: CLLocationCoordinate2D(latitude: latitude, longitude: longitude))]) { pin in
                    MapMarker(coordinate: pin.coordinate, tint: .appPrimary)
                }
                .frame(height: 140)
                .cornerRadius(Dimens.radiusLg, corners: [.topLeft, .topRight])
                .disabled(true)

                // Address row
                HStack(spacing: Dimens.paddingM) {
                    Image(systemName: "mappin.circle.fill")
                        .font(.system(size: 18))
                        .foregroundColor(.appPrimary)
                    VStack(alignment: .leading, spacing: 2) {
                        Text("Location")
                            .font(.system(size: 11))
                            .foregroundColor(.appTextSecondary)
                        Text(address)
                            .font(.system(size: 13))
                            .foregroundColor(.appTextPrimary)
                            .lineLimit(2)
                    }
                    Spacer()
                    Button(action: openInMaps) {
                        Text("Open")
                            .font(.system(size: 12, weight: .semibold))
                            .padding(.horizontal, 12)
                            .padding(.vertical, 6)
                            .background(Color.appPrimaryLight)
                            .foregroundColor(.appPrimary)
                            .cornerRadius(Dimens.radiusPill)
                    }
                }
                .padding(Dimens.paddingL)
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
