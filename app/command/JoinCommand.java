package command;

import models.ChatRoom;
import play.data.validation.Required;

public class JoinCommand extends AbstractCommand {

    @Required
    public Long id;
    
    @Override
    public void validate() {
        super.validate();
        ChatRoom chatRoom = ChatRoom.findById(id);
        if (isClean()) {
            if (chatRoom == null) {
                addError("id", "no.chatroom.found");
            }
        }
        addToRequestArgs("chatRoom", chatRoom);
    }
}
