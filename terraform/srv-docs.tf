resource "helm_release" "srv-docs" {
  name       = "srv-docs"
  namespace  = "smart-home"
  repository = "oci://cr.yandex/crph729i50ahaneb2gjr/charts/"
  chart      = "srv-docs"
  force_update = true
  version = "1.0.0"
}