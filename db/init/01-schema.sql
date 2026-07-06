CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS department (
    id              UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    department_name VARCHAR(255) NOT NULL,
    department_code VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS infrastructure_type (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    infrastructure_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS service_type (
    id           UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    service_name VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS issue_topic (
    id               UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    topic_name       VARCHAR(255) NOT NULL,
    sla_target_hours INTEGER NOT NULL
);

CREATE TABLE IF NOT EXISTS region (
    id                  UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    city_name           VARCHAR(255) NOT NULL,
    geographical_region VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS agent (
    id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    department_id UUID NOT NULL REFERENCES department(id),
    full_name     VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS customer (
    id         UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL,
    address    TEXT NOT NULL,
    birthdate  DATE,
    phone      VARCHAR(50),
    created_at DATE,
    segment    VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS ticket (
    id                          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_number               VARCHAR(50) NOT NULL UNIQUE,
    customer_id                 UUID REFERENCES customer(id),
    topic_id                    UUID NOT NULL REFERENCES issue_topic(id),
    current_department_id      UUID NOT NULL REFERENCES department(id),
    agent_id                    UUID NOT NULL REFERENCES agent(id),
    region_id                   UUID NOT NULL REFERENCES region(id),
    service_type_id             UUID NOT NULL REFERENCES service_type(id),
    infrastructure_type_id      UUID NOT NULL REFERENCES infrastructure_type(id),
    description                 TEXT,
    status                      VARCHAR(50) NOT NULL,
    priority                    VARCHAR(50) NOT NULL,
    is_sla_breached             BOOLEAN NOT NULL DEFAULT false,
    resolution_time_hours       NUMERIC,
    customer_satisfaction_score INTEGER,
    created_at                  TIMESTAMP NOT NULL,
    resolved_at                 TIMESTAMP,
    creation_source             VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS ticket_state_history (
    id                     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ticket_id              UUID NOT NULL REFERENCES ticket(id),
    previous_department_id UUID REFERENCES department(id),
    new_department_id      UUID REFERENCES department(id),
    previous_status        VARCHAR(50),
    new_status              VARCHAR(50),
    action_type            VARCHAR(50) NOT NULL,
    changed_by_agent_id     UUID REFERENCES agent(id),
    changed_at              TIMESTAMP NOT NULL,
    duration_minutes        INTEGER
);
