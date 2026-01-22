import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { albumService } from '../services/albumService';
import { Album } from '../types/album';

export function ArtistDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const [albuns, setAlbuns] = useState<Album[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) {
      carregarAlbuns(Number(id));
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const carregarAlbuns = async (artistaId: number) => {
    try {
      const dados = await albumService.getByArtist(artistaId);
      setAlbuns(dados.content);
    } catch (error) {
      console.error('Erro ao carregar álbuns', error);
      alert('Erro ao carregar álbuns');
    } finally {
      setLoading(false);
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
        <button className="bg-green-600 text-white px-4 py-2 rounded hover:bg-green-700">+ Novo Álbum</button>
      </div>

      {loading ? (
        <div>Carregando...</div>
      ) : (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          {albuns.length === 0 ? (
            <p>Nenhum álbum encontrado para este artista.</p>
          ) : (
            albuns.map((album) => (
              <div key={album.id} className="bg-white rounded-lg shadow overflow-hidden group">
                <div className="w-full bg-gray-200">
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
            ))
          )}
        </div>
      )}
    </div>
  );
}

export default ArtistDetails;
