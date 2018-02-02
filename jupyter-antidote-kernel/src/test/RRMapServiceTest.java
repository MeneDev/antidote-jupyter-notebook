import eu.antidote.jupyter.antidote.crdt.*;
import eu.antidotedb.client.*;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RRMapServiceTest extends AbstractAntidoteTest {
    private RRMapService map_service;
    private IntegerService int_service;
    private CounterService counter_service;
    private SetService set_service;
    private RegisterService register_service;

    public RRMapServiceTest() {
        super();
        map_service = new RRMapService();
        int_service = new IntegerService();
        counter_service = new CounterService();
        set_service = new SetService();
        register_service = new RegisterService();
    }

    @Test
    public void testUpdatesMapRR() {

        MapKey mapKey = map_service.getKey("map_rr_test_updates_map_key");
        IntegerKey y_key = int_service.getKey("map_rr_test_updates_y_key");
        CounterKey z_key = counter_service.getKey("map_rr_test_updates_z_key");

        UpdateOp update = map_service.updateMap(mapKey, int_service.assignInteger(y_key, 1),
                                                        counter_service.incrementCounter(z_key,3));

        antidoteService.applyUpdate(update);

        long readValue_y = (Long) antidoteService.readKeyInMap(mapKey, y_key);
        assertEquals(1, readValue_y);

        int readValue_z = (Integer) antidoteService.readKeyInMap(mapKey, z_key);
        assertEquals(3, readValue_z);

    }

    @Test
    public void testRemovesMapRR() {

        MapKey mapKey = map_service.getKey("map_rr_test_removes_map_key");
        RegisterKey y_key = register_service.getKey("map_rr_test_removes_y_key");
        CounterKey z_key = counter_service.getKey("map_rr_test_removes_z_key");

        antidoteService.applyUpdate(map_service.updateMap(mapKey, register_service.assignRegister(y_key, "Hello"),
                                                                counter_service.incrementCounter(z_key,3)));

        String readValue_y = (String) antidoteService.readKeyInMap(mapKey, y_key);
        assertEquals("Hello", readValue_y);

        int readValue_z = (Integer) antidoteService.readKeyInMap(mapKey, z_key);
        assertEquals(3, readValue_z);

        antidoteService.applyUpdate(map_service.removeKey(mapKey, y_key));

        readValue_y = (String) antidoteService.readKeyInMap(mapKey, y_key);
        assertEquals(null, readValue_y);

        readValue_z = (Integer) antidoteService.readKeyInMap(mapKey, z_key);
        assertEquals(3, readValue_z);
    }

    @Test
    public void testResetMapRR() {

        MapKey mapKey = map_service.getKey("map_rr_test_reset_map_key");
        SetKey x_key = set_service.getKey("map_rr_test_reset_x_key");
        SetKey y_key = set_service.getKey("map_rr_test_reset_y_key");

        antidoteService.applyUpdate(map_service.updateMap(mapKey, set_service.addToSet(x_key, "1","2","3")));
        antidoteService.applyUpdate(map_service.updateMap(mapKey, set_service.addToSet(y_key, "4","5")));
        antidoteService.applyUpdate(map_service.reset(mapKey));

        MapKey.MapReadResult readValue_map = (MapKey.MapReadResult) antidoteService.readByKey(mapKey);
        assertTrue(readValue_map.isEmpty());

    }


}

