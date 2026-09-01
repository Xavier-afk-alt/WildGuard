create table if not exists public.news (
    id uuid primary key,
    title text not null,
    content text not null,
    image_base64 text,
    created_at bigint not null
);

create table if not exists public.announcements (
    id uuid primary key,
    title text not null,
    content text not null,
    created_at bigint not null
);

create table if not exists public.profiles (
    id uuid primary key references auth.users(id) on delete cascade,
    full_name text not null default '',
    email text not null default '',
    phone text not null default '',
    is_disabled boolean not null default false,
    created_at timestamptz not null default now()
);

alter table public.profiles add column if not exists full_name text not null default '';
alter table public.profiles add column if not exists email text not null default '';
alter table public.profiles add column if not exists phone text not null default '';
alter table public.profiles add column if not exists is_disabled boolean not null default false;
alter table public.profiles add column if not exists created_at timestamptz not null default now();

create or replace function public.create_user_profile()
returns trigger language plpgsql security definer set search_path = public as $$
begin
    insert into public.profiles (id, full_name, email, phone)
    values (
        new.id,
        coalesce(new.raw_user_meta_data->>'full_name', ''),
        coalesce(new.email, ''),
        coalesce(new.raw_user_meta_data->>'phone', '')
    ) on conflict (id) do nothing;
    return new;
end;
$$;

drop trigger if exists create_profile_after_signup on auth.users;
create trigger create_profile_after_signup after insert on auth.users
for each row execute procedure public.create_user_profile();

insert into public.profiles (id, full_name, email, phone)
select id, coalesce(raw_user_meta_data->>'full_name', ''), coalesce(email, ''),
       coalesce(raw_user_meta_data->>'phone', '')
from auth.users on conflict (id) do nothing;

alter table public.news enable row level security;
alter table public.announcements enable row level security;
alter table public.profiles enable row level security;

create or replace function public.is_wildguard_admin()
returns boolean language sql stable as $$
    select coalesce(auth.jwt()->>'email', '') = 'admin@wildguard.app';
$$;

drop policy if exists "Everyone reads news" on public.news;
create policy "Everyone reads news" on public.news for select to anon, authenticated using (true);
drop policy if exists "Admin manages news" on public.news;
create policy "Admin manages news" on public.news for all to authenticated
using (public.is_wildguard_admin()) with check (public.is_wildguard_admin());

drop policy if exists "Everyone reads announcements" on public.announcements;
create policy "Everyone reads announcements" on public.announcements for select to anon, authenticated using (true);
drop policy if exists "Admin manages announcements" on public.announcements;
create policy "Admin manages announcements" on public.announcements for all to authenticated
using (public.is_wildguard_admin()) with check (public.is_wildguard_admin());

drop policy if exists "Users read own profile" on public.profiles;
create policy "Users read own profile" on public.profiles for select to authenticated
using (auth.uid() = id or public.is_wildguard_admin());
drop policy if exists "Admin manages profiles" on public.profiles;
create policy "Admin manages profiles" on public.profiles for all to authenticated
using (public.is_wildguard_admin()) with check (public.is_wildguard_admin());

grant select on public.news, public.announcements to anon, authenticated;
grant insert, update, delete on public.news, public.announcements to authenticated;
grant select, insert, update, delete on public.profiles to authenticated;
