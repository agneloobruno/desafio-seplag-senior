# Estágio 1: Build
FROM node:20-alpine as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Estágio 2: Servidor Nginx (Produção)
FROM nginx:alpine
# Copia o build gerado no estágio anterior
COPY --from=build /app/dist /usr/share/nginx/html
# Configuração customizada do Nginx para suportar React Router
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]