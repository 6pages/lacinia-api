


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

module "lambda_graphql_api" {
  source         = "../../modules/lacinia-api/lambda"
  
  environment    = local.environment
  name           = "graphql-api-${local.environment}"
  description    = "[${local.environment}] Responds to GraphQL queries."
}