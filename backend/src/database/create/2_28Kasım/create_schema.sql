CREATE SCHEMA IF NOT EXISTS expense_management;
SET search_path TO expense_management;

CREATE TABLE role (
    role_id     INT PRIMARY KEY,
    name        VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255)
);

CREATE TABLE department (
    department_id       INT PRIMARY KEY,
    name                VARCHAR(100) NOT NULL,
    manager_id          INT,
    super_department_id INT,
    FOREIGN KEY (super_department_id) REFERENCES department(department_id) ON DELETE SET NULL
);

CREATE TABLE employee (
    employee_id   INT PRIMARY KEY,
    first_name    VARCHAR(100) NOT NULL,
    last_name     VARCHAR(100) NOT NULL,
    department_id INT NOT NULL,
    super_id      INT,
    role_id       INT NOT NULL,
    start_date    DATE NOT NULL DEFAULT,
    mail          VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    FOREIGN KEY (department_id) REFERENCES department(department_id) ON DELETE RESTRICT,
    FOREIGN KEY (super_id) REFERENCES employee(employee_id) ON DELETE SET NULL,
    FOREIGN KEY (role_id) REFERENCES role(role_id) ON DELETE RESTRICT
);

ALTER TABLE department
    ADD CONSTRAINT fk_department_manager
    FOREIGN KEY (manager_id) REFERENCES employee(employee_id) ON DELETE SET NULL;

CREATE TABLE category (
    category_id INT PRIMARY KEY,
    name        VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE vendor (
    vendor_id INT PRIMARY KEY,
    name      VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE currency (
    currency_id  INT PRIMARY KEY,
    name         VARCHAR(50) NOT NULL,
    decimal_name VARCHAR(50) NOT NULL
);

CREATE TABLE payout_batch (
    batch_id     INT PRIMARY KEY,
    description  VARCHAR(255) NOT NULL,
    payment_date TIMESTAMPTZ,
    status       VARCHAR(20) NOT NULL DEFAULT 'DRAFT'
                 CHECK (status IN ('DRAFT', 'COMPLETED', 'CANCELLED'))
);

CREATE TABLE expense (
    expense_id    INT PRIMARY KEY,
    employee_id   INT NOT NULL,
    department_id INT NOT NULL,
    vendor_id     INT NOT NULL,
    category_id   INT NOT NULL,
    currency_id   INT NOT NULL,
    date          TIMESTAMPTZ NOT NULL,
    price         INT NOT NULL CHECK (price >= 0),
    status        VARCHAR(20) NOT NULL DEFAULT 'PENDING'
                  CHECK (status IN ('PENDING', 'APPROVED', 'REJECTED', 'PAID')),
    FOREIGN KEY (employee_id) REFERENCES employee(employee_id) ON DELETE RESTRICT,
    FOREIGN KEY (department_id) REFERENCES department(department_id) ON DELETE RESTRICT,
    FOREIGN KEY (vendor_id) REFERENCES vendor(vendor_id) ON DELETE RESTRICT,
    FOREIGN KEY (category_id) REFERENCES category(category_id) ON DELETE RESTRICT,
    FOREIGN KEY (currency_id) REFERENCES currency(currency_id) ON DELETE RESTRICT
);

CREATE TABLE expense_approval (
    expense_id  INT PRIMARY KEY,
    reviewed_by INT NOT NULL,
    reviewed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    batch_id    INT,
    note        VARCHAR(500),
    FOREIGN KEY (expense_id) REFERENCES expense(expense_id) ON DELETE CASCADE,
    FOREIGN KEY (reviewed_by) REFERENCES employee(employee_id) ON DELETE RESTRICT,
    FOREIGN KEY (batch_id) REFERENCES payout_batch(batch_id) ON DELETE SET NULL
);

-- Her bir rol bir öncekinin izinlerine sahip
INSERT INTO role (role_id, name, description) VALUES
    (1, 'employee', 'Sadece kendi harcamalarını ekleyebilir ve görüntüleyebilir.'),
    (2, 'manager', 'Kendi departmanındaki ve alt departmanlarındaki harcamaları görüntüleyip onaylayabilir.'),
    (3, 'admin', 'Yönetici izinlerine ek olarak departmanına yeni çalışan ekleyebilir.'),
    (4, 'finance_admin', 'Tüm departmanları görüntüleyebilir, onaylayabilir, çalışan ve kategori ekleyebilir.');