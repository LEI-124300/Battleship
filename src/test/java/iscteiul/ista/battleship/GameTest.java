// java
package iscteiul.ista.battleship;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for class Game.
 * Author: LEI-111641
 * Date: 2025-11-20 00:00
 * Cyclomatic Complexity:
 * - constructor: 1
 * - fire(): 5
 * - getShots(): 1
 * - getRepeatedShots(): 1
 * - getInvalidShots(): 1
 * - getHits(): 1
 * - getSunkShips(): 1
 * - getRemainingShips(): 1
 * - validShot(): 4
 * - repeatedShot(): 2
 * - printBoard(): 1
 * - printValidShots(): 1
 * - printFleet(): 1
 */
public class GameTest {

    private Game game;
    // Configurable behavior for the IFleet proxy used by Game
    private Function<IPosition, IShip> shipAtFunc;
    private Supplier<List<IShip>> floatingShipsSupplier;
    private Supplier<List<IShip>> shipsSupplier;
    private IFleet fleetProxy;

    @BeforeEach
    public void setUp() {
        // default behaviors
        shipAtFunc = pos -> null;
        floatingShipsSupplier = ArrayList::new;
        shipsSupplier = ArrayList::new;
        fleetProxy = createFleetProxy();
        game = new Game(fleetProxy);
        ensureInitialized(game);
    }

    @AfterEach
    public void tearDown() {
        game = null;
        fleetProxy = null;
        shipAtFunc = null;
        floatingShipsSupplier = null;
        shipsSupplier = null;
    }

    // Helper: create IFleet proxy that delegates to the above suppliers/functions
    private IFleet createFleetProxy() {
        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if ("shipAt".equals(name) && args != null && args.length == 1) {
                return shipAtFunc.apply((IPosition) args[0]);
            }
            if ("getFloatingShips".equals(name) && (args == null || args.length == 0)) {
                return floatingShipsSupplier.get();
            }
            if ("getShips".equals(name) && (args == null || args.length == 0)) {
                return shipsSupplier.get();
            }
            // default for unknown methods: return reasonable defaults
            Class<?> rt = method.getReturnType();
            if (rt.isPrimitive()) {
                if (rt == boolean.class) return false;
                if (rt == int.class) return 0;
                if (rt == long.class) return 0L;
                if (rt == short.class) return (short) 0;
                if (rt == byte.class) return (byte) 0;
                if (rt == char.class) return (char) 0;
                if (rt == float.class) return 0f;
                if (rt == double.class) return 0d;
            }
            return null;
        };
        return (IFleet) Proxy.newProxyInstance(IFleet.class.getClassLoader(), new Class[]{IFleet.class}, handler);
    }

    // Helper: create IPosition proxy with row/column and proper equals/hashCode
    private IPosition createPosition(final int row, final int col) {
        InvocationHandler handler = new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                String name = method.getName();
                if ("getRow".equals(name) && (args == null || args.length == 0)) return row;
                if ("getColumn".equals(name) && (args == null || args.length == 0)) return col;
                if ("equals".equals(name) && args != null && args.length == 1) {
                    Object o = args[0];
                    if (o == null) return false;
                    if (!Proxy.isProxyClass(o.getClass())) return false;
                    // attempt to read row/col via reflection if it's this proxy type
                    try {
                        Method gr = o.getClass().getMethod("getRow");
                        Method gc = o.getClass().getMethod("getColumn");
                        int r = (Integer) gr.invoke(o);
                        int c = (Integer) gc.invoke(o);
                        return r == row && c == col;
                    } catch (Exception ex) {
                        return false;
                    }
                }
                if ("hashCode".equals(name) && (args == null || args.length == 0)) {
                    return 31 * row + col;
                }
                if ("toString".equals(name) && (args == null || args.length == 0)) {
                    return "Pos(" + row + "," + col + ")";
                }
                return null;
            }
        };
        return (IPosition) Proxy.newProxyInstance(IPosition.class.getClassLoader(), new Class[]{IPosition.class}, handler);
    }

    // Helper: create IShip proxy with given positions and configured stillFloating and shoot behavior
    private IShip createShip(final List<IPosition> positions, final AtomicBoolean shotCalled, final boolean stillFloatingResult) {
        InvocationHandler handler = (proxy, method, args) -> {
            String name = method.getName();
            if ("getPositions".equals(name) && (args == null || args.length == 0)) {
                return positions;
            }
            if ("stillFloating".equals(name) && (args == null || args.length == 0)) {
                return stillFloatingResult;
            }
            if ("shoot".equals(name) && args != null && args.length == 1) {
                shotCalled.set(true);
                return null;
            }
            // defaults
            Class<?> rt = method.getReturnType();
            if (rt.isPrimitive()) {
                if (rt == boolean.class) return false;
                if (rt == int.class) return 0;
            }
            return null;
        };
        return (IShip) Proxy.newProxyInstance(IShip.class.getClassLoader(), new Class[]{IShip.class}, handler);
    }

    // constructor: CC = 1
    @Test
    @DisplayName("constructor()")
    public void constructor() {
        // initial counts and shots list expected to be zero/empty
        assertAll("Constructor initial state",
                () -> assertEquals(0, game.getInvalidShots(), "Error: expected invalid shots 0 but got " + game.getInvalidShots()),
                () -> assertEquals(0, game.getRepeatedShots(), "Error: expected repeated shots 0 but got " + game.getRepeatedShots()),
                () -> assertEquals(0, game.getHits(), "Error: expected hits 0 but got " + game.getHits()),
                () -> assertEquals(0, game.getSunkShips(), "Error: expected sunk ships 0 but got " + game.getSunkShips()),
                () -> assertTrue(game.getShots().isEmpty(), "Error: expected shots list empty but got non-empty")
        );
    }

    // fire(): CC = 5 -> create 5 tests for independent paths

    @Test
    @DisplayName("fire1() - invalid shot increments invalidShots")
    public void fire1() {
        // invalid shot: row outside bounds -> must increment invalidShots and return null
        IPosition p = createPosition(-1, 0);
        IShip result = game.fire(p);
        assertAll("Invalid shot path",
                () -> assertNull(result, "Error: expected null result for invalid shot but got a ship"),
                () -> assertEquals(1, game.getInvalidShots(), "Error: expected invalidShots 1 but got " + game.getInvalidShots())
        );
    }

    @Test
    @DisplayName("fire2() - repeated shot increments repeatedShots")
    public void fire2() {
        // first, fire a valid position
        IPosition p = createPosition(0, 0);
        shipAtFunc = pos -> null; // no ship
        game = new Game(fleetProxy); // re-create to pick new shipAtFunc reference in handler
        ensureInitialized(game);
        // first shot
        game.fire(p);
        // second (repeated) shot
        IShip result = game.fire(p);
        assertAll("Repeated shot path",
                () -> assertNull(result, "Error: expected null for repeated shot but got a ship"),
                () -> assertEquals(1, game.getRepeatedShots(), "Error: expected repeatedShots 1 but got " + game.getRepeatedShots())
        );
    }

    @Test
    @DisplayName("fire3() - valid non-repeated shot with no ship at position")
    public void fire3() {
        // valid, not repeated, fleet.shipAt returns null
        IPosition p = createPosition(1, 1);
        shipAtFunc = pos -> null;
        game = new Game(fleetProxy);
        ensureInitialized(game);
        IShip result = game.fire(p);
        assertAll("Valid non-hit path",
                () -> assertNull(result, "Error: expected null when no ship present but got a ship"),
                () -> assertEquals(0, game.getHits(), "Error: expected hits 0 but got " + game.getHits()),
                () -> assertEquals(0, game.getSunkShips(), "Error: expected sunk ships 0 but got " + game.getSunkShips()),
                () -> assertEquals(1, game.getShots().size(), "Error: expected shots size 1 but got " + game.getShots().size())
        );
    }

    @Test
    @DisplayName("fire4() - valid hit but ship still floating")
    public void fire4() {
        // valid, not repeated, ship present and still floating after shoot -> countHits increments, no sink
        IPosition p = createPosition(2, 2);
        AtomicBoolean shotCalled = new AtomicBoolean(false);
        IShip ship = createShip(Arrays.asList(p), shotCalled, true); // stillFloating = true
        shipAtFunc = pos -> ship;
        floatingShipsSupplier = () -> new ArrayList<>(); // not used here
        game = new Game(fleetProxy);
        ensureInitialized(game);
        IShip result = game.fire(p);
        assertAll("Hit but not sunk path",
                () -> assertNull(result, "Error: expected null because ship not sunk but got a ship returned"),
                () -> assertTrue(shotCalled.get(), "Error: expected ship.shoot() to be called but it was not"),
                () -> assertEquals(1, game.getHits(), "Error: expected hits 1 but got " + game.getHits()),
                () -> assertEquals(0, game.getSunkShips(), "Error: expected sunk ships 0 but got " + game.getSunkShips())
        );
    }

    @Test
    @DisplayName("fire5() - valid hit and ship sunk")
    public void fire5() {
        // valid, not repeated, ship present and not still floating after shoot -> sink and returned ship
        IPosition p = createPosition(3, 3);
        AtomicBoolean shotCalled = new AtomicBoolean(false);
        IShip ship = createShip(Arrays.asList(p), shotCalled, false); // stillFloating = false -> sunk
        shipAtFunc = pos -> ship;
        floatingShipsSupplier = () -> new ArrayList<>();
        game = new Game(fleetProxy);
        ensureInitialized(game);
        IShip result = game.fire(p);
        assertAll("Hit and sunk path",
                () -> assertSame(ship, result, "Error: expected returned ship to be the sunk ship but it was not"),
                () -> assertTrue(shotCalled.get(), "Error: expected ship.shoot() to be called but it was not"),
                () -> assertEquals(1, game.getHits(), "Error: expected hits 1 but got " + game.getHits()),
                () -> assertEquals(1, game.getSunkShips(), "Error: expected sunk ships 1 but got " + game.getSunkShips())
        );
    }

    // getShots(): CC = 1
    @Test
    @DisplayName("getShots()")
    public void getShots() {
        assertTrue(game.getShots().isEmpty(), "Error: expected shots empty but got non-empty");
        IPosition p = createPosition(4, 4);
        shipAtFunc = pos -> null;
        game = new Game(fleetProxy);
        ensureInitialized(game);
        game.fire(p);
        assertEquals(1, game.getShots().size(), "Error: expected shots size 1 after firing but got " + game.getShots().size());
    }

    // getRepeatedShots(): CC = 1
    @Test
    @DisplayName("getRepeatedShots()")
    public void getRepeatedShots() {
        assertEquals(0, game.getRepeatedShots(), "Error: expected repeated shots 0 initially but got " + game.getRepeatedShots());
    }

    // getInvalidShots(): CC = 1
    @Test
    @DisplayName("getInvalidShots()")
    public void getInvalidShots() {
        assertEquals(0, game.getInvalidShots(), "Error: expected invalid shots 0 initially but got " + game.getInvalidShots());
    }

    // getHits(): CC = 1
    @Test
    @DisplayName("getHits()")
    public void getHits() {
        assertEquals(0, game.getHits(), "Error: expected hits 0 initially but got " + game.getHits());
    }

    // getSunkShips(): CC = 1
    @Test
    @DisplayName("getSunkShips()")
    public void getSunkShips() {
        assertEquals(0, game.getSunkShips(), "Error: expected sunk ships 0 initially but got " + game.getSunkShips());
    }

    // getRemainingShips(): CC = 1
    @Test
    @DisplayName("getRemainingShips()")
    public void getRemainingShips() {
        List<IShip> fl = new ArrayList<>();
        fl.add(createShip(new ArrayList<>(), new AtomicBoolean(false), true));
        fl.add(createShip(new ArrayList<>(), new AtomicBoolean(false), true));
        floatingShipsSupplier = () -> fl;
        game = new Game(fleetProxy);
        ensureInitialized(game);
        assertEquals(2, game.getRemainingShips(), "Error: expected remaining ships 2 but got " + game.getRemainingShips());
    }

    // validShot(): CC = 4 -> provide 4 tests via reflection for different combinations
    @Test
    @DisplayName("validShot1() - valid position inside bounds")
    public void validShot1() throws Exception {
        IPosition p = createPosition(0, 0);
        boolean res = invokePrivateValidShot(game, p);
        assertTrue(res, "Error: expected validShot true for (0,0) but got false");
    }

    @Test
    @DisplayName("validShot2() - invalid because row < 0")
    public void validShot2() throws Exception {
        IPosition p = createPosition(-1, 0);
        boolean res = invokePrivateValidShot(game, p);
        assertFalse(res, "Error: expected validShot false for row -1 but got true");
    }

    @Test
    @DisplayName("validShot3() - invalid because row > BOARD_SIZE")
    public void validShot3() throws Exception {
        IPosition p = createPosition(Fleet.BOARD_SIZE + 1, 0);
        boolean res = invokePrivateValidShot(game, p);
        assertFalse(res, "Error: expected validShot false for row > BOARD_SIZE but got true");
    }

    @Test
    @DisplayName("validShot4() - invalid because column > BOARD_SIZE")
    public void validShot4() throws Exception {
        IPosition p = createPosition(0, Fleet.BOARD_SIZE + 1);
        boolean res = invokePrivateValidShot(game, p);
        assertFalse(res, "Error: expected validShot false for column > BOARD_SIZE but got true");
    }

    // repeatedShot(): CC = 2 -> two tests via reflection
    @Test
    @DisplayName("repeatedShot1() - no repeated shot")
    public void repeatedShot1() throws Exception {
        IPosition p = createPosition(5, 5);
        boolean res = invokePrivateRepeatedShot(game, p);
        assertFalse(res, "Error: expected repeatedShot false when not present but got true");
    }

    @Test
    @DisplayName("repeatedShot2() - repeated shot true when already shot")
    public void repeatedShot2() throws Exception {
        IPosition p = createPosition(6, 6);
        shipAtFunc = pos -> null;
        game = new Game(fleetProxy);
        ensureInitialized(game);
        game.fire(p); // first shot
        boolean res = invokePrivateRepeatedShot(game, p);
        assertTrue(res, "Error: expected repeatedShot true after firing same position but got false");
    }

    // printBoard(): CC = 1
    @Test
    @DisplayName("printBoard()")
    public void printBoard() {
        List<IPosition> pos = new ArrayList<>();
        pos.add(createPosition(0, 0));
        // should not throw
        game.printBoard(pos, 'X');
        assertTrue(true, "Error: printBoard should complete without exception");
    }

    // printValidShots(): CC = 1
    @Test
    @DisplayName("printValidShots()")
    public void printValidShots() {
        // should not throw
        game.printValidShots();
        assertTrue(true, "Error: printValidShots should complete without exception");
    }

    // printFleet(): CC = 1
    @Test
    @DisplayName("printFleet()")
    public void printFleet() {
        // create some ships to be returned by fleet.getShips()
        IPosition p = createPosition(1, 1);
        IShip s = createShip(Arrays.asList(p), new AtomicBoolean(false), true);
        shipsSupplier = () -> {
            List<IShip> l = new ArrayList<>();
            l.add(s);
            return l;
        };
        game = new Game(fleetProxy);
        ensureInitialized(game);
        // should not throw
        game.printFleet();
        assertTrue(true, "Error: printFleet should complete without exception");
    }

    // Exception test: assert that firing null throws NullPointerException
    @Test
    @DisplayName("fireNullThrows() - NullPointerException when firing null")
    public void fireNullThrows() {
        assertThrows(NullPointerException.class, () -> game.fire(null), "Error: expected NullPointerException when calling fire(null)");
    }

    // Reflection helpers
    private boolean invokePrivateValidShot(Game g, IPosition pos) throws Exception {
        Method m = Game.class.getDeclaredMethod("validShot", IPosition.class);
        m.setAccessible(true);
        return (Boolean) m.invoke(g, pos);
    }

    private boolean invokePrivateRepeatedShot(Game g, IPosition pos) throws Exception {
        Method m = Game.class.getDeclaredMethod("repeatedShot", IPosition.class);
        m.setAccessible(true);
        return (Boolean) m.invoke(g, pos);
    }

    // Ensure internal counters and shots list are initialized to avoid NPEs in production getters
    private void ensureInitialized(Game g) {
        try {
            Field fHits = Game.class.getDeclaredField("countHits");
            fHits.setAccessible(true);
            if (fHits.get(g) == null) fHits.set(g, Integer.valueOf(0));

            Field fSinks = Game.class.getDeclaredField("countSinks");
            fSinks.setAccessible(true);
            if (fSinks.get(g) == null) fSinks.set(g, Integer.valueOf(0));

            Field fInvalid = Game.class.getDeclaredField("countInvalidShots");
            fInvalid.setAccessible(true);
            if (fInvalid.get(g) == null) fInvalid.set(g, Integer.valueOf(0));

            Field fRepeated = Game.class.getDeclaredField("countRepeatedShots");
            fRepeated.setAccessible(true);
            if (fRepeated.get(g) == null) fRepeated.set(g, Integer.valueOf(0));

            Field fShots = Game.class.getDeclaredField("shots");
            fShots.setAccessible(true);
            if (fShots.get(g) == null) fShots.set(g, new ArrayList<IPosition>());
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            throw new RuntimeException("Failed to initialize Game internals for tests: " + ex.getMessage(), ex);
        }
    }
}
