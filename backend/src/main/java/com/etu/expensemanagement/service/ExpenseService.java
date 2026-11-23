package com.etu.expensemanagement.service;

import com.etu.expensemanagement.dto.request.CreateExpenseRequest;
import com.etu.expensemanagement.dto.request.DecisionRequest;
import com.etu.expensemanagement.dto.response.ExpenseResponse;
import com.etu.expensemanagement.entity.ApprovedExpense;
import com.etu.expensemanagement.entity.Department;
import com.etu.expensemanagement.entity.Employee;
import com.etu.expensemanagement.entity.Expense;
import com.etu.expensemanagement.entity.ExpenseKey;
import com.etu.expensemanagement.entity.Vendor;
import com.etu.expensemanagement.exception.BadRequestException;
import com.etu.expensemanagement.exception.ConflictException;
import com.etu.expensemanagement.exception.ForbiddenException;
import com.etu.expensemanagement.exception.NotFoundException;
import com.etu.expensemanagement.repository.ApprovedExpenseRepository;
import com.etu.expensemanagement.repository.DepartmentRepository;
import com.etu.expensemanagement.repository.EmployeeRepository;
import com.etu.expensemanagement.repository.ExpenseRepository;
import com.etu.expensemanagement.repository.VendorRepository;
import com.etu.expensemanagement.util.AmountConverter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Gider akisi: kullanici gider acar (pending), departman yoneticisi (admin) kendi
 * departmanindaki giderleri listeler ve approve/reject eder.
 *
 * Yetki ve is kurallari HER ZAMAN burada (eylemin gerceklestigi yerde) denetlenir;
 * listeleme filtresi UX icindir, guvenlik siniri degildir (defense in depth).
 * (Auth akisi henuz yok; kimlik gecici DevIdentityInterceptor'dan gelir.)
 */
@Service
public class ExpenseService {

    private final EmployeeService employeeService;
    private final ExpenseRepository expenses;
    private final ApprovedExpenseRepository approvedExpenses;
    private final VendorRepository vendors;
    private final DepartmentRepository departments;
    private final EmployeeRepository employees;

    public ExpenseService(EmployeeService employeeService, ExpenseRepository expenses,
                          ApprovedExpenseRepository approvedExpenses, VendorRepository vendors,
                          DepartmentRepository departments, EmployeeRepository employees) {
        this.employeeService = employeeService;
        this.expenses = expenses;
        this.approvedExpenses = approvedExpenses;
        this.vendors = vendors;
        this.departments = departments;
        this.employees = employees;
    }

    // ---------------------------------------------------------------------
    // KULLANICI: gider acma menusu
    // ---------------------------------------------------------------------

    @Transactional
    public ExpenseResponse createExpense(CreateExpenseRequest req) {
        Employee me = employeeService.requireCurrentEmployee();

        // Departman CLIENT'tan alinmaz; calisanin kendi departmanidir.
        Integer departmentId = me.getDepartmentNo();
        if (departmentId == null) {
            throw new ConflictException("Your account has no department; an expense cannot be filed");
        }

        // KONTROL 1: vendor tablosunda olmayan saticidan gider acilamaz.
        Vendor vendor = vendors.findById(req.vendorId())
                .orElseThrow(() -> new NotFoundException("vendorId " + req.vendorId() + " does not exist"));

        // KONTROL 2: ise giris tarihinden ONCEye gider acilamaz.
        if (req.expenseDate().isBefore(me.getStartDate())) {
            throw new BadRequestException("expenseDate " + req.expenseDate()
                    + " is before your start date " + me.getStartDate());
        }

        // Tutar > 0 ve en fazla 2 ondalik: AmountConverter dogrular (DB CHECK + DTO ile birlikte, 3 kat).
        AmountConverter.AmountParts parts = AmountConverter.toParts(req.amount());

        ExpenseKey key = new ExpenseKey(me.getEmployeeId(), departmentId, req.vendorId(),
                req.expenseDate(), parts.price(), parts.decimalPrice());

        // KONTROL 3: composite PK cakismasi (ayni kisi/vendor/gun/tutar) -> 409.
        // (Bilinen sinir: ayni gun ayni tutarda ikinci gider su an girilemez; surrogate key ileride.)
        if (expenses.existsById(key) || approvedExpenses.existsById(key)) {
            throw new ConflictException("An identical expense already exists (same vendor, date and amount)");
        }

        expenses.save(new Expense(key, req.currencyCode()));

        return toResponse(key, req.currencyCode(),
                Map.of(vendor.getVendorId(), vendor.getName()),
                Map.of(me.getEmployeeId(), fullName(me)));
    }

    // ---------------------------------------------------------------------
    // ADMIN: onay bekleyen / onaylanmis gider menuleri (sayfalanmis)
    // ---------------------------------------------------------------------

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> listPendingForAdmin(Pageable pageable) {
        List<Integer> deptIds = managedDepartmentIdsOrForbidden();
        Page<Expense> page = expenses.findByIdDepartmentIdIn(deptIds, pageable);
        return mapExpensePage(page);
    }

    @Transactional(readOnly = true)
    public Page<ExpenseResponse> listApprovedForAdmin(Pageable pageable) {
        List<Integer> deptIds = managedDepartmentIdsOrForbidden();
        Page<ApprovedExpense> page = approvedExpenses.findByIdDepartmentIdIn(deptIds, pageable);
        List<ExpenseKey> keys = page.getContent().stream().map(ApprovedExpense::getId).toList();
        Map<Integer, String> vendorNames = vendorNames(keys.stream().map(ExpenseKey::getVendorId).toList());
        Map<Integer, String> empNames = employeeNames(keys.stream().map(ExpenseKey::getEmployeeId).toList());
        return page.map(a -> toResponse(a.getId(), a.getCurrency(), vendorNames, empNames));
    }

    // ---------------------------------------------------------------------
    // ADMIN: approve / reject
    // ---------------------------------------------------------------------

    @Transactional
    public void approve(DecisionRequest req) {
        Employee me = employeeService.requireCurrentEmployee();
        List<Integer> deptIds = managedDepartmentIdsOrForbidden(me);
        Expense pending = requirePending(req);

        // Yetki: yalnizca kendi yonettigin departmanin gideri.
        if (!deptIds.contains(pending.getId().getDepartmentId())) {
            throw new ForbiddenException("This expense is not in a department you manage");
        }
        // Cikar catismasi: kendi giderini onaylayamazsin.
        if (pending.getId().getEmployeeId().equals(me.getEmployeeId())) {
            throw new ForbiddenException("You cannot approve your own expense");
        }

        // Atomik tasima: approved'a ekle + pending'den sil (tek transaction).
        approvedExpenses.save(new ApprovedExpense(pending.getId(), pending.getCurrency()));
        expenses.delete(pending);
    }

    @Transactional
    public void reject(DecisionRequest req) {
        Employee me = employeeService.requireCurrentEmployee();
        List<Integer> deptIds = managedDepartmentIdsOrForbidden(me);
        Expense pending = requirePending(req);

        if (!deptIds.contains(pending.getId().getDepartmentId())) {
            throw new ForbiddenException("This expense is not in a department you manage");
        }

        // Semada "rejected" tablosu yok: ret = pending'den silme.
        expenses.delete(pending);
    }

    // ---------------------------------------------------------------------
    // yardimcilar
    // ---------------------------------------------------------------------

    private Expense requirePending(DecisionRequest req) {
        AmountConverter.AmountParts parts = AmountConverter.toParts(req.amount());
        ExpenseKey key = new ExpenseKey(req.employeeId(), req.departmentId(), req.vendorId(),
                req.expenseDate(), parts.price(), parts.decimalPrice());
        return expenses.findById(key)
                .orElseThrow(() -> new NotFoundException("No pending expense matches the given details"));
    }

    private List<Integer> managedDepartmentIdsOrForbidden() {
        return managedDepartmentIdsOrForbidden(employeeService.requireCurrentEmployee());
    }

    private List<Integer> managedDepartmentIdsOrForbidden(Employee me) {
        List<Integer> ids = departments.findByManagerId(me.getEmployeeId()).stream()
                .map(Department::getDepartmentNo)
                .toList();
        if (ids.isEmpty()) {
            throw new ForbiddenException("Only a department manager (admin) can use this menu");
        }
        return ids;
    }

    private Page<ExpenseResponse> mapExpensePage(Page<Expense> page) {
        List<ExpenseKey> keys = page.getContent().stream().map(Expense::getId).toList();
        Map<Integer, String> vendorNames = vendorNames(keys.stream().map(ExpenseKey::getVendorId).toList());
        Map<Integer, String> empNames = employeeNames(keys.stream().map(ExpenseKey::getEmployeeId).toList());
        return page.map(e -> toResponse(e.getId(), e.getCurrency(), vendorNames, empNames));
    }

    /** Sayfadaki vendor adlarini TEK sorguda toplar (N+1'den kacinmak icin). */
    private Map<Integer, String> vendorNames(Collection<Integer> vendorIds) {
        List<Integer> distinct = vendorIds.stream().distinct().toList();
        return vendors.findAllById(distinct).stream()
                .collect(Collectors.toMap(Vendor::getVendorId, Vendor::getName));
    }

    private Map<Integer, String> employeeNames(Collection<Integer> employeeIds) {
        List<Integer> distinct = employeeIds.stream().distinct().toList();
        return employees.findAllById(distinct).stream()
                .collect(Collectors.toMap(Employee::getEmployeeId, (Function<Employee, String>) this::fullName));
    }

    private String fullName(Employee e) {
        return e.getFirstName() + " " + e.getLastName();
    }

    private ExpenseResponse toResponse(ExpenseKey k, String currency,
                                       Map<Integer, String> vendorNames, Map<Integer, String> empNames) {
        return new ExpenseResponse(
                k.getEmployeeId(),
                empNames.get(k.getEmployeeId()),
                k.getDepartmentId(),
                k.getVendorId(),
                vendorNames.get(k.getVendorId()),
                k.getExpenseDate(),
                AmountConverter.toAmount(k.getPrice(), k.getDecimalPrice()),
                currency);
    }
}
