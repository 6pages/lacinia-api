
#
# GraphQL API

resource "aws_cloudwatch_log_group" "group" {
   name = "/aws/lambda/${aws_lambda_function.fn.function_name}"
   tags = {
    Environment = var.environment
  }
}



#
## IAM

resource "aws_iam_role_policy" "policy" {
  name = var.name
  role = aws_iam_role.role.id

  policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
         "logs:CreateLogGroup",
         "logs:CreateLogStream",
         "logs:PutLogEvents"
      ],
      "Resource": [
         "arn:aws:logs:*:*:*"
      ]
    }
  ]
}
EOF
}



resource "aws_iam_role" "role" {
  name = var.name

  tags = {
    Environment = var.environment
  }

  assume_role_policy = <<EOF
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Action": "sts:AssumeRole",
      "Principal": {
        "Service": "lambda.amazonaws.com"
      },
      "Effect": "Allow"
    }
  ]
}
EOF
}



#
## Lambda

locals {
  target       = "${var.target_path}/${var.target_name}"
}

data "aws_region" "current" {}


resource "aws_lambda_function" "fn" {
  function_name    = var.name
  role             = aws_iam_role.role.arn
  
  runtime          = "java11"
  timeout          = var.timeout
  memory_size      = var.memory_size

  handler          = "com.sixpages.lacinia-api.lambda.handler"
  description      = var.description

  s3_bucket        = var.build_artifacts_bucket
  s3_key           = "${var.s3_bucket_key}/${var.artifact_version}/${target_name}"
  source_code_hash = filebase64sha256(local.target)

  environment {
    variables = {
      environment               = var.environment
      aws_region                = data.aws_region.current.name
    }
  }

  tags = {
    Environment = var.environment
  }
}