package controllers;

import static utils.ForymerConstants.getCurrentChatter;

import java.util.List;

import models.ChatRoom;
import models.Chatter;
import models.Message;
import play.Logger;
import play.libs.F.IndexedEvent;
import play.mvc.Before;
import play.mvc.Controller;
import play.mvc.With;
import service.ForymerService;
import utils.ForymerConstants;

import command.CommandedAction;
import command.JoinCommand;
import command.LoginCommand;
import command.MessageCommand;

import flexjson.JSONSerializer;

@With(CommandController.class)
public class Application extends Controller {

    @Before(unless = { "login", "index" }, priority = 0)
    static void before() {
        Logger.info("sessionId: %s", session.getId());
        Chatter chatter = getCurrentChatter();
        if (chatter == null) {
            index();
        }
        ForymerConstants.putCurrentChatter(chatter);
    }

    @CommandedAction(clazz = LoginCommand.class, name = "c")
    public static void login(LoginCommand c) {
        Chatter chatter = ForymerService.createNewChatter(c.name, c.latitude, c.longitude);
        ForymerConstants.putCurrentChatter(chatter);
        ok();
    }

    public static void index() {
        render();
    }

    public static void chatroom(String name) {
        getCurrentChatter().createChatRoom(name);
        ok();
    }

    @CommandedAction(clazz = JoinCommand.class, name = "c")
    public static void join(JoinCommand c) {
        Logger.info("currentUser: %s", getCurrentChatter().getName());
        getCurrentChatter().joinChatRoom((ChatRoom) request.args.get("chatRoom"));
        ok();
    }

    public static void leave() {
        getCurrentChatter().leaveChatRoom();
        ok();
    }

    @CommandedAction(clazz = MessageCommand.class, name = "c")
    public static void message(MessageCommand c) {
        getCurrentChatter().addMessage(c.messageText);
        ok();
    }
    
    public static void consume(long lastEventSeen) {
        List<IndexedEvent<Message>> list = await(getCurrentChatter().getChatRoom().getMessagingStream(false).nextEvents(lastEventSeen));
        renderJSON(new JSONSerializer().include("id","data.messageText","data.owner.name","data.dateCreated").exclude("*").serialize(list));
    }
}