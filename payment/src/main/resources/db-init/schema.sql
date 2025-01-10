CREATE TABLE IF NOT EXISTS TB_PAYMENT (
    id bigint auto_increment primary key,
    user_id bigint not null,
    payment_price bigint,
    description varchar(300),
    pg_order_id varchar(32) not null,
    pg_key varchar(49),
    pg_status varchar(20) not null,
    pg_retry_count int,
    is_deleted boolean,
    is_public boolean,
    created_at timestamp,
    updated_at timestamp
);