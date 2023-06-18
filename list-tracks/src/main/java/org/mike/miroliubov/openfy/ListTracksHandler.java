package org.mike.miroliubov.openfy;

import com.amazonaws.services.lambda.runtime.Context;
import io.micronaut.context.BeanProvider;
import io.micronaut.context.annotation.Any;
import io.micronaut.function.aws.MicronautRequestHandler;
import jakarta.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.mike.miroliubov.openfy.service.ListTrackService;
import org.mike.miroliubov.openfy.vo.ListTracksRequest;
import org.mike.miroliubov.openfy.vo.ListTrackResult;

@Slf4j
public class ListTracksHandler extends MicronautRequestHandler<ListTracksRequest, ListTrackResult> {
    @Inject
    private ListTrackService listTrackService;
    @Any
    private BeanProvider<Context> contextProvider;

    @Override
    public ListTrackResult execute(ListTracksRequest input) {
        Context context = contextProvider.get();
        log.info("Received request [{}] [{}, {}}]", context.getAwsRequestId(), input, context);
        var result = listTrackService.listTracks(input);
        log.info("Finished request [{}] processing: {}", context.getAwsRequestId(), result);
        var vos = result.stream().map(t -> new ListTrackResult.TrackVo(t.getName(), t.getAuthor())).toList();
        return new ListTrackResult(vos);
    }
}
