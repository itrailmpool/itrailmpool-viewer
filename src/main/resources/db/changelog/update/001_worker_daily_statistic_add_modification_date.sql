ALTER TABLE worker_daily_statistic ADD COLUMN IF NOT EXISTS modification_date timestamptz not null default CURRENT_TIMESTAMP;