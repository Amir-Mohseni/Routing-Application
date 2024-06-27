import org.junit.Test;

public class GraphHopperTest {
    @Test
    public void testCalculateRoute() {
        GraphHopperUtil graphHopperUtil = new GraphHopperUtil();
        try {
            //13 minutes 11 seconds 1084.9943657550336 meters
            assert graphHopperUtil.calculateRoute("6229EN", "6229HD", "foot").distance() == 1084.9943657550336;
            //3 minutes 53 seconds 1145.8430401810058 meters
            assert graphHopperUtil.calculateRoute("6229EN", "6229HD", "bike").distance() == 1145.8430401810058;
            //2 minutes 11 seconds 1310.7785604229407 meters
            assert graphHopperUtil.calculateRoute("6229EN", "6229HD", "car").distance() == 1310.7785604229407;
        } catch (Exception e) {
            assert false;
        }
    }
}
