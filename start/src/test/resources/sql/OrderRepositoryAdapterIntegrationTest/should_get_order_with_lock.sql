-- 插入订单数据
INSERT INTO "order" (order_id, order_status, currency, exchange_rate, should_pay, actual_pay, email,
                     phone_number, first_name, last_name, address_line1, address_line2, country,
                     add_time, update_time, user_id)
VALUES (100, 'WAIT_PAY', 'CNY', 1, 199.99, 0, 'user1@example.com', '13800000001', 'User1', 'Test', 'Address1', 'Address2', 'CHINA',
        '2023-01-03 10:00:00', '2023-01-03 10:00:00', 1);

-- 插入订单详情数据
INSERT INTO order_item (id, order_id, product_id, order_status, price, add_time, update_time)
VALUES (1, 100, 401, 'WAIT_PAY', 400, '2023-01-04 10:00:00', '2023-01-04 10:00:00');
