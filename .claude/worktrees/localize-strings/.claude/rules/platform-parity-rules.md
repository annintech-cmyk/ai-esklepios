# Platform Parity Rules

These rules ensure the Android and iOS apps remain functionally equivalent — same features, same flows, consistent UX.

## Rule PP-1: Every Screen Must Exist on Both Platforms
The 15 screens of eSklepios must be present and functional on both Android and iOS:

| Screen | Android | iOS |
|--------|---------|-----|
| Splash | SplashScreen.kt | SplashView.swift |
| Landing | LandingScreen.kt | LandingView.swift |
| Login | LoginScreen.kt | LoginView.swift |
| Register | RegisterScreen.kt | RegisterView.swift |
| Forgot Password | ForgotPasswordScreen.kt | ForgotPasswordView.swift |
| Home | HomeScreen.kt | HomeView.swift |
| Practitioner Detail | PractitionerDetailScreen.kt | PractitionerDetailView.swift |
| Book Appointment | BookAppointmentScreen.kt | BookAppointmentView.swift |
| Appointment Success | AppointmentSuccessScreen.kt | AppointmentSuccessView.swift |
| My Appointments | MyAppointmentsScreen.kt | MyAppointmentsView.swift |
| Profile | ProfileScreen.kt | ProfileView.swift |
| Edit Profile | EditProfileScreen.kt | EditProfileView.swift |
| Change Email | ChangeEmailScreen.kt | ChangeEmailView.swift |
| Change Password | ChangePasswordScreen.kt | ChangePasswordView.swift |
| Search Results | SearchResultsScreen.kt | SearchResultsView.swift |

## Rule PP-2: Shared ViewModel is the Contract
Both platforms MUST use the same ViewModel. The UiState fields are the contract for what each platform must render. If the shared ViewModel exposes `val appointments: List<Appointment>`, both Android and iOS must display the appointment list.

## Rule PP-3: Navigation Flows Must Match
Authentication flow (Landing → Login/Register → Home), booking flow (Home → PractitionerDetail → BookAppointment → Success), and profile flow (Profile → Edit/ChangeEmail/ChangePassword) must be implemented identically on both platforms.

## Rule PP-4: Error States Must Be Shown on Both Platforms
If `uiState.error != null`, both Android and iOS must show an error message. Do not silently swallow errors on one platform.

## Rule PP-5: Loading States Must Be Shown on Both Platforms
If `uiState.isLoading == true`, both platforms show a loading indicator (spinner or skeleton).

## Rule PP-6: Token Storage Must Be Equivalent
Android uses `SecureStorage` (EncryptedSharedPreferences). iOS uses `KeychainStorage`. Both implement `TokenStorage` with identical method semantics: `setToken`, `setRefreshToken`, `getToken`, `getRefreshToken`, `clear`.

## Rule PP-7: Localization Parity
All string keys in `strings/twine.txt` must have values in all 4 languages. A key with an empty value for any language is a violation.

## Rule PP-8: Feature Flags Must Apply on Both Platforms
If a feature is gated by a feature flag or `BuildKonfig` value, ensure the gate is respected on both platforms.

## Acceptable Platform Differences (Not Violations)
- Platform-native UI patterns: Android uses Material 3 bottom sheets, iOS uses native sheets
- Android uses BottomNavigation; iOS uses TabView
- Android uses `AsyncImage` (Coil); iOS uses `AsyncImage` (SwiftUI)
- Map integration: Android uses Google Maps / MapKit via Compose; iOS uses MapKit native
- Keyboard handling differs between platforms
- iOS may show the status bar differently
