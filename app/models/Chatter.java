package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import play.Logger;
import play.db.jpa.Model;

@Entity
public class Chatter extends Model {

    private String name;

    @OneToOne(optional = false, orphanRemoval = true)
    @Cascade(value = { CascadeType.PERSIST, CascadeType.DELETE })
    private GeoLocation geoLocation;

    @ManyToOne(optional = true)
    @Cascade({ CascadeType.MERGE, CascadeType.REFRESH })
    private ChatRoom chatRoom;

    public Chatter(String name, GeoLocation geoLocation) {
        super();
        this.name = name;
        this.geoLocation = geoLocation;
    }

    public List<ChatRoom> getNearChatRooms(double distance) {
        GeoLocation location = this.geoLocation;
        GeoLocation[] boundingCoordinates = location.boundingCoordinates(distance, GeoLocation.RADIUS);
        for (GeoLocation bound : boundingCoordinates) {
            Logger.info(bound.getDegLat() + " " + bound.getDegLon());
        }
        boolean meridian180WithinDistance = boundingCoordinates[0].getRadLon() > boundingCoordinates[1].getRadLon();
        return ChatRoom.find(
                "select chatRoom from ChatRoom chatRoom join chatRoom.geoLocation geo " + "where (geo.radLat >= ? and geo.radLat <= ? ) and (geo.radLon >= ? "
                        + (meridian180WithinDistance ? "OR" : "AND") + " geo.radLon <= ? ) and "
                        + "(acos(sin(?) * sin(geo.radLat) + cos(?) * cos(geo.radLat) * cos(geo.radLon - ?)) <= ?)",
                boundingCoordinates[0].getRadLat(), boundingCoordinates[1].getRadLat(), boundingCoordinates[0].getRadLon(), boundingCoordinates[1].getRadLon(),
                location.getRadLat(), location.getRadLat(), location.getRadLon(), distance / GeoLocation.RADIUS).fetch();
    }

    public ChatRoom createChatRoom(String roomName) {
        ChatRoom chatRoom = new ChatRoom(roomName, (GeoLocation) GeoLocation.fromDegrees(this.geoLocation.getDegLat(), this.geoLocation.getDegLon()).save());
        chatRoom = chatRoom.save();
        this.chatRoom = chatRoom;
        this.save();
        chatRoom = chatRoom.refresh();
        chatRoom.getMessagingStream(true);
        return chatRoom;
    }
    

    public ChatRoom joinChatRoom(ChatRoom chatRoom) {
        if (this.chatRoom != null) {
            leaveChatRoom();
        }
        this.chatRoom = chatRoom;
        this.save();
        return this.chatRoom.refresh();
    }

    public ChatRoom leaveChatRoom() {
        if (this.chatRoom == null) {
            return null;
        }
        Long chatRoomId = this.chatRoom.id;
        this.chatRoom = null;
        this.save();
        ChatRoom chatRoom = ((ChatRoom) ChatRoom.findById(chatRoomId));
        if (chatRoom.getChatterCount() == 0) {
            chatRoom.delete();
            return null;
        }
        return chatRoom;
    }

    public Message addMessage(String messageText) {
        Message message =new Message(messageText, this, this.chatRoom).save();
        this.chatRoom.getMessagingStream(false).publish(message);
        return message;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GeoLocation getGeoLocation() {
        return geoLocation;
    }

    public void setGeoLocation(GeoLocation geoLocation) {
        this.geoLocation = geoLocation;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public static Chatter findByName(String chatterName) {
        return Chatter.find("byName", chatterName).first();
    }
}
