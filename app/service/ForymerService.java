package service;

import models.Chatter;
import models.GeoLocation;
import models.Message;

public class ForymerService {

    static CacheService cacheService = CacheService.getInstance();

    public static void setLocation(double lng, double lat, Chatter c) {
        c.setGeoLocation(GeoLocation.fromDegrees(lat, lng));
        c.save();
    }

    public static Chatter createNewChatter(String name, double latitude, double longitude) {
        Chatter c = new Chatter(name, GeoLocation.fromDegrees(latitude, longitude));
        return c.save();
    }

    public static Message addMessage(Chatter chatter, String messageText) {
        return chatter.addMessage(messageText);
    }
}
