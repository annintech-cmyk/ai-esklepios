# eSklepios

Healthcare appointment booking for Luxembourg — built with Kotlin Multiplatform Mobile (KMM).

eSklepios lets patients in Luxembourg discover healthcare practitioners, book appointments, and manage their health journey from a single cross-platform app.

---

## Platforms

| Platform | Target | UI Framework |
|---------|--------|-------------|
| Android | API 26+ (Android 8.0+) | Jetpack Compose |
| iOS | iOS 17+ | SwiftUI |

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| Shared logic | Kotlin Multiplatform (KMM) |
| Networking | Ktor 3.0.3 |
| Dependency injection | Koin 4.0.0 |
| Local database | SQLDelight 2.0.2 |
| Android UI | Jetpack Compose (BOM 2024.12.01) |
| iOS UI | SwiftUI |
| ViewModel | AndroidX Lifecycle 2.8.7 (shared via KMM) |
| Localization | Twine (EN / FR / DE / LB) |
| Secure storage | EncryptedSharedPreferences (Android), Keychain (iOS) |

---

## Project Structure

```
esklepios/
├── androidApp/          Android Compose application
├── iosApp/              iOS SwiftUI application
├── shared/              KMM shared module (ViewModels, domain, data, DI)
├── strings/twine.txt    Master localization file (EN/FR/DE/LB)
├── docs/                Project documentation
├── .claude/             Claude Code project intelligence
└── Makefile             Build shortcuts
```

---

## Prerequisites

| Tool | Minimum Version |
|------|----------------|
| JDK | 17 |
| Android Studio | Hedgehog (2023.1.1)+ |
| Xcode | 15+ |
| Ruby + Twine | Latest (for localization) |

---

## Getting Started

### 1. Clone the repository

```bash
git clone <repo-url>
cd esklepios
```

### 2. Configure environment

Copy the dev properties template and fill in your values:

```bash
cp dev.properties.example dev.properties
# Edit dev.properties with your API URL and settings
```

### 3. Generate localization strings

```bash
gem install twine       # if not installed
make strings
```

### 4. Build Android

```bash
./gradlew :androidApp:assembleDebug
```

Or open the project in Android Studio and run the `androidApp` configuration.

### 5. Build iOS

Open `iosApp/eSklepios.xcodeproj` in Xcode and run on the simulator or a device.

Before the first iOS build, generate the shared KMM framework:

```bash
./gradlew :shared:assembleXCFramework
```

---

## Running Tests

### Shared (common) tests
```bash
./gradlew :shared:testDebugUnitTest
```

### Android unit tests
```bash
./gradlew :androidApp:test
```

### iOS tests
```bash
xcodebuild test \
  -project iosApp/eSklepios.xcodeproj \
  -scheme eSklepios \
  -destination 'platform=iOS Simulator,name=iPhone 16'
```

---

## Development Workflow

### Adding a new screen
See `.claude/skills/create-screen.md` for a step-by-step guide.

### Adding a new API endpoint
See `.claude/skills/create-api.md`.

### Adding a new string key
1. Add to `strings/twine.txt` with EN/FR/DE/LB values.
2. Run `make strings`.
3. Use `stringResource(R.string.key)` on Android.

### Pre-build checks
```bash
.claude/hooks/pre-build-check.sh
```

### Lint check
```bash
.claude/hooks/lint-check.sh
```

---

## Architecture

eSklepios follows Clean Architecture with MVVM:

```
UI (Compose / SwiftUI)
  → ViewModel (shared KMM)
    → Use Case (shared domain)
      → Repository (shared — interface in domain, impl in data)
        → ApiService (Ktor) / SQLDelight Database
```

For full details, see `docs/architecture.md`.

---

## Localization

The app supports 4 languages:

| Language | Code |
|---------|------|
| English | en |
| French | fr |
| German | de |
| Luxembourgish | lb |

All strings are managed in `strings/twine.txt`. Android resources are generated via `make strings`.

---

## Documentation

| Document | Description |
|----------|------------|
| `docs/projects_specs.md` | Full product requirements and API contract |
| `docs/architecture.md` | Architecture decisions and patterns |
| `docs/project_design.md` | Design system: colors, typography, components |
| `docs/project_status.md` | Current feature status and known issues |
| `docs/changelog.md` | Version history |
| `.claude/CLAUDE.md` | Developer guide (pitfalls, patterns, commands) |

---

## Contributing

### Branch naming
```
feature/<description>
fix/<description>
chore/<description>
```

### Commit format (Conventional Commits)
```
feat(shared): add MedicalRecordsViewModel
fix(android): correct SecureStorage method names
chore(deps): update Ktor to 3.0.3
```

### Before opening a PR
- [ ] `./gradlew :androidApp:test` passes
- [ ] `./gradlew :androidApp:lintDebug` passes
- [ ] iOS builds without errors
- [ ] All string keys have EN/FR/DE/LB values in `strings/twine.txt`
- [ ] `make strings` has been run if strings changed

---

## License

Proprietary — eSklepios / Intech Luxembourg. All rights reserved.
# ai-esklepios
