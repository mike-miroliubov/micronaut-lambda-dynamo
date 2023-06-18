package org.mike.miroliubov.openfy.entity;


import io.micronaut.core.annotation.Introspected;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@Introspected
@DynamoDbImmutable(builder = Track.TrackBuilder.class)
@Value
@Builder
public class Track {
    public static final String NAME_INDEX = "idx-name";

    @NonNull
    @Getter(onMethod_=@DynamoDbPartitionKey)
    private final String uuid;

    @NonNull
    @Getter(onMethod_={
            @DynamoDbSecondaryPartitionKey(indexNames = NAME_INDEX),
            @DynamoDbAttribute("name_hash")
    })
    private final String nameHash;
    @NonNull
    @Getter(onMethod_=@DynamoDbSecondarySortKey(indexNames = NAME_INDEX))
    private final String name;

    private final String author;
    private final String album;
}
