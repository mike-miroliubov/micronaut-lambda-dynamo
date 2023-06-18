package org.mike.miroliubov.openfy.service;

import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.mike.miroliubov.openfy.entity.Track;
import org.mike.miroliubov.openfy.repository.TrackRepository;
import org.mike.miroliubov.openfy.vo.ListTracksRequest;

import java.util.Collection;

@Slf4j
@Singleton
@RequiredArgsConstructor
public class ListTrackService {
    private final TrackRepository trackRepository;

    public Collection<Track> listTracks(ListTracksRequest listRequest) {
        if (listRequest.name() != null) {
            return trackRepository.findByName(listRequest.name());
        }

        return trackRepository.findAll();
    }
}
