rootProject.name = "delivery"
include("core", "common-proto", "domainservice", "gateway", "grpcapi", "internal-api")
include("domainservice:product")
include("domainservice:order")
include("domainservice:user")
include("domainservice:order")
include("domainservice:core")
include("domainservice:migration")
