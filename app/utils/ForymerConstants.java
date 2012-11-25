package utils;

import models.Chatter;
import play.cache.Cache;
import play.mvc.Http.Request;
import play.mvc.Scope.Session;
import service.CacheService;

public class ForymerConstants {

    private final static String CHATTER_REQUEST_ARG_KEY = "chatter";

    public static Chatter getCurrentChatter() {
        Chatter c = (Chatter) Request.current().args.get(CHATTER_REQUEST_ARG_KEY);
        if (c == null) {
            Long id = (Long) Cache.get(Session.current().getId());
            if (id != null) {
                return Chatter.findById(id);
            }
        }
        return c;
    }

    // comes from logger
    public static void putCurrentChatter(Chatter chatter) {
        CacheService.getInstance().updateOrPut(Session.current().getId(), chatter.id, Long.class, "1h");
    }
}
