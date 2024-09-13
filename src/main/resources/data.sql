INSERT INTO app_user (name, email, password) VALUES
('John Doe', 'john.doe@example.com', 'password123'),
('Jane Smith', 'jane.smith@example.com', 'password456'),
('Alice Johnson', 'alice.johnson@example.com', 'alicePass789'),
('Bob Williams', 'bob.williams@example.com', 'securePass321');


INSERT INTO user_roles (user_id, roles) VALUES
(1, 'RIDER'),
(2, 'RIDER'),
(2, 'DRIVER'),
(3, 'RIDER'),
(3, 'DRIVER'),
(4, 'RIDER'),
(4, 'DRIVER');


INSERT INTO rider (user_id, rating) VALUES
(1, 4.9);


INSERT INTO driver (user_id, rating, available, current_location) VALUES
(2, 4.7, true, ST_GeomFromText('POINT(77.1025 28.7041)', 4326)),
(3, 4.8, true, ST_GeomFromText('POINT(77.2167 28.6667)', 4326)),
(4, 4.6, true, ST_GeomFromText('POINT(77.2237 28.6353)', 4326));


INSERT INTO wallet (user_id, balance) VALUES
(1, 100),
(2, 500);
