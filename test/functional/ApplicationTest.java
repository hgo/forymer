package functional;

import java.lang.reflect.Type;
import java.util.HashMap;

import models.ChatRoom;
import models.Chatter;
import models.GeoLocation;

import org.junit.Before;
import org.junit.Test;

import play.Logger;
import play.cache.Cache;
import play.mvc.Http.Response;
import play.test.Fixtures;
import play.test.FunctionalTest;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class ApplicationTest extends FunctionalTest {

    Chatter chatter = null;

    @Before
    public void before() {
        Fixtures.deleteDatabase();
        Cache.clear();
        chatter = new Chatter("test1", GeoLocation.fromDegrees(40.97d, 28.92d));
        chatter.save();
    }

    @Test
    public void testThatIndexPageWorks() {
        Response response = GET("/");
        assertIsOk(response);
        assertContentType("text/html", response);
        assertCharset(play.Play.defaultWebEncoding, response);
    }

    @Test
    public void testLogin() {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("c.name", "test1234");
        map.put("c.latitude", "41");
        map.put("c.longitude", "28.9");
        Response response = POST("/login", map);
        assertIsOk(response);
    }

    @Test
    public void testCreateChatRoom() {
        login("test");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("name", "testChatRoom");
        Response response = POST("/chatroom", map);
        assertStatus(200, response);
        Chatter chatter = Chatter.findByName("test");
        assertNotNull(chatter.getChatRoom());

    }

    @Test
    public void testMessageFailNoChatRoom() {
        login("test");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("c.messageText", "lan olm bu ne hal!");
        Response response = POST("/message", map);
        assertEquals(400, response.status.intValue());
        Type type = new TypeToken<HashMap>() {
        }.getType();
        HashMap map2 = new Gson().fromJson(getContent(response), type);
        assertTrue(Boolean.valueOf(map2.get("isFailed").toString()));
    }

    @Test
    public void testMessageSuccess() {
        login("test");
        Chatter bob = Chatter.findByName("test");
        bob.createChatRoom("testJoinRoom");
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("c.messageText", "lan olm bu ne hal!");
        Response response = POST("/message", map);
        assertStatus(200, response);
        assertEquals(1, Chatter.findByName("test").getChatRoom().getMessages().size());
    }

    @Test
    public void testLeave() {
        login("test");
        Chatter c = Chatter.findByName("test");
        Response response = POST("/leave");
        assertStatus(200, response);
        c.refresh();
        assertEquals(null, c.getChatRoom());
    }

    @Test
    public void testJoin() {
        login("bob");
        Chatter bob = Chatter.findByName("bob");
        ChatRoom c = chatter.createChatRoom("testJoinRoom");
        assertNull(bob.getChatRoom());
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("c.id", c.id + "");
        Response response = POST("/join", map);
        assertStatus(200, response);
        bob.refresh();
        assertEquals(c, bob.getChatRoom());
    }
    
    @Test
    public void testConsume() {
        login("bob");
        Chatter bob = Chatter.findByName("bob");
        ChatRoom c = chatter.createChatRoom("testJoinRoom");
        assertNull(bob.getChatRoom());
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("c.id", c.id + "");
        Response response = POST("/join", map);
        assertStatus(200, response);
        bob.refresh();
        assertEquals(c, bob.getChatRoom());
        map.clear();
        map.put("c.messageText", "lan olm bu ne hal!");
        Response messageResponse = POST("/message", map);
        assertStatus(200, messageResponse);
        map.clear();
        map.put("lastEventSeen", "0");
        Response response2 = POST("/consume",map);
        Logger.info(getContent(response2));
        
    }

    private Response login(String username) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("c.name", username);
        map.put("c.latitude", "41");
        map.put("c.longitude", "28.9");
        return POST("/login", map);
    }

}