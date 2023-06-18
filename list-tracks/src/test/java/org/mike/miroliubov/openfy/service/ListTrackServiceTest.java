package org.mike.miroliubov.openfy.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mike.miroliubov.openfy.entity.Track;
import org.mike.miroliubov.openfy.repository.TrackRepository;
import org.mike.miroliubov.openfy.vo.ListTracksRequest;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListTrackServiceTest {
    @InjectMocks
    private ListTrackService listTrackService;

    @Mock
    private TrackRepository trackRepository;

    @Test
    void shouldListTracksByName() {
        // given
        var expectedResult = List.of(Track.builder()
                .name("test")
                .nameHash("tes")
                .author("testAuthor")
                .album("testAlbum")
                .uuid(UUID.randomUUID().toString())
                .build());
        when(trackRepository.findByName("test")).thenReturn(expectedResult);

        // when
        var result = listTrackService.listTracks(new ListTracksRequest("test"));

        // then
        assertThat(result).isSameAs(expectedResult);
    }
}