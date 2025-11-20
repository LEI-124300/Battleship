/**
 *
 */
package iscteiul.ista.battleship;

public class Frigate extends Ship {
    private static final Integer SIZE = 4;
    private static final String NAME = "Fragata";

    /**
     * @param bearing
     * @param pos
     */
    public Frigate(Compass bearing, IPosition pos) throws IllegalArgumentException {
        super(NAME, bearing, pos);

        switch (bearing) {
            case NORTH:
            case SOUTH:
                for (int r = 0; r < SIZE; r++)
                    getPositions().add(new Position(pos.getRow() + r, pos.getColumn()));
                break;
            case EAST:
            case WEST:
                for (int c = 0; c < SIZE; c++)
                    getPositions().add(new Position(pos.getRow(), pos.getColumn() + c));
                break;
        }
    }

    public static Frigate of(Compass bearing, IPosition pos) {
        if (bearing == null) {
            throw new IllegalArgumentException("ERROR! bearing cannot be null for the frigate");
        }
        return new Frigate(bearing, pos);
    }

    /*
     * (non-Javadoc)
     *
     * @see battleship.Ship#getSize()
     */
    @Override
    public Integer getSize() {
        return Frigate.SIZE;
    }

}
