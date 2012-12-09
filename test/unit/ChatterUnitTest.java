package unit;

import java.util.List;

import models.ChatRoom;
import models.Chatter;
import models.GeoLocation;
import models.Message;

import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.test.Fixtures;
import play.test.UnitTest;

public class ChatterUnitTest extends UnitTest {

    Chatter chatter = null;

    @Before
    public void before() {
        Fixtures.deleteDatabase();
        chatter = new Chatter("test1", GeoLocation.fromDegrees(40.97d, 28.92d));
        chatter.save();
    }

    @Test
    public void testNearChatRooms() {
        chatter.createChatRoom("room1");
        Chatter chatter2 = new Chatter("test2", GeoLocation.fromDegrees(41d, 28.9d));
        chatter2.save();
        List<ChatRoom> list = chatter.getNearChatRooms(5d);
        assertEquals(1, list.size());
        assertEquals("room1", list.get(0).getName());
    }

    @Test
    public void testCreateChatRoom() {
        ChatRoom room = chatter.createChatRoom(chatter.getName() + "'s Room");
        assertTrue(room.getChatters().size() == 1);
    }

    @Test
    public void testJoinChatRoom() {
        ChatRoom room = chatter.createChatRoom(chatter.getName() + "'s Room");

        Chatter chatter2 = new Chatter("test2", GeoLocation.fromDegrees(41d, 28.9d));
        chatter2.save();
        room = chatter2.joinChatRoom(room);
        assertTrue(room.getChatters().size() == 2);

        chatter2.refresh();
        room = chatter2.leaveChatRoom();
        assertTrue(room.getChatters().size() == 1);
        assertEquals(chatter, room.getChatters().get(0));

        room = chatter.leaveChatRoom();
        assertEquals(null, room);
    }

    @Test
    public void testLeaveChatRoom() {
        ChatRoom room = chatter.createChatRoom(chatter.getName() + "'s Room");
        Chatter chatter2 = new Chatter("test2", GeoLocation.fromDegrees(41d, 28.9d));
        chatter2.save();
        room = chatter2.joinChatRoom(room);
        assertTrue(room.getChatters().size() == 2);

        chatter2.refresh();
        room = chatter2.leaveChatRoom();
        assertTrue(room.getChatters().size() == 1);
        assertEquals(chatter, room.getChatters().get(0));
    }

    @Test
    public void testLeaveChatRoomReturnNull() {
        ChatRoom room = chatter.createChatRoom(chatter.getName() + "'s Room");

        Chatter chatter2 = new Chatter("test2", GeoLocation.fromDegrees(41d, 28.9d));
        chatter2.save();
        room = chatter2.joinChatRoom(room);
        assertTrue(room.getChatters().size() == 2);

        chatter2.refresh();
        room = chatter2.leaveChatRoom();
        assertTrue(room.getChatters().size() == 1);
        assertEquals(chatter, room.getChatters().get(0));

        room = chatter.leaveChatRoom();
        assertEquals(null, room);
    }

    @Test
    public void testAddMessage() {
        ChatRoom room = chatter.createChatRoom(chatter.getName() + "'s Room - addToMessage");

        Chatter chatter2 = new Chatter("test2", GeoLocation.fromDegrees(41d, 28.9d));
        chatter2.save();

        room = chatter2.joinChatRoom(room);
        assertTrue(room.getChatters().size() == 2);

        chatter2.refresh();
        Message message = chatter.addMessage("naber lan!");
        message.refresh();
        assertEquals("naber lan!", message.getMessageText());
    }

    @Test
    public void testAddMessageToInvalidRoom() {
        try {
            chatter.addMessage("naber lan!");
            fail();
        } catch (Exception e) {
            String jdbcError = e.getCause().getCause().toString();
            Logger.error(jdbcError);
            boolean b = jdbcError.contains("MESSAGE FOREIGN KEY(CHATROOM_ID)") && jdbcError.contains("CHATROOM(ID) (0)");
            assertEquals(true, b);
        }
    }

}
