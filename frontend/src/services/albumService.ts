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
