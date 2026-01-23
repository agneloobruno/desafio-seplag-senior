import { api } from './api';
import { Album } from '../types/album';
import { Page } from '../types/artist';

export const albumService = {
  getByArtist: async (artistaId: number, page: number = 0): Promise<Page<Album>> => {
    const params = { page, size: 8, sort: 'ano,desc', artistaId };
    const { data } = await api.get<Page<Album>>('/v1/albuns', { params });
    return data;
  }
};

export const albumServiceExtras = {
  create: async (artistaId: number, titulo: string, ano: number) => {
    const payload = { artistaId, titulo, ano };
    const { data } = await api.post('/v1/albuns', payload);
    return data;
  },

  uploadCover: async (albumId: number, file: File) => {
    const form = new FormData();
    form.append('file', file);
    const { data } = await api.post(`/v1/albuns/${albumId}/capa`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return data;
  },

  updateCover: async (albumId: number, file: File) => {
    const form = new FormData();
    form.append('file', file);
    const { data } = await api.put(`/v1/albuns/${albumId}/capa`, form, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
    return data;
  }
};
