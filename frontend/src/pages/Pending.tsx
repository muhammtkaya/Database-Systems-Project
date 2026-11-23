import { useState } from 'react';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';
import { ApiError, approveExpense, listPending, rejectExpense } from '../api/client';
import type { DecisionRequest, Expense } from '../api/types';

function keyOf(e: Expense): DecisionRequest {
  return {
    employeeId: e.employeeId,
    departmentId: e.departmentId,
    vendorId: e.vendorId,
    expenseDate: e.expenseDate,
    amount: e.amount,
  };
}

export default function Pending() {
  const qc = useQueryClient();
  const [page, setPage] = useState(0);
  const [msg, setMsg] = useState<string | null>(null);

  const PAGE_SIZE = 5; // demo: sayfalamanin gorunur calismasi icin kucuk tutuldu
  const q = useQuery({ queryKey: ['pending', page], queryFn: () => listPending(page, PAGE_SIZE) });

  const refresh = () => {
    qc.invalidateQueries({ queryKey: ['pending'] });
    qc.invalidateQueries({ queryKey: ['approved'] });
  };

  const approve = useMutation({
    mutationFn: approveExpense,
    onSuccess: () => { setMsg('✓ Onaylandı'); refresh(); },
    onError: (e) => setMsg(`✕ ${(e as ApiError).status} — ${(e as ApiError).message}`),
  });
  const reject = useMutation({
    mutationFn: rejectExpense,
    onSuccess: () => { setMsg('✓ Reddedildi (listeden kaldırıldı)'); refresh(); },
    onError: (e) => setMsg(`✕ ${(e as ApiError).status} — ${(e as ApiError).message}`),
  });

  if (q.isLoading) return <p>Yükleniyor…</p>;
  if (q.isError) return <p className="error">{(q.error as ApiError).message}</p>;

  const data = q.data!;
  const busy = approve.isPending || reject.isPending;

  return (
    <div>
      <h2>Onay Bekleyen Giderler</h2>
      {msg && <p className="banner">{msg}</p>}
      <table className="grid">
        <thead>
          <tr>
            <th>Çalışan</th><th>Dept</th><th>Satıcı</th><th>Tarih</th>
            <th className="right">Tutar</th><th>İşlem</th>
          </tr>
        </thead>
        <tbody>
          {data.content.map((e, i) => (
            <tr key={i}>
              <td>{e.employeeFullName} <span className="muted">#{e.employeeId}</span></td>
              <td>{e.departmentId}</td>
              <td>{e.vendorName}</td>
              <td>{e.expenseDate}</td>
              <td className="right">{e.amount} {e.currency}</td>
              <td className="actions">
                <button type="button" className="ok" disabled={busy} onClick={() => { setMsg(null); approve.mutate(keyOf(e)); }}>Onayla</button>
                <button type="button" className="no" disabled={busy} onClick={() => { setMsg(null); reject.mutate(keyOf(e)); }}>Reddet</button>
              </td>
            </tr>
          ))}
          {data.content.length === 0 && (
            <tr><td colSpan={6} className="muted center">Onay bekleyen gider yok.</td></tr>
          )}
        </tbody>
      </table>

      <div className="pager">
        <button type="button" disabled={data.first} onClick={() => setPage((p) => p - 1)}>← Önceki</button>
        <span>Sayfa {data.number + 1} / {Math.max(data.totalPages, 1)} · {data.totalElements} kayıt</span>
        <button type="button" disabled={data.last} onClick={() => setPage((p) => p + 1)}>Sonraki →</button>
      </div>
      <p className="muted">Not: Kendi girdiğin gideri onaylayamazsın (403). Başka departman → 403.</p>
    </div>
  );
}
