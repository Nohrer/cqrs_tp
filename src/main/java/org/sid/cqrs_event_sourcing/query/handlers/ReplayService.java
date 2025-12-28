package org.sid.cqrs_event_sourcing.query.handlers;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.config.EventProcessingConfiguration;
import org.axonframework.eventhandling.TrackingEventProcessor;

//@Service
@AllArgsConstructor
@Slf4j
public class ReplayService {
    private EventProcessingConfiguration eventProcessingConfiguration;

    public void reply(){
        String name="org.sid.cqrs_event_sourcing.query.handlers";
        eventProcessingConfiguration.eventProcessor(name, TrackingEventProcessor.class)
                .ifPresent(processor->{
                    processor.shutDown();
                    processor.resetTokens();
                    processor.start();
                });
    }
}
