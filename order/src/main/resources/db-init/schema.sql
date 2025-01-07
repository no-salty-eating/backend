CREATE TABLE IF NOT EXISTS TB_ORDER (
    id bigint auto_increment primary key,
    user_id bigint,
    pg_order_id varchar(32),
    total_price bigint,
    description varchar(300),
    point_amount bigint,
    order_status varchar(20),
    is_deleted boolean,
    is_public boolean,
    created_at timestamp,
    updated_at timestamp
);

CREATE TABLE IF NOT EXISTS TB_ORDER_DETAIL (
   id serial unique,
   order_id bigint,
   product_id bigint,
   price bigint,
   quantity int,
   is_deleted boolean,
   is_public boolean,
   created_at timestamp,
   updated_at timestamp,
   primary key (order_id, product_id)
);