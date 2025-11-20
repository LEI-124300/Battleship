package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JUnit 6 Tests for Carrack (size 3)")
class CarrackTest {

    
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
    @DisplayName("Carrack size must be 3")
    void testSize() {
        Carrack c = new Carrack(Compass.NORTH, startPos);
        assertEquals(3, c.getSize(), "Carrack must have size 3");
    }

    @Test
    @DisplayName("NORTH → 3 positions vertical")
    void testNorthPositions() {
        Carrack c = new Carrack(Compass.NORTH, startPos);

        assertAll(
                () -> assertEquals(3, c.getPositions().size()),
                () -> assertEquals(2, c.getPositions().get(0).getRow()),
                () -> assertEquals(3, c.getPositions().get(1).getRow()),
                () -> assertEquals(4, c.getPositions().get(2).getRow()),
                () -> assertEquals(2, c.getPositions().get(0).getColumn()),
                () -> assertEquals(2, c.getPositions().get(1).getColumn()),
                () -> assertEquals(2, c.getPositions().get(2).getColumn())
        );
    }

    @Test
    @DisplayName("SOUTH → 3 positions vertical")
    void testSouthPositions() {
        Carrack c = new Carrack(Compass.SOUTH, startPos);
        assertEquals(3, c.getPositions().size());
    }

    @Test
    @DisplayName("EAST → 3 positions horizontal")
    void testEastPositions() {
        Carrack c = new Carrack(Compass.EAST, startPos);

        assertAll(
                () -> assertEquals(3, c.getPositions().size()),
                () -> assertEquals(2, c.getPositions().get(0).getColumn()),
                () -> assertEquals(3, c.getPositions().get(1).getColumn()),
                () -> assertEquals(4, c.getPositions().get(2).getColumn())
        );
    }

    @Test
    @DisplayName("WEST → 3 positions horizontal")
    void testWestPositions() {
        Carrack c = new Carrack(Compass.WEST, startPos);
        assertEquals(3, c.getPositions().size());
    }

    @Test
    @DisplayName("Null bearing → AssertionError (Ship constructor)")
    void testNullBearingException() {
        assertThrows(AssertionError.class,
                () -> new Carrack(null, startPos),
                "Null bearing must throw AssertionError");
    }

    @Test
    @DisplayName("Invalid bearing (UNKNOWN) → IllegalArgumentException")
    void testInvalidBearingException() {
        assertThrows(IllegalArgumentException.class,
                () -> new Carrack(Compass.UNKNOWN, startPos),
                "Invalid bearing must throw IllegalArgumentException");
    }
}
