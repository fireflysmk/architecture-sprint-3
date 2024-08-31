resource "helm_release" "srv-telemetry" {
  name       = "srv-telemetry"
  namespace  = "smart-home"
  repository = "oci://cr.yandex/crph729i50ahaneb2gjr/charts/"
  chart      = "srv-telemetry"
  force_update = true
  version = "1.0.0"
}