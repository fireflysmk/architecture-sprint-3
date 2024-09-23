CREATE TABLE IF NOT EXISTS device_types (
    id text DEFAULT gen_random_uuid(),
    type text NOT NULL,
    default_parameters text NOT null,
    PRIMARY KEY (id)
);

INSERT INTO device_types (type, default_parameters)
    VALUES
        ('heat-checker', '{"period": "10s"}'),
        ('light-manager', '{"status": "off"}'),
        ('door-manager', '{"status": "off"}'),
        ('videocamera', '{"period": "5s"}');

CREATE TABLE IF NOT EXISTS modules (
    id text DEFAULT gen_random_uuid(),
    serial_number text NOT NULL,
    name text NOT NULL,
    status boolean NOT NULL,
    user_id text NOT NULL,
    house_id text,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS devices (
    id text DEFAULT gen_random_uuid(),
    serial_number text NOT NULL,
    name text NOT NULL,
    current_parameters text NOT NULL,
    status boolean NOT NULL,
    user_id text NOT NULL,
    type_id text NOT NULL,
    house_id text,
    module_id text,
    PRIMARY KEY (id),
    FOREIGN KEY (type_id) REFERENCES device_types (id),
    FOREIGN KEY (module_id) REFERENCES modules (id)
);
