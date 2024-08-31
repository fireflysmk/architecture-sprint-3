service {
  name = "srv-devices"
  id = "srv-devices-1"
  tags = ["primary"]

  address = "http://172.24.0.10"
  port = 8080

  check {
    id = "srv-devices-check"
    name = "HTTP Check on port 8080"
    http = "http://172.24.0.10:8080/api/health"
    method = "GET"
    interval = "10s"
    timeout = "1s"
  }
}
