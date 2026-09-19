package chess;

import java.util.Arrays;

/**
 * A chessboard that can hold and rearrange chess pieces.
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessBoard {

    ChessPiece[][] squares = new ChessPiece[8][8];
    public ChessBoard() {}

    /**
     * Adds a chess piece to the chessboard
     *
     * @param position where to add the piece to
     * @param piece    the piece to add
     */
    public void addPiece(ChessPosition position, ChessPiece piece) {
        squares[position.getRow()-1][position.getColumn()-1] = piece;
    }

    /**
     * Gets a chess piece on the chessboard
     *
     * @param position The position to get the piece from
     * @return Either the piece at the position, or null if no piece is at that
     * position
     */
    public ChessPiece getPiece(ChessPosition position) {
        return squares[position.getRow() - 1][position.getColumn() - 1];
    }

    public boolean squareOccupied(int row, int col) {
        ChessPosition testPosition = new ChessPosition(row, col);
        if (this.getPiece(testPosition) != null) {
            return true;
        }
        return false;
    }

    public boolean inBounds(int row, int col) {
        if (row > 8 || row < 1 || col > 8 || col < 1) {
            return false;
        }
        return true;
    }

    /**
     * Sets the board to the default starting board
     * (How the game of chess normally starts)
     */
    public void resetBoard() {
        this.squares = new ChessPiece[8][8];

        // Add white pawns
        for (int i = 1; this.inBounds(2, i); i++) {
            this.addPiece(new ChessPosition(2, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN));
        }

        // Add white specials
        for (int i = 1; this.inBounds(1, i); i++) {
            if (i == 1 || i == 8) {
                this.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.ROOK));
            } else if (i == 2 || i == 7) {
                this.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KNIGHT));
            } else if (i == 3 || i == 6) {
                this.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.BISHOP));
            } else if (i == 4) {
                this.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.QUEEN));
            } else if (i == 5) {
                this.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.KING));
            }
        }

        // Add black pawns
        for (int i = 1; this.inBounds(7, i); i++) {
            this.addPiece(new ChessPosition(7, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.PAWN));
        }

        // Add black specials
        for (int i = 1; this.inBounds(1, i); i++) {
            if (i == 1 || i == 8) {
                this.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.ROOK));
            } else if (i == 2 || i == 7) {
                this.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KNIGHT));
            } else if (i == 3 || i == 6) {
                this.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP));
            } else if (i == 4) {
                this.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.QUEEN));
            } else if (i == 5) {
                this.addPiece(new ChessPosition(1, i), new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.KING));
            }
        }
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ChessBoard other) {
            if (Arrays.deepEquals(this.squares, other.squares)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.deepHashCode(this.squares);
    }
}
