terraform {
  required_providers {
    kubernetes = {
      source = "hashicorp/kubernetes"
      version = "2.31.0"
    }
    yandex = {
      source = "yandex-cloud/yandex"
    }    
  }
}

provider "kubernetes" {
  config_path    = "~/.kube/config"
  config_context = "minikube"
}

provider "helm" {
  kubernetes {
    config_path    = "~/.kube/config"
    config_context = "minikube"    
  }
}

provider "yandex" {
  token = var.yc_token
  zone = "ru-central1-a"
}

variable "yc_token" {
  type      = string
  sensitive = true
}