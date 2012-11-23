package command;

import models.ChatRoom;
import play.data.validation.Required;

public class JoinCommand extends AbstractCommand {

    @Required
    public Long id;

    @Override
    public void validate() {
        super.validate();
        if (isClean()) {
            if (ChatRoom.findById(id) == null) {
                addError("id", "no.chatroom.found");
            }
        }
    }
}
