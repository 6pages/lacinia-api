
# API

resource "aws_api_gateway_rest_api" "api" {
  name        = var.name
  description = var.description

  endpoint_configuration {
    types = ["REGIONAL"]
  }

  tags = {
    Environment = var.environment
  }
}


# API Resources

resource "aws_api_gateway_resource" "proxy" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  parent_id   = aws_api_gateway_rest_api.api.root_resource_id
  path_part   = "{proxy+}"
}

resource "aws_api_gateway_method" "proxy" {
  rest_api_id   = aws_api_gateway_rest_api.api.id
  resource_id   = aws_api_gateway_resource.proxy.id
  http_method   = "POST"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "lambda" {
  rest_api_id = aws_api_gateway_rest_api.api.id
  resource_id = aws_api_gateway_method.proxy.resource_id
  http_method = aws_api_gateway_method.proxy.http_method

  integration_http_method = "POST"
  type                    = "AWS_PROXY"
  uri                     = aws_lambda_function.fn.invoke_arn
}


# Permission

resource "aws_lambda_permission" "apigw" {
  statement_id  = "AllowAPIGatewayInvoke"
  action        = "lambda:InvokeFunction"
  function_name = aws_lambda_function.fn.function_name
  principal     = "apigateway.amazonaws.com"

  # The "/*/*" portion grants access from any method on any resource
  # within the API Gateway REST API.
  source_arn = "${aws_api_gateway_rest_api.api.execution_arn}/*/*"
}


# Deployment

resource "aws_api_gateway_deployment" "endpoint" {
  depends_on = [aws_api_gateway_integration.lambda]

  rest_api_id = aws_api_gateway_rest_api.api.id

  stage_name  = var.environment
  stage_description = var.description

  description       = var.deployment_description
}
