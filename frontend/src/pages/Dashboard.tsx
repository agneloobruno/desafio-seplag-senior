import { useEffect, useState, useContext } from 'react';
import { artistService } from '../services/artistService';
import { Artist } from '../types/artist';
import { AuthContext } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

export function Dashboard() {
  const [artistas, setArtistas] = useState<Artist[]>([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [novoNome, setNovoNome] = useState('');
  const [creating, setCreating] = useState(false);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [busca, setBusca] = useState('');

  const { logout } = useContext(AuthContext);
  const navigate = useNavigate();

  const carregarArtistas = async (paginaParaCarregar: number) => {
    setLoading(true);
    try {
      const dados = await artistService.getAll(paginaParaCarregar, busca);
      setArtistas(dados.content);
      setTotalPages(dados.totalPages);
      setPage(dados.number);
    } catch (error) {
      console.error('Erro ao carregar artistas', error);
      alert('Erro ao buscar dados. Verifique se o backend está rodando.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    carregarArtistas(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [busca]);

  const mudarPagina = (novaPagina: number) => {
    if (novaPagina >= 0 && novaPagina < totalPages) {
      carregarArtistas(novaPagina);
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      <header className="bg-white shadow">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8 flex justify-between items-center">
          <h1 className="text-3xl font-bold text-gray-900">Gestão de Artistas</h1>
          <button onClick={logout} className="text-red-600 hover:text-red-800 font-medium">
            Sair
          </button>
        </div>
      </header>

      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="mb-6 flex gap-4">
          <input
            type="text"
            placeholder="Buscar artista por nome..."
            className="flex-1 p-2 border border-gray-300 rounded shadow-sm"
            value={busca}
            onChange={(e) => setBusca(e.target.value)}
          />
          <div>
            <button onClick={() => setShowForm((s) => !s)} className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700">
              + Novo Artista
            </button>
          </div>
        </div>

          {showForm && (
            <form
              onSubmit={async (e) => {
                e.preventDefault();
                if (!novoNome || novoNome.trim().length === 0) {
                  alert('Informe o nome do artista');
                  return;
                }

                try {
                  setCreating(true);
                  await artistService.create(novoNome.trim());
                  setNovoNome('');
                  setShowForm(false);
                  carregarArtistas(0);
                } catch (err) {
                  console.error('Erro ao criar artista', err);
                  alert('Erro ao criar artista. Verifique o backend e o token.');
                } finally {
                  setCreating(false);
                }
              }}
              className="mb-6 flex gap-2 items-center"
            >
              <input
                type="text"
                placeholder="Nome do artista"
                className="p-2 border border-gray-300 rounded shadow-sm"
                value={novoNome}
                onChange={(e) => setNovoNome(e.target.value)}
                disabled={creating}
              />
              <button type="submit" disabled={creating} className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">
                {creating ? 'Salvando...' : 'Salvar'}
              </button>
              <button type="button" onClick={() => { setShowForm(false); setNovoNome(''); }} className="px-3 py-2 border rounded">
                Cancelar
              </button>
            </form>
          )}

        {loading ? (
          <div className="text-center py-10">Carregando...</div>
        ) : (
          <>
            {artistas.length === 0 ? (
              <div className="text-center text-gray-500 py-10">Nenhum artista encontrado.</div>
            ) : (
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                {artistas.map((artista) => (
                  <div key={artista.id} className="bg-white overflow-hidden shadow rounded-lg hover:shadow-md transition cursor-pointer border border-gray-200">
                    <div className="px-4 py-5 sm:p-6">
                      <h3 className="text-lg leading-6 font-medium text-gray-900">{artista.nome}</h3>
                      <div className="mt-2 max-w-xl text-sm text-gray-500">
                        <p>ID: {artista.id}</p>
                      </div>
                      <div className="mt-4">
                        <button onClick={() => navigate(`/artista/${artista.id}`)} className="text-blue-600 hover:text-blue-800 text-sm font-medium">Ver Álbuns →</button>
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            )}

            <div className="mt-6 flex justify-center gap-2 items-center">
              <button onClick={() => mudarPagina(page - 1)} disabled={page === 0} className="px-3 py-1 border rounded disabled:opacity-50">
                Anterior
              </button>
              <span className="text-gray-700">Página {page + 1} de {totalPages}</span>
              <button onClick={() => mudarPagina(page + 1)} disabled={page === totalPages - 1} className="px-3 py-1 border rounded disabled:opacity-50">
                Próxima
              </button>
            </div>
          </>
        )}
      </main>
    </div>
  );
}

export default Dashboard;
