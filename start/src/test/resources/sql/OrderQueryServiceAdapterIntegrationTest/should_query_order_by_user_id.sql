
-- 插入用户1的订单数据
INSERT INTO "order" (order_id, order_status, currency, exchange_rate, should_pay, actual_pay, email,
                     phone_number, first_name, last_name, address_line1, address_line2, country,
                     add_time, update_time, user_id)
VALUES (1, 'WAIT_PAY', 'CNY', 1, 100, 100, 'user1@example.com', '13800000001', 'User1', 'Test', 'Address1', 'Address2', 'CHINA',
        '2023-01-01 10:00:00', '2023-01-01 10:00:00', 1);

INSERT INTO "order" (order_id, order_status, currency, exchange_rate, should_pay, actual_pay, email,
                     phone_number, first_name, last_name, address_line1, address_line2, country,
                     add_time, update_time, user_id)
VALUES (2, 'PAID', 'CNY', 1, 200, 200, 'user1@example.com', '13800000001', 'User1', 'Test', 'Address1', 'Address2', 'CHINA',
        '2023-01-02 10:00:00', '2023-01-02 10:00:00', 1);

INSERT INTO "order" (order_id, order_status, currency, exchange_rate, should_pay, actual_pay, email,
                     phone_number, first_name, last_name, address_line1, address_line2, country,
                     add_time, update_time, user_id)
VALUES (3, 'CANCELED', 'CNY', 1, 300, 0, 'user1@example.com', '13800000001', 'User1', 'Test', 'Address1', 'Address2', 'CHINA',
        '2023-01-03 10:00:00', '2023-01-03 10:00:00', 1);

-- 插入用户2的订单数据
INSERT INTO "order" (order_id, order_status, currency, exchange_rate, should_pay, actual_pay, email,
                     phone_number, first_name, last_name, address_line1, address_line2, country,
                     add_time, update_time, user_id)
VALUES (4, 'WAIT_PAY', 'CNY', 1, 400, 400, 'user2@example.com', '13800000002', 'User2', 'Test', 'Address1', 'Address2', 'CHINA',
        '2023-01-04 10:00:00', '2023-01-04 10:00:00', 2);

-- 插入订单详情
INSERT INTO order_item (id, order_id, product_id, order_status, price, add_time, update_time)
VALUES (1, 1, 101, 'WAIT_PAY', 50, '2023-01-01 10:00:00', '2023-01-01 10:00:00');

INSERT INTO order_item (id, order_id, product_id, order_status, price, add_time, update_time)
VALUES (2, 1, 102, 'WAIT_PAY', 50, '2023-01-01 10:00:00', '2023-01-01 10:00:00');

INSERT INTO order_item (id, order_id, product_id, order_status, price, add_time, update_time)
VALUES (3, 2, 201, 'PAID', 200, '2023-01-02 10:00:00', '2023-01-02 10:00:00');

INSERT INTO order_item (id, order_id, product_id, order_status, price, add_time, update_time)
VALUES (4, 3, 301, 'CANCELED', 300, '2023-01-03 10:00:00', '2023-01-03 10:00:00');

INSERT INTO order_item (id, order_id, product_id, order_status, price, add_time, update_time)
VALUES (5, 4, 401, 'WAIT_PAY', 400, '2023-01-04 10:00:00', '2023-01-04 10:00:00');
