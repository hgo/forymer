package controllers;

import static controllers.util.CustomJSONRenderer.jsonBuilder;
import play.mvc.Before;
import play.mvc.Controller;

import command.CommandedAction;

public class CommandController extends Controller {

    @Before(priority = 1)
    static void before() {
        CommandedAction annotation = getActionAnnotation(CommandedAction.class);
        if (annotation != null) {
            command.AbstractCommand command = params.get(annotation.name(), annotation.clazz());
            command.validate();
            if (validation.hasErrors()) {
                if(!request.isAjax()){
                    String from = params.get("from");
                    validation.keep();
                    redirect(from);
                }
                jsonBuilder().buildAndRenderError();
            }
        }
    }
}
