/**
 * Test class for class Galleon.
 * Author: ${user.name}
 * Date: 2025-11-19 00:00
 *
 * Cyclomatic Complexity:
 * - constructor: 6
 * - getSize(): 1
 */
package iscteiul.ista.battleship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GalleonTest {

    private Galleon galleon;
    private IPosition defaultPos;

    @BeforeEach
    public void setUp() {
        defaultPos = new Position(5, 5);
        galleon = new Galleon(Compass.NORTH, defaultPos);
    }

    @AfterEach
    public void tearDown() {
        galleon = null;
        defaultPos = null;
    }

    // ============================================================
    // constructor: CC = 6 → 6 test methods
    // ============================================================

    /**
     * Path 1: bearing == null → NullPointerException
     */
    @Test
    public void constructor1() {
        assertThrows(NullPointerException.class,
                () -> new Galleon(null, new Position(0, 0)),
                "Error: expected NullPointerException for null bearing but none thrown");
    }

    /**
     * Path 2: NORTH branch
     */
    @Test
    public void constructor2() {
        IPosition pos = new Position(2, 3);
        Galleon g = new Galleon(Compass.NORTH, pos);

        assertAll(
                "NORTH shape validation",
                () -> assertEquals(5, g.getPositions().size(),
                        "Error: expected SIZE=5 but got " + g.getPositions().size()),
                () -> assertEquals(new Position(2, 3), g.getPositions().get(0),
                        "Error: unexpected NORTH pos 0"),
                () -> assertEquals(new Position(2, 4), g.getPositions().get(1),
                        "Error: unexpected NORTH pos 1"),
                () -> assertEquals(new Position(2, 5), g.getPositions().get(2),
                        "Error: unexpected NORTH pos 2"),
                () -> assertEquals(new Position(3, 4), g.getPositions().get(3),
                        "Error: unexpected NORTH pos 3"),
                () -> assertEquals(new Position(4, 4), g.getPositions().get(4),
                        "Error: unexpected NORTH pos 4")
        );
    }

    /**
     * Path 3: EAST branch
     */
    @Test
    public void constructor3() {
        IPosition pos = new Position(10, 10);
        Galleon g = new Galleon(Compass.EAST, pos);

        assertAll(
                "EAST shape validation",
                () -> assertEquals(5, g.getPositions().size(),
                        "Error: expected SIZE=5 but got " + g.getPositions().size()),
                () -> assertEquals(new Position(10, 10), g.getPositions().get(0),
                        "Error: unexpected EAST pos 0"),
                () -> assertEquals(new Position(11, 8), g.getPositions().get(1),
                        "Error: unexpected EAST pos 1"),
                () -> assertEquals(new Position(11, 9), g.getPositions().get(2),
                        "Error: unexpected EAST pos 2"),
                () -> assertEquals(new Position(11, 10), g.getPositions().get(3),
                        "Error: unexpected EAST pos 3"),
                () -> assertEquals(new Position(12, 10), g.getPositions().get(4),
                        "Error: unexpected EAST pos 4")
        );
    }

    /**
     * Path 4: SOUTH branch
     */
    @Test
    public void constructor4() {
        IPosition pos = new Position(0, 0);
        Galleon g = new Galleon(Compass.SOUTH, pos);

        assertAll(
                "SOUTH shape validation",
                () -> assertEquals(5, g.getPositions().size(),
                        "Error: expected SIZE=5 but got " + g.getPositions().size()),
                () -> assertEquals(new Position(0, 0), g.getPositions().get(0),
                        "Error: unexpected SOUTH pos 0"),
                () -> assertEquals(new Position(1, 0), g.getPositions().get(1),
                        "Error: unexpected SOUTH pos 1"),
                () -> assertEquals(new Position(2, 0), g.getPositions().get(2),
                        "Error: unexpected SOUTH pos 2"),
                () -> assertEquals(new Position(2, 1), g.getPositions().get(3),
                        "Error: unexpected SOUTH pos 3"),
                () -> assertEquals(new Position(2, 2), g.getPositions().get(4),
                        "Error: unexpected SOUTH pos 4")
        );
    }

    /**
     * Path 5: WEST branch
     */
    @Test
    public void constructor5() {
        IPosition pos = new Position(7, 7);
        Galleon g = new Galleon(Compass.WEST, pos);

        assertAll(
                "WEST shape validation",
                () -> assertEquals(5, g.getPositions().size(),
                        "Error: expected SIZE=5 but got " + g.getPositions().size()),
                () -> assertEquals(new Position(7, 7), g.getPositions().get(0),
                        "Error: unexpected WEST pos 0"),
                () -> assertEquals(new Position(8, 7), g.getPositions().get(1),
                        "Error: unexpected WEST pos 1"),
                () -> assertEquals(new Position(8, 8), g.getPositions().get(2),
                        "Error: unexpected WEST pos 2"),
                () -> assertEquals(new Position(8, 9), g.getPositions().get(3),
                        "Error: unexpected WEST pos 3"),
                () -> assertEquals(new Position(9, 7), g.getPositions().get(4),
                        "Error: unexpected WEST pos 4")
        );
    }

    /**
     * Path 6: default → IllegalArgumentException (valor inválido)
     */
    @Test
    public void constructor6() {
        Compass invalid = Compass.valueOf("INVALID"); // se enum não permitir, substituir por mock
        IPosition pos = new Position(0, 0);

        assertThrows(IllegalArgumentException.class,
                () -> new Galleon(invalid, pos),
                "Error: expected IllegalArgumentException for invalid bearing but none thrown");
    }

    // ============================================================
    // getSize(): CC = 1
    // ============================================================

    @Test
    public void getSize() {
        Integer expected = 5;
        Integer actual = galleon.getSize();

        assertEquals(expected, actual,
                "Error: expected getSize() to return 5 but got " + actual);
    }

}