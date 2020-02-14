


#
# AWS provider

provider "aws" {
   profile   = "6Pages"
   region    = "us-east-1"
}



#
# local vars

locals {
  environment            = "dev"
}



#
# graphql api

module "graphql_api" {
  source         = "../../module/lacinia-api"
  environment    = local.environment
  name           = "graphql-api-${local.environment}"
  description    = "[${local.environment}] Responds to GraphQL queries."
}