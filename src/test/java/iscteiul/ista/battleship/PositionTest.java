package iscteiul.ista.battleship;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Position tests")
class PositionTest {

    @Test
    @DisplayName("getRow and getColumn return correct values")
    void testGetters() {
        Position p = new Position(2, 3);
        assertEquals(2, p.getRow());
        assertEquals(3, p.getColumn());
    }

    @Test
    @DisplayName("occupy sets position as occupied")
    void testOccupyAndIsOccupied() {
        Position p = new Position(0, 0);
        assertFalse(p.isOccupied());
        p.occupy();
        assertTrue(p.isOccupied());
    }

    @Test
    @DisplayName("shoot sets position as hit")
    void testShootAndIsHit() {
        Position p = new Position(0, 1);
        assertFalse(p.isHit());
        p.shoot();
        assertTrue(p.isHit());
    }

    @Test
    @DisplayName("equals and hashCode for equal positions")
    void testEqualsAndHashCode() {
        Position a = new Position(1, 1);
        Position b = new Position(1, 1);
        Position c = new Position(1, 2);

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }

    @Test
    @DisplayName("equals returns false for null and different types")
    void testEqualsAgainstNullAndDifferentType() {
        Position p = new Position(3, 3);
        assertFalse(p.equals(null));
        assertFalse(p.equals("not a position"));
    }

    @Test
    @DisplayName("isAdjacentTo identifies adjacent and non-adjacent positions")
    void testIsAdjacentTo() {
        Position center = new Position(2, 2);

        // Adjacent including diagonals and itself
        assertTrue(center.isAdjacentTo(new Position(2, 2))); // same
        assertTrue(center.isAdjacentTo(new Position(1, 1)));
        assertTrue(center.isAdjacentTo(new Position(1, 2)));
        assertTrue(center.isAdjacentTo(new Position(3, 3)));
        assertTrue(center.isAdjacentTo(new Position(2, 1)));

        // Non-adjacent
        assertFalse(center.isAdjacentTo(new Position(0, 0)));
        assertFalse(center.isAdjacentTo(new Position(4, 2)));
        assertFalse(center.isAdjacentTo(new Position(2, 4)));
    }

    @Test
    @DisplayName("toString returns expected format")
    void testToString() {
        Position p = new Position(2, 3);
        assertEquals("Linha = 2 Coluna = 3", p.toString());
    }
}
