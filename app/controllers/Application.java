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
        chatrooms();
    }

    public static void logout() {
        ForymerConstants.logoutChatter();
        session.clear();
        index();
    }

    public static void chatrooms() {
        List<ChatRoom> rooms = getCurrentChatter().getNearChatRooms(5.0);
        render(rooms);
    }

    public static void index() {
        if (getCurrentChatter() != null)
            chatrooms();
        render();
    }

    public static void chatroom() {
        renderArgs.put("room", getCurrentChatter().getChatRoom());
        render();
    }

    public static void createChatroom(String name) {
        getCurrentChatter().createChatRoom(name);
        chatroom();
    }

    @CommandedAction(clazz = JoinCommand.class, name = "c")
    public static void join(JoinCommand c) {
        Chatter chatter = getCurrentChatter();
        if (chatter.getChatRoom() != null) {
            if (chatter.getChatRoom().id == ((ChatRoom) request.args.get("chatRoom")).id) {
                chatroom();
            }
        }
        getCurrentChatter().joinChatRoom((ChatRoom) request.args.get("chatRoom"));
        chatroom();
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
        if (getCurrentChatter() == null)
            error();
        List<IndexedEvent<Message>> list = await(getCurrentChatter().getChatRoom().getMessagingStream(false).nextEvents(lastEventSeen));
        renderJSON(new JSONSerializer().include("id", "data.messageText", "data.owner.name", "data.dateCreated").exclude("*").serialize(list));
    }
}