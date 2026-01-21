import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, AuthContext } from './context/AuthContext';
import { Login } from './pages/Login';
import { useContext } from 'react';
import './App.css';

// Componente para proteger rotas privadas
function PrivateRoute({ children }: { children: React.ReactElement }) {
  const { isAuthenticated, isLoading } = useContext(AuthContext);

  if (isLoading) return <div>Carregando...</div>;
  
  return isAuthenticated ? children : <Navigate to="/login" />;
}

function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<Login />} />
          
          {/* Rotas Protegidas */}
          <Route 
            path="/" 
            element={
              <PrivateRoute>
                <div className="p-10 text-center text-2xl">
                  <h1>Bem-vindo ao Sistema de Gestão de Álbuns</h1>
                  {/* Aqui entraremos com a Listagem de Artistas depois */}
                </div>
              </PrivateRoute>
            } 
          />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}

export default App;
