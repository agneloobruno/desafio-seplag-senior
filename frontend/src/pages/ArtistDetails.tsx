import { useEffect, useState, useRef } from 'react';
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
  const [novoCapaConverting, setNovoCapaConverting] = useState(false);
  const [editingAlbumId, setEditingAlbumId] = useState<number | null>(null);
  const [editingCapaFile, setEditingCapaFile] = useState<File | null>(null);
  const [uploadingCapa, setUploadingCapa] = useState<number | null>(null);
  const [editingCapaConverting, setEditingCapaConverting] = useState(false);

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

  // Converte HEIC/HEIF para JPEG no cliente antes de enviar
  const convertIfHeicToJpeg = async (file: File | null, setConverting?: (v: boolean) => void): Promise<File | null> => {
    if (!file) return null;
    const lower = file.name.toLowerCase();
    const isHeic = file.type === 'image/heic' || lower.endsWith('.heic') || lower.endsWith('.heif');
    if (!isHeic) return file;
    if (setConverting) setConverting(true);
    try {
      const module = await import('heic2any');
      const heic2any = module.default ?? module;
      const convertedBlob = await heic2any({ blob: file, toType: 'image/jpeg', quality: 0.9 });
      const jpgFile = new File([convertedBlob], file.name.replace(/\.heic$/i, '.jpg').replace(/\.heif$/i, '.jpg'), { type: 'image/jpeg' });
      return jpgFile;
    } catch (err) {
      console.error('Erro convertendo HEIC', err);
      alert('Não foi possível converter o arquivo HEIC. Converta para JPG/PNG e tente novamente.');
      return null;
    } finally {
      if (setConverting) setConverting(false);
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
    <div className="min-h-screen bg-gray-50 p-8 dark:bg-neutral-900 dark:text-white">
      <button
        onClick={() => navigate('/')}
        className="mb-4 text-blue-600 hover:underline flex items-center gap-2"
      >
        ← Voltar para Artistas
      </button>

      <div className="flex justify-between items-center mb-6">
        <h1 className="text-3xl font-bold text-gray-900 dark:text-white">Discografia</h1>
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
          <input
            type="file"
            accept="image/*"
            onChange={async (e) => {
              const file = e.target.files && e.target.files[0] ? e.target.files[0] : null;
              const converted = await convertIfHeicToJpeg(file, setNovoCapaConverting);
              setNovoCapa(converted);
            }}
          />
          <div className="flex gap-2">
            <button type="submit" disabled={creatingAlbum || novoCapaConverting} className="bg-green-600 text-white px-4 py-2 rounded">{creatingAlbum ? 'Salvando...' : novoCapaConverting ? 'Convertendo...' : 'Salvar'}</button>
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
            <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-6 gap-1">
              {albuns.map((album) => (
                <div key={album.id} className="flex justify-center">
                  <div className="group cursor-default rounded-xl overflow-hidden border border-gray-200 bg-white" style={{ width: 140 }}>
                    <div className="relative">
                      {album.capaUrl ? (
                        <img src={album.capaUrl} alt={album.titulo} className="w-full h-32 rounded-md object-cover" />
                      ) : (
                        <div className="w-full h-32 flex items-center justify-center text-gray-400">Sem Capa</div>
                      )}
                      {isAdmin && (
                        <div className="absolute top-2 right-2 z-10">
                          <input
                            ref={(el) => (fileInputsRef.current[album.id] = el)}
                            type="file"
                            accept="image/*"
                            className="hidden"
                            onChange={async (e) => {
                              const file = e.target.files && e.target.files[0];
                              if (!file) return;
                              try {
                                await (await import('../services/albumService')).albumServiceExtras.uploadCover(album.id, file);
                                carregarAlbuns(Number(id), page);
                              } catch (err) {
                                console.error('Erro ao enviar capa', err);
                                alert('Falha ao enviar capa. Verifique o backend e o token.');
                              }
                            }}
                          />
                          <button
                            onClick={(ev) => { ev.stopPropagation(); fileInputsRef.current[album.id]?.click(); }}
                            className="bg-white/80 hover:bg-white text-gray-800 px-2 py-1 rounded shadow text-xs"
                            title="Adicionar/Alterar capa"
                          >
                            Foto
                          </button>
                        </div>
                      )}
                      <div className="absolute bottom-1 right-1 opacity-0 group-hover:opacity-100 transition-opacity duration-150">
                        <button
                          onClick={(ev) => ev.stopPropagation()}
                          aria-label={`Tocar ${album.titulo}`}
                          className="bg-[#1DB954] p-1.5 rounded-full shadow text-white flex items-center justify-center ring-2 ring-white hover:scale-105 transform transition-transform duration-150"
                        >
                          <svg xmlns="http://www.w3.org/2000/svg" className="h-3 w-3" viewBox="0 0 24 24" fill="currentColor">
                            <path d="M3 22v-20l18 10-18 10z" />
                          </svg>
                        </button>
                      </div>
                    </div>
                    <div className="mt-2 px-1 pb-2 text-left">
                      <div className="text-sm font-medium text-gray-900 truncate">{album.titulo}</div>
                      <div className="text-xs text-gray-500">{album.ano} • Álbum</div>
                    </div>
                  </div>
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
                        onChange={async (e) => {
                          const file = e.target.files?.[0] || null;
                          const converted = await convertIfHeicToJpeg(file, setEditingCapaConverting);
                          setEditingCapaFile(converted);
                        }}
                        className="flex-1 text-xs"
                      />
                    </div>
                    <div className="flex gap-2">
                      <button
                        onClick={() => handleEditarCapa(album.id)}
                        disabled={!editingCapaFile || uploadingCapa === album.id || editingCapaConverting}
                        className="bg-green-600 text-white px-2 py-1 rounded text-xs hover:bg-green-700 disabled:opacity-50"
                      >
                        {uploadingCapa === album.id ? 'Enviando...' : editingCapaConverting ? 'Convertendo...' : 'Enviar'}
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
              ))}
            </div>
          )}
        </>
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
    </div>
  );
}

export default ArtistDetails;
