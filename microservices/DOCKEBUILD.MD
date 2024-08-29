docker build . -t ghcr.io/foxgooodman/device-service:latest

echo access-token | docker login ghcr.io -u foxgooodman --password-stdin

docker push ghcr.io/foxgooodman/device-service:latest