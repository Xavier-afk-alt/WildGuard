# WildGuard Admin Module Setup

Complete these steps once before testing the Admin Dashboard.

## 1. Create the fixed administrator account

In Supabase Dashboard, open **Authentication > Users > Add user > Create new user** and enter:

```text
Email: admin@wildguard.app
Password: Admin@123
Auto Confirm User: enabled
```

The Android Admin Login still uses:

```text
Username: admin
Password: Admin@123
```

The app signs in to the Supabase administrator account behind the scenes so Row Level Security can distinguish the administrator from ordinary users.

## 2. Create the Admin Module tables and policies

Open **SQL Editor > New query**, copy all content from:

```text
supabase/migrations/20260901_admin_module.sql
```

Select **Run**. Confirm that Table Editor contains:

- `news`
- `announcements`
- `profiles`

The script also creates profiles for existing Auth users and automatically creates a profile whenever a new user signs up.

## 3. Keep the Incident Report table

The Report Details page uses the existing `incident_reports` table. If it has not been created, also run:

```text
supabase/migrations/20260901_incident_reports.sql
```

Do not place a Supabase Secret or service-role key in the Android project. The public/publishable key and included Row Level Security policies are sufficient.
