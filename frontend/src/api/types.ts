// Backend DTO'larinin birebir TS karsiliklari (com.etu.expensemanagement.dto)

export interface Employee {
  id: number;
  firstName: string;
  lastName: string;
  departmentNo: number | null;
  departmentName: string | null;
  supervisorId: number | null;
  startDate: string; // ISO date
  manager: boolean;  // bir departmanin yoneticisi mi => ADMIN
}

export interface Vendor {
  id: number;
  name: string;
}

export interface Expense {
  employeeId: number;
  employeeFullName: string;
  departmentId: number;
  vendorId: number;
  vendorName: string;
  expenseDate: string;
  amount: number;
  currency: string;
}

export interface CreateExpenseRequest {
  vendorId: number;
  amount: number;
  currencyCode: string;
  expenseDate: string;
}

export interface DecisionRequest {
  employeeId: number;
  departmentId: number;
  vendorId: number;
  expenseDate: string;
  amount: number;
  comment?: string;
}

// Spring Data Page<T> (ihtiyac duyulan alanlar)
export interface Page<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

export interface ApiErrorBody {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  fieldErrors?: { field: string; message: string }[];
}
