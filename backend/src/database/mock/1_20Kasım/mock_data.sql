-- 1. DEPARTMENT DATA (5 satır - 3 kademeli departman hiyerarşisi)
-- manager_id, employee'ler henuz yok oldugu icin burada NULL birakilir;
-- employee INSERT'inden SONRA UPDATE ile atanir (dongusel FK sirasi).
INSERT INTO expense_management.department (department_no, name, super_no) VALUES
(1, 'Genel Müdürlük', NULL),
(2, 'Bilgi Teknolojileri', 1),
(3, 'Finans', 1),
(4, 'Pazarlama', 1),
(5, 'Yazılım Geliştirme', 2)
ON CONFLICT (department_no) DO NOTHING;

-- 2. EMPLOYEE DATA (20 satır - 4 kademeli ağaç yapısı)
-- Seviye 1 (Genel Müdür): 101
-- Seviye 2 (Direktörler -> 101'e bağlı): 102, 103, 104
-- Seviye 3 (Takım Liderleri -> Direktörlere bağlı): 105, 106, 107, 108
-- Seviye 4 (Uzmanlar -> Takım Liderlerine bağlı): 109..120
INSERT INTO expense_management.employee (employee_id, first_name, last_name, department_no, super_id, start_date) VALUES
(101, 'Ahmet', 'Yılmaz', 1, NULL, '2020-01-15'),
(102, 'Ayşe', 'Kaya', 2, 101, '2020-06-10'),
(103, 'Zeynep', 'Çelik', 3, 101, '2020-09-20'),
(104, 'Can', 'Öztürk', 4, 101, '2021-02-14'),
(105, 'Mehmet', 'Demir', 5, 102, '2021-05-01'),
(106, 'Elif', 'Aydın', 2, 102, '2021-07-12'),
(107, 'Burak', 'Şahin', 3, 103, '2021-10-01'),
(108, 'Selin', 'Yıldız', 4, 104, '2022-01-18'),
(109, 'Emre', 'Arslan', 5, 105, '2022-04-22'),
(110, 'Deniz', 'Koç', 5, 105, '2022-06-10'),
(111, 'Merve', 'Kurt', 5, 105, '2022-08-15'),
(112, 'Oğuz', 'Özkan', 2, 106, '2023-01-09'),
(113, 'Ece', 'Şimşek', 2, 106, '2023-03-03'),
(114, 'Murat', 'Polat', 2, 106, '2023-05-11'),
(115, 'Gizem', 'Korkmaz', 3, 107, '2023-07-05'),
(116, 'Kaan', 'Çetin', 3, 107, '2023-09-20'),
(117, 'Buse', 'Aksoy', 3, 107, '2024-01-14'),
(118, 'Tarık', 'Güneş', 4, 108, '2024-04-01'),
(119, 'Ceren', 'Yalçın', 4, 108, '2024-08-17'),
(120, 'Onur', 'Kılıç', 4, 108, '2025-01-25')
ON CONFLICT (employee_id) DO NOTHING;

-- 2b. DEPARTMAN YONETICILERI (manager_id) - employee'ler olustuktan sonra atanir.
-- Hiyerarsiye gore dogal esleme => ADMIN'ler: 101, 102, 103, 104, 105
UPDATE expense_management.department AS d
SET manager_id = m.mgr
FROM (VALUES (1, 101), (2, 102), (3, 103), (4, 104), (5, 105)) AS m(dept, mgr)
WHERE d.department_no = m.dept;

-- 3. VENDOR DATA (20 satır)
INSERT INTO expense_management.vendor (vendor_id, name) VALUES
(1, 'AWS Cloud Services'),
(2, 'Türk Hava Yolları'),
(3, 'Ofis Kırtasiye A.Ş.'),
(4, 'Google Ads'),
(5, 'Microsoft Azure'),
(6, 'Pegasus Hava Yolları'),
(7, 'Hilton Otelleri'),
(8, 'Meta Business'),
(9, 'LinkedIn Talent Solutions'),
(10, 'Turkcell Kurumsal'),
(11, 'Hepsiburada Kurumsal'),
(12, 'Trendyol Kurumsal'),
(13, 'Yurtiçi Kargo'),
(14, 'Aras Kargo'),
(15, 'Shell Filo Yönetimi'),
(16, 'Opet Kurumsal'),
(17, 'Sodexo Yemek Kartı'),
(18, 'Multinet Up'),
(19, 'Atlassian Software'),
(20, 'Zoom Video Communications')
ON CONFLICT (vendor_id) DO NOTHING;

-- 4. EXPENSE DATA (30 satır - Güncel / Onay bekleyen harcamalar)
INSERT INTO expense_management.expense (employee_id, department_id, vendor_id, date, price, decimal_price, currency)
SELECT
    e.employee_id,
    e.department_no,
    ((gs % 20) + 1) AS vendor_id,
    DATE '2026-10-01' + (gs % 45) AS date,
    (150 + (gs * 73) % 5000) AS price,
    ((gs * 17) % 100) AS decimal_price,
    (ARRAY['TRY', 'USD', 'EUR'])[(gs % 3) + 1] AS currency
FROM generate_series(1, 30) AS gs
JOIN expense_management.employee e ON e.employee_id = 101 + ((gs - 1) % 20)
ON CONFLICT DO NOTHING;

-- 5. APPROVED EXPENSE DATA (100 satır - Onaylanmış harcamalar)
INSERT INTO expense_management.approved_expense (employee_id, department_id, vendor_id, date, price, decimal_price, currency)
SELECT
    e.employee_id,
    e.department_no,
    (((gs * 3) % 20) + 1) AS vendor_id,
    DATE '2026-01-01' + (gs * 2) AS date,
    (200 + (gs * 97) % 10000) AS price,
    ((gs * 13) % 100) AS decimal_price,
    (ARRAY['TRY', 'USD', 'EUR'])[(gs % 3) + 1] AS currency
FROM generate_series(1, 100) AS gs
JOIN expense_management.employee e ON e.employee_id = 101 + ((gs - 1) % 20)
ON CONFLICT DO NOTHING;