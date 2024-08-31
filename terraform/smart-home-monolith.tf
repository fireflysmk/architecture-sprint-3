resource "helm_release" "smart-home-monolith" {
  name = "smart-home-monolith"
  namespace = "smart-home"
  repository = "oci://cr.yandex/crph729i50ahaneb2gjr/charts/"
  chart = "smart-home-monolith"
}