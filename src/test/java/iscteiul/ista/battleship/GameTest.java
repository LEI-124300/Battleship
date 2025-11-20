package iscteiul.ista.battleship;

import org.junit.jupiter.api.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * GameTest rewritten with @Nested structure + high/near-100% branch coverage.
 */
@DisplayName("JUnit 6 Nested Tests for Game")
class GameTest {

    private Game game;

    // Proxy-configurable IFleet behavior
    private Function<IPosition, IShip> shipAtFunc;
    private Supplier<List<IShip>> floatingShipsSupplier;
    private Supplier<List<IShip>> shipsSupplier;
    private IFleet fleetProxy;

    @BeforeEach
    void setUp() {
        shipAtFunc = pos -> null;
        floatingShipsSupplier = ArrayList::new;
        shipsSupplier = ArrayList::new;
        fleetProxy = createFleetProxy();
        game = new Game(fleetProxy);
        ensureInitialized(game);
    }

    @AfterEach
    void tearDown() {
        game = null;
        fleetProxy = null;
        shipAtFunc = null;
        floatingShipsSupplier = null;
        shipsSupplier = null;
    }

    // ======================================================================
    // Proxy helpers
    // ======================================================================

    private IFleet createFleetProxy() {
        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if (name.equals("shipAt")) return shipAtFunc.apply((IPosition) args[0]);
            if (name.equals("getFloatingShips")) return floatingShipsSupplier.get();
            if (name.equals("getShips")) return shipsSupplier.get();

            Class<?> rt = method.getReturnType();
            if (rt.isPrimitive()) {
                if (rt == boolean.class) return false;
                if (rt == int.class) return 0;
                if (rt == long.class) return 0L;
                if (rt == float.class) return 0f;
                if (rt == double.class) return 0d;
                if (rt == byte.class) return (byte) 0;
                if (rt == short.class) return (short) 0;
                if (rt == char.class) return (char) 0;
            }
            return null;
        };
        return (IFleet) Proxy.newProxyInstance(
                IFleet.class.getClassLoader(), new Class[]{IFleet.class}, handler);
    }

    private IPosition createPosition(int row, int col) {
        InvocationHandler h = (proxy, method, args) -> {
            String name = method.getName();
            switch (name) {
                case "getRow": return row;
                case "getColumn": return col;
                case "hashCode": return 31 * row + col;
                case "toString": return "Pos(" + row + "," + col + ")";
                case "equals":
                    Object o = args[0];
                    if (o == null) return false;
                    try {
                        Method gr = o.getClass().getMethod("getRow");
                        Method gc = o.getClass().getMethod("getColumn");
                        return (int) gr.invoke(o) == row && (int) gc.invoke(o) == col;
                    } catch (Exception e) { return false; }
            }
            return null;
        };
        return (IPosition) Proxy.newProxyInstance(
                IPosition.class.getClassLoader(), new Class[]{IPosition.class}, h);
    }

    private IShip createShip(List<IPosition> pos,
                             AtomicBoolean hit,
                             boolean stillFloat) {

        InvocationHandler h = (proxy, m, a) -> {
            switch (m.getName()) {
                case "getPositions": return pos;
                case "stillFloating": return stillFloat;
                case "shoot": hit.set(true); return null;
            }

            Class<?> rt = m.getReturnType();
            if (rt.isPrimitive() && rt == boolean.class) return false;
            if (rt.isPrimitive() && rt == int.class) return 0;
            return null;
        };
        return (IShip) Proxy.newProxyInstance(
                IShip.class.getClassLoader(), new Class[]{IShip.class}, h);
    }

    // ======================================================================
    // Nested Groups
    // ======================================================================

    // -----------------------------------------------------------
    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Initial state is correct")
        void constructor() {
            assertAll(
                    () -> assertEquals(0, game.getInvalidShots()),
                    () -> assertEquals(0, game.getRepeatedShots()),
                    () -> assertEquals(0, game.getHits()),
                    () -> assertEquals(0, game.getSunkShips()),
                    () -> assertTrue(game.getShots().isEmpty())
            );
        }
    }

    // -----------------------------------------------------------
    @Nested
    @DisplayName("fire() Tests — all branches")
    class FireTests {

        @Test
        @DisplayName("Invalid shot increments invalidShots")
        void invalidShot() {
            IPosition p = createPosition(-1, 0);
            assertNull(game.fire(p));
            assertEquals(1, game.getInvalidShots());
        }

        @Test
        @DisplayName("Repeated shot increments repeatedShots")
        void repeatedShot() {
            IPosition p = createPosition(0, 0);
            game.fire(p);
            game.fire(p);
            assertEquals(1, game.getRepeatedShots());
        }

        @Test
        @DisplayName("Valid shot but no ship at location")
        void validNoShip() {
            IPosition p = createPosition(1, 1);
            assertNull(game.fire(p));
            assertEquals(1, game.getShots().size());
        }

        @Test
        @DisplayName("Hit but ship still floating")
        void hitFloating() {
            IPosition p = createPosition(2, 2);
            AtomicBoolean called = new AtomicBoolean(false);
            IShip s = createShip(List.of(p), called, true);
            shipAtFunc = pos -> s;

            game = new Game(fleetProxy); ensureInitialized(game);

            assertNull(game.fire(p));
            assertTrue(called.get());
            assertEquals(1, game.getHits());
            assertEquals(0, game.getSunkShips());
        }

        @Test
        @DisplayName("Hit and ship sunk")
        void hitSink() {
            IPosition p = createPosition(3, 3);
            AtomicBoolean called = new AtomicBoolean(false);
            IShip s = createShip(List.of(p), called, false);

            shipAtFunc = pos -> s;
            game = new Game(fleetProxy); ensureInitialized(game);

            assertSame(s, game.fire(p));
            assertTrue(called.get());
            assertEquals(1, game.getSunkShips());
        }

        @Test
        @DisplayName("fire(null) throws NullPointerException")
        void fireNull() {
            assertThrows(NullPointerException.class, () -> game.fire(null));
        }
    }

    // -----------------------------------------------------------
    @Nested
    @DisplayName("Getter Tests")
    class GetterTests {

        @Test void repeated() { assertEquals(0, game.getRepeatedShots()); }
        @Test void invalid()  { assertEquals(0, game.getInvalidShots()); }
        @Test void hits()     { assertEquals(0, game.getHits()); }
        @Test void sunk()     { assertEquals(0, game.getSunkShips()); }

        @Test
        @DisplayName("getShots increases after fire()")
        void getShots() {
            IPosition p = createPosition(4, 4);
            game = new Game(fleetProxy); ensureInitialized(game);
            game.fire(p);
            assertEquals(1, game.getShots().size());
        }

        @Test
        @DisplayName("getRemainingShips from floating fleet")
        void remainingShips() {
            List<IShip> fl = new ArrayList<>();
            fl.add(createShip(new ArrayList<>(), new AtomicBoolean(false), true));
            fl.add(createShip(new ArrayList<>(), new AtomicBoolean(false), true));

            floatingShipsSupplier = () -> fl;
            game = new Game(fleetProxy); ensureInitialized(game);
            assertEquals(2, game.getRemainingShips());
        }
    }

    // -----------------------------------------------------------
    @Nested
    @DisplayName("validShot() Tests via Reflection")
    class ValidShotTests {

        @Test void valid_0_0() throws Exception {
            assertTrue(invokePrivateValidShot(game, createPosition(0, 0)));
        }
        @Test void rowNeg() throws Exception {
            assertFalse(invokePrivateValidShot(game, createPosition(-1, 0)));
        }
        @Test void rowBig() throws Exception {
            assertFalse(invokePrivateValidShot(game, createPosition(Fleet.BOARD_SIZE + 1, 1)));
        }
        @Test void colBig() throws Exception {
            assertFalse(invokePrivateValidShot(game, createPosition(1, Fleet.BOARD_SIZE + 1)));
        }
        @Test void colNeg() throws Exception {
            assertFalse(invokePrivateValidShot(game, createPosition(1, -1)));
        }
        @Test void rowEqBoundary() throws Exception {
            assertTrue(invokePrivateValidShot(game, createPosition(Fleet.BOARD_SIZE, 5)));
        }
        @Test void colEqBoundary() throws Exception {
            assertTrue(invokePrivateValidShot(game, createPosition(5, Fleet.BOARD_SIZE)));
        }
    }

    // -----------------------------------------------------------
    @Nested
    @DisplayName("repeatedShot() Tests via Reflection")
    class RepeatedShotTests {

        @Test
        void none() throws Exception {
            assertFalse(invokePrivateRepeatedShot(game, createPosition(5, 5)));
        }

        @Test
        void repeatedSameInstance() throws Exception {
            IPosition p = createPosition(6, 6);
            game = new Game(fleetProxy); ensureInitialized(game);
            game.fire(p);
            assertTrue(invokePrivateRepeatedShot(game, p));
        }

        @Test
        void repeatedEqualDifferentProxy() throws Exception {
            IPosition p1 = createPosition(7, 7);
            IPosition p2 = createPosition(7, 7);

            game = new Game(fleetProxy); ensureInitialized(game);
            game.fire(p1);

            assertTrue(invokePrivateRepeatedShot(game, p2));
        }

        @Test
        void multipleNoMatch() throws Exception {
            IPosition p1 = createPosition(1, 1);
            IPosition p2 = createPosition(2, 2);
            IPosition p3 = createPosition(3, 3);

            game = new Game(fleetProxy); ensureInitialized(game);
            game.fire(p1);
            game.fire(p2);

            assertFalse(invokePrivateRepeatedShot(game, p3));
        }
    }

    // -----------------------------------------------------------
    @Nested
    @DisplayName("Print Methods Tests")
    class PrintTests {

        @Test
        void printBoard() {
            assertDoesNotThrow(() ->
                    game.printBoard(List.of(createPosition(0, 0)), 'X'));
        }

        @Test
        void printValidShots() {
            assertDoesNotThrow(game::printValidShots);
        }

        @Test
        void printFleet() {
            IPosition p = createPosition(1, 1);
            IShip s = createShip(List.of(p), new AtomicBoolean(false), true);
            shipsSupplier = () -> List.of(s);

            game = new Game(fleetProxy); ensureInitialized(game);
            assertDoesNotThrow(game::printFleet);
        }
    }


    // ======================================================================
    // Reflection helpers
    // ======================================================================

    private boolean invokePrivateValidShot(Game g, IPosition p) throws Exception {
        Method m = Game.class.getDeclaredMethod("validShot", IPosition.class);
        m.setAccessible(true);
        return (boolean) m.invoke(g, p);
    }

    private boolean invokePrivateRepeatedShot(Game g, IPosition p) throws Exception {
        Method m = Game.class.getDeclaredMethod("repeatedShot", IPosition.class);
        m.setAccessible(true);
        return (boolean) m.invoke(g, p);
    }

    // Ensure internal mutable fields are initialized
    private void ensureInitialized(Game g) {
        try {
            for (String field : List.of(
                    "countHits", "countSinks", "countInvalidShots", "countRepeatedShots", "shots")) {

                Field f = Game.class.getDeclaredField(field);
                f.setAccessible(true);
                if (f.get(g) == null) {
                    if (field.equals("shots")) f.set(g, new ArrayList<IPosition>());
                    else f.set(g, 0);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Initialization failed", e);
        }
    }
}
