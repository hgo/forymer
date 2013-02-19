package utils;

import models.Chatter;
import play.cache.Cache;
import play.mvc.Http.Request;
import play.mvc.Scope.Session;

public class ForymerConstants {

    private final static String CHATTER_REQUEST_ARG_KEY = "chatter";
    
    public static Chatter getCurrentChatter() {
        Chatter c = (Chatter) Request.current().args.get(CHATTER_REQUEST_ARG_KEY);
        if (c == null) {
            Long id = (Long) Cache.get(Session.current().getId());
            if (id != null) {
                Chatter chatter = Chatter.findById(id);
                Request.current().args.put(CHATTER_REQUEST_ARG_KEY, chatter);
                return chatter;
            }
        }
        return c;
    }

    // comes from logger
    public static void putCurrentChatter(Chatter chatter) {
        Cache.set(Session.current().getId(), chatter.id, "1h");
    }
    
    public static void logoutChatter() {
        Cache.delete(Session.current().getId());
    }
}
