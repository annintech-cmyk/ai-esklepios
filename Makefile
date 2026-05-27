.PHONY: strings

strings:
	twine generate-all-localization-files strings/twine.txt androidApp/src/main/res --format android
