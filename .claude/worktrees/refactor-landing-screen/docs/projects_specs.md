# eSklepios — Project Specifications

## 1. Project Overview

**Name:** eSklepios
**Domain:** Healthcare appointment booking for Luxembourg
**Type:** Kotlin Multiplatform Mobile (KMM) application
**Platforms:** Android 8.0+ (API 26), iOS 17+
**Languages:** English, French, German, Luxembourgish
**Bundle/Package ID:** `lu.esklepios.app`

### Business Goals
- Allow patients in Luxembourg to discover and book appointments with healthcare practitioners.
- Enable practitioners to manage their availability and patient appointments.
- Provide a bilingual (minimum) experience respecting Luxembourg's multilingual culture (LU/FR/DE/EN).
- Reduce friction in the healthcare access journey by offering a clean, modern mobile experience.

---

## 2. User Roles

| Role | Description |
|------|-------------|
| **Patient** | Primary user. Searches for practitioners, books and manages appointments, manages their profile. |
| **Practitioner** | Healthcare provider. (Future: manages availability, views appointments.) Currently read-only in v0.1. |

---

## 3. Core User Flows

### 3.1 Authentication Flow
```
App Launch
  → Splash Screen (checks auth token)
    → [Not authenticated] → Landing Screen
      → Login Screen
        → [Success] → Home
      → Register Screen
        → [Success] → Home
      → Forgot Password Screen
    → [Authenticated] → Home
```

### 3.2 Practitioner Discovery Flow
```
Home (Search + Filter)
  → Search Results
    → Practitioner Detail
      → Book Appointment (slot selection)
        → Appointment Success (confirmation)
```

### 3.3 Appointment Management Flow
```
My Appointments Tab
  → View upcoming appointments
  → View past appointments
  → Cancel upcoming appointment (with confirmation)
```

### 3.4 Profile Management Flow
```
Profile Tab
  → Edit Profile
  → Change Email
  → Change Password
  → Logout
```

---

## 4. Screens

| # | Screen | Role | Description |
|---|--------|------|-------------|
| 1 | Splash | Any | Auth check + animated launch |
| 2 | Landing | Guest | App introduction + CTA to login/register |
| 3 | Login | Guest | Email + password login, Google/Apple SSO |
| 4 | Register | Guest | New account creation (name, email, password) |
| 5 | Forgot Password | Guest | Password reset email trigger |
| 6 | Home | Patient | Practitioner search with filters |
| 7 | Search Results | Patient | Filtered list of practitioners |
| 8 | Practitioner Detail | Patient | Full practitioner profile + slot picker |
| 9 | Book Appointment | Patient | Slot confirmation + booking |
| 10 | Appointment Success | Patient | Booking confirmation screen |
| 11 | My Appointments | Patient | Upcoming + past appointment tabs |
| 12 | Profile | Patient | User info summary |
| 13 | Edit Profile | Patient | Update personal info |
| 14 | Change Email | Patient | Secure email change |
| 15 | Change Password | Patient | Secure password change |

---

## 5. Functional Requirements

### Authentication
- FR-AUTH-01: Users can register with email and password.
- FR-AUTH-02: Users can log in with email and password.
- FR-AUTH-03: Users can reset their password via email.
- FR-AUTH-04: Users can sign in with Google (OAuth2).
- FR-AUTH-05: Users can sign in with Apple (Sign in with Apple).
- FR-AUTH-06: Authentication tokens are persisted securely (EncryptedSharedPreferences / Keychain).
- FR-AUTH-07: Expired tokens are refreshed automatically using a refresh token.
- FR-AUTH-08: Users can log out (clears all tokens).

### Practitioner Search
- FR-SEARCH-01: Users can search practitioners by name or speciality.
- FR-SEARCH-02: Users can filter by location.
- FR-SEARCH-03: Users can apply category filters (e.g., specialty type).
- FR-SEARCH-04: Search results show practitioner name, speciality, clinic, rating, and accepting-patients status.
- FR-SEARCH-05: Users can mark practitioners as favorites.

### Booking
- FR-BOOK-01: Users can view available appointment slots on a practitioner's detail screen.
- FR-BOOK-02: Users must be authenticated to book an appointment.
- FR-BOOK-03: Users can confirm a booking for a selected slot.
- FR-BOOK-04: A confirmation screen is shown after a successful booking.

### Appointments
- FR-APPT-01: Users can view their upcoming appointments.
- FR-APPT-02: Users can view their past appointment history.
- FR-APPT-03: Users can cancel an upcoming appointment.

### Profile
- FR-PROF-01: Users can view their profile (name, email, phone, profile type).
- FR-PROF-02: Users can edit their first name, last name, and phone.
- FR-PROF-03: Users can change their email (requires current password confirmation).
- FR-PROF-04: Users can change their password (requires current password + new password x2).

---

## 6. Non-Functional Requirements

| ID | Requirement |
|----|------------|
| NFR-01 | App launches to interactive state in under 3 seconds on mid-range devices. |
| NFR-02 | All network calls have a 30-second timeout. |
| NFR-03 | Authentication tokens are stored encrypted (not in plain SharedPreferences or NSUserDefaults). |
| NFR-04 | App must not crash on network unavailability — display appropriate error states. |
| NFR-05 | All user-visible text must be translated in EN, FR, DE, and LB. |
| NFR-06 | The app must pass Android lint with no errors. |
| NFR-07 | The app must build without warnings in Xcode for the target SDK. |
| NFR-08 | Unit test coverage must cover all ViewModel state transitions. |

---

## 7. API Contract (Summary)

**Base URL:** `https://api.esklepios.lu`
**Authentication:** Bearer token in `Authorization` header, with refresh via `/auth/refresh`.
**Format:** JSON (`Content-Type: application/json`)

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/auth/login` | POST | Login with email + password |
| `/auth/register` | POST | Create new account |
| `/auth/refresh` | POST | Refresh access token |
| `/auth/forgot-password` | POST | Trigger password reset email |
| `/auth/change-email` | POST | Change email (authenticated) |
| `/auth/change-password` | POST | Change password (authenticated) |
| `/practitioners` | GET | List/search practitioners |
| `/practitioners/{id}` | GET | Get practitioner detail |
| `/practitioners/{id}/slots` | GET | Get available slots |
| `/appointments` | GET | List user's appointments |
| `/appointments` | POST | Book an appointment |
| `/appointments/{id}` | DELETE | Cancel an appointment |
| `/users/me` | GET | Get current user profile |
| `/users/me` | PUT | Update user profile |

---

## 8. Data Models

### User
```
id: String
email: String
firstName: String
lastName: String
phone: String? (optional)
profileType: PATIENT | PRACTITIONER
avatarUrl: String? (optional)
```

### Appointment
```
id: String
practitionerId: String
practitionerName: String
clinicName: String
dateTime: String (ISO 8601)
status: PENDING | CONFIRMED | CANCELLED | COMPLETED | NO_SHOW
notes: String? (optional)
```

### Practitioner
```
id: String
name: String
speciality: String
clinicName: String
address: String
latitude: Double
longitude: Double
rating: Double
reviewCount: Int
isAcceptingPatients: Boolean
avatarUrl: String?
```

### Slot
```
id: String
practitionerId: String
dateTime: String (ISO 8601)
isAvailable: Boolean
durationMinutes: Int
```

---

## 9. Localization

| Language | Code | Status |
|---------|------|--------|
| English | en | Complete |
| French | fr | Complete |
| German | de | Complete |
| Luxembourgish | lb | Complete |

All strings maintained in `strings/twine.txt`. Android resources generated via `make strings`.

---

## 10. Out of Scope (v0.1)

- Practitioner-facing app (scheduling management, patient list)
- Push notifications
- Video/telemedicine appointments
- Prescription management
- Insurance integration
- Payments
- In-app chat / messaging
- Ratings and reviews submission
