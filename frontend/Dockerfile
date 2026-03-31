# Stage 1: Build the static web app
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
# npx expo export --platform web produces a 'dist' folder by default
RUN npx expo export --platform web

# Stage 2: Serve the static files with Nginx
FROM nginx:alpine
# Copy the built files from Stage 1 to Nginx's default public directory
COPY --from=build /app/dist /usr/share/nginx/html
# Custom Nginx config to handle single page app routing if needed
# COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
