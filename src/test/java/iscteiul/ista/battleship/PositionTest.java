package iscteiul.ista.battleship;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("JUnit 6 Nested Tests for Position (100% Branch Coverage)")
class PositionTest {

    /* ============================================================
       GROUP 1 — GETTERS
    ============================================================ */
    @Nested
    @DisplayName("Getter tests")
    class GetterTests {

        @Test
        @DisplayName("getRow and getColumn return correct values")
        void testGetters() {
            Position p = new Position(2, 3);
            assertEquals(2, p.getRow());
            assertEquals(3, p.getColumn());
        }
    }

    /* ============================================================
       GROUP 2 — STATE CHANGES (occupy, shoot)
    ============================================================ */
    @Nested
    @DisplayName("State mutation tests (occupy/shoot)")
    class StateTests {

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
        @DisplayName("hashCode changes after occupy() or shoot()")
        void testHashCodeChanges() {
            Position p = new Position(1, 1);
            int initial = p.hashCode();

            p.occupy();
            int afterOccupy = p.hashCode();
            assertNotEquals(initial, afterOccupy, "hashCode must change after occupy()");

            p.shoot();
            int afterShoot = p.hashCode();
            assertNotEquals(afterOccupy, afterShoot, "hashCode must change after shoot()");
        }
    }

    /* ============================================================
       GROUP 3 — EQUALS & HASHCODE
    ============================================================ */
    @Nested
    @DisplayName("Equality tests (equals/hashCode)")
    class EqualityTests {

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
        @DisplayName("equals returns true when comparing object with itself")
        void testEqualsSelf() {
            Position p = new Position(5, 5);
            assertTrue(p.equals(p)); // covers "this == other" branch
        }

        @Test
        @DisplayName("equals returns false for null and different type")
        void testEqualsAgainstNullAndDifferentType() {
            Position p = new Position(3, 3);
            assertFalse(p.equals(null));
            assertFalse(p.equals("not a position"));
        }
    }

    /* ============================================================
       GROUP 4 — ADJACENCY TESTS
    ============================================================ */
    @Nested
    @DisplayName("Adjacency tests")
    class AdjacencyTests {

        @Test
        @DisplayName("isAdjacentTo identifies adjacent and non-adjacent positions")
        void testIsAdjacentTo() {
            Position center = new Position(2, 2);

            // Adjacent including diagonals and itself
            assertTrue(center.isAdjacentTo(new Position(2, 2)));
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
        @DisplayName("isAdjacentTo throws NullPointerException on null input")
        void testIsAdjacentToNull() {
            Position p = new Position(1, 1);
            assertThrows(NullPointerException.class, () -> p.isAdjacentTo(null));
        }
    }

    /* ============================================================
       GROUP 5 — STRING REPRESENTATION
    ============================================================ */
    @Nested
    @DisplayName("String representation tests")
    class ToStringTests {

        @Test
        @DisplayName("toString returns expected format")
        void testToString() {
            Position p = new Position(2, 3);
            assertEquals("Linha = 2 Coluna = 3", p.toString());
        }
    }
}
