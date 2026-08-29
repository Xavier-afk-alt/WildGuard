-- WildGuard Reward module schema.
-- Existing public.profiles is intentionally NOT modified here.

create table if not exists public.reward_progress (
    user_id uuid primary key references auth.users(id) on delete cascade,
    current_points integer not null default 0 check (current_points >= 0),
    plant_growth_points integer not null default 0 check (plant_growth_points >= 0),
    total_earned_points integer not null default 0 check (total_earned_points >= 0),
    last_daily_login_date date,
    login_count integer not null default 0 check (login_count >= 0),
    report_count integer not null default 0 check (report_count >= 0),
    guidebook_count integer not null default 0 check (guidebook_count >= 0),
    selected_animal text not null default 'TIGER',
    updated_at timestamptz not null default now()
);

create table if not exists public.point_transactions (
    id uuid primary key default gen_random_uuid(),
    user_id uuid not null references auth.users(id) on delete cascade,
    source text not null,
    source_id text not null,
    amount integer not null check (amount <> 0),
    title text not null,
    description text not null default '',
    created_at timestamptz not null default now(),
    unique (user_id, source_id)
);

create table if not exists public.user_inventory (
    user_id uuid not null references auth.users(id) on delete cascade,
    reward_id integer not null,
    quantity integer not null default 0 check (quantity >= 0),
    updated_at timestamptz not null default now(),
    primary key (user_id, reward_id)
);

create table if not exists public.user_achievements (
    user_id uuid not null references auth.users(id) on delete cascade,
    achievement_id integer not null,
    progress integer not null default 0 check (progress >= 0),
    unlocked boolean not null default false,
    reward_granted boolean not null default false,
    updated_at timestamptz not null default now(),
    primary key (user_id, achievement_id)
);

create table if not exists public.discovered_animals (
    user_id uuid not null references auth.users(id) on delete cascade,
    animal_type text not null,
    discovered_at timestamptz not null default now(),
    primary key (user_id, animal_type)
);

alter table public.reward_progress enable row level security;
alter table public.point_transactions enable row level security;
alter table public.user_inventory enable row level security;
alter table public.user_achievements enable row level security;
alter table public.discovered_animals enable row level security;

-- Drop/recreate policies so this migration can safely be rerun during development.
drop policy if exists "Users can read own reward progress" on public.reward_progress;
drop policy if exists "Users can create own reward progress" on public.reward_progress;
drop policy if exists "Users can update own reward progress" on public.reward_progress;
drop policy if exists "Users can read own point transactions" on public.point_transactions;
drop policy if exists "Users can create own point transactions" on public.point_transactions;
drop policy if exists "Users can read own inventory" on public.user_inventory;
drop policy if exists "Users can create own inventory" on public.user_inventory;
drop policy if exists "Users can update own inventory" on public.user_inventory;
drop policy if exists "Users can delete own inventory" on public.user_inventory;
drop policy if exists "Users can read own achievements" on public.user_achievements;
drop policy if exists "Users can create own achievements" on public.user_achievements;
drop policy if exists "Users can update own achievements" on public.user_achievements;
drop policy if exists "Users can read own discovered animals" on public.discovered_animals;
drop policy if exists "Users can add own discovered animals" on public.discovered_animals;
drop policy if exists "Users can delete own discovered animals" on public.discovered_animals;

create policy "Users can read own reward progress"
on public.reward_progress for select to authenticated
using ((select auth.uid()) = user_id);

create policy "Users can create own reward progress"
on public.reward_progress for insert to authenticated
with check ((select auth.uid()) = user_id);

create policy "Users can update own reward progress"
on public.reward_progress for update to authenticated
using ((select auth.uid()) = user_id)
with check ((select auth.uid()) = user_id);

create policy "Users can read own point transactions"
on public.point_transactions for select to authenticated
using ((select auth.uid()) = user_id);

create policy "Users can create own point transactions"
on public.point_transactions for insert to authenticated
with check ((select auth.uid()) = user_id);

create policy "Users can read own inventory"
on public.user_inventory for select to authenticated
using ((select auth.uid()) = user_id);

create policy "Users can create own inventory"
on public.user_inventory for insert to authenticated
with check ((select auth.uid()) = user_id);

create policy "Users can update own inventory"
on public.user_inventory for update to authenticated
using ((select auth.uid()) = user_id)
with check ((select auth.uid()) = user_id);

create policy "Users can delete own inventory"
on public.user_inventory for delete to authenticated
using ((select auth.uid()) = user_id);

create policy "Users can read own achievements"
on public.user_achievements for select to authenticated
using ((select auth.uid()) = user_id);

create policy "Users can create own achievements"
on public.user_achievements for insert to authenticated
with check ((select auth.uid()) = user_id);

create policy "Users can update own achievements"
on public.user_achievements for update to authenticated
using ((select auth.uid()) = user_id)
with check ((select auth.uid()) = user_id);

create policy "Users can read own discovered animals"
on public.discovered_animals for select to authenticated
using ((select auth.uid()) = user_id);

create policy "Users can add own discovered animals"
on public.discovered_animals for insert to authenticated
with check ((select auth.uid()) = user_id);

create policy "Users can delete own discovered animals"
on public.discovered_animals for delete to authenticated
using ((select auth.uid()) = user_id);

-- Least-privilege Data API grants. RLS still controls individual rows.
grant select, insert, update on public.reward_progress to authenticated;
grant select, insert on public.point_transactions to authenticated;
grant select, insert, update, delete on public.user_inventory to authenticated;
grant select, insert, update on public.user_achievements to authenticated;
grant select, insert, delete on public.discovered_animals to authenticated;
