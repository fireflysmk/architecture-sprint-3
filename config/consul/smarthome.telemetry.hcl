service {
  name = "srv-telemetry"
  id = "srv-telemetry-1"
  tags = ["primary"]

  address = "172.24.0.11"
  port = 8080

  check {
    id = "srv-telemetry-check"
    name = "HTTP Check on port 8080"
    http = "http://172.24.0.11:8080/api/health"
    method = "GET"
    interval = "10s"
    timeout = "1s"
  }
}
