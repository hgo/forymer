package command;

import models.Chatter;
import play.data.validation.Validation;
import play.mvc.Http.Request;
import utils.ForymerConstants;

public abstract class AbstractCommand {

    public void validate() {
        Validation.current().valid(this);
    }

    protected final boolean isClean() {
        return !Validation.current().hasErrors();
    }

    protected final void addError(String field, String message, String variables) {
        Validation.current().addError(field, message, variables);
    }

    protected final void addError(String field, String message) {
        addError(field, message, null);
    }

    protected final Chatter currentChatter() {
        return ForymerConstants.getCurrentChatter();
    }
    
    protected final void addToRequestArgs(String key, Object arg){
        Request.current().args.put(key, arg);
    }
}
