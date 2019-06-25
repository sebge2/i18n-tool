package be.sgerard.i18n.service.event;

import be.sgerard.i18n.model.event.EventType;

/**
 * @author Sebastien Gerard
 */
public interface InternalEventListener<O> {

    boolean support(EventType eventType);

    void onEvent(O event);
}
