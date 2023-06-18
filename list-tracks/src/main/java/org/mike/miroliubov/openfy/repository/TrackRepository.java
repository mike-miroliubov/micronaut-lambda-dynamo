package org.mike.miroliubov.openfy.repository;

import jakarta.inject.Named;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mike.miroliubov.openfy.entity.Track;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;

import java.util.Collection;
import java.util.List;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class TrackRepository {
    private final DynamoDbTable<Track> trackTable;
    @Named(Track.NAME_INDEX)
    private final DynamoDbIndex<Track> nameIndex;

    public Collection<Track> findByName(String name) {
        if (name.length() < 3) {
            // search only starting 3 letters
            return List.of();
        }

        // build the query
        var query = QueryEnhancedRequest.builder()
                .queryConditional(
                        QueryConditional
                                .sortBeginsWith(Key.builder()
                                        .partitionValue(name.substring(0, 3))
                                        .sortValue(name)
                                        .build()))
                .build();

        // TODO: pagination
        return nameIndex.query(query).stream()
                .flatMap(page -> page.items().stream())
                .toList();
    }

    public Collection<Track> findAll() {
        return trackTable.scan().items().stream().toList();
    }

    public void save(Track track) {
        trackTable.putItem(track);
    }
}
