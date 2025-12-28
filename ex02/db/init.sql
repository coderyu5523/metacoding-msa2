CREATE TABLE user_tb (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50),
  email VARCHAR(50),
  password VARCHAR(50),
  roles VARCHAR(50),
  status VARCHAR(50),
  created_at DATETIME,
  updated_at DATETIME
);


CREATE TABLE product_tb (
  id INT AUTO_INCREMENT PRIMARY KEY,
  product_name VARCHAR(50),
  quantity INT,
  price BIGINT,
  status VARCHAR(50),
  created_at DATETIME,
  updated_at DATETIME
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

CREATE TABLE order_item_tb (
  id INT AUTO_INCREMENT PRIMARY KEY,
  order_id INT,
  product_id INT,
  quantity INT,
  price BIGINT,
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

INSERT INTO user_tb (username, email, password, roles, status, created_at, updated_at) VALUES ('ssar','ssar@metacoding.com','1234','USER','COMPLETED',now(),now());
INSERT INTO user_tb (username, email, password, roles, status, created_at, updated_at) VALUES ('cos','cos@metacoding.com','1234','USER','COMPLETED',now(),now());
INSERT INTO user_tb (username, email, password, roles, status, created_at, updated_at) VALUES ('love','love@metacoding.com','1234','USER','COMPLETED',now(),now());

INSERT INTO product_tb (product_name, quantity, price, status, created_at, updated_at) VALUES ('MacBook Pro', 10, 2500000, 'COMPLETED', now(), now());
INSERT INTO product_tb (product_name, quantity, price, status, created_at, updated_at) VALUES ('iPhone 15', 3, 1300000, 'COMPLETED', now(), now());
INSERT INTO product_tb (product_name, quantity, price, status, created_at, updated_at) VALUES ('AirPods', 10, 300000, 'COMPLETED', now(), now());

INSERT INTO order_tb (user_id, product_id, quantity, status, created_at, updated_at) VALUES (1, 1, 1, 'COMPLETED', now(), now()); 
INSERT INTO order_tb (user_id, product_id, quantity, status, created_at, updated_at) VALUES (2, 3, 1, 'CANCELLED', now(), now()); 
INSERT INTO order_tb (user_id, product_id, quantity, status, created_at, updated_at) VALUES (3, 2, 2, 'PENDING', now(), now());

