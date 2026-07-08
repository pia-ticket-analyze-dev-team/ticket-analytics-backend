COPY department(id, department_name, department_code)
FROM '/docker-entrypoint-initdb.d/seed-data/department.csv'
WITH (FORMAT csv, HEADER true);

COPY infrastructure_type(id, infrastructure_name)
FROM '/docker-entrypoint-initdb.d/seed-data/infrastructure_type.csv'
WITH (FORMAT csv, HEADER true);

COPY service_type(id, service_name)
FROM '/docker-entrypoint-initdb.d/seed-data/service_type.csv'
WITH (FORMAT csv, HEADER true);

COPY issue_topic(id, topic_name, sla_target_hours)
FROM '/docker-entrypoint-initdb.d/seed-data/issue_topic.csv'
WITH (FORMAT csv, HEADER true);

COPY region(id, city_name, geographical_region)
FROM '/docker-entrypoint-initdb.d/seed-data/region.csv'
WITH (FORMAT csv, HEADER true);

COPY agent(id, department_id, full_name)
FROM '/docker-entrypoint-initdb.d/seed-data/agent.csv'
WITH (FORMAT csv, HEADER true);

COPY user_role(id, role_code, role_name, description)
FROM '/docker-entrypoint-initdb.d/seed-data/user_role.csv'
WITH (FORMAT csv, HEADER true);

COPY user_account(id, agent_id, role_id, first_name, last_name, email, password_hash, is_active, created_at, updated_at)
FROM '/docker-entrypoint-initdb.d/seed-data/user_account.csv'
WITH (FORMAT csv, HEADER true);

COPY customer(id, first_name, last_name, email, address, birthdate, phone, created_at, segment)
FROM '/docker-entrypoint-initdb.d/seed-data/customer.csv'
WITH (FORMAT csv, HEADER true);

COPY ticket(id, ticket_number, customer_id, topic_id, current_department_id, agent_id, region_id, service_type_id, infrastructure_type_id, description, status, priority, is_sla_breached, resolution_time_hours, customer_satisfaction_score, created_at, resolved_at, creation_source)
FROM '/docker-entrypoint-initdb.d/seed-data/ticket.csv'
WITH (FORMAT csv, HEADER true);

COPY ticket_state_history(id, ticket_id, previous_department_id, new_department_id, previous_status, new_status, action_type, changed_by_agent_id, changed_at, duration_minutes)
FROM '/docker-entrypoint-initdb.d/seed-data/ticket_state_history.csv'
WITH (FORMAT csv, HEADER true);
