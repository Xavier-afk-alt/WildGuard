# Shared incident map setup

The app can display a report immediately on the same device. To share reports between all users and devices, create the public incident table once:

1. Open the WildGuard project in Supabase.
2. Open **SQL Editor** and choose **New query**.
3. Copy all SQL from `supabase/migrations/20260901_incident_reports.sql`.
4. Select **Run**.
5. Confirm that **Table Editor** now contains `incident_reports`.

The included Row Level Security rules allow every app client to read map reports while only authenticated users can submit or update their own report.
