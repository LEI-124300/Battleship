/**
 * Test class for class Frigate.
 * Author: ${user.name}
 * Date: 2025-11-19 00:00
 *
 * Cyclomatic Complexity:
 * - constructor: 3
 *   (Paths: NORTH/SOUTH branch, EAST/WEST branch, default-invalid branch)
 * - getSize(): 1
 */
package iscteiul.ista.battleship;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class FrigateTest {

    private Frigate frigate;
    private Compass defaultBearing;
    private IPosition defaultPos;

    @BeforeEach
    public void setUp() {
        // Default initial values — each test that needs a different path overrides these.
        defaultBearing = Compass.NORTH;
        defaultPos = new Position(5, 5);
        frigate = new Frigate(defaultBearing, defaultPos);
    }

    @AfterEach
    public void tearDown() {
        frigate = null;
        defaultBearing = null;
        defaultPos = null;
    }

    // ============================================================
    // constructor: CC = 3 → constructor1(), constructor2(), constructor3()
    // ============================================================

    /**
     * Path 1: NORTH/SOUTH branch (NORTH selected)
     */
    @Test
    public void constructor1() {
        Compass bearing = Compass.NORTH;
        IPosition pos = new Position(2, 3);

        Frigate f = new Frigate(bearing, pos);

        assertAll(
                "NORTH path position validation",
                () -> assertEquals(4, f.getPositions().size(),
                        "Error: expected SIZE=4 but got " + f.getPositions().size()),
                () -> assertEquals(new Position(2, 3), f.getPositions().get(0),
                        "Error: expected first position row=2 col=3"),
                () -> assertEquals(new Position(3, 3), f.getPositions().get(1),
                        "Error: expected second position row=3 col=3"),
                () -> assertEquals(new Position(4, 3), f.getPositions().get(2),
                        "Error: expected third position row=4 col=3"),
                () -> assertEquals(new Position(5, 3), f.getPositions().get(3),
                        "Error: expected fourth position row=5 col=3")
        );
    }

    /**
     * Path 2: EAST/WEST branch (EAST selected)
     */
    @Test
    public void constructor2() {
        Compass bearing = Compass.EAST;
        IPosition pos = new Position(10, 1);

        Frigate f = new Frigate(bearing, pos);

        assertAll(
                "EAST path position validation",
                () -> assertEquals(4, f.getPositions().size(),
                        "Error: expected SIZE=4 but got " + f.getPositions().size()),
                () -> assertEquals(new Position(10, 1), f.getPositions().get(0),
                        "Error: expected first position row=10 col=1"),
                () -> assertEquals(new Position(10, 2), f.getPositions().get(1),
                        "Error: expected second position row=10 col=2"),
                () -> assertEquals(new Position(10, 3), f.getPositions().get(2),
                        "Error: expected third position row=10 col=3"),
                () -> assertEquals(new Position(10, 4), f.getPositions().get(3),
                        "Error: expected fourth position row=10 col=4")
        );
    }

    /**
     * Path 3: default branch — invalid bearing → must throw IllegalArgumentException
     */
    @Test
    public void constructor3() {
        IPosition pos = new Position(0, 0);

        assertThrows(AssertionError.class,
                () -> new Frigate(null, pos),
                "Error: expected AssertionError when bearing is null but none thrown");
    }

    // ============================================================
    // getSize(): CC = 1 → getSize()
    // ============================================================

    @Test
    public void getSize() {
        Integer expected = 4;
        Integer actual = frigate.getSize();

        assertEquals(expected, actual,
                "Error: expected getSize() to return 4 but got " + actual);
    }

}
