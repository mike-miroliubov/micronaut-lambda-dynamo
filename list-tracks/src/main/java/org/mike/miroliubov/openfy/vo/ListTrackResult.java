package org.mike.miroliubov.openfy.vo;

import java.util.Collection;

public record ListTrackResult(Collection<TrackVo> tracks) {
    public record TrackVo(String name, String author) {};
}
