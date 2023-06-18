resource "aws_dynamodb_table" "tracks_db" {
  name = "tracks-db"
  attribute {
    name = "uuid"
    type = "S"
  }
  attribute {
    name = "name"
    type = "S"
  }
  attribute {
    name = "name_hash"
    type = "S"
  }

  billing_mode = "PAY_PER_REQUEST"
  hash_key = "uuid"

  global_secondary_index {
    name               = "idx-name"
    hash_key           = "name_hash"
    range_key          = "name"
    projection_type    = "ALL"
  }
}