package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JUnit 6 Tests for Caravel (size 2)")
class CaravelTest {

    IPosition startPos;

    @BeforeEach
    void setUp() {
        startPos = new Position(2, 2);
    }

    @AfterEach
    void tearDown() {
        startPos = null;
    }

    @Test
    @DisplayName("Caravel size must be 2")
    void testSize() {
        Caravel c = new Caravel(Compass.NORTH, startPos);
        assertEquals(2, c.getSize(), "Error: Caravel must have size 2");
    }

    @Test
    @DisplayName("NORTH → creates 2 vertical positions")
    void testNorthPositions() {
        Caravel c = new Caravel(Compass.NORTH, startPos);

        assertAll(
                () -> assertEquals(2, c.getPositions().size(),
                        "Caravel must have 2 positions"),
                () -> assertEquals(2, c.getPositions().get(0).getRow()),
                () -> assertEquals(3, c.getPositions().get(1).getRow()),
                () -> assertEquals(2, c.getPositions().get(0).getColumn()),
                () -> assertEquals(2, c.getPositions().get(1).getColumn())
        );
    }

    @Test
    @DisplayName("SOUTH → creates 2 vertical positions")
    void testSouthPositions() {
        Caravel c = new Caravel(Compass.SOUTH, startPos);

        assertEquals(2, c.getPositions().size());
        assertEquals(2, c.getPositions().get(0).getRow());
        assertEquals(3, c.getPositions().get(1).getRow());
    }

    @Test
    @DisplayName("EAST → creates 2 horizontal positions")
    void testEastPositions() {
        Caravel c = new Caravel(Compass.EAST, startPos);

        assertAll(
                () -> assertEquals(2, c.getPositions().size()),
                () -> assertEquals(2, c.getPositions().get(0).getRow()),
                () -> assertEquals(2, c.getPositions().get(1).getRow()),
                () -> assertEquals(2, c.getPositions().get(0).getColumn()),
                () -> assertEquals(3, c.getPositions().get(1).getColumn())
        );
    }

    @Test
    @DisplayName("WEST → creates 2 horizontal positions")
    void testWestPositions() {
        Caravel c = new Caravel(Compass.WEST, startPos);

        assertEquals(2, c.getPositions().size());
        assertEquals(2, c.getPositions().get(0).getRow());
        assertEquals(2, c.getPositions().get(1).getRow());
    }

    @Test
    @DisplayName("Null bearing must throw AssertionError (from Ship constructor)")
    void testNullBearingException() {
        assertThrows(AssertionError.class,
                () -> new Caravel(null, startPos),
                "Error: null bearing must trigger AssertionError from Ship constructor");
    }

    @Test
    @DisplayName("Unknown/invalid bearing must throw IllegalArgumentException")
    void testInvalidBearingException() {
        // Simulate invalid bearing by using Compass.UNKNOWN
        assertThrows(IllegalArgumentException.class,
                () -> new Caravel(Compass.UNKNOWN, startPos),
                "Error: invalid bearing must throw IllegalArgumentException");
    }
}
