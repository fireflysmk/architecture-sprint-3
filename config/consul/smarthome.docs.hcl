service {
  name = "srv-docs"
  id = "srv-docs-1"
  tags = ["primary"]

  address = "172.24.0.9"
  port = 80

  check {
    id = "srv-docs-check"
    name = "HTTP Check on port 80"
    http = "http://172.24.0.9:80"
    method = "GET"
    interval = "10s"
    timeout = "1s"
  }
}
