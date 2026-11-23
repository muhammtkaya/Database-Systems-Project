import { useQuery, useQueryClient } from '@tanstack/react-query';
import { Link, Navigate, Outlet, useLocation, useNavigate } from 'react-router-dom';
import { clearEmployeeId, getEmployeeId, getMe } from '../api/client';

export default function Layout() {
  const navigate = useNavigate();
  const location = useLocation();
  const qc = useQueryClient();
  const id = getEmployeeId();

  // Dev kimlik yoksa login'e yonlendir (gercek auth guard'inin gecici karsiligi)
  const me = useQuery({ queryKey: ['me'], queryFn: getMe, enabled: !!id });

  if (!id) return <Navigate to="/login" replace state={{ from: location.pathname }} />;

  function logout() {
    clearEmployeeId();
    qc.clear(); // onceki kullanicinin cache'i kalmasin
    navigate('/login', { replace: true });
  }

  return (
    <div className="app">
      <header className="topbar">
        <div className="brand">Harcama Yönetimi</div>
        <nav className="nav">
          <Link to="/">Panel</Link>
          <Link to="/expenses/new">Gider Aç</Link>
          {me.data?.manager && (
            <>
              <Link to="/admin/pending">Onay Bekleyen</Link>
              <Link to="/admin/approved">Onaylanmış</Link>
            </>
          )}
        </nav>
        <div className="user">
          {me.data ? (
            <span>
              {me.data.firstName} {me.data.lastName}
              {me.data.manager ? ' · Admin' : ' · Staff'}
            </span>
          ) : (
            <span>#{id}</span>
          )}
          <button type="button" onClick={logout}>Çıkış</button>
        </div>
      </header>
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
}
