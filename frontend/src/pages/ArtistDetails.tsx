import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { albumService, albumServiceExtras } from '../services/albumService';
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
  const [editingAlbumId, setEditingAlbumId] = useState<number | null>(null);
  const [editingCapaFile, setEditingCapaFile] = useState<File | null>(null);
  const [uploadingCapa, setUploadingCapa] = useState<number | null>(null);

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

  const handleEditarCapa = async (albumId: number) => {
    if (!editingCapaFile) {
      alert('Selecione uma imagem');
      return;
    }

    try {
      setUploadingCapa(albumId);
      await albumServiceExtras.updateCover(albumId, editingCapaFile);
      setEditingAlbumId(null);
      setEditingCapaFile(null);
      if (id) {
        carregarAlbuns(Number(id), page);
      }
    } catch (err) {
      console.error('Erro ao atualizar capa', err);
      alert('Erro ao atualizar capa');
    } finally {
      setUploadingCapa(null);
    }
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
              alert('Erro ao criar álbum. Verifique o backend e o token.');
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
        <div>Carregando...</div>
      ) : (
        <>
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {albuns.length === 0 ? (
            <p>Nenhum álbum encontrado para este artista.</p>
          ) : (
            albuns.map((album) => (
              <div key={album.id} className="bg-white rounded-lg shadow overflow-hidden group hover:shadow-lg transition">
                <div className="w-full bg-gray-200 relative">
                  {album.capaUrl ? (
                    <img src={album.capaUrl} alt={album.titulo} className="w-full h-48 object-cover group-hover:opacity-75 transition" />
                  ) : (
                    <div className="w-full h-48 flex items-center justify-center text-gray-400">Sem Capa</div>
                  )}
                  {/* Botão para editar capa */}
                  <button
                    onClick={() => setEditingAlbumId(album.id)}
                    className="absolute top-2 right-2 bg-blue-600 text-white px-3 py-1 rounded text-sm opacity-0 group-hover:opacity-100 transition"
                  >
                    📷 Editar
                  </button>
                </div>

                {/* Formulário de edição de capa */}
                {editingAlbumId === album.id && (
                  <div className="p-2 bg-blue-50 border-t border-blue-200">
                    <div className="flex gap-2 mb-2">
                      <input
                        type="file"
                        accept="image/*"
                        onChange={(e) => setEditingCapaFile(e.target.files?.[0] || null)}
                        className="flex-1 text-xs"
                      />
                    </div>
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleEditarCapa(album.id)}
                        disabled={!editingCapaFile || uploadingCapa === album.id}
                        className="bg-green-600 text-white px-2 py-1 rounded text-xs hover:bg-green-700 disabled:opacity-50"
                      >
                        {uploadingCapa === album.id ? 'Enviando...' : 'Enviar'}
                      </button>
                      <button
                        onClick={() => {
                          setEditingAlbumId(null);
                          setEditingCapaFile(null);
                        }}
                        className="bg-gray-400 text-white px-2 py-1 rounded text-xs hover:bg-gray-500"
                      >
                        Cancelar
                      </button>
                    </div>
                  </div>
                )}

                <div className="p-4">
                  <h3 className="text-lg font-medium text-gray-900">{album.titulo}</h3>
                  <p className="text-sm text-gray-500">{album.ano}</p>
                </div>
              </div>
            ))
          )}
          </div>

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
