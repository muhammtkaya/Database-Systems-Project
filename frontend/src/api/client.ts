import type {
  ApiErrorBody,
  CreateExpenseRequest,
  DecisionRequest,
  Employee,
  Expense,
  Page,
  Vendor,
} from './types';

const BASE = 'http://localhost:8080';
const ID_KEY = 'devEmployeeId';

// --- Gecici "dev kimlik" (gercek login/JWT sonraki faz) ---
export function getEmployeeId(): string | null {
  try {
    return localStorage.getItem(ID_KEY);
  } catch {
    return null;
  }
}
export function setEmployeeId(id: string) {
  try {
    localStorage.setItem(ID_KEY, id);
  } catch {
    /* localStorage yoksa yok say */
  }
}
export function clearEmployeeId() {
  try {
    localStorage.removeItem(ID_KEY);
  } catch {
    /* yok say */
  }
}

export class ApiError extends Error {
  status: number;
  body: ApiErrorBody | null;
  constructor(status: number, message: string, body: ApiErrorBody | null) {
    super(message);
    this.status = status;
    this.body = body;
  }
}

async function request<T>(path: string, opts: RequestInit = {}): Promise<T> {
  const headers: Record<string, string> = { ...(opts.headers as Record<string, string>) };
  const id = getEmployeeId();
  if (id) headers['X-Dev-Employee-Id'] = id; // her istekte dev kimligi
  if (opts.body) headers['Content-Type'] = 'application/json';

  const res = await fetch(BASE + path, { ...opts, headers });
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;

  if (!res.ok) {
    const body = data as ApiErrorBody | null;
    const msg = body?.message || res.statusText || `HTTP ${res.status}`;
    throw new ApiError(res.status, msg, body);
  }
  return data as T;
}

// --- Uc noktalar ---
export const getMe = () => request<Employee>('/api/employees/me');

export const listVendors = () => request<Vendor[]>('/api/vendors');

export const createExpense = (req: CreateExpenseRequest) =>
  request<Expense>('/api/expenses', { method: 'POST', body: JSON.stringify(req) });

export const listPending = (page = 0, size = 20) =>
  request<Page<Expense>>(`/api/expenses/pending?page=${page}&size=${size}`);

export const listApproved = (page = 0, size = 20) =>
  request<Page<Expense>>(`/api/expenses/approved?page=${page}&size=${size}`);

export const approveExpense = (req: DecisionRequest) =>
  request<void>('/api/expenses/approve', { method: 'POST', body: JSON.stringify(req) });

export const rejectExpense = (req: DecisionRequest) =>
  request<void>('/api/expenses/reject', { method: 'POST', body: JSON.stringify(req) });
