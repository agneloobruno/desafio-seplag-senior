import { useEffect, useState, useContext, useRef } from 'react';
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
  const fileInputsRef = useRef<Record<number, HTMLInputElement | null>>({});

  const isAdmin = (() => {
    try {
      const token = localStorage.getItem('token');
      if (!token) return false;
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload?.role === 'ADMIN';
    } catch (e) {
      return false;
    }
  })();

  const carregarArtistas = async (paginaParaCarregar: number) => {
    setLoading(true);
    try {
      const dados = await artistService.getAll(paginaParaCarregar, busca);
      setArtistas(dados.content);
      setTotalPages(dados.totalPages);
      setPage(dados.number);
      } catch (error) {
      console.error('Erro ao carregar artistas', error);
      // Se a sessão expirou, o interceptor já direcionou para login e mostrou mensagem.
      if (!localStorage.getItem('sessionExpiredMessage')) {
        alert('Erro ao buscar dados. Verifique se o backend está rodando.');
      }
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
    <div className="min-h-screen bg-gray-50 dark:bg-neutral-900 dark:text-white">
      <header className="bg-white shadow dark:bg-neutral-800">
        <div className="max-w-7xl mx-auto py-6 px-4 sm:px-6 lg:px-8 flex justify-between items-center">
          <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Gestão de Artistas</h1>
          <button onClick={logout} className="text-red-600 hover:text-red-800 font-medium dark:text-red-400">
            Sair
          </button>
        </div>
      </header>

      <main className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
        <div className="mb-6 flex gap-4">
          <input
            type="text"
            placeholder="Buscar artista por nome..."
            className="flex-1 p-2 border border-gray-300 rounded shadow-sm dark:bg-neutral-800 dark:border-neutral-700 dark:text-white"
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
              <div className="grid grid-cols-1 md:grid-cols-4 lg:grid-cols-6 xl:grid-cols-8 gap-1">
                {artistas.map((artista) => {
                  const imgSrc = artista.fotoUrl ?? `https://api.dicebear.com/6.x/initials/svg?seed=${encodeURIComponent(artista.nome)}`;
                  return (
                    <div key={artista.id} className="flex justify-center">
                      <div className="relative group" style={{ width: 120 }}>
                        <div
                          onClick={() => navigate(`/artista/${artista.id}`)}
                          className="cursor-pointer flex flex-col items-center"
                        >
                          <div className="relative">
                            <img src={imgSrc} alt={artista.nome} className="w-28 h-28 rounded-full object-cover border border-gray-200 shadow-sm transform transition-transform duration-200 group-hover:scale-105" />

                            <div className="absolute bottom-1 right-1 opacity-0 group-hover:opacity-100 transition-all duration-150">
                              <button
                                onClick={(ev) => {
                                  ev.stopPropagation();
                                  // Ação de play (placeholder)
                                }}
                                aria-label={`Tocar ${artista.nome}`}
                                className="bg-[#1DB954] hover:bg-[#1ed760] text-white p-1 rounded-full shadow-md text-xs flex items-center justify-center ring-2 ring-white transform transition-transform duration-150 hover:scale-105"
                                title="Play"
                              >
                                <svg xmlns="http://www.w3.org/2000/svg" className="h-3 w-3" viewBox="0 0 24 24" fill="currentColor">
                                  <path d="M3 22v-20l18 10-18 10z" />
                                </svg>
                              </button>
                            </div>
                          </div>

                          <div className="mt-1 text-center">
                            <div className="text-sm font-semibold text-gray-900">{artista.nome}</div>
                            <div className="text-xs text-gray-500">Artista</div>
                          </div>
                        </div>

                        {isAdmin && (
                          <div className="absolute top-2 right-2 z-10">
                            <input
                              ref={(el) => (fileInputsRef.current[artista.id] = el)}
                              type="file"
                              accept="image/*"
                              className="hidden"
                              onChange={async (e) => {
                                const file = e.target.files && e.target.files[0];
                                if (!file) return;
                                try {
                                  await artistService.uploadPhoto(artista.id, file);
                                  carregarArtistas(page);
                                } catch (err) {
                                  console.error('Erro ao enviar foto', err);
                                  alert('Falha ao enviar foto. Verifique o backend e o token.');
                                }
                              }}
                            />
                            <button
                              onClick={(ev) => {
                                ev.stopPropagation();
                                fileInputsRef.current[artista.id]?.click();
                              }}
                              className="bg-white/80 hover:bg-white text-gray-800 px-2 py-1 rounded shadow text-xs"
                              title="Trocar foto do artista"
                            >
                              Editar
                            </button>
                          </div>
                        )}
                      </div>
                    </div>
                  );
                })}
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
