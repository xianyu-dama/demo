INSERT INTO "order" (order_id, order_status, currency, exchange_rate, should_pay, actual_pay, email,
                     phone_number, first_name, last_name, address_line1, address_line2, country,
                     add_time, update_time, user_id)
VALUES (1, 'WAIT_PAY', 'CNY', 1, 88, 88, '123456@qq.com', '13800000000', 'first', 'last', 'line1', 'line2', 'CHINA',
        '2025-11-01 01:00:00', '2025-11-01 01:00:00', 1);

INSERT INTO order_item (id, order_id, product_id, order_status, price, add_time, update_time)
VALUES (1, 1, 1, 'WAIT_PAY', 1, '2025-11-01 01:00:00', '2025-11-01 01:00:00');
INSERT INTO order_item (id, order_id, product_id, order_status, price, add_time, update_time)
VALUES (2, 1, 2, 'WAIT_PAY', 10, '2025-11-01 01:00:00', '2025-11-01 01:00:00');
