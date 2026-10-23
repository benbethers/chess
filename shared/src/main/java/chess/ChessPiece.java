
package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private ChessGame.TeamColor pieceColor;
    private ChessPiece.PieceType type;


    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece opstions
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        switch (type) {
            case PAWN:
                return this.findPawnMoves(board, myPosition);
            case ROOK:
                return this.findRookMoves(board, myPosition);
            case BISHOP:
                return this.findBishopMoves(board, myPosition);
            case KNIGHT:
                return this.findKnightMoves(board, myPosition);
            case QUEEN:
                return this.findQueenMoves(board, myPosition);
            case KING:
                return this.findKingMoves(board, myPosition);
        }

        return new ArrayList<>();
    }

    public Collection<ChessMove> findQueenMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        possibleMoves.addAll(this.findBishopMoves(board, myPosition));
        possibleMoves.addAll(this.findRookMoves(board, myPosition));

        return possibleMoves;
    }

    public Collection<ChessMove> findBishopMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1, -1},
                {-1, -1},
                {-1, 1},
                {1, 1}
        };

        return findSlidingMoves(board, myPosition, directions);
    }

    public Collection<ChessMove> findRookMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] directions = {
                {1, 0},
                {-1, 0},
                {0, -1},
                {0, 1}
        };

        return findSlidingMoves(board, myPosition, directions);
    }

    private Collection<ChessMove> findSlidingMoves(
            ChessBoard board,
            ChessPosition myPosition,
            int[][] directions) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int[] direction : directions) {
            int nextRow = row + direction[0];
            int nextCol = col + direction[1];

            while (board.inBounds(nextRow, nextCol)) {
                ChessPosition destination = new ChessPosition(nextRow, nextCol);
                ChessPiece target = board.getPiece(destination);

                if (target == null) {
                    possibleMoves.add(new ChessMove(myPosition, destination, null));
                } else {
                    if (target.getTeamColor() != pieceColor) {
                        possibleMoves.add(new ChessMove(myPosition, destination, null));
                    }
                    break;
                }

                nextRow += direction[0];
                nextCol += direction[1];
            }
        }

        return possibleMoves;
    }

    public Collection<ChessMove> findPawnMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int verticalProgression = 0;

        //Determine if pawn is moving backward or forward
        if (this.pieceColor == ChessGame.TeamColor.WHITE) {
            verticalProgression = 1;
        } else if (this.pieceColor == ChessGame.TeamColor.BLACK){
            verticalProgression = -1;
        }

        // Regular pawn advance
        int nextRow = row + verticalProgression;

        if (board.inBounds(nextRow, col) && !board.squareOccupied(nextRow, col)) {
            addPawnMove(possibleMoves, myPosition, nextRow, col);

            int startingRow = pieceColor == ChessGame.TeamColor.WHITE ? 2 : 7;
            int doubleStepRow = row + 2 * verticalProgression;

            //Starting double white move
            if (this.pieceColor == ChessGame.TeamColor.WHITE
                    && myPosition.getRow() == 2
                    && board.inBounds(doubleStepRow, col)
                    && !board.squareOccupied(doubleStepRow, col)) {
                possibleMoves.add(new ChessMove(
                        myPosition,
                        new ChessPosition(doubleStepRow, col),
                        null
                ));
            }

            // Starting double black move
            else if (this.pieceColor == ChessGame.TeamColor.BLACK
                    && myPosition.getRow() == 7
                    && board.inBounds(doubleStepRow, col)
                    && !board.squareOccupied(doubleStepRow, col)) {
                possibleMoves.add(new ChessMove(
                        myPosition,
                        new ChessPosition(doubleStepRow, col),
                        null
                ));
            }
        }

        // Possible pawn capture right
        addPawnCapture(board, myPosition, possibleMoves, row, col,
                verticalProgression, 1);

        // Possible pawn capture left
        addPawnCapture(board, myPosition, possibleMoves, row, col,
                verticalProgression, -1);

        return possibleMoves;
    }

    private void addPawnMove(
            Collection<ChessMove> possibleMoves,
            ChessPosition myPosition,
            int row,
            int col) {
        ChessPosition destination = new ChessPosition(row, col);

        if (row == 1 || row == 8) {
            possibleMoves.add(new ChessMove(myPosition, destination, PieceType.BISHOP));
            possibleMoves.add(new ChessMove(myPosition, destination, PieceType.QUEEN));
            possibleMoves.add(new ChessMove(myPosition, destination, PieceType.ROOK));
            possibleMoves.add(new ChessMove(myPosition, destination, PieceType.KNIGHT));
        } else {
            possibleMoves.add(new ChessMove(myPosition, destination, null));
        }
    }

    private void addPawnCapture(
            ChessBoard board,
            ChessPosition myPosition,
            Collection<ChessMove> possibleMoves,
            int row,
            int col,
            int verticalProgression,
            int columnOffset) {
        int nextRow = row + verticalProgression;
        int nextCol = col + columnOffset;

        if (!board.inBounds(nextRow, nextCol)
                || !board.squareOccupied(nextRow, nextCol)) {
            return;
        }

        ChessPiece target = board.getPiece(new ChessPosition(nextRow, nextCol));

        if (target.getTeamColor() != pieceColor) {
            addPawnMove(possibleMoves, myPosition, nextRow, nextCol);
        }
    }

    public Collection<ChessMove> findKnightMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] offsets = {
                {1, -2},
                {-1, -2},
                {2, -1},
                {2, 1},
                {1, 2},
                {-1, 2},
                {-2, -1},
                {-2, 1}
        };

        return findStepMoves(board, myPosition, offsets);
    }

    public Collection<ChessMove> findKingMoves(ChessBoard board, ChessPosition myPosition) {
        int[][] offsets = {
                {1, -1},
                {1, 0},
                {1, 1},
                {0, -1},
                {0, 1},
                {-1, -1},
                {-1, 0},
                {-1, 1}
        };

        return findStepMoves(board, myPosition, offsets);
    }

    private Collection<ChessMove> findStepMoves(
            ChessBoard board,
            ChessPosition myPosition,
            int[][] offsets) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        for (int[] offset : offsets) {
            int nextRow = row + offset[0];
            int nextCol = col + offset[1];

            if (!board.inBounds(nextRow, nextCol)) {
                continue;
            }

            ChessPosition destination = new ChessPosition(nextRow, nextCol);
            ChessPiece target = board.getPiece(destination);

            if (target == null || target.getTeamColor() != pieceColor) {
                possibleMoves.add(new ChessMove(myPosition, destination, null));
            }
        }

        return possibleMoves;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ChessPiece other) {
            if (
                    this.pieceColor == other.getTeamColor()
                            && this.type == other.getPieceType()
            ) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return 31 * pieceColor.hashCode() + type.hashCode();
    }
}
