import XCTest
@testable import eSklepios

final class KeychainStorageTests: XCTestCase {
    private let testKey = "test_token_\(UUID().uuidString)"
    private let testValue = "test-jwt-token-value"

    override func setUp() {
        super.setUp()
        // Clean any lingering test keys
        KeychainStorage.shared.delete(key: testKey)
    }

    override func tearDown() {
        KeychainStorage.shared.delete(key: testKey)
        super.tearDown()
    }

    func testSaveAndRetrieveToken() {
        KeychainStorage.shared.save(key: testKey, value: testValue)
        let retrieved = KeychainStorage.shared.get(key: testKey)
        XCTAssertEqual(retrieved, testValue, "Retrieved token should match saved token")
    }

    func testDeleteToken() {
        KeychainStorage.shared.save(key: testKey, value: testValue)
        KeychainStorage.shared.delete(key: testKey)
        let retrieved = KeychainStorage.shared.get(key: testKey)
        XCTAssertNil(retrieved, "Token should be nil after deletion")
    }

    func testRetrieveNonExistentKeyReturnsNil() {
        let result = KeychainStorage.shared.get(key: "non_existent_key_\(UUID().uuidString)")
        XCTAssertNil(result, "Non-existent key should return nil")
    }

    func testOverwriteToken() {
        KeychainStorage.shared.save(key: testKey, value: "old-token")
        KeychainStorage.shared.save(key: testKey, value: "new-token")
        let retrieved = KeychainStorage.shared.get(key: testKey)
        XCTAssertEqual(retrieved, "new-token", "Should return latest value after overwrite")
    }
}
