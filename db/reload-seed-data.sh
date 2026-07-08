#!/usr/bin/env bash
# Re-loads the seed CSVs into the running postgres container without
# recreating the container/volume (existing CRUD data in these tables is wiped).
set -euo pipefail

DB_USER="${DB_USER:-myuser}"
DB_NAME="${DB_NAME:-mydatabase}"

docker compose exec -T postgres psql -U "$DB_USER" -d "$DB_NAME" <<'SQL'
TRUNCATE ticket_state_history, ticket, user_account, customer, agent, user_role, region, issue_topic, service_type, infrastructure_type, department RESTART IDENTITY CASCADE;
SQL

docker compose exec -T postgres psql -U "$DB_USER" -d "$DB_NAME" -f /docker-entrypoint-initdb.d/02-load-data.sql

echo "Seed data reloaded."
