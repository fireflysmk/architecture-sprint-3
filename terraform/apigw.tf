
resource "kubernetes_deployment" "hello_world" {
  metadata {
    name      = "hello-world"
    namespace = "smart-home"
    labels = {
      app = "hello-world"
    }
  }

  spec {
    replicas = 1

    selector {
      match_labels = {
        app = "hello-world"
      }
    }

    template {
      metadata {
        labels = {
          app = "hello-world"
        }
      }

      spec {
        container {
          name  = "hello-world"
          image = "kubeshop/kusk-hello-world:v1.0.0"
        }
      }
    }
  }
}

resource "kubernetes_service" "hello_world_svc" {
  metadata {
    name      = "hello-world-svc"
    namespace = "smart-home"
  }

  spec {
    selector = {
      app = "hello-world"      
    }

    port {
      port        = 8080
      target_port = 8080      
    }
  }
}
