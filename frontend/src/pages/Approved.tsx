import { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import { ApiError, listApproved } from '../api/client';

export default function Approved() {
  const [page, setPage] = useState(0);
  const PAGE_SIZE = 5; // demo: sayfalamanin gorunur calismasi icin kucuk tutuldu
  const q = useQuery({ queryKey: ['approved', page], queryFn: () => listApproved(page, PAGE_SIZE) });

  if (q.isLoading) return <p>Yükleniyor…</p>;
  if (q.isError) return <p className="error">{(q.error as ApiError).message}</p>;

  const data = q.data!;
  return (
    <div>
      <h2>Onaylanmış Giderler</h2>
      <table className="grid">
        <thead>
          <tr>
            <th>Çalışan</th><th>Dept</th><th>Satıcı</th><th>Tarih</th><th className="right">Tutar</th>
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
            </tr>
          ))}
          {data.content.length === 0 && (
            <tr><td colSpan={5} className="muted center">Onaylanmış gider yok.</td></tr>
          )}
        </tbody>
      </table>

      <div className="pager">
        <button type="button" disabled={data.first} onClick={() => setPage((p) => p - 1)}>← Önceki</button>
        <span>Sayfa {data.number + 1} / {Math.max(data.totalPages, 1)} · {data.totalElements} kayıt</span>
        <button type="button" disabled={data.last} onClick={() => setPage((p) => p + 1)}>Sonraki →</button>
      </div>
    </div>
  );
}
