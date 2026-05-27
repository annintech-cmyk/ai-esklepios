import SwiftUI

extension View {
    func placeholder<Content: View>(when shouldShow: Bool, @ViewBuilder placeholder: () -> Content) -> some View {
        ZStack(alignment: .leading) {
            if shouldShow { placeholder() }
            self
        }
    }

    func heroTransition(id: String?, in namespace: Namespace.ID?) -> some View {
        modifier(HeroModifier(heroId: id, namespace: namespace))
    }
}

private struct HeroModifier: ViewModifier {
    let heroId: String?
    let namespace: Namespace.ID?

    @ViewBuilder
    func body(content: Content) -> some View {
        if let id = heroId, let ns = namespace {
            content.matchedGeometryEffect(id: id, in: ns)
        } else {
            content
        }
    }
}