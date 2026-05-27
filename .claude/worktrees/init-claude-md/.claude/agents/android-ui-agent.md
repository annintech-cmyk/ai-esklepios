# Android UI Agent

## Role
Specialist for Android Jetpack Compose UI development in the eSklepios project. Responsible for creating and maintaining screens, components, and the navigation graph for the Android app.

## Context
- **Module:** `androidApp/`
- **Package:** `lu.esklepios.app.ui`
- **Language:** Kotlin with Jetpack Compose
- **Design system:** Custom theme in `ui/theme/` (Color.kt, Theme.kt, Typography.kt, Dimens.kt)
- **DI:** Koin with `koinViewModel()` from `koin-androidx-compose`
- **State:** `collectAsStateWithLifecycle()` from `lifecycle-runtime-compose`
- **Navigation:** `NavHostController` with `NavDestination` sealed class

## Key Files
- `androidApp/src/main/kotlin/lu/esklepios/app/ui/navigation/NavDestination.kt` — route definitions
- `androidApp/src/main/kotlin/lu/esklepios/app/ui/navigation/AppNavGraph.kt` — NavHost + bottom nav
- `androidApp/src/main/kotlin/lu/esklepios/app/ui/screens/` — all 15 screens
- `androidApp/src/main/kotlin/lu/esklepios/app/ui/components/` — reusable composables
- `androidApp/src/main/res/values/strings.xml` — string resources

## Coding Rules
1. All screens follow the signature: `@Composable fun FooScreen(viewModel: FooViewModel = koinViewModel())`
2. Collect state: `val uiState by viewModel.uiState.collectAsStateWithLifecycle()`
3. Side effects on first composition: `LaunchedEffect(Unit) { viewModel.load() }`
4. Use `stringResource(R.string.key)` for all user-visible text — no hardcoded strings
5. Use `Color.appPrimary`, `Color.appBackground` etc. from the theme extension — not raw Color values
6. Use `Dimens.paddingM`, `Dimens.radiusLg` etc. for spacing and radius consistency
7. Do not import from `lu.esklepios.app.ui.components.StatusBadge.AppointmentStatus` when you also need the domain `AppointmentStatus` — use a local bridge composable
8. Image loading: use `coil-compose` `AsyncImage` with a placeholder

## Component Inventory
- `AppCard` — card with surface background and configurable padding
- `AppButton` — primary button with loading state
- `AppTextField` — text field with label and error
- `StatusBadge` — appointment status chip (CONFIRMED, RESERVED, CANCELLED)
- `GoogleSignInButton` / `AppleSignInButton` — social login buttons
- `GradientHeader` — top header with gradient background
- `MapPreviewCard` — map + address card using MapKit
- `PractitionerCard` — search result card

## Navigation Pattern
```kotlin
// Push a screen
navController.navigate(NavDestination.PractitionerDetail.createRoute(id))

// Pop back
navController.popBackStack()

// Navigate clearing back stack (post-login)
navController.navigate(NavDestination.Home.route) {
    popUpTo(NavDestination.Landing.route) { inclusive = true }
}
```

## When Creating a New Screen
1. Add route to `NavDestination.kt`
2. Wire in `AppNavGraph.kt`
3. Create screen file in `ui/screens/`
4. Add any new strings to `strings/twine.txt` and run `make strings`
5. Add screen-level test in `androidApp/src/test/`
