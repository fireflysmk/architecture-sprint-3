resource "helm_release" "srv-devices" {
  name       = "srv-devices"
  namespace  = "smart-home"
  repository = "oci://cr.yandex/crph729i50ahaneb2gjr/charts/"
  chart      = "srv-devices"
  force_update = true
  version = "1.0.0"  
}
