# Token Optimization Rules

## Scope & Analysis
1. Analyze impacted files only — never scan the entire repository unless explicitly requested.
2. Prefer file-specific modifications over broad rewrites.

## Code & Output
3. Reuse existing rules, skills, and patterns before introducing new ones.
4. Avoid repeating code examples already shown in the conversation.
5. Group similar fixes into a single pass.

## Communication
6. Do not explain architecture unless asked.
7. Do not restate requirements back to the user.
8. Output concise plans — bullet points over paragraphs.

## Safety Gates
9. Ask before large-scale refactors (> 20 files).
10. Confirm destructive operations (deletes, renames, moves) before executing.

11. Prefer `str_replace` over full file rewrites when < 30% of file changes.
12. Read SKILL.md files only when the task matches their domain.
13. Use `view_range` when reading large files — never read the whole file for a local fix.
14. Cache grep/find results in the same turn; don't re-run identical searches.