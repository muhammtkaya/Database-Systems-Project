-- EXPENSE_MANAGEMENT SCHEMA
CREATE SCHEMA IF NOT EXISTS expense_management;

-- DEPARTMENT
CREATE TABLE IF NOT EXISTS expense_management.department (
    department_no INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    manager_id INT,
    super_no INT,
    CONSTRAINT fk_dept_super FOREIGN KEY (super_no)
        REFERENCES expense_management.department(department_no) ON DELETE SET NULL
    -- fk_dept_manager (manager_id -> employee.employee_id) dosyanin sonunda ekleniyor:
    -- employee ile department birbirine FK verdigi icin (dongusel), employee
    -- olusturulduktan SONRA ALTER ile baglanir.
);

-- EMPLOYEE
CREATE TABLE IF NOT EXISTS expense_management.employee (
    employee_id INT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    department_no INT,
    super_id INT,
    start_date DATE NOT NULL,
    -- Giris kimligi: sifrenin KENDISI degil, tek yonlu hash tutulur (BCrypt/Argon2id).
    email VARCHAR(100) UNIQUE,
    password_hash VARCHAR(100),
    CONSTRAINT fk_emp_dept FOREIGN KEY (department_no)
        REFERENCES expense_management.department(department_no) ON DELETE SET NULL,
    CONSTRAINT fk_emp_super FOREIGN KEY (super_id) 
        REFERENCES expense_management.employee(employee_id) ON DELETE SET NULL
);

-- VENDOR
CREATE TABLE IF NOT EXISTS expense_management.vendor (
    vendor_id INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- EXPENSE
CREATE TABLE IF NOT EXISTS expense_management.expense (
    employee_id INT NOT NULL,
    department_id INT NOT NULL,
    vendor_id INT NOT NULL,
    date DATE NOT NULL,
    price INT NOT NULL,
    decimal_price INT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    PRIMARY KEY (employee_id, department_id, vendor_id, date, price, decimal_price),
    CONSTRAINT fk_exp_emp FOREIGN KEY (employee_id) 
        REFERENCES expense_management.employee(employee_id) ON DELETE CASCADE,
    CONSTRAINT fk_exp_dept FOREIGN KEY (department_id) 
        REFERENCES expense_management.department(department_no) ON DELETE CASCADE,
    CONSTRAINT fk_exp_vendor FOREIGN KEY (vendor_id)
        REFERENCES expense_management.vendor(vendor_id) ON DELETE CASCADE,
    CHECK (decimal_price >= 0 AND decimal_price < 100),
    -- Toplam tutar sifirdan buyuk olmali (price tam kisim, decimal_price kurus 0-99):
    -- price>0 VEYA decimal_price>0 => en az 0.01. price negatif olamaz.
    CHECK (price >= 0 AND (price > 0 OR decimal_price > 0))
);

-- APPROVED_EXPENSE
CREATE TABLE IF NOT EXISTS expense_management.approved_expense (
    employee_id INT NOT NULL,
    department_id INT NOT NULL,
    vendor_id INT NOT NULL,
    date DATE NOT NULL,
    price INT NOT NULL,
    decimal_price INT NOT NULL,
    currency VARCHAR(10) NOT NULL,
    PRIMARY KEY (employee_id, department_id, vendor_id, date, price, decimal_price),
    CONSTRAINT fk_app_exp_emp FOREIGN KEY (employee_id) 
        REFERENCES expense_management.employee(employee_id) ON DELETE CASCADE,
    CONSTRAINT fk_app_exp_dept FOREIGN KEY (department_id) 
        REFERENCES expense_management.department(department_no) ON DELETE CASCADE,
    CONSTRAINT fk_app_exp_vendor FOREIGN KEY (vendor_id)
        REFERENCES expense_management.vendor(vendor_id) ON DELETE CASCADE,
    CHECK (decimal_price >= 0 AND decimal_price < 100),
    -- expense ile ayni tutar kurali.
    CHECK (price >= 0 AND (price > 0 OR decimal_price > 0))
);

-- ============================================================
-- Dongusel FK: bir departmanin yoneticisi (manager_id) bir employee'dir.
-- department ve employee birbirine referans verdigi icin bu constraint,
-- her iki tablo da olustuktan SONRA burada eklenir.
-- Admin rolu bu kolondan turetilir: bir employee bir departmanin manager_id'si
-- ise ADMIN, degilse STAFF (ayrica rol kolonu TUTULMAZ).
-- ============================================================
ALTER TABLE expense_management.department
    DROP CONSTRAINT IF EXISTS fk_dept_manager;
ALTER TABLE expense_management.department
    ADD CONSTRAINT fk_dept_manager FOREIGN KEY (manager_id)
        REFERENCES expense_management.employee(employee_id) ON DELETE SET NULL;