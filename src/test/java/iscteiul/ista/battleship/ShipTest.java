package iscteiul.ista.battleship;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests for generic Ship behavior")
class ShipTest {

    /** Simple test stub implementing IPosition for unit tests */
    private static class TestPosition implements IPosition {
        private final int row;
        private final int column;
        private boolean hit;
        private boolean occupied; // tracks occupation state if needed by IPosition

        TestPosition(int row, int column) {
            this.row = row;
            this.column = column;
            this.hit = false;
            this.occupied = false;
        }

        @Override
        public int getRow() {
            return row;
        }

        @Override
        public int getColumn() {
            return column;
        }

        @Override
        public boolean isHit() {
            return hit;
        }

        @Override
        public void shoot() {
            this.hit = true;
        }

        /**
         * Adjacent defined as any cell with row and column difference <= 1
         * but not the same cell.
         */
        @Override
        public boolean isAdjacentTo(IPosition other) {
            if (other == null) return false;
            int dr = Math.abs(this.row - other.getRow());
            int dc = Math.abs(this.column - other.getColumn());
            if (dr == 0 && dc == 0) return false; // not adjacent, same cell
            return Math.max(dr, dc) <= 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof IPosition)) return false;
            IPosition other = (IPosition) obj;
            return this.row == other.getRow() && this.column == other.getColumn();
        }

        @Override
        public String toString() {
            return "(" + row + "," + column + ")";
        }

        @Override
        public boolean isOccupied() {
            return occupied;
        }

        // Provide a test helper to mark as occupied; avoid @Override in case interface uses a different signature.
        public void occupy() {
            this.occupied = true;
        }
    }

    /** Helper to create a simple Ship with contiguous positions along a bearing */
    private Ship createShipAt(int startRow, int startCol, Compass bearing, int length) {
        TestPosition start = new TestPosition(startRow, startCol);
        // Implement the abstract getSize() so the anonymous subclass is concrete
        Ship s = new Ship("barca", bearing, start) {
            @Override
            public Integer getSize() {
                return positions == null ? 0 : positions.size();
            }
        };

        for (int i = 0; i < length; i++) {
            int r = startRow;
            int c = startCol;
            switch (bearing) {
                case NORTH:
                    r = startRow - i;
                    break;
                case SOUTH:
                    r = startRow + i;
                    break;
                case EAST:
                    c = startCol + i;
                    break;
                case WEST:
                    c = startCol - i;
                    break;
                default:
                    // keep as start for unknown bearings
            }
            s.positions.add(new TestPosition(r, c));
        }
        return s;
    }

    @Test
    @DisplayName("stillFloating returns true when any position not hit; false when all hit")
    void testStillFloating() {
        Ship s = createShipAt(5, 5, Compass.EAST, 3);
        assertTrue(s.stillFloating(), "Ship with unhit positions should be floating");

        // hit all positions
        for (IPosition p : s.getPositions()) {
            p.shoot();
        }
        assertFalse(s.stillFloating(), "Ship with all positions hit should not be floating");
    }

    @Test
    @DisplayName("Top/Bottom/Left/Right most position calculations")
    void testExtremes() {
        // create vertical ship going NORTH from (5,2): positions (5,2),(4,2),(3,2)
        Ship s = createShipAt(5, 2, Compass.NORTH, 3);
        assertEquals(3, s.getTopMostPos(), "Top most row should be smallest row");
        assertEquals(5, s.getBottomMostPos(), "Bottom most row should be largest row");
        assertEquals(2, s.getLeftMostPos(), "Left most column should be smallest column");
        assertEquals(2, s.getRightMostPos(), "Right most column should be largest column");
    }

    @Test
    @DisplayName("occupies returns true for contained position and false otherwise")
    void testOccupies() {
        Ship s = createShipAt(2, 3, Compass.SOUTH, 2); // positions (2,3),(3,3)
        assertTrue(s.occupies(new TestPosition(2, 3)));
        assertTrue(s.occupies(new TestPosition(3, 3)));
        assertFalse(s.occupies(new TestPosition(4, 3)));
    }

    @Test
    @DisplayName("tooCloseTo(IPosition) detects adjacency")
    void testTooCloseToPosition() {
        Ship s = createShipAt(4, 4, Compass.EAST, 2); // (4,4),(4,5)
        // adjacent diagonal to first cell
        TestPosition adj = new TestPosition(3, 3);
        assertTrue(s.tooCloseTo(adj), "Ship should be too close to adjacent diagonal position");
        // far away
        TestPosition far = new TestPosition(1, 1);
        assertFalse(s.tooCloseTo(far), "Ship should not be too close to distant position");
    }

    @Test
    @DisplayName("tooCloseTo(IShip) detects adjacency between ships")
    void testTooCloseToShip() {
        Ship s1 = createShipAt(6, 6, Compass.EAST, 2); // (6,6),(6,7)
        Ship s2 = createShipAt(5, 7, Compass.NORTH, 1); // (5,7) adjacent to (6,7)
        assertTrue(s1.tooCloseTo(s2), "Ships with adjacent cells should be too close");

        Ship s3 = createShipAt(1, 1, Compass.EAST, 1);
        assertFalse(s1.tooCloseTo(s3), "Distant ships should not be too close");
    }

    @Test
    @DisplayName("shoot marks matching position as hit")
    void testShoot() {
        Ship s = createShipAt(8, 8, Compass.WEST, 3); // (8,8),(8,7),(8,6)
        TestPosition target = new TestPosition(8, 7);
        s.shoot(target);

        // only the matching position should be marked hit
        boolean hitFound = false;
        for (IPosition p : s.getPositions()) {
            if (p.equals(target)) {
                assertTrue(p.isHit(), "Matching position should be hit");
                hitFound = true;
            } else {
                assertFalse(p.isHit(), "Non-target positions should remain unhit");
            }
        }
        assertTrue(hitFound, "Target position must exist on the ship");
    }

    @Test
    @DisplayName("toString contains category, bearing letter, and position")
    void testToString() {
        Ship s = createShipAt(3, 2, Compass.NORTH, 1);
        String rep = s.toString();

        assertTrue(rep.contains("barca"), "toString should contain category");
        assertTrue(rep.contains(" " + s.getBearing().getDirection() + " "),
                "toString should contain bearing letter");

        // Because TestPosition.toString() = "(row,col)"
        assertTrue(rep.contains("(") && rep.contains(")"),
                "toString should include TestPosition string format");
    }



}
