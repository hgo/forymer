package models;

import java.io.Serializable;
import java.util.List;

import play.libs.F.ArchivedEventStream;
import play.libs.F.IndexedEvent;
import play.libs.F.Promise;

public class MessagingStream implements Serializable {

    private final ArchivedEventStream<Message> messages = new ArchivedEventStream<Message>(1000);

    public Promise<List<IndexedEvent<Message>>> nextEvents(long lastEventSeen) {
        return messages.nextEvents(lastEventSeen);
    }

    public void publish(Message event) {
        messages.publish(event);
    }
    
}
