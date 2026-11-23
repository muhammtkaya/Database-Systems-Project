import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { setEmployeeId } from '../api/client';

/**
 * GECICI dev giris: gercek sifre/JWT yerine employee id girilir ve X-Dev-Employee-Id
 * olarak her istege eklenir. (Mock: Admin 101-105, Staff orn. 109)
 */
export default function Login() {
  const navigate = useNavigate();
  const [id, setId] = useState('');

  function submit(e: FormEvent) {
    e.preventDefault();
    const n = parseInt(id.trim(), 10);
    if (!Number.isInteger(n) || n <= 0) return;
    setEmployeeId(String(n));
    navigate('/');
  }

  return (
    <div className="login-wrap">
      <form className="card login-card" onSubmit={submit}>
        <h1>Harcama Yönetimi</h1>
        <p className="muted">Geçici geliştirme girişi — çalışan numaranı gir.</p>
        <label>
          Çalışan No
          <input
            autoFocus
            type="number"
            min={1}
            value={id}
            onChange={(e) => setId(e.target.value)}
            placeholder="örn. 105 (admin) veya 109 (staff)"
          />
        </label>
        <button type="submit" disabled={!id.trim()}>
          Giriş
        </button>
        <p className="hint">Admin: 101–105 · Staff: 106–120</p>
      </form>
    </div>
  );
}
