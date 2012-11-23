package command;

import models.Chatter;
import play.data.validation.Max;
import play.data.validation.MaxSize;
import play.data.validation.Min;
import play.data.validation.MinSize;
import play.data.validation.Required;

public class LoginCommand extends AbstractCommand {

    @Required
    @MaxSize(30)
    @MinSize(3)
    public String name;

    @Required
    @Min(-90)
    @Max(90)
    public double latitude;

    @Required
    @Min(-180)
    @Max(180)
    public double longitude;

    @Override
    public void validate() {
        super.validate();
        if (isClean()) {
            if (Chatter.findByName(this.name) != null) {
                addError("name", "validation.unique");
            }
        }
    }
}
