package org.mike.miroliubov.openfy.repository;

import com.fasterxml.uuid.Generators;
import io.micronaut.context.event.BeanCreatedEvent;
import io.micronaut.context.event.BeanCreatedEventListener;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mike.miroliubov.openfy.entity.Track;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.model.CreateTableEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.EnhancedGlobalSecondaryIndex;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClientBuilder;
import software.amazon.awssdk.services.dynamodb.model.ProjectionType;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;


@MicronautTest
@TestInstance(PER_CLASS)
@Testcontainers
@Slf4j
class TrackRepositoryIT {
    @Container
    private static final LocalStackContainer LOCAL_STACK_CONTAINER = new LocalStackContainer(
            DockerImageName.parse("localstack/localstack:latest"))
                            .withServices(LocalStackContainer.Service.DYNAMODB);

    @Singleton
    public static class DynamoDbClientBuilderListener implements BeanCreatedEventListener<DynamoDbClientBuilder> {
        @Override
        public DynamoDbClientBuilder onCreated(BeanCreatedEvent<DynamoDbClientBuilder> event) {
            log.info("Substituting DynamoDB client");
            if (!LOCAL_STACK_CONTAINER.isRunning()) {
                LOCAL_STACK_CONTAINER.start();
            }

            return event.getBean().endpointOverride(LOCAL_STACK_CONTAINER.getEndpointOverride(LocalStackContainer.Service.DYNAMODB))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create("dummy", "dummy")));
        }
    }

    @Inject
    private TrackRepository trackRepository;
    @Inject
    private DynamoDbTable<Track> trackTable;
    @Inject
    private DynamoDbClient dynamoDbClient;

    @BeforeEach
    void setUp() {
        if (!tableExists()) {
            trackTable.createTable(
                    CreateTableEnhancedRequest.builder()
                            .globalSecondaryIndices(
                                    EnhancedGlobalSecondaryIndex.builder()
                                            .indexName(Track.NAME_INDEX)
                                            .projection(p -> p.projectionType(ProjectionType.ALL))
                                            .build())
                            .build());

            dynamoDbClient.waiter().waitUntilTableExists(b -> b.tableName(trackTable.tableName()));
        }
    }

    @AfterEach
    void tearDown() {
        // Force delete table after each test. Sometimes it gets deleted by localstack, sometimes - not.
        // This ensures reproducibility.
        try {
            trackTable.deleteTable();
        } catch (ResourceNotFoundException e) {
            // nothing to do here
        }
    }

    private boolean tableExists() {
        try {
            trackTable.describeTable();
            return true;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }

    @Test
    void shouldListAllTracks() {
        // given
        Track track1 = Track.builder()
                .uuid(Generators.timeBasedEpochGenerator().generate().toString())
                .nameHash("all")
                .name("all you need is love")
                .author("Beatles, The")
                .album("Magical Mystery Tour")
                .build();
        Track track2 = Track.builder()
                .uuid(Generators.timeBasedEpochGenerator().generate().toString())
                .nameHash("all")
                .name("all my loving")
                .author("Beatles, The")
                .album("With The Beatles")
                .build();

        trackRepository.save(track1);
        trackRepository.save(track2);

        // when
        var results = trackRepository.findAll();

        // then
        assertThat(results).containsExactlyInAnyOrder(track1, track2);
    }

    @Test
    void shouldFindByName() {
        // given
        Track track1 = Track.builder()
                .uuid(Generators.timeBasedEpochGenerator().generate().toString())
                .nameHash("all")
                .name("all you need is love")
                .author("Beatles, The")
                .album("Magical Mystery Tour")
                .build();
        Track track2 = Track.builder()
                .uuid(Generators.timeBasedEpochGenerator().generate().toString())
                .nameHash("all")
                .name("all my loving")
                .author("Beatles, The")
                .album("With The Beatles")
                .build();

        trackRepository.save(track1);
        trackRepository.save(track2);

        // when
        var results = trackRepository.findByName("all y");

        // then
        assertThat(results).containsExactly(track1);
    }
}