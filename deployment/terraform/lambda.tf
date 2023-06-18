locals {
  jar_location = "../../list-tracks/build/libs/list-tracks-0.1-all.jar"
}

resource "aws_lambda_function" "list_tracks" {
  function_name = "openfy-list-tracks"
  role = aws_iam_role.list_tracks_lambda_role.arn
  architectures = ["x86_64"]
  filename = local.jar_location
  handler = "org.mike.miroliubov.openfy.ListTracksHandler"
  memory_size = 1024
  package_type = "Zip"
  runtime = "java17"
  timeout = 120
  source_code_hash = base64sha256(local.jar_location)

  environment {
    variables = {
      OPENFY_DYNAMODB_TABLE_TRACKS = aws_dynamodb_table.tracks_db.name
    }
  }
}

resource "aws_cloudwatch_log_group" "list_tracks" {
  name              = "/aws/lambda/${aws_lambda_function.list_tracks.function_name}"
  retention_in_days = 14
}