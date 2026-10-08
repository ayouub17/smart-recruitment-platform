# Etape 1: Build de l'application avec Node
FROM node:20-alpine AS build

WORKDIR /app

# Copier les fichiers de dépendances et installer
COPY package.json package-lock.json* ./
RUN npm install

# Copier le code source et builder
COPY . .
RUN npm run build

# Etape 2: Serveur web léger avec Nginx
FROM nginx:alpine

# Remplacer la configuration par défaut de Nginx pour gérer le routage React
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copier les fichiers statiques générés lors de l'étape 1
COPY --from=build /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
