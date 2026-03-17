CREATE TABLE category(
	category_id SERIAL PRIMARY KEY,
	name VARCHAR UNIQUE NOT NULL CHECK (LENGTH(name) != 0)
);

CREATE TABLE food_item(
	food_item_id SERIAL PRIMARY KEY,
	name VARCHAR NOT NULL CHECK (LENGTH(name) != 0),
	description VARCHAR NOT NULL,
	price NUMERIC NOT NULL CHECK (price >= 0),
	category_id INT NOT NULL REFERENCES category(category_id) ON DELETE CASCADE,
	preparation_time INT CHECK (preparation_time >= 0),
	calories INT CHECK (calories >= 0),
	is_available BOOLEAN DEFAULT TRUE,
	UNIQUE(category_id, name)
);

CREATE TABLE orders(
	order_id BIGSERIAL PRIMARY KEY,
	payment_id BIGINT REFERENCES payment(payment_id) ON DELETE SET NULL,
	delivery_partner_id INT REFERENCES delivery_partner(delivery_partner_id) ON DELETE SET NULL,
	order_status order_status NOT NULL,
	order_placed_on TIMESTAMP,
	customer_id INT REFERENCES customer(customer_id) ON DELETE CASCADE,
	discount_amount NUMERIC CHECK (discount_amount >= 0) DEFAULT 0
);

CREATE TABLE order_item(
	order_item_id BIGSERIAL PRIMARY KEY,
	order_id BIGINT NOT NULL REFERENCES orders(order_id) ON DELETE CASCADE, 
	food_item_id INT REFERENCES food_item(food_item_id) ON DELETE SET NULL,
	food_name VARCHAR NOT NULL CHECK(LENGTH(food_name) != 0),
	price_at_order NUMERIC NOT NULL CHECK (price_at_order >= 0),
	quantity INT NOT NULL CHECK (quantity > 0),
	sub_total NUMERIC GENERATED ALWAYS AS (price_at_order * quantity) STORED,
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE notification(
	notification_id BIGSERIAL PRIMARY KEY,
	description VARCHAR NOT NULL,
	user_id INT REFERENCES app_user(user_id) ON DELETE CASCADE,
	is_broadcast BOOLEAN DEFAULT FALSE,
	role role,
	created_on TIMESTAMP NOT NULL
);

CREATE TABLE app_user(
	user_id BIGSERIAL PRIMARY KEY,
	name VARCHAR NOT NULL CHECK(LENGTH(name) != 0),
	seen_notification_count BIGINT DEFAULT 0, 
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	is_deleted BOOLEAN DEFAULT FALSE
);

CREATE TABLE customer(
	customer_id BIGINT PRIMARY KEY REFERENCES app_user(user_id) ON DELETE CASCADE,
	phone VARCHAR NOT NULL CHECK (LENGTH(phone) = 10) UNIQUE,
	password VARCHAR NOT NULL CHECK(LENGTH(password) >= 8),
	upi_id VARCHAR,
	address VARCHAR
);

CREATE TABLE admin(
	admin_id BIGINT PRIMARY KEY REFERENCES app_user(user_id) ON DELETE CASCADE,
	phone VARCHAR NOT NULL CHECK (LENGTH(phone) = 10) UNIQUE,
	password VARCHAR NOT NULL CHECK(LENGTH(password) >= 8)
);

CREATE TABLE delivery_partner(
	delivery_partner_id BIGINT PRIMARY KEY REFERENCES app_user(user_id) ON DELETE CASCADE,
	phone VARCHAR NOT NULL CHECK (LENGTH(phone) = 10) UNIQUE,
	password VARCHAR NOT NULL CHECK(LENGTH(password) >= 8),
	is_available BOOLEAN NOT NULL,
	total_earnings NUMERIC NOT NULL DEFAULT 0,
	is_approved BOOLEAN NOT NULL DEFAULT FALSE,
	priority BIGINT DEFAULT nextval('priority_seq')
);

CREATE TABLE delivery_assignment(
	order_id BIGINT REFERENCES orders(order_id) ON DELETE CASCADE,
	delivery_partner_id BIGINT REFERENCES delivery_partner(delivery_partner_id) ON DELETE CASCADE,
	created_on TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(order_id, delivery_partner_id)
);

CREATE TABLE PAYMENT(
	payment_id BIGSERIAL PRIMARY KEY,
	payment_mode payment_mode,
	transaction_reference_id VARCHAR
);

CREATE TABLE app_config (
    key VARCHAR PRIMARY KEY,
    value VARCHAR NOT NULL 
);

CREATE SEQUENCE priority_seq
AS BIGINT
START WITH 1
INCREMENT BY 1
MINVALUE 1
MAXVALUE 9223372036854775807
CACHE 100
CYCLE;


CREATE OR REPLACE FUNCTION update_priority_on_delivery_completion()
RETURNS TRIGGER AS $$
BEGIN
    IF OLD.is_available = FALSE AND NEW.is_available = TRUE THEN
        NEW.priority := nextval('priority_seq');
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;


CREATE TRIGGER trg_update_priority
BEFORE UPDATE OF is_available ON delivery_partner
FOR EACH ROW
EXECUTE FUNCTION update_priority_on_delivery_completion();

CREATE TYPE role AS ENUM (
    'CUSTOMER',
    'DELIVERY_PARTNER',
    'ADMIN'
);

CREATE TYPE order_status AS ENUM (
    'CREATED',
    'CONFIRMED',
    'PREPARING',
    'WAITING_FOR_DELIVERY_PARTNER',
    'OUT_FOR_DELIVERY',
    'DELIVERED',
    'CANCELLED'
);

CREATE TYPE payment_mode AS ENUM (
    'UPI',
	'CASH_ON_DELIVERY'
);


INSERT INTO app_user(name) VALUES
('Rahul'),
('Amit'),
('Suresh'),
('Vikram'),
('Arjun');

INSERT INTO delivery_partner(
    delivery_partner_id,
    phone,
    password,
    is_available,
    commission_percentage
)
VALUES
(1, '9876543210', 'password123', TRUE, 20),
(2, '9876543211', 'password123', TRUE, 18),
(3, '9876543212', 'password123', FALSE, 22),
(4, '9876543213', 'password123', TRUE, 15),
(5, '9876543214', 'password123', TRUE, 25);

SELECT * FROM delivery_partner ORDER BY delivery_partner_id;

UPDATE delivery_partner set is_available = true where delivery_partner_id = 4;

INSERT INTO app_config VALUES('commission_percentage', '10');
SELECT * FROM app_config;