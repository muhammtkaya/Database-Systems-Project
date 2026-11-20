-- EXPENSE_MANAGEMENT SCHEMA
CREATE SCHEMA IF NOT EXISTS expense_management;

-- DEPARTMENT
CREATE TABLE IF NOT EXISTS expense_management.department (
    department_no INT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    manager_ssn VARCHAR(20),
    super_no INT,
    CONSTRAINT fk_dept_super FOREIGN KEY (super_no) 
        REFERENCES expense_management.department(department_no) ON DELETE SET NULL
);

-- EMPLOYEE
CREATE TABLE IF NOT EXISTS expense_management.employee (
    employee_id INT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    department_no INT,
    super_id INT,
    start_date DATE NOT NULL,
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
    CHECK (decimal_price >= 0 AND decimal_price < 100)
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
    CHECK (decimal_price >= 0 AND decimal_price < 100)
);