import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './context/AuthContext';
import { Login } from './pages/Login';
import { Dashboard } from './pages/Dashboard';
import { ArtistDetails } from './pages/ArtistDetails';
import { useContext, useEffect, useState } from 'react';
import { connectWebsocket } from './services/websocket';
import './App.css';
import NotificationToast from './components/NotificationToast';
import { ThemeProvider, useTheme } from './context/ThemeContext';

// Componente para proteger rotas privadas
function PrivateRoute({ children }: { children: any }) {
  const { isAuthenticated, isLoading } = useContext(AuthContext);

  // Sem mensagem, simplesmente redireciona silenciosamente para login
  if (isLoading) return null;
  
  return isAuthenticated ? children : <Navigate to="/login" replace />;
}

function App() {
  const [toast, setToast] = useState<string | null>(null);
  const [sessionNotice, setSessionNotice] = useState<string | null>(null);

  useEffect(() => {
    const msg = localStorage.getItem('sessionExpiredMessage');
    if (msg) {
      setSessionNotice(msg);
      localStorage.removeItem('sessionExpiredMessage');
      setTimeout(() => setSessionNotice(null), 5000);
    }
  }, []);

  useEffect(() => {
    const disconnect = connectWebsocket((msg: string) => {
      setToast(msg);
      setTimeout(() => setToast(null), 5000);
    });

    return () => disconnect && disconnect();
  }, []);

  return (
    <BrowserRouter>
      <ThemeProvider>
      <AuthProvider>
        {toast && (
          <NotificationToast message={toast} onClose={() => setToast(null)} position="top-right" />
        )}
        {sessionNotice && (
          <NotificationToast message={sessionNotice} onClose={() => setSessionNotice(null)} variant="error" position="top-left" />
        )}
        <ThemeToggleButton />
        <Routes>
          <Route path="/login" element={<Login />} />
          
          {/* Rotas Protegidas */}
          <Route 
            path="/" 
            element={
              <PrivateRoute>
                <Dashboard />
              </PrivateRoute>
            } 
          />
          <Route
            path="/artista/:id"
            element={
              <PrivateRoute>
                <ArtistDetails />
              </PrivateRoute>
            }
          />
        </Routes>
      </AuthProvider>
      </ThemeProvider>
    </BrowserRouter>
  );
}

function ThemeToggleButton() {
  const { theme, toggle } = useTheme();
  return (
    <div className="fixed top-4 right-20 z-50">
      <button
        onClick={toggle}
        className="bg-white/90 dark:bg-neutral-800 text-sm px-3 py-1 rounded shadow border"
        title="Alternar tema claro/escuro"
      >
        {theme === 'dark' ? '🌙 Dark' : '☀️ Light'}
      </button>
    </div>
  );
}

export default App;
