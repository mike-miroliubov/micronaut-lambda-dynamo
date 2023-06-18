resource "aws_iam_role" "list_tracks_lambda_role" {
  name = "openfy-list-tracks-lambda-role"
  assume_role_policy = jsonencode({
    Version = "2012-10-17"
    Statement = [
      {
        Action = "sts:AssumeRole"
        Effect = "Allow"
        Sid = ""
        Principal = {
          Service = "lambda.amazonaws.com"
        }
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "basic_access" {
  role = aws_iam_role.list_tracks_lambda_role.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

resource "aws_iam_policy" "dynamo_access" {
  name = "dynamo_access"
  policy = jsonencode({
    Version = "2012-10-17",
    Statement = [
      {
        Effect = "Allow",
        Action = [
          "dynamodb:GetItem",
          "dynamodb:PutItem",
          "dynamodb:Scan",
          "dynamodb:Query",
          "dynamodb:UpdateItem"
        ],
        Resource = aws_dynamodb_table.tracks_db.arn
      },
      {
        Effect = "Allow",
        Action = [
          "dynamodb:Scan",
          "dynamodb:Query"
        ],
        Resource = "${aws_dynamodb_table.tracks_db.arn}/index/*"
      }
    ]
  })
}

resource "aws_iam_role_policy_attachment" "dynamo_access" {
  policy_arn = aws_iam_policy.dynamo_access.arn
  role       = aws_iam_role.list_tracks_lambda_role.name
}