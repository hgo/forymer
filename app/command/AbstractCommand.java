package command;

import models.Chatter;
import play.data.validation.Validation;
import utils.ForymerConstants;

public abstract class AbstractCommand {

    public void validate() {
        Validation.current().valid(this);
    }

    public boolean isClean() {
        return !Validation.current().hasErrors();
    }

    public void addError(String field, String message, String variables) {
        Validation.current().addError(field, message, variables);
    }

    public void addError(String field, String message) {
        addError(field, message, null);
    }

    public Chatter currentChatter() {
        return ForymerConstants.getCurrentChatter();
    }
}
