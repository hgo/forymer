package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.PreRemove;

import net.sf.oval.guard.Pre;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import play.cache.Cache;
import play.db.jpa.Model;

@Entity
public class ChatRoom extends Model {

    private static final String CACHE_SUFFIX = "_ch";

    private transient Integer chatterCount = null;
    
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
    
    private String cacheName(){
        return this.id + CACHE_SUFFIX;
    }
    
    public MessagingStream getMessagingStream(boolean createIfNotExists) {
        String cacheName = cacheName();
        MessagingStream messagingStream = Cache.get(cacheName, MessagingStream.class);
        if (messagingStream == null && createIfNotExists) {
            messagingStream = new MessagingStream();
            Cache.add(cacheName, messagingStream);
        }
        return messagingStream;
    }
    
    @PreRemove
    void preRemove(){
        Cache.delete(cacheName());
    }
    @PostLoad
    void postLoad(){
        getChatterCount();
    }
    
    public void setChatterCount(Integer chatterCount) {
        this.chatterCount = chatterCount;
    }
    public Integer getChatterCount() {
        if(chatterCount == null){
            setChatterCount((Integer) ChatRoom.find("select size(chatters) from ChatRoom where id=?", this.id).first());
        }
        return this.chatterCount;
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
