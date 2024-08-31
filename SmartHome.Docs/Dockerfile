FROM python:3.9-slim AS build

LABEL org.opencontainers.image.source=https://github.com/synaxintelligence/smart-home

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

RUN mkdocs build

FROM nginx:alpine
COPY --from=build /app/site /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
