# Skill: rebuild-screen

Rebuild the UI layer of an existing screen on Android and/or iOS to enforce all project
rules. Never modifies the ViewModel, UiState, or any shared layer.

---

## 0. Parse Arguments

`$ARGUMENTS` format:
- `HomeScreen` → both platforms
- `android:LoginScreen` → Android only
- `ios:ProfileView` → iOS only
- `dashboard/home/PractitionerDetailScreen` → both (partial path hint)

Extract: `{platform}` (android | ios | both) and `{screen}` (name without suffix).

---

## 1. Read Rules First

Before touching any file, read and internalise:

```
.claude/rules/ui-rules.md
.claude/rules/design-system-rules.md
.claude/rules/architecture-rules.md
.claude/rules/accessibility-rules.md
.claude/rules/localization-rules.md
```

These are the source of truth. Everything in this skill defers to them.

---

## 2. Read Source of Truth Files

Read these files to know what tokens and components actually exist.
Never assume — always verify from the file.

**Design tokens**
```
androidApp/src/main/kotlin/lu/esklepios/app/core/ui/theme/Dimens.kt
iosApp/eSklepios/Core/UI/Theme/AppDimens.swift
iosApp/eSklepios/Core/UI/Theme/AppFonts.swift
iosApp/eSklepios/Core/UI/Theme/AppColors.swift
```

**Component library**
```
androidApp/src/main/kotlin/lu/esklepios/app/core/ui/components/   ← list directory
iosApp/eSklepios/Core/UI/Components/                               ← list directory
```

For every raw primitive you find in the screen, locate its wrapper here.
If no wrapper exists, use the raw primitive and add a TODO comment — do not invent wrappers.

---

## 3. Locate Screen Files

**Android**
```
androidApp/src/main/kotlin/lu/esklepios/app/view/**/{Screen}Screen.kt
```

**iOS**
```
iosApp/eSklepios/Features/**/{Screen}View.swift
iosApp/eSklepios/Features/**/{Screen}ViewModelWrapper.swift
```

**Shared (read-only)**
```
shared/src/commonMain/.../presentation/viewmodel/{Screen}ViewModel.kt
```

---

## 4. Understand Before Writing

Read the ViewModel file and extract:
- Every field in `UiState` — name, type, default
- Every public function — name, parameters
- Whether data loads on init or via explicit call
- Navigation side-effect flags (e.g. `isLoggedOut`, `navigateToHome`)

Read the current screen files and note:
- Current layout structure
- Which components are already wrappers vs raw primitives
- Navigation in/out — what triggers it, where it goes
- All user-visible strings (including accessibility labels)

---

## 5. Rebuild Android Screen

Apply rules read in Step 1. Use only tokens and components found in Step 2.

**Signature**
```kotlin
@Composable
fun {Screen}Screen(viewModel: {Screen}ViewModel = koinViewModel()) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
}
```

**One-time load**
```kotlin
LaunchedEffect(Unit) { viewModel.load() }
```

**All async screens must handle all three states**
```kotlin
when {
    uiState.isLoading -> LoadingIndicator(Modifier.fillMaxSize())
    uiState.error != null -> ErrorView(uiState.error!!, onRetry = { viewModel.load() })
    uiState.isEmpty -> EmptyStateView(...)
    else -> { /* main UI */ }
}
```

**User actions** — call ViewModel directly, never launch coroutines in onClick.

**Strings** — all from `stringResource(R.string.*)`. No hardcoded text.

**Dimensions** — all from `Dimens.kt`. No numeric `.dp` / `.sp` literals.

**Colors** — all from `Color.app*`. No hex values.

**Primitives** — replace with wrappers found in Step 2. No raw `Text`, `Icon`, `Button`.

**Accessibility** — every Icon has `contentDescription`; decorative ones documented with `// a11y: decorative`.

---

## 6. Rebuild iOS View + Wrapper

Apply rules read in Step 1. Use only tokens and components found in Step 2.

**ViewModelWrapper**
```swift
@MainActor
class {Screen}ViewModelWrapper: ObservableObject {
    let viewModel: {Screen}ViewModel
    @Published var uiState: {Screen}UiState
    private var stateObserver: FlowWatcher?

    init(viewModel: {Screen}ViewModel = KoinHelper.get()) {
        self.viewModel = viewModel
        uiState = viewModel.uiState.value as! {Screen}UiState
        stateObserver = FlowExtensionsKt.watch(viewModel.uiState) { [weak self] state in
            guard let state = state as? {Screen}UiState else { return }
            Task { @MainActor [weak self] in self?.uiState = state }
        }
    }

    deinit { stateObserver?.close() }
}
```

**View ownership**
```swift
@StateObject private var viewModel = {Screen}ViewModelWrapper()
```

**Async load**
```swift
.task { viewModel.viewModel.load() }
```

**All async views must handle all three states**
```swift
if viewModel.uiState.isLoading {
    LoadingView()
} else if let error = viewModel.uiState.error {
    ErrorView(message: error, retry: { viewModel.viewModel.load() })
} else if viewModel.uiState.data.isEmpty {
    EmptyStateView(...)
} else {
    // main UI
}
```

**User actions** — call ViewModel directly, never wrap in `Task` inside button closures.

**Strings** — all from `NSLocalizedString("key", comment: "")`. No hardcoded text.

**Dimensions** — all from `AppDimens.swift` / `AppFonts.swift`. No numeric literals.

**Colors** — all from `.app*` extensions. No hex values.

**Primitives** — replace with wrappers found in Step 2.

**Accessibility** — decorative images `.accessibilityHidden(true)`; informative images `.accessibilityLabel(Text(...))`.

---

## 7. Localization

1. Grep both rebuilt files for any hardcoded user-visible string.
2. For each one, add to `strings/twine.txt`:
```
[screen.key_name]
en = ...
fr = ...
de = ...
lb = ...
```
3. Run `make strings`.
4. Replace hardcoded strings with `stringResource` / `NSLocalizedString`.

---

## 8. Verify

**Android**
```bash
./gradlew :androidApp:assembleDebug
./gradlew :androidApp:lint
```

**iOS**
```bash
xcodebuild -project iosApp/eSklepios.xcodeproj -scheme eSklepios build
```

Fix any errors before committing. Do not skip this step.

---

## 9. Commit

```
refactor(<android|ios|shared>): rebuild {Screen} — enforce design system rules

- Tokens: all dims/colors via Dimens.kt / AppDimens.swift
- Components: raw primitives → semantic wrappers
- State: loading/error/empty handled (Rules PP-4, PP-5)
- iOS: @MainActor FlowWatcher pattern (Rule FW-1)
- Android: collectAsStateWithLifecycle (Rule SM-8)
- Strings: centralised to twine.txt
- Accessibility: labels added (Rule A11Y-1)
```

---

## Invariants

- ViewModel, UiState, SharedModule — **never modified**
- No wrapper invented that doesn't exist in the component library
- No token used that doesn't exist in Dimens.kt / AppDimens.swift
- Both platforms must reach parity unless explicitly single-platform
