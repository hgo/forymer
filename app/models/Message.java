package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import play.db.jpa.Model;

@Entity
public class Message extends Model {

    @Column(updatable = false, length = 150)
    private String messageText;

    @ManyToOne(optional = false)
    private Chatter owner;

    @ManyToOne(optional = false)
    private ChatRoom chatRoom;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreated;

    public Message(String messageText, Chatter owner, ChatRoom chatRoom) {
        super();
        this.messageText = messageText;
        this.owner = owner;
        this.chatRoom = chatRoom;
        this.dateCreated = new Date();
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public Chatter getOwner() {
        return owner;
    }

    public void setOwner(Chatter owner) {
        this.owner = owner;
    }

    public ChatRoom getChatRoom() {
        return chatRoom;
    }

    public void setChatRoom(ChatRoom chatRoom) {
        this.chatRoom = chatRoom;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}
