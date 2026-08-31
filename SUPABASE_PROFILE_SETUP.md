# WildGuard Profile/Auth + Reward/Report Supabase setup

This merged project uses ONE Supabase client for authentication and the existing Reward/Report data layer.

## Connection properties

The project accepts these values from either `local.properties` or root `gradle.properties`:

```properties
SUPABASE_URL=https://YOUR_PROJECT_REF.supabase.co
SUPABASE_PUBLISHABLE_KEY=YOUR_PUBLISHABLE_OR_ANON_KEY
```

`local.properties` takes priority when both are present. Use only a public/publishable/anon key in the Android app; never embed a service-role key.

The member branch's publishable connection settings were migrated to the merged root `gradle.properties` using the `SUPABASE_PUBLISHABLE_KEY` name expected by the Reward/Report project.

## Email authentication

In Supabase Dashboard -> Authentication -> Providers -> Email, enable Email.

## OAuth callback

In Supabase Dashboard -> Authentication -> URL Configuration -> Redirect URLs, add:

```text
wildguard://auth
```

The same callback is configured in AndroidManifest.xml and in the shared Supabase Auth client.

## Google / Facebook

Enable the provider in Supabase Authentication and configure the provider's client/app credentials. The Android app starts OAuth through Supabase and receives the result through `wildguard://auth`.

## Reward database

If Reward persistence is used, apply the existing migration:

`supabase/migrations/20260826_reward_module.sql`

The migration uses `auth.users.id`, so authentication must be enabled for per-user reward data.
