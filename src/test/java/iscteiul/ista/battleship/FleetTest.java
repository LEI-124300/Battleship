package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JUnit 6 Tests for Fleet")
class FleetTest {

    Fleet fleet;
    IPosition p;

    @BeforeEach
    void setUp() {
        fleet = new Fleet();
        p = new Position(0, 0);
    }

    @AfterEach
    void tearDown() {
        fleet = null;
        p = null;
    }

    @Test
    @DisplayName("AddShip should add ships inside board")
    void testAddShip() {
        Ship s = new Barge(Compass.EAST, new Position(0, 0));
        assertTrue(fleet.addShip(s), "Ship must be added");
        assertEquals(1, fleet.getShips().size());
    }

    @Test
    @DisplayName("getShipsLike must return matching category")
    void testGetShipsLike() {

        Ship s1 = new Caravel(Compass.EAST, new Position(0, 0)); // Category: "Caravela"
        Ship s2 = new Barge(Compass.EAST, new Position(5, 5));   // Category: "Barca"

        fleet.addShip(s1);
        fleet.addShip(s2);

        List<IShip> caravels = fleet.getShipsLike("Caravela");

        assertEquals(1, caravels.size(), "Fleet should return one Caravel");
        assertEquals("Caravela", caravels.get(0).getCategory());
    }

    @Test
    @DisplayName("getFloatingShips must return only ships not fully hit")
    void testGetFloatingShips() {

        Ship s1 = new Barge(Compass.EAST, new Position(0, 0)); // size = 1
        Ship s2 = new Caravel(Compass.EAST, new Position(2, 2)); // size = 2

        fleet.addShip(s1);
        fleet.addShip(s2);

        // afundar completamente a Barca
        s1.shoot(new Position(0, 0));

        // Caravel ainda está toda flutuante
        List<IShip> floating = fleet.getFloatingShips();

        assertEquals(1, floating.size(), "Only one ship should be floating");
        assertEquals("Caravela", floating.get(0).getCategory());
    }

    @Test
    @DisplayName("shipAt must return the correct ship on a position")
    void testShipAt() {

        Ship s1 = new Barge(Compass.EAST, new Position(0, 0));

        fleet.addShip(s1);

        IShip found = fleet.shipAt(new Position(0, 0));

        assertNotNull(found);
        assertEquals("Barca", found.getCategory());
    }
}
