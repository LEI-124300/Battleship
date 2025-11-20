package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JUnit 6 Tests for Fleet")
class FleetTest {

    Fleet fleet;

    @BeforeEach
    void setUp() {
        fleet = new Fleet();
    }

    @AfterEach
    void tearDown() {
        fleet = null;
    }

    // ======================================================
    @Nested
    @DisplayName("AddShip Tests")
    class AddShipTests {

        @Test
        void testAddValidShip() {
            Ship s = new Barge(Compass.EAST, new Position(0, 0));
            assertTrue(fleet.addShip(s));
            assertEquals(1, fleet.getShips().size());
        }

        @Test
        void testAddShipOutsideBoard() {
            Ship s = new Caravel(Compass.EAST, new Position(100, 100));
            assertFalse(fleet.addShip(s));
        }

        @Test
        void testAddShipCollisionRisk() {
            Ship s1 = new Barge(Compass.EAST, new Position(0, 0));
            Ship s2 = new Caravel(Compass.EAST, new Position(0, 0));

            assertTrue(fleet.addShip(s1));
            assertFalse(fleet.addShip(s2));
        }



        @Test
        void testCollisionRiskTrue() {
            Ship s1 = new Barge(Compass.EAST, new Position(0, 0));
            Ship s2 = new Barge(Compass.EAST, new Position(0, 1));

            assertTrue(fleet.addShip(s1));
            assertFalse(fleet.addShip(s2));
        }

        @Test
        void testCollisionRiskFalse() {
            Ship s1 = new Barge(Compass.EAST, new Position(0, 0));
            Ship s2 = new Barge(Compass.EAST, new Position(5, 5));

            assertTrue(fleet.addShip(s1));
            assertTrue(fleet.addShip(s2));
        }

        @Test
        void testOutsideLeftBoundary() {
            Ship s = new Barge(Compass.EAST, new Position(0, -1));
            assertFalse(fleet.addShip(s));
        }

        @Test
        void testOutsideTopBoundary() {
            Ship s = new Barge(Compass.NORTH, new Position(-1, 0));
            assertFalse(fleet.addShip(s));
        }

        @Test
        void testOutsideRightBoundary() {
            Ship s = new Caravel(Compass.EAST, new Position(0, Fleet.BOARD_SIZE));
            assertFalse(fleet.addShip(s));
        }

        @Test
        void testOutsideBottomBoundary() {
            Ship s = new Caravel(Compass.SOUTH, new Position(Fleet.BOARD_SIZE, 0));
            assertFalse(fleet.addShip(s));
        }
    }

    // ======================================================
    @Nested
    @DisplayName("Query Methods Tests")
    class QueryTests {

        @Test
        void testGetShipsLike() {
            fleet.addShip(new Caravel(Compass.EAST, new Position(0, 0)));
            fleet.addShip(new Barge(Compass.EAST, new Position(5, 5)));

            List<IShip> result = fleet.getShipsLike("Caravela");
            assertEquals(1, result.size());
        }

        @Test
        void testGetShipsLikeEmpty() {
            assertTrue(fleet.getShipsLike("Nada").isEmpty());
        }

        @Test
        void testGetShipsLikeLoopButNoMatch() {
            fleet.addShip(new Barge(Compass.EAST, new Position(0, 0)));
            fleet.addShip(new Frigate(Compass.EAST, new Position(3, 3)));

            assertTrue(fleet.getShipsLike("Galeao").isEmpty());
        }

        @Test
        void testShipAtFound() {
            fleet.addShip(new Barge(Compass.EAST, new Position(0, 0)));
            IShip found = fleet.shipAt(new Position(0, 0));

            assertNotNull(found);
            assertEquals("Barca", found.getCategory());
        }

        @Test
        void testShipAtNotFound() {
            assertNull(fleet.shipAt(new Position(9, 9)));
        }

        @Test
        void testShipAtLoopNoMatch() {
            fleet.addShip(new Barge(Compass.EAST, new Position(0, 0)));
            fleet.addShip(new Barge(Compass.EAST, new Position(3, 3)));

            assertNull(fleet.shipAt(new Position(9, 9)));
        }
    }

    // ======================================================
    @Nested
    @DisplayName("Floating Ships Tests")
    class FloatingTests {

        @Test
        void testGetFloatingShips() {
            Ship s1 = new Barge(Compass.EAST, new Position(0, 0));
            Ship s2 = new Caravel(Compass.EAST, new Position(2, 2));

            fleet.addShip(s1);
            fleet.addShip(s2);

            s1.shoot(new Position(0, 0));

            List<IShip> floating = fleet.getFloatingShips();
            assertEquals(1, floating.size());
        }

        @Test
        void testGetFloatingShipsEmpty() {
            Ship s = new Barge(Compass.EAST, new Position(0, 0));

            fleet.addShip(s);
            s.shoot(new Position(0, 0));

            assertTrue(fleet.getFloatingShips().isEmpty());
        }
    }

    // ======================================================
    @Nested
    @DisplayName("Print Methods Coverage")
    class PrintTests {

        @Test
        void testPrintMethods() {
            fleet.addShip(new Barge(Compass.EAST, new Position(0, 0)));

            fleet.printAllShips();
            fleet.printFloatingShips();
            fleet.printShipsByCategory("Barca");
            fleet.printShipsByCategory("Nada");
        }
    }
}
