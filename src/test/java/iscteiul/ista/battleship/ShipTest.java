package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Tests for Ship class with full branch coverage")
class ShipTest {

    /* -------------------------------------------------------------
       TEST POSITION IMPLEMENTATION
    ------------------------------------------------------------- */
    private static class TestPosition implements IPosition {
        private final int row;
        private final int column;
        private boolean hit = false;
        private boolean occupied = false;

        TestPosition(int row, int column) {
            this.row = row;
            this.column = column;
        }

        @Override public int getRow() { return row; }
        @Override public int getColumn() { return column; }
        @Override public boolean isHit() { return hit; }
        @Override public void shoot() { hit = true; }
        @Override public boolean isOccupied() { return occupied; }
        @Override public void occupy() { occupied = true; }

        @Override
        public boolean isAdjacentTo(IPosition other) {
            if (other == null) return false;
            int dr = Math.abs(row - other.getRow());
            int dc = Math.abs(column - other.getColumn());
            return !(dr == 0 && dc == 0) && Math.max(dr, dc) <= 1;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof IPosition o)) return false;
            return row == o.getRow() && column == o.getColumn();
        }

        @Override
        public String toString() {
            return "(" + row + "," + column + ")";
        }
    }

    /* -------------------------------------------------------------
       SECOND POSITION TYPE (to cover equals branches)
    ------------------------------------------------------------- */
    private static class OtherPos implements IPosition {
        int r, c;
        OtherPos(int r, int c) { this.r = r; this.c = c; }

        @Override public int getRow() { return r; }
        @Override public int getColumn() { return c; }
        @Override public boolean isHit() { return false; }
        @Override public void shoot() {}
        @Override public boolean isAdjacentTo(IPosition pos) { return false; }
        @Override public boolean isOccupied() { return false; }
        @Override public void occupy() {}
    }


    /* -------------------------------------------------------------
       HELPER: CREATE SHIP
    ------------------------------------------------------------- */
    private Ship createShipAt(int r, int c, Compass bearing, int len) {
        TestPosition start = new TestPosition(r, c);

        Ship s = new Ship("barca", bearing, start) {
            @Override public Integer getSize() { return positions.size(); }
        };

        for (int i = 0; i < len; i++) {
            int rr = r, cc = c;
            switch (bearing) {
                case NORTH -> rr = r - i;
                case SOUTH -> rr = r + i;
                case EAST  -> cc = c + i;
                case WEST  -> cc = c - i;
            }
            s.positions.add(new TestPosition(rr, cc));
        }
        return s;
    }


    private Ship defaultShip;

    @BeforeEach
    void setUp() { defaultShip = createShipAt(5, 5, Compass.EAST, 2); }

    @AfterEach
    void tearDown() { defaultShip = null; }


    /* =============================================================
       FLOATING TESTS
    ============================================================= */
    @Nested @DisplayName("Floating tests")
    class FloatingTests {

        @Test
        void floatingTrueThenFalse() {
            Ship s = createShipAt(5, 5, Compass.EAST, 3);
            assertTrue(s.stillFloating());
            s.getPositions().forEach(IPosition::shoot);
            assertFalse(s.stillFloating());
        }

        @Test
        void floatingEmptyShip() {
            Ship s = createShipAt(0, 0, Compass.NORTH, 0);
            assertFalse(s.stillFloating());
        }

        @Test
        void stillFloatingPartialHit() {
            Ship s = createShipAt(5, 5, Compass.EAST, 2);
            s.getPositions().get(0).shoot();
            assertTrue(s.stillFloating());
        }
    }


    /* =============================================================
       EXTREMES TESTS
    ============================================================= */
    @Nested @DisplayName("Extreme coordinate tests")
    class ExtremesTests {

        @Test
        void normalExtremes() {
            Ship s = createShipAt(5, 2, Compass.NORTH, 3);
            assertEquals(3, s.getTopMostPos());
            assertEquals(5, s.getBottomMostPos());
            assertEquals(2, s.getLeftMostPos());
            assertEquals(2, s.getRightMostPos());
        }

        @Test
        void extremesWestOrientation() {
            Ship s = createShipAt(5,5,Compass.WEST,3); // (5,5),(5,4),(5,3)
            assertEquals(3, s.getLeftMostPos());
            assertEquals(5, s.getRightMostPos());
        }

        @Test
        void extremesEastOrientation() {
            Ship s = createShipAt(5,5,Compass.EAST,3); // (5,5),(5,6),(5,7)
            assertEquals(5, s.getLeftMostPos());
            assertEquals(7, s.getRightMostPos());
        }

        @Test
        void extremesThrowForEmptyShip() {
            Ship s = createShipAt(0, 0, Compass.NORTH, 0);
            assertThrows(IndexOutOfBoundsException.class, s::getTopMostPos);
            assertThrows(IndexOutOfBoundsException.class, s::getBottomMostPos);
            assertThrows(IndexOutOfBoundsException.class, s::getLeftMostPos);
            assertThrows(IndexOutOfBoundsException.class, s::getRightMostPos);
        }
    }


    /* =============================================================
       OCCUPIES TESTS
    ============================================================= */
    @Nested @DisplayName("Occupation tests")
    class OccupiesTests {

        @Test
        void occupiesCorrectCells() {
            Ship s = createShipAt(2, 3, Compass.SOUTH, 2);
            assertTrue(s.occupies(new TestPosition(2, 3)));
            assertTrue(s.occupies(new TestPosition(3, 3)));
            assertFalse(s.occupies(new TestPosition(9, 9)));
        }

        @Test
        void occupiesNullThrowsAssertion() {
            Ship s = createShipAt(2, 3, Compass.SOUTH, 1);
            assertThrows(AssertionError.class, () -> s.occupies(null));
        }

        @Test
        void occupiesWorksWithDifferentImplementationMatching() {
            Ship s = createShipAt(5,5,Compass.EAST,1);
            assertTrue(s.occupies(new OtherPos(5,5)));
        }

        @Test
        void occupiesFalseWhenCoordinatesDoNotMatch() {
            Ship s = createShipAt(4,4,Compass.NORTH,1);
            assertFalse(s.occupies(new OtherPos(4,99)));
        }

        @Test
        void occupiesRowMatchButColumnDifferent() {
            Ship s = createShipAt(4,4,Compass.NORTH,1);
            assertFalse(s.occupies(new TestPosition(4,9)));
        }
    }


    /* =============================================================
       ADJACENCY TESTS
    ============================================================= */
    @Nested @DisplayName("Adjacency tests")
    class AdjacencyTests {

        @Test
        void tooCloseToPositionVariants() {
            Ship s = createShipAt(10, 10, Compass.EAST, 2);
            assertTrue(s.tooCloseTo(new TestPosition(9, 11)));
            assertFalse(s.tooCloseTo(new TestPosition(15, 15)));

            Ship one = createShipAt(20, 20, Compass.EAST, 1);
            assertFalse(one.tooCloseTo(new TestPosition(20, 20)));

            Ship empty = createShipAt(0, 0, Compass.EAST, 0);
            assertFalse(empty.tooCloseTo(new TestPosition(0, 1)));
        }

        @Test
        void tooCloseToNullPositionReturnsFalse() {
            Ship s = createShipAt(10, 10, Compass.EAST, 2);
            assertFalse(s.tooCloseTo((IPosition) null));
        }

        @Test
        void tooCloseToShipVariants() {
            Ship base = createShipAt(6, 6, Compass.EAST, 2);
            Ship adj  = createShipAt(5, 7, Compass.NORTH, 1);
            Ship far  = createShipAt(1, 1, Compass.NORTH, 1);
            Ship empty = createShipAt(0, 0, Compass.NORTH, 0);

            assertTrue(base.tooCloseTo(adj));
            assertFalse(base.tooCloseTo(far));
            assertFalse(base.tooCloseTo(empty));
        }

        @Test
        void tooCloseToMultiPositionShip() {
            Ship big = createShipAt(10,10,Compass.EAST,3);
            Ship near = createShipAt(9,12,Compass.SOUTH,2);
            assertTrue(big.tooCloseTo(near));
        }

        @Test
        void tooCloseToDiagonalTwoAwayFalse() {
            Ship s1 = createShipAt(5,5,Compass.EAST,2);
            Ship s2 = createShipAt(7,7,Compass.NORTH,1); // diagonal but not adjacent
            assertFalse(s1.tooCloseTo(s2));
        }

        @Test
        void tooCloseToNullShipThrowsAssertion() {
            Ship s = createShipAt(5, 5, Compass.NORTH, 1);
            assertThrows(AssertionError.class, () -> s.tooCloseTo((IShip) null));
        }
    }


    /* =============================================================
       SHOOTING TESTS
    ============================================================= */
    @Nested @DisplayName("Shooting tests")
    class ShootingTests {

        @Test
        void shootMarksOnlyMatchingPosition() {
            Ship s = createShipAt(8, 8, Compass.WEST, 3);
            TestPosition target = new TestPosition(8, 7);

            s.shoot(target);

            for (IPosition pos : s.getPositions()) {
                if (pos.equals(target)) assertTrue(pos.isHit());
                else assertFalse(pos.isHit());
            }
        }

        @Test
        void shootNoMatchDoesNothing() {
            Ship s = createShipAt(8, 8, Compass.WEST, 3);
            s.shoot(new TestPosition(999, 999));
            s.getPositions().forEach(p -> assertFalse(p.isHit()));
        }

        @Test
        void shootSameInstancePosition() {
            Ship s = createShipAt(4,4,Compass.EAST,2);
            IPosition same = s.getPositions().get(0);

            s.shoot(same);
            assertTrue(same.isHit());
        }
    }


    /* =============================================================
       STRING + FACTORY TESTS
    ============================================================= */
    @Nested @DisplayName("String & factory tests")
    class BuildShipTests {

        @Test
        void toStringContainsCategoryBearingAndPosition() {
            Ship s = createShipAt(3, 2, Compass.NORTH, 1);
            String rep = s.toString();

            assertTrue(rep.startsWith("["));
            assertTrue(rep.endsWith("]"));
            assertTrue(rep.contains("barca"));
            assertTrue(rep.contains(s.getBearing().toString()));
            assertTrue(rep.contains("(3,2)"));
        }

        @Test
        void toStringExactFormatRegexCheck() {
            Ship s = createShipAt(1,1,Compass.SOUTH,1);
            String rep = s.toString();

            // Build dynamic regex using actual enum string
            String expected = "\\[barca\\s+" + s.getBearing().toString() + "\\s+\\(1,1\\)\\]";
            assertTrue(rep.matches(expected));
        }


        @Test
        void buildShipValidKindProducesCorrectTypes() {
            Position pos = new Position(1, 1);

            assertTrue(Ship.buildShip("barca", Compass.NORTH, pos) instanceof Barge);
            assertTrue(Ship.buildShip("caravela", Compass.EAST, pos) instanceof Caravel);
            assertTrue(Ship.buildShip("nau", Compass.SOUTH, pos) instanceof Carrack);
            assertTrue(Ship.buildShip("fragata", Compass.WEST, pos) instanceof Frigate);
            assertTrue(Ship.buildShip("galeao", Compass.NORTH, pos) instanceof Galleon);
        }

        @Test
        void buildShipCaseSensitiveFails() {
            assertNull(Ship.buildShip("Barca", Compass.NORTH, new Position(0,0)));
        }

        @Test
        void buildShipUnknownReturnsNull() {
            assertNull(Ship.buildShip("somethingElse", Compass.NORTH, new Position(0,0)));
        }

        @Test
        void buildShipNullKindThrowsNPE() {
            assertThrows(NullPointerException.class,
                    () -> Ship.buildShip(null, Compass.NORTH, new Position(0,0)));
        }

        @Test
        void buildShipNullBearingThrowsAssertionError() {
            assertThrows(AssertionError.class,
                    () -> Ship.buildShip("barca", null, new Position(0,0)));
        }

        @Test
        void buildShipNullPositionThrowsAssertionError() {
            assertThrows(AssertionError.class,
                    () -> Ship.buildShip("barca", Compass.NORTH, null));
        }
    }
}
