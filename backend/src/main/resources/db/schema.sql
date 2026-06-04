SET NAMES utf8mb4;

CREATE TABLE IF NOT EXISTS sys_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(64) NOT NULL,
  password VARCHAR(128) NOT NULL,
  phone VARCHAR(32) NOT NULL,
  nickname VARCHAR(64),
  avatar VARCHAR(255),
  role VARCHAR(32) NOT NULL,
  status VARCHAR(32) NOT NULL,
  last_login_time DATETIME,
  create_time DATETIME,
  update_time DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_sys_user_username (username),
  UNIQUE KEY uk_sys_user_phone (phone),
  KEY idx_sys_user_role (role),
  KEY idx_sys_user_status (status)
);

CREATE TABLE IF NOT EXISTS merchant_apply (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  merchant_name VARCHAR(128) NOT NULL,
  contact_name VARCHAR(64),
  contact_phone VARCHAR(32),
  origin_address VARCHAR(255),
  description TEXT,
  license_image VARCHAR(255),
  apply_status VARCHAR(32) NOT NULL,
  reviewer_id BIGINT,
  review_time DATETIME,
  reject_reason VARCHAR(255),
  create_time DATETIME,
  update_time DATETIME,
  deleted TINYINT DEFAULT 0,
  KEY idx_merchant_apply_user_id (user_id),
  KEY idx_merchant_apply_status (apply_status),
  KEY idx_merchant_apply_create_time (create_time)
);

CREATE TABLE IF NOT EXISTS merchant (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  merchant_name VARCHAR(128) NOT NULL,
  contact_name VARCHAR(64),
  contact_phone VARCHAR(32),
  origin_address VARCHAR(255),
  description TEXT,
  license_image VARCHAR(255),
  status VARCHAR(32) NOT NULL,
  total_sales_amount DECIMAL(12,2) DEFAULT 0,
  total_order_count INT DEFAULT 0,
  create_time DATETIME,
  update_time DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_merchant_user_id (user_id),
  KEY idx_merchant_name (merchant_name),
  KEY idx_merchant_status (status)
);

CREATE TABLE IF NOT EXISTS product_category (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  category_name VARCHAR(64) NOT NULL,
  parent_id BIGINT DEFAULT 0,
  sort_order INT DEFAULT 0,
  status VARCHAR(32) NOT NULL,
  create_time DATETIME,
  update_time DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_product_category_name (category_name),
  KEY idx_product_category_parent_id (parent_id),
  KEY idx_product_category_status (status)
);

CREATE TABLE IF NOT EXISTS product (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  merchant_id BIGINT NOT NULL,
  category_id BIGINT,
  product_name VARCHAR(128) NOT NULL,
  product_image VARCHAR(255),
  description TEXT,
  origin_place VARCHAR(128),
  price DECIMAL(12,2) NOT NULL,
  stock INT NOT NULL,
  unit VARCHAR(16),
  shelf_life VARCHAR(64),
  audit_status VARCHAR(32) NOT NULL,
  sale_status VARCHAR(32) NOT NULL,
  sales_count INT DEFAULT 0,
  review_count INT DEFAULT 0,
  average_rating DECIMAL(3,2) DEFAULT 0,
  create_time DATETIME,
  update_time DATETIME,
  deleted TINYINT DEFAULT 0,
  KEY idx_product_merchant_id (merchant_id),
  KEY idx_product_category_id (category_id),
  KEY idx_product_audit_status (audit_status),
  KEY idx_product_sale_status (sale_status),
  KEY idx_product_name (product_name)
);

CREATE TABLE IF NOT EXISTS cart_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  merchant_id BIGINT NOT NULL,
  quantity INT NOT NULL,
  selected TINYINT NOT NULL DEFAULT 1,
  create_time DATETIME,
  update_time DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_cart_item_user_product (user_id, product_id),
  KEY idx_cart_item_user_id (user_id),
  KEY idx_cart_item_product_id (product_id),
  KEY idx_cart_item_merchant_id (merchant_id)
);

CREATE TABLE IF NOT EXISTS trade_order (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_no VARCHAR(64) NOT NULL,
  user_id BIGINT NOT NULL,
  merchant_id BIGINT NOT NULL,
  total_amount DECIMAL(12,2) NOT NULL,
  pay_amount DECIMAL(12,2) NOT NULL,
  order_status VARCHAR(32) NOT NULL,
  receiver_name VARCHAR(64),
  receiver_phone VARCHAR(32),
  receiver_address VARCHAR(255),
  pay_time DATETIME,
  ship_time DATETIME,
  complete_time DATETIME,
  cancel_time DATETIME,
  cancel_reason VARCHAR(255),
  remark VARCHAR(255),
  create_time DATETIME,
  update_time DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_trade_order_no (order_no),
  KEY idx_trade_order_user_id (user_id),
  KEY idx_trade_order_merchant_id (merchant_id),
  KEY idx_trade_order_status (order_status),
  KEY idx_trade_order_create_time (create_time)
);

CREATE TABLE IF NOT EXISTS trade_order_item (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  order_no VARCHAR(64) NOT NULL,
  product_id BIGINT NOT NULL,
  merchant_id BIGINT NOT NULL,
  product_name VARCHAR(128) NOT NULL,
  product_image VARCHAR(255),
  product_price DECIMAL(12,2) NOT NULL,
  quantity INT NOT NULL,
  total_amount DECIMAL(12,2) NOT NULL,
  create_time DATETIME,
  KEY idx_trade_order_item_order_id (order_id),
  KEY idx_trade_order_item_order_no (order_no),
  KEY idx_trade_order_item_product_id (product_id),
  KEY idx_trade_order_item_merchant_id (merchant_id)
);

CREATE TABLE IF NOT EXISTS payment_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  payment_no VARCHAR(64) NOT NULL,
  order_id BIGINT NOT NULL,
  order_no VARCHAR(64) NOT NULL,
  user_id BIGINT NOT NULL,
  pay_amount DECIMAL(12,2) NOT NULL,
  pay_type VARCHAR(32),
  pay_status VARCHAR(32) NOT NULL,
  pay_time DATETIME,
  create_time DATETIME,
  update_time DATETIME,
  UNIQUE KEY uk_payment_record_no (payment_no),
  KEY idx_payment_record_order_id (order_id),
  KEY idx_payment_record_order_no (order_no),
  KEY idx_payment_record_user_id (user_id),
  KEY idx_payment_record_status (pay_status)
);

CREATE TABLE IF NOT EXISTS inventory_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  product_id BIGINT NOT NULL,
  merchant_id BIGINT NOT NULL,
  order_id BIGINT,
  order_no VARCHAR(64),
  change_type VARCHAR(32) NOT NULL,
  change_quantity INT NOT NULL,
  before_stock INT NOT NULL,
  after_stock INT NOT NULL,
  remark VARCHAR(255),
  create_time DATETIME,
  KEY idx_inventory_log_product_id (product_id),
  KEY idx_inventory_log_merchant_id (merchant_id),
  KEY idx_inventory_log_order_id (order_id),
  KEY idx_inventory_log_order_no (order_no),
  KEY idx_inventory_log_create_time (create_time)
);

CREATE TABLE IF NOT EXISTS message_notice (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL,
  title VARCHAR(128) NOT NULL,
  content VARCHAR(500),
  notice_type VARCHAR(32),
  read_status VARCHAR(32) NOT NULL,
  related_id BIGINT,
  related_type VARCHAR(32),
  create_time DATETIME,
  KEY idx_message_notice_user_id (user_id),
  KEY idx_message_notice_read_status (read_status),
  KEY idx_message_notice_type (notice_type),
  KEY idx_message_notice_create_time (create_time)
);

CREATE TABLE IF NOT EXISTS product_review (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  order_id BIGINT NOT NULL,
  order_item_id BIGINT NOT NULL,
  product_id BIGINT NOT NULL,
  merchant_id BIGINT NOT NULL,
  user_id BIGINT NOT NULL,
  rating INT NOT NULL,
  content VARCHAR(500),
  images TEXT,
  reply_content VARCHAR(500),
  reply_time DATETIME,
  create_time DATETIME,
  update_time DATETIME,
  deleted TINYINT DEFAULT 0,
  UNIQUE KEY uk_product_review_order_item_id (order_item_id),
  KEY idx_product_review_product_id (product_id),
  KEY idx_product_review_merchant_id (merchant_id),
  KEY idx_product_review_user_id (user_id)
);

CREATE TABLE IF NOT EXISTS daily_statistics (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  stat_date DATE NOT NULL,
  order_count INT DEFAULT 0,
  sales_amount DECIMAL(12,2) DEFAULT 0,
  pay_user_count INT DEFAULT 0,
  new_user_count INT DEFAULT 0,
  new_merchant_count INT DEFAULT 0,
  product_count INT DEFAULT 0,
  cancel_order_count INT DEFAULT 0,
  create_time DATETIME,
  update_time DATETIME,
  UNIQUE KEY uk_daily_statistics_date (stat_date)
);

INSERT IGNORE INTO product_category (id, category_name, parent_id, sort_order, status, create_time, update_time, deleted)
VALUES
  (1, '蔬菜', 0, 1, 'ENABLED', NOW(), NOW(), 0),
  (2, '水果', 0, 2, 'ENABLED', NOW(), NOW(), 0),
  (3, '粮油', 0, 3, 'ENABLED', NOW(), NOW(), 0);
