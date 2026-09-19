package chess;

/**
 * Represents a single square position on a chess board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPosition {
    int row;
    int col;

    public ChessPosition(int row, int col) {
        this.row = row;
        this.col = col;
    }

    /**
     * @return which row this position is in
     * 1 codes for the bottom row
     */
    public int getRow() {
        return this.row;
    }

    /**
     * @return which column this position is in
     * 1 codes for the left column
     */
    public int getColumn() {
        return this.col;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ChessPosition other) {
            if (this.row == other.getRow() && this.col == other.getColumn()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * row + col;
    }
}
