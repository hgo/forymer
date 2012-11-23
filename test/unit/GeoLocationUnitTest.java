package unit;

import models.GeoLocation;

import org.junit.Before;
import org.junit.Test;

import play.test.Fixtures;
import play.test.UnitTest;

public class GeoLocationUnitTest extends UnitTest {

    @Before
    public void before() {
        Fixtures.deleteDatabase();
    }

    @Test
    public void testBoundingCoordinates() {
        // 03:48:46,782 INFO ~ 40.960303990283265 28.917374683787507
        // 03:48:46,783 INFO ~ 41.05023600971674 29.03654531621249
        GeoLocation location = GeoLocation.fromDegrees(41.00527, 28.97696);
        GeoLocation[] boundingCoordinates = location.boundingCoordinates(5d, GeoLocation.RADIUS);
        GeoLocation bound = boundingCoordinates[0];
        assertEquals(bound.getDegLat(), 40.960303990283265, 0.000001d);
        assertEquals(bound.getDegLon(), 28.917374683787507, 0.000001d);
        bound = boundingCoordinates[1];
        assertEquals(bound.getDegLat(), 41.05023600971674, 0.000001d);
        assertEquals(bound.getDegLon(), 29.03654531621249, 0.000001d);
    }
}
