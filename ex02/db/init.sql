CREATE TABLE user_tb (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50),
  email VARCHAR(50),
  password VARCHAR(50)
);


CREATE TABLE product_tb (
  id INT AUTO_INCREMENT PRIMARY KEY,
  product_name VARCHAR(50),
  quantity INT,
  price BIGINT,
  status VARCHAR(50)
);

CREATE TABLE order_tb (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT,
  product_id INT,
  quantity INT,
  status VARCHAR(50),
  created_at DATETIME,
  updated_at DATETIME
);

CREATE TABLE delivery_tb (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT,
  address VARCHAR(50),
  status VARCHAR(50),
  created_at DATETIME,
  updated_at DATETIME
);

INSERT INTO user_tb (username, email, password) VALUES ('ssar','ssar@metacoding.com','1234');
INSERT INTO user_tb (username, email, password) VALUES ('cos','cos@metacoding.com','1234');
INSERT INTO user_tb (username, email, password) VALUES ('love','love@metacoding.com','1234');


INSERT INTO product_tb (product_name, quantity, price, status) VALUES ('MacBook Pro', 10, 2500000, 'NONE');
INSERT INTO product_tb (product_name, quantity, price, status) VALUES ('iPhone 15', 3, 1300000, 'NONE');
INSERT INTO product_tb (product_name, quantity, price, status) VALUES ('AirPods', 10, 300000, 'NONE');

INSERT INTO order_tb (user_id, product_id, quantity, status, created_at, updated_at) VALUES (1, 1, 1, 'success', now(), now()); 
INSERT INTO order_tb (user_id, product_id, quantity, status, created_at, updated_at) VALUES (2, 3, 1, 'failed', now(), now()); 
INSERT INTO order_tb (user_id, product_id, quantity, status, created_at, updated_at) VALUES (3, 2, 2, 'pending', now(), now());

INSERT INTO delivery_tb (order_id, address, status, created_at, updated_at) VALUES (1, '서울시 강남구 테헤란로 123', 'PENDING', NOW(), NOW());
INSERT INTO delivery_tb (order_id, address, status, created_at, updated_at) VALUES (2, '서울시 서초구 서초대로 456', 'PENDING', NOW(), NOW());
INSERT INTO delivery_tb (order_id, address, status, created_at, updated_at) VALUES (3, '서울시 송파구 올림픽로 789', 'IN_TRANSIT', NOW(), NOW());

