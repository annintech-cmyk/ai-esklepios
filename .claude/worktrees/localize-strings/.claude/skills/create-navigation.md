# Skill: Create Navigation

Wires a new route into both the Android NavGraph and the iOS NavigationStack.

## Usage
```
/create-navigation <RouteName> [parameters] [description]
```
Example: `/create-navigation MedicalRecordDetail recordId:String Show detail for a medical record`

## Android

### Step 1: Add NavDestination
In `androidApp/src/main/kotlin/lu/esklepios/app/ui/navigation/NavDestination.kt`:

**Simple route (no parameters):**
```kotlin
object MedicalRecords : NavDestination("medical_records")
```

**Route with single parameter:**
```kotlin
object MedicalRecordDetail : NavDestination("medical_record/{recordId}") {
    fun createRoute(recordId: String) = "medical_record/$recordId"
    const val ARG_RECORD_ID = "recordId"
}
```

**Route with multiple parameters:**
```kotlin
object FooDetail : NavDestination("foo/{id}/{type}") {
    fun createRoute(id: String, type: String) = "foo/$id/$type"
    const val ARG_ID = "id"
    const val ARG_TYPE = "type"
}
```

### Step 2: Add to NavHost in AppNavGraph.kt

**Simple:**
```kotlin
composable(NavDestination.MedicalRecords.route) {
    MedicalRecordsScreen(
        onNavigateToDetail = { id ->
            navController.navigate(NavDestination.MedicalRecordDetail.createRoute(id))
        }
    )
}
```

**With arguments:**
```kotlin
composable(
    route = NavDestination.MedicalRecordDetail.route,
    arguments = listOf(
        navArgument(NavDestination.MedicalRecordDetail.ARG_RECORD_ID) {
            type = NavType.StringType
        }
    )
) { backStackEntry ->
    val recordId = backStackEntry.arguments?.getString(
        NavDestination.MedicalRecordDetail.ARG_RECORD_ID
    ) ?: return@composable
    MedicalRecordDetailScreen(recordId = recordId)
}
```

### Step 3: Wire into parent screen
In the source screen, inject `navController` via parameter and navigate:
```kotlin
Button(onClick = { navController.navigate(NavDestination.MedicalRecordDetail.createRoute(record.id)) }) {
    Text("View Detail")
}
```

### Adding to Bottom Nav (top-level only)
In `AppNavGraph.kt` `AppBottomNavBar` composable, add a `BottomNavigationItem`:
```kotlin
val bottomNavItems = listOf(
    BottomNavItem(NavDestination.Home, R.string.nav_home, Icons.Default.Home),
    BottomNavItem(NavDestination.MyAppointments, R.string.nav_appointments, Icons.Default.CalendarToday),
    BottomNavItem(NavDestination.Profile, R.string.nav_profile, Icons.Default.Person),
    // Add new top-level item here
)
```

## iOS

### Step 1: Add to AppDestination Enum
In `iosApp/eSklepios/Navigation/AppTabView.swift` or a `NavigationDestination.swift` file:

```swift
enum AppDestination: Hashable {
    case practitionerDetail(id: String)
    case bookAppointment(practitionerId: String, slotId: String)
    case medicalRecordDetail(id: String)  // Add new case
}
```

### Step 2: Add navigationDestination Handler

In the relevant `NavigationStack` view:
```swift
.navigationDestination(for: AppDestination.self) { destination in
    switch destination {
    case .practitionerDetail(let id):
        PractitionerDetailView(practitionerId: id)
    case .bookAppointment(let practitionerId, let slotId):
        BookAppointmentView(practitionerId: practitionerId, slotId: slotId)
    case .medicalRecordDetail(let id):          // Add new case
        MedicalRecordDetailView(recordId: id)
    }
}
```

### Step 3: Trigger Navigation
Using `NavigationLink`:
```swift
NavigationLink(value: AppDestination.medicalRecordDetail(id: record.id)) {
    MedicalRecordRow(record: record)
}
```

Using programmatic navigation (with `@State var path: NavigationPath`):
```swift
Button("View Detail") {
    navigationPath.append(AppDestination.medicalRecordDetail(id: record.id))
}
```

### Adding to TabView (top-level only)
In `AppTabView.swift`:
```swift
TabView {
    HomeView()
        .tabItem { Label("Home", systemImage: "house") }
    MyAppointmentsView()
        .tabItem { Label("Appointments", systemImage: "calendar") }
    ProfileView()
        .tabItem { Label("Profile", systemImage: "person") }
    // Add new top-level tab here
}
```

## Checklist
- [ ] NavDestination added to Kotlin sealed class
- [ ] `composable(...)` added in AppNavGraph
- [ ] Arguments extracted correctly in `backStackEntry`
- [ ] `AppDestination` enum case added (iOS)
- [ ] `navigationDestination` switch case added (iOS)
- [ ] New view accepts the route parameter in its `init`
