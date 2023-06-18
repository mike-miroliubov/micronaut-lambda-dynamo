package org.mike.miroliubov.openfy.config;

import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Value;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import org.mike.miroliubov.openfy.entity.Track;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Factory
public class DynamoDbTableFactory {
    @Singleton
    public DynamoDbEnhancedClient enhancedClient(DynamoDbClient dynamoDbClient) {
        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
    }

    @Singleton
    public DynamoDbTable<Track> trackTable(DynamoDbEnhancedClient enhancedClient,
                                           @Value("${openfy.dynamodb.table.tracks}") String tableName) {
        return enhancedClient.table(tableName, TableSchema.fromImmutableClass(Track.class));
    }

    @Singleton
    @Named(Track.NAME_INDEX)
    public DynamoDbIndex<Track> nameIndex(DynamoDbTable<Track> trackTable) {
        return trackTable.index(Track.NAME_INDEX);
    }
}
