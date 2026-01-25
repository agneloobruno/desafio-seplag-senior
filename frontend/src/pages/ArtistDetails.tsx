import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { albumService } from '../services/albumService';
import { Album } from '../types/album';
import { Page } from '../types/artist';

export function ArtistDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [albuns, setAlbuns] = useState<Album[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [showAlbumForm, setShowAlbumForm] = useState(false);
  const [novoTitulo, setNovoTitulo] = useState('');
  const [novoAno, setNovoAno] = useState<number | ''>('');
  const [novoCapa, setNovoCapa] = useState<File | null>(null);
  const [creatingAlbum, setCreatingAlbum] = useState(false);

  useEffect(() => {
    if (id) {
      carregarAlbuns(Number(id), 0);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const carregarAlbuns = async (artistaId: number, pagina: number = 0) => {
    setLoading(true);
    try {
      const dados: Page<Album> = await albumService.getByArtist(artistaId, pagina);
      setAlbuns(dados.content);
      setPage(dados.number);
      setTotalPages(dados.totalPages);
    } catch (error) {
      console.error('Erro ao carregar álbuns', error);
      alert('Erro ao carregar álbuns');
    } finally {
      setLoading(false);
    }
  };

  const mudarPagina = (novaPagina: number) => {
    if (!id) return;
    if (novaPagina < 0 || novaPagina >= totalPages) return;
    carregarAlbuns(Number(id), novaPagina);
  };
  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <button
        onClick={() => navigate('/')}
        className="mb-4 text-blue-600 hover:underline flex items-center gap-2"
      >
        ← Voltar para Artistas
      </button>

      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900">Discografia</h1>
        <button onClick={() => setShowAlbumForm((s) => !s)} className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">+ Novo Álbum</button>
      </div>

      {showAlbumForm && (
        <form
          onSubmit={async (e) => {
            e.preventDefault();
            if (!id) return;
            if (!novoTitulo || novoTitulo.trim().length === 0) {
              alert('Informe o título');
              return;
            }
            if (!novoAno || Number(novoAno) <= 0) {
              alert('Informe um ano válido');
              return;
            }

            try {
              setCreatingAlbum(true);
              const created = await (await import('../services/albumService')).albumServiceExtras.create(Number(id), novoTitulo.trim(), Number(novoAno));
              if (novoCapa) {
                await (await import('../services/albumService')).albumServiceExtras.uploadCover(created.id, novoCapa);
              }
              setNovoTitulo('');
              setNovoAno('');
              setNovoCapa(null);
              setShowAlbumForm(false);
              carregarAlbuns(Number(id), 0);
            } catch (err) {
              console.error('Erro ao criar álbum', err);
              if (!localStorage.getItem('sessionExpiredMessage')) {
                alert('Erro ao criar álbum. Verifique o backend e o token.');
              }
            } finally {
              setCreatingAlbum(false);
            }
          }}
          className="mb-6 flex flex-col gap-2"
        >
          <input type="text" placeholder="Título" value={novoTitulo} onChange={(e) => setNovoTitulo(e.target.value)} className="p-2 border rounded" />
          <input type="number" placeholder="Ano" value={novoAno as any} onChange={(e) => setNovoAno(e.target.value ? Number(e.target.value) : '')} className="p-2 border rounded" />
          <input type="file" accept="image/*" onChange={(e) => setNovoCapa(e.target.files && e.target.files[0] ? e.target.files[0] : null)} />
          <div className="flex gap-2">
            <button type="submit" disabled={creatingAlbum} className="bg-green-600 text-white px-4 py-2 rounded">{creatingAlbum ? 'Salvando...' : 'Salvar'}</button>
            <button type="button" onClick={() => { setShowAlbumForm(false); setNovoTitulo(''); setNovoAno(''); setNovoCapa(null); }} className="px-3 py-2 border rounded">Cancelar</button>
          </div>
        </form>
      )}

      {loading ? (
        <div className="text-center py-10">Carregando...</div>
      ) : (
        <>
          {albuns.length === 0 ? (
            <div className="text-center text-gray-500 py-10">Nenhum álbum encontrado.</div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
              {albuns.map((album) => (
                <div key={album.id} className="bg-white overflow-hidden rounded-lg transition transform hover:-translate-y-1 hover:shadow-lg border border-gray-200">
                  <div className="group">
                    {album.capaUrl ? (
                      // eslint-disable-next-line jsx-a11y/img-redundant-alt
                      <img src={album.capaUrl} alt={album.titulo} className="w-full h-48 object-cover group-hover:opacity-75" />
                    ) : (
                      <div className="w-full h-48 flex items-center justify-center text-gray-400">Sem Capa</div>
                    )}
                  </div>
                  <div className="p-4">
                    <h3 className="text-lg font-medium text-gray-900">{album.titulo}</h3>
                    <p className="text-sm text-gray-500">{album.ano}</p>
                  </div>
                </div>
              ))}
            </div>
          )}

          <div className="mt-6 flex justify-center items-center gap-3">
            <button onClick={() => mudarPagina(page - 1)} disabled={page === 0} className="px-3 py-1 border rounded disabled:opacity-50">
              Anterior
            </button>
            <span className="text-gray-700">Página {page + 1} de {totalPages}</span>
            <button onClick={() => mudarPagina(page + 1)} disabled={page === totalPages - 1 || totalPages === 0} className="px-3 py-1 border rounded disabled:opacity-50">
              Próxima
            </button>
          </div>
        </>
      )}
    </div>
  );
}

export default ArtistDetails;
