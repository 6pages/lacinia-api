
variable "environment" {
  type = string
}

variable "name" {
  type    = string
  default = "graphql-api"
}

variable "description" {
  type    = string
  default = "Responds to GraphQL queries."
}

variable "deployment_description" {
  type    = string
  default = "first deployment."
}

variable "source_path" {
  default = "../../.."
}

variable "target_path" {
  default = "target/lacinia-api-0.0.1-SNAPSHOT-standalone.jar"
}