.PHONY: strings hooks lint lint-kotlin lint-ios test test-shared test-android format

# ─── Localization ─────────────────────────────────────────────────────────────
strings:
	twine generate-all-localization-files strings/twine.txt androidApp/src/main/res --format android

# ─── Developer setup ──────────────────────────────────────────────────────────
hooks:
	bash scripts/install-hooks.sh

# ─── Lint ─────────────────────────────────────────────────────────────────────
lint: lint-kotlin lint-ios

lint-kotlin:
	./gradlew :shared:detekt :androidApp:detekt :shared:ktlintCheck :androidApp:ktlintCheck :androidApp:lintDebug

lint-ios:
	swiftlint lint --config .swiftlint.yml

# ─── Tests ───────────────────────────────────────────────────────────────────
test: test-shared test-android

test-shared:
	./gradlew :shared:testDebugUnitTest

test-android:
	./gradlew :androidApp:testDebugUnitTest

# ─── Auto-format ──────────────────────────────────────────────────────────────
format:
	./gradlew :shared:ktlintFormat :androidApp:ktlintFormat
	swiftlint lint --fix --config .swiftlint.yml
