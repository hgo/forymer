package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import play.db.jpa.Model;

@Entity
public class ChatRoom extends Model {

    private String name;

    @OneToMany(mappedBy = "chatRoom")
    @Cascade(value = { CascadeType.PERSIST, CascadeType.MERGE })
    private List<Chatter> chatters;

    @OneToMany(mappedBy = "chatRoom")
    @Cascade(value = { CascadeType.DELETE })
    private List<Message> messages;

    @OneToOne(optional = false)
    @Cascade(value = { CascadeType.PERSIST, CascadeType.DELETE })
    private GeoLocation geoLocation;

    public ChatRoom(String name, GeoLocation geoLocation) {
        super();
        this.name = name;
        this.geoLocation = geoLocation;
    }

    public Integer getChatterCount() {
        return ChatRoom.find("select size(chatters) from ChatRoom where id=?", this.id).first();
    }

    public static ChatRoom findByName(String name) {
        return ChatRoom.find("name=?", name).first();
    }

    public List<Chatter> getChatters() {
        return chatters;
    }

    public void setChatters(List<Chatter> chatters) {
        this.chatters = chatters;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }
}
