package command;

import play.data.validation.MaxSize;
import play.data.validation.MinSize;
import play.data.validation.Required;

public class MessageCommand extends AbstractCommand {

    @Required
    @MaxSize(140)
    @MinSize(1)
    public String messageText;

    @Override
    public void validate() {
        super.validate();
        if (currentChatter().getChatRoom() == null) {
            addError("chatRoom", "no.chatroom");
        }
    }

}
