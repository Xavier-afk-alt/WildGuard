# User Profile Module Merge Notes

Base project: WildGuard(10) (Reward + Report + Emergency)
Profile/auth source: WildGuard (2)

## Integrated from the profile branch

- Supabase email login
- Supabase email sign-up with full name and phone metadata
- Google OAuth login entry point
- Facebook OAuth login entry point
- Authentication/session state gate before the main app
- OAuth deep-link handling in MainActivity
- `wildguard://auth` Android intent filter
- Login/Sign Up UI
- WildGuard login logo resource
- Log Out button on Profile page

## Integration decisions

- Kept the newer WildGuard(10) navigation routes and Report flow.
- Kept the newer Report -> Reward interaction.
- Kept the newer Emergency Response/SOS implementation.
- Kept the newer Reward screens/ViewModel and Supabase repository files.
- Did not copy the profile branch's separate SupabaseProvider. Auth now uses `data.remote.SupabaseProvider`, the same client used by Reward/Report, avoiding split sessions.
- Kept the existing profile subpages from the base project because the corresponding files in WildGuard (2) are the same placeholder implementations.
- Did not overwrite Home with the other branch's unrelated Home redesign.
- Corrected the default instrumented test package assertion to match `com.example.wildguard`.

## Configuration

See `SUPABASE_PROFILE_SETUP.md`.
