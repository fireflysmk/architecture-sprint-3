CREATE TABLE IF NOT EXISTS telemetries (
    id text DEFAULT gen_random_uuid(),
    indications text NOT NULL,
    device_id text NOT NULL,
    created_at bigint NOT NULL,
    PRIMARY KEY (id)
);
