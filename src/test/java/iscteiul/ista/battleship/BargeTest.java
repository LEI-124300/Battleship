package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("JUnit 6 tests for Barge")
class BargeTest {

    Barge barge;
    IPosition startPos;

    @BeforeEach
    void setUp() {
        startPos = new Position(3, 4);
        barge = new Barge(Compass.NORTH, startPos);
    }

    @AfterEach
    void tearDown() {
        barge = null;
        startPos = null;
    }

    @Test
    @DisplayName("Barge size must be 1")
    void testSize() {
        assertEquals(1, barge.getSize(),
                "Error: Barge size should always be 1");
    }

    @Test
    @DisplayName("Constructor must set 1 occupied position")
    void testPositionsCreated() {
        assertEquals(1, barge.getPositions().size(),
                "Error: Barge must occupy exactly 1 position");

        IPosition p = barge.getPositions().get(0);

        assertAll(
                () -> assertEquals(3, p.getRow(), "Row incorrect"),
                () -> assertEquals(4, p.getColumn(), "Column incorrect")
        );
    }

    @Test
    @DisplayName("Barge must occupy its starting position")
    void testOccupies() {
        assertTrue(barge.occupies(startPos),
                "Error: barge should occupy the given starting position");
    }

    @Test
    @DisplayName("Barge should not occupy unrelated position")
    void testDoesNotOccupy() {
        Position other = new Position(0, 0);

        assertFalse(barge.occupies(other),
                "Error: barge must NOT occupy a different position");
    }
}
