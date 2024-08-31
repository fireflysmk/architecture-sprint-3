# README

## Arrange

```shell
minikube start --driver=hyperv
```

```shell
kubectl config set-context dev-context `
  --cluster minikube `
  --namespace smart-home `
  --user minikube
```

```shell
kubectl create ns smart-home
kubectl create ns kafka
```

```shell
kubectl create secret docker-registry ghcr-secret -n smart-home `
  --docker-server=ghcr.io `
  --docker-username="" `
  --docker-password="" `
  --docker-email=""
```

```shell
kubectl create secret docker-registry ghcr -n smart-home `
  --docker-server=ghcr.io `
  --docker-username="" `
  --docker-password="" `
  --docker-email=""
```

```shell
echo "$(minikube ip) srv-docs.local" | sudo tee -a /etc/hosts
echo "$(minikube ip) srv-devices.local" | sudo tee -a /etc/hosts
echo "$(minikube ip) smart-home.local" | sudo tee -a /etc/hosts
```

### 3.2
**Helm:**
- Созданы Helm чарты для каждого микросервиса и фронтенда.
- В Helm чартах указаны необходимые ресурсы и зависимости для развертывания.

```shell
cd charts
```

```shell
helm install srv-devices ./srv-devices -n smart-home
helm install srv-telemetry ./srv-telemetry -n smart-home
helm install srv-docs ./srv-docs -n smart-home
helm install smart-home-monolith ./smart-home-monolith -n smart-home
```
### 3.3

**Установка:**
- Istio установлен в Kubernetes кластер.
- Istio работает корректно.

#### Istio Setup

```shell
istioctl install --set profile=demo
```

```shell
cd kube/istio
```

```shell
kubectl label namespace smart-home istio-injection=enabled
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.23/samples/addons/kiali.yaml
```

**Маршрутизация и балансировка:**
- Настроены правила маршрутизации трафика между микросервисами.
- Настроена балансировка нагрузки.

```shell
cd kube
```

```shell
kubectl apply -f smart-home-gateway.yaml
kubectl apply -f smart-home-vs.yaml
kubectl apply -f smart-home-dr.yaml
```

**Мониторинг и трассировка:**
- Настроен мониторинг сетевых запросов (например, с помощью Prometheus).
- Настроена трассировка запросов (например, с помощью Jaeger).

```shell
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.18/samples/addons/prometheus.yaml
kubectl apply -f https://raw.githubusercontent.com/istio/istio/release-1.18/samples/addons/jaeger.yaml
```

**Тестирование:**
- Проверена работоспособность Istio путём отправки запросов к микросервисам.
- Проверено, что Istio правильно маршрутизирует трафик и применяет балансировку нагрузки.

```shell
istioctl dashboard kiali
```

_if running minikube_

```shell
minikube tunnel
```
_or_

```shell
kubectl edit svc istio-ingressgateway -n istio-system
type: NodePort
```

**Get Node Port**

```shell
kubectl get svc istio-ingressgateway -n istio-system
```
**Access Swagger Specs**

> http://smart-home.local:31998/swagger

**Test Add Device**

```shell
curl --location 'http://smart-home.local:31998/api/devices' \
--header 'Content-Type: application/json' \
--data '{
"status": true
}'
```
**Test Get Device**

```shell
curl --location 'http://smart-home.local:31998/api/devices'
```
