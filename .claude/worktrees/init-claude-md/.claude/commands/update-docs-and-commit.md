# Update Docs and Commit

Update project documentation files to reflect the current state of the codebase, then create a git commit.

## Steps

1. Read the current `docs/project_status.md` and `docs/changelog.md`.
2. Ask the user for the version tag and a brief summary of what changed.
3. Update `docs/project_status.md`:
   - Set the "Last Updated" date to today.
   - Update the phase completion checkboxes based on what has been built.
   - Update any known issue or blocker notes.
4. Update `docs/changelog.md`:
   - Add a new `## [X.Y.Z] - YYYY-MM-DD` section at the top (below the header).
   - List changes under `### Added`, `### Changed`, `### Fixed`, `### Removed` as applicable.
   - Follow Keep a Changelog format: https://keepachangelog.com
5. Update `strings/twine.txt` if any new string keys were added during this session.
6. Run `make strings` to regenerate Android resource files.
7. Stage all modified documentation files:
   ```
   git add docs/project_status.md docs/changelog.md strings/twine.txt androidApp/src/main/res/values/strings.xml
   ```
8. Create a commit following Conventional Commits format:
   ```
   docs(<scope>): update project status and changelog for vX.Y.Z
   ```

## Notes

- Do NOT commit `dev.properties` — it is gitignored and contains secrets.
- Do NOT commit generated build files or `.gradle/` directories.
- Always verify the commit is on the correct branch before pushing.
- If there are Android or iOS UI changes, confirm whether screenshots in `docs/` need updating.
