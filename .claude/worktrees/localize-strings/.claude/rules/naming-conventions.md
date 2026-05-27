# Naming Conventions

## Kotlin (Shared + Android)

### Classes and Objects
| Item | Convention | Example |
|------|-----------|---------|
| Class | PascalCase | `HomeViewModel`, `AppointmentRepositoryImpl` |
| Object | PascalCase | `NavDestination`, `SharedModule` |
| Interface | PascalCase (no "I" prefix) | `AuthRepository`, `TokenStorage` |
| Enum class | PascalCase | `AppointmentStatus`, `ProfileType` |
| Enum values | SCREAMING_SNAKE_CASE | `CONFIRMED`, `NO_SHOW` |
| Sealed class | PascalCase | `AuthResult` |
| Data class | PascalCase | `FooUiState`, `LoginRequest` |

### Functions and Properties
| Item | Convention | Example |
|------|-----------|---------|
| Function | camelCase | `loadAppointments()`, `toggleFavorite()` |
| Property | camelCase | `isLoading`, `selectedSlot` |
| Private backing field | _camelCase | `_uiState` |
| Constant (companion) | SCREAMING_SNAKE_CASE | `BASE_URL`, `MAX_RETRY` |

### Files
| Item | Convention | Example |
|------|-----------|---------|
| Kotlin source file | PascalCase matching class | `HomeViewModel.kt` |
| One class per file (preferred) | — | — |

### Packages
```
lu.esklepios.app.data.network
lu.esklepios.app.data.repository
lu.esklepios.app.domain.model
lu.esklepios.app.domain.repository
lu.esklepios.app.domain.usecase
lu.esklepios.app.presentation.viewmodel
lu.esklepios.app.di
lu.esklepios.app.ui.screens       (androidApp)
lu.esklepios.app.ui.components    (androidApp)
lu.esklepios.app.ui.navigation    (androidApp)
lu.esklepios.app.ui.theme         (androidApp)
lu.esklepios.app.storage          (androidApp)
```

## Swift (iOS)

### Types
| Item | Convention | Example |
|------|-----------|---------|
| Struct | PascalCase | `PractitionerCard`, `AppointmentCard` |
| Class | PascalCase | `HomeViewModelWrapper`, `ThemeManager` |
| Protocol | PascalCase | `ViewModelWrapper` |
| Enum | PascalCase | `AppDestination`, `AppointmentStatusDisplay` |
| Enum cases | camelCase | `.practitionerDetail`, `.confirmed` |

### Functions and Properties
| Item | Convention | Example |
|------|-----------|---------|
| Function | camelCase | `loadPractitioners()`, `openInMaps()` |
| Property | camelCase | `isLoading`, `selectedSlot` |
| `@Published` property | camelCase | `uiState` |
| Private property | camelCase (no underscore) | `viewModel` |

### Files
- One view/component per file, named to match the struct: `HomeView.swift`
- Extensions in separate files if large: `Color+Extensions.swift`

## String Keys (Twine / Android Resources)
- Section.key: `snake_case` with dot separator: `home.search_placeholder`
- Android resource name: same with underscore: `home_search_placeholder`
- Twine section headers: `[[section_name]]` in double brackets

## Git Branch Names
```
feature/<short-description>
fix/<short-description>
chore/<short-description>
refactor/<short-description>
docs/<short-description>
test/<short-description>
```
All lowercase, hyphens for spaces.

## Commit Types (Conventional Commits)
`feat`, `fix`, `refactor`, `test`, `docs`, `chore`, `style`, `perf`, `ci`
