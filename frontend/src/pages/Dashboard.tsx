import { useQuery } from '@tanstack/react-query';
import { Link } from 'react-router-dom';
import { getMe } from '../api/client';

export default function Dashboard() {
  const me = useQuery({ queryKey: ['me'], queryFn: getMe });

  if (me.isLoading) return <p>Yükleniyor…</p>;
  if (me.isError) return <p className="error">Kimlik alınamadı: {(me.error as Error).message}</p>;

  const e = me.data!;
  return (
    <div>
      <h2>Panel</h2>
      <div className="card">
        <div className="kv"><span>Ad Soyad</span><b>{e.firstName} {e.lastName}</b></div>
        <div className="kv"><span>Çalışan No</span><b>{e.id}</b></div>
        <div className="kv"><span>Departman</span><b>{e.departmentName ?? '—'}</b></div>
        <div className="kv"><span>Rol</span><b>{e.manager ? 'Admin (departman yöneticisi)' : 'Staff'}</b></div>
        <div className="kv"><span>İşe Başlama</span><b>{e.startDate}</b></div>
      </div>

      <div className="tiles">
        <Link className="tile" to="/expenses/new">
          <h3>Gider Aç</h3>
          <p>Yeni bir harcama kaydı oluştur.</p>
        </Link>
        {e.manager && (
          <>
            <Link className="tile" to="/admin/pending">
              <h3>Onay Bekleyen</h3>
              <p>Departmanının onay bekleyen giderleri.</p>
            </Link>
            <Link className="tile" to="/admin/approved">
              <h3>Onaylanmış</h3>
              <p>Departmanının onaylanmış giderleri.</p>
            </Link>
          </>
        )}
      </div>
    </div>
  );
}
