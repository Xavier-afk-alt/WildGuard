create table if not exists public.incident_reports (
    report_id text primary key,
    user_id uuid references auth.users(id) on delete set null,
    incident_type text not null,
    animal_species text not null default '',
    animal_count integer not null default 1 check (animal_count > 0),
    is_aggressive boolean not null default false,
    latitude double precision not null,
    longitude double precision not null,
    location_name text not null,
    description text not null,
    severity text not null default 'MEDIUM',
    reported_at bigint not null
);

alter table public.incident_reports enable row level security;

drop policy if exists "Everyone can view incident reports" on public.incident_reports;
create policy "Everyone can view incident reports"
on public.incident_reports for select
to anon, authenticated
using (true);

drop policy if exists "Users can submit incident reports" on public.incident_reports;
create policy "Users can submit incident reports"
on public.incident_reports for insert
to authenticated
with check (auth.uid() = user_id);

drop policy if exists "Users can update own incident reports" on public.incident_reports;
create policy "Users can update own incident reports"
on public.incident_reports for update
to authenticated
using (auth.uid() = user_id)
with check (auth.uid() = user_id);

grant select on public.incident_reports to anon, authenticated;
grant insert, update on public.incident_reports to authenticated;
