
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

variable "timeout" {
  type    = number
  default = 300
}

variable "memory_size" {
  type    = number
  default = 1024
}

variable "target_path" {
  # NOTE: relative to state/main.tf (not this file)
  default = "../../../target"
}

variable "target_name" {
  default = "lacinia-api-0.0.1-SNAPSHOT-standalone.jar"
}

variable "s3_bucket_key" {
  default = "lacinia-api"
}

variable "artifact_version" {
  default = "0.0.1"
}