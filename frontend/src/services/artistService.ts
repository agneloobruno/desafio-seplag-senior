import { api } from './api';
import { Artist, Page } from '../types/artist';

export const artistService = {
  getAll: async (page: number = 0, nome: string = ''): Promise<Page<Artist>> => {
    const params: any = {
      page,
      size: 10,
      sort: 'nome,asc',
    };

    if (nome && nome.trim().length > 0) params.nome = nome;

    const { data } = await api.get<Page<Artist>>('/v1/artistas', { params });
    return data;
  }
  ,
  create: async (nome: string): Promise<Artist> => {
    const payload = { nome };
    const { data } = await api.post<Artist>('/v1/artistas', payload);
    return data;
  }
  ,
  uploadPhoto: async (artistaId: number, file: File) => {
    const form = new FormData();
    form.append('file', file);
    const { data } = await api.post(`/v1/artistas/${artistaId}/foto`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return data;
  }
};
