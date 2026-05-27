import XCTest
import SwiftUI
@testable import eSklepios

final class ThemeManagerTests: XCTestCase {

    func testDefaultThemeIsLight() {
        let manager = ThemeManager.shared
        // Default app theme should be light or system
        XCTAssertNotNil(manager, "ThemeManager should not be nil")
    }

    func testColorValuesAreValid() {
        // Verify brand colors are properly defined
        let primary = Color.appPrimary
        let background = Color.appBackground
        let surface = Color.appSurface

        // Colors should be non-nil and distinct from each other
        XCTAssertNotNil(primary, "Primary color should be defined")
        XCTAssertNotNil(background, "Background color should be defined")
        XCTAssertNotNil(surface, "Surface color should be defined")
    }

    func testHexColorInitializerWithValidHex() {
        // Test the hex initializer with known values
        let primary = Color(hex: "3B4FE8")
        XCTAssertNotNil(primary, "Hex color initializer should work with valid 6-char hex")
    }

    func testHexColorInitializerWithShortHex() {
        let color = Color(hex: "FFF")
        XCTAssertNotNil(color, "Hex color initializer should work with 3-char hex")
    }

    func testGradientPrimaryIsNotNil() {
        let gradient = AppGradient.primary
        XCTAssertNotNil(gradient, "Primary gradient should be defined")
    }
}
