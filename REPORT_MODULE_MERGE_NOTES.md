# Report Module Merge Notes

The Report module from `WildGuard2` has been integrated into the latest `WildGuard(8)` project structure.

## Main implementation

- Replaced the old stub report screens with a complete two-step incident reporting flow inside `page/report_page.kt`.
- Added incident models under `data/report/IncidentReport.kt`.
- Rebuilt `viewmodel/ReportViewModel.kt` in the correct `com.example.wildguard.viewmodel` package.
- Removed the stale old-package `navigation/NavGraph.kt` and old `report_secondpage.kt` flow to avoid duplicate navigation implementations.
- Updated `navigation/navigation_bar.kt` so the Report tab uses the merged module and shares the same `RewardViewModel` as Reward/Achievements.

## Report features included

- Incident type selection
- Emergency/SOS navigation
- Map location picker
- Location search
- Current GPS location with runtime permission request
- Date and time selection
- Severity selection
- Animal count
- Aggressive-animal flag
- Weather selection
- Species field
- Gallery evidence upload
- Camera evidence capture (captured photo is now actually attached)
- ML Kit image labeling to suggest a species/label
- Description and validation
- Session-local submitted report record and generated report ID
- Authenticated Supabase user ID is attached when a session is available

## Report -> Reward interaction

A successful valid report now:

1. Adds 50 report points through the existing Reward system.
2. Increments report achievement progress.
3. Can unlock achievement bonus points such as `First Report`.
4. Queues the existing Reward point popup(s).
5. Queues a wildlife interaction message for the Reward scene.
6. Shows a success dialog with a `View Reward` action.

## Android dependencies/permissions added

- Google Play Services Location
- Coil Compose
- ML Kit Image Labeling
- osmdroid
- Fine/coarse location permissions

## Backend note

The original teammate Report module did not include a Supabase report table/repository. This merge therefore keeps report submissions in the ReportViewModel for the current app session while integrating the authenticated user ID when available. Reward integration uses the latest project's existing RewardViewModel behavior.

## 2026-08-29 Emergency + map follow-up
- Emergency Response now contains the group member-inspired press-and-hold SOS interface.
- Holding SOS for 3 seconds opens the PERHILITAN hotline in the phone dialer. ACTION_DIAL is used intentionally so no CALL_PHONE runtime permission is required and accidental immediate calls are avoided.
- Emergency GPS now requests location permission and uses getCurrentLocation(), with lastLocation as a fallback.
- Emergency guide cards can continue into the Report module.
- The Report picker uses OpenStreetMap/osmdroid for map tiles, not Google Maps SDK. Google Play Services Location is used only for GPS coordinates.
- Map initialization was strengthened for emulator/device tile loading and the UI now explicitly tells the user to tap the map to place a marker.
- Report current-location lookup now uses getCurrentLocation() and explains how to inject an emulator location if no fix exists.
