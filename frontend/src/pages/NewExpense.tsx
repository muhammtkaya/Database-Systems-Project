import { useState, type FormEvent } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { ApiError, createExpense, listVendors } from '../api/client';
import type { Expense } from '../api/types';

const today = () => new Date().toISOString().slice(0, 10);
const CURRENCIES = ['TRY', 'USD', 'EUR'];

export default function NewExpense() {
  const qc = useQueryClient();
  const vendors = useQuery({ queryKey: ['vendors'], queryFn: listVendors });

  const [vendorId, setVendorId] = useState('');
  const [amount, setAmount] = useState('');
  const [currencyCode, setCurrency] = useState('TRY');
  const [expenseDate, setDate] = useState(today());
  const [ok, setOk] = useState<Expense | null>(null);

  const mutation = useMutation({
    mutationFn: createExpense,
    onSuccess: (created) => {
      setOk(created);
      setAmount('');
      qc.invalidateQueries({ queryKey: ['pending'] });
    },
  });

  function submit(e: FormEvent) {
    e.preventDefault();
    setOk(null);
    mutation.mutate({
      vendorId: parseInt(vendorId, 10),
      amount: parseFloat(amount),
      currencyCode,
      expenseDate,
    });
  }

  const err = mutation.error as ApiError | null;

  return (
    <div>
      <h2>Gider Aç</h2>
      <form className="card form" onSubmit={submit}>
        <label>
          Satıcı (Vendor)
          <select value={vendorId} onChange={(e) => setVendorId(e.target.value)} required>
            <option value="" disabled>Seçiniz…</option>
            {vendors.data?.map((v) => (
              <option key={v.id} value={v.id}>{v.name}</option>
            ))}
          </select>
        </label>

        <label>
          Tutar
          <input type="number" step="0.01" min="0.01" value={amount}
                 onChange={(e) => setAmount(e.target.value)} required placeholder="örn. 1250.75" />
        </label>

        <label>
          Para Birimi
          <select value={currencyCode} onChange={(e) => setCurrency(e.target.value)}>
            {CURRENCIES.map((c) => <option key={c} value={c}>{c}</option>)}
          </select>
        </label>

        <label>
          Tarih
          <input type="date" value={expenseDate} max={today()}
                 onChange={(e) => setDate(e.target.value)} required />
        </label>

        <button type="submit" disabled={mutation.isPending}>
          {mutation.isPending ? 'Gönderiliyor…' : 'Gideri Oluştur'}
        </button>

        {ok && (
          <p className="success">
            ✓ Gider oluşturuldu: {ok.vendorName} · {ok.amount} {ok.currency} · {ok.expenseDate} (onay bekliyor)
          </p>
        )}
        {err && <p className="error">✕ {err.status} — {err.message}</p>}
      </form>
      <p className="muted">
        Departman sunucuda senin kaydından alınır. Olmayan satıcı → 404, işe giriş tarihinden önce → 400.
      </p>
    </div>
  );
}
