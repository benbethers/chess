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
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();
        int j;

        // Check upper left squares
        j = col - 1;
        for (int i = row + 1; board.inBounds(i, j); i++) {
            if (!board.squareOccupied(i, j)) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(i, j),
                        null
                    )
                );
            } else if (board.squareOccupied(i, j) && board.getPiece(new ChessPosition(i, j)).pieceColor != pieceColor) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(i, j),
                        null
                    )
                );
                break;
            } else {
                break;
            }
            j--;
        }

        // Check lower left squares
        j = col - 1;
        for (int i = row - 1; board.inBounds(i, j); i--) {
            if (!board.squareOccupied(i, j)) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(i, j),
                        null
                    )
                );
            } else if (board.squareOccupied(i, j) && board.getPiece(new ChessPosition(i, j)).pieceColor != pieceColor) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(i, j),
                        null
                    )
                );
                break;
            } else {
                break;
            }
            j--;
        }

        // Check lower right squares
        j = row - 1;
        for (int i = col + 1; board.inBounds(j, i); i++) {
            if (!board.squareOccupied(j, i)) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(j, i),
                        null
                    )
                );
            } else if (board.squareOccupied(j, i) && board.getPiece(new ChessPosition(j, i)).pieceColor != pieceColor) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(j, i),
                        null
                    )
                );
                break;
            } else {
                break;
            }
            j--;
        }

        // Check upper right squares
        j = row + 1;
        for (int i = col + 1; board.inBounds(j, i); i++) {
            if (!board.squareOccupied(j, i)) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(j, i),
                        null
                    )
                );
            } else if (board.squareOccupied(j, i) && board.getPiece(new ChessPosition(j, i)).pieceColor != pieceColor) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(j, i),
                        null
                    )
                );
                break;
            } else {
                break;
            }
            j++;
        }

        return possibleMoves;
    }

    public Collection<ChessMove> findRookMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Check upper squares
        for (int i = row + 1; board.inBounds(i, col); i++) {
            if (!board.squareOccupied(i, col)) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(i, col),
                        null
                    )
                );
            } else if (board.squareOccupied(i, col) && board.getPiece(new ChessPosition(i, col)).pieceColor != pieceColor) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(i, col),
                        null
                    )
                );
                break;
            } else {
                break;
            }
        }

        // Check lower squares
        for (int i = row - 1; board.inBounds(i, col); i--) {
            if (!board.squareOccupied(i, col)) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(i, col),
                        null
                    )
                );
            } else if (board.squareOccupied(i, col) && board.getPiece(new ChessPosition(i, col)).pieceColor != pieceColor) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(i, col),
                        null
                    )
                );
                break;
            } else {
                break;
            }
        }

        // Check left squares
        for (int i = col - 1; board.inBounds(row, i); i--) {
            if (!board.squareOccupied(row, i)) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(row, i),
                        null
                    )
                );
            } else if (board.squareOccupied(row, i) && board.getPiece(new ChessPosition(row, i)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row, i),
                                null
                        )
                );
                break;
            } else {
                break;
            }
        }

        // Check right squares
        for (int i = col + 1; board.inBounds(row, i); i++) {
            if (!board.squareOccupied(row, i)) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(row, i),
                        null
                    )
                );
            } else if (board.squareOccupied(row, i) && board.getPiece(new ChessPosition(row, i)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row, i),
                                null
                        )
                );
                break;
            } else {
                break;
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
        if (board.inBounds(row + verticalProgression, col) && !board.squareOccupied(row + verticalProgression, col)) {
            if (row + verticalProgression == 1 || row + verticalProgression == 8) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col),
                                PieceType.BISHOP
                        )
                );
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col),
                                PieceType.QUEEN
                        )
                );
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col),
                                PieceType.ROOK
                        )
                );
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col),
                                PieceType.KNIGHT
                        )
                );
            } else {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col),
                                null
                        )
                );
            }
        }

        // Possible pawn capture right
        if (
            board.inBounds(row + verticalProgression, col + 1)
            && board.squareOccupied(row + verticalProgression, col + 1)
            && board.getPiece(new ChessPosition(row + verticalProgression, col + 1)).pieceColor != this.pieceColor
        ) {
            if (row + verticalProgression == 1 || row+ verticalProgression == 8) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col + 1),
                                PieceType.BISHOP
                        )
                );
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col + 1),
                                PieceType.QUEEN
                        )
                );
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col + 1),
                                PieceType.ROOK
                        )
                );
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col + 1),
                                PieceType.KNIGHT
                        )
                );
            } else {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col + 1),
                                null
                        )
                );
            }
        }

        // Possible pawn capture left
        if (
            board.inBounds(row + verticalProgression, col - 1)
            && board.squareOccupied(row + verticalProgression, col - 1)
            && board.getPiece(new ChessPosition(row + verticalProgression, col - 1)).pieceColor != this.pieceColor
        ) {
            if (row + verticalProgression == 1 || row + verticalProgression == 8) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col - 1),
                                PieceType.BISHOP
                        )
                );
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col - 1),
                                PieceType.QUEEN
                        )
                );
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col - 1),
                                PieceType.ROOK
                        )
                );
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col - 1),
                                PieceType.KNIGHT
                        )
                );
            } else {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + verticalProgression, col - 1),
                                null
                        )
                );
            }
        }

        //Starting double white move
        if (
            this.pieceColor == ChessGame.TeamColor.WHITE
            && myPosition.getRow() == 2
            && !board.squareOccupied(row + 1, col)
            && !board.squareOccupied(row + 2, col)
        ) {
            possibleMoves.add(
                new ChessMove(
                    myPosition,
                    new ChessPosition(row + 2, col),
                    null
                )
            );
        }

        // Starting double black move
        else if (
            this.pieceColor == ChessGame.TeamColor.BLACK
            && myPosition.getRow() == 7
            && !board.squareOccupied(row - 1, col)
            && !board.squareOccupied(row - 2, col)
        ) {
            possibleMoves.add(
                new ChessMove(
                    myPosition,
                    new ChessPosition(row - 2, col),
                    null
                )
            );
        }

        return possibleMoves;
    }

    public Collection<ChessMove> findKnightMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Check left up
        if (board.inBounds(row + 1, col - 2)) {
            if (!board.squareOccupied(row + 1, col - 2)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 1, col - 2),
                                null
                        )
                );
            } else if (board.squareOccupied(row + 1, col - 2) && board.getPiece(new ChessPosition(row + 1, col - 2)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 1, col - 2),
                                null
                        )
                );
            }
        }

        // Check left down
        if (board.inBounds(row - 1, col - 2)) {
            if (!board.squareOccupied(row - 1, col - 2)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 1, col - 2),
                                null
                        )
                );
            } else if (board.squareOccupied(row - 1, col - 2) && board.getPiece(new ChessPosition(row - 1, col - 2)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 1, col - 2),
                                null
                        )
                );
            }
        }

        //Check up left
        if (board.inBounds(row + 2, col - 1)) {
            if (!board.squareOccupied(row + 2, col - 1)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 2, col - 1),
                                null
                        )
                );
            } else if (board.squareOccupied(row + 2, col - 1) && board.getPiece(new ChessPosition(row + 2, col - 1)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 2, col - 1),
                                null
                        )
                );
            }
        }

        // Check up right
        if (board.inBounds(row + 2, col + 1)) {
            if (!board.squareOccupied(row + 2, col + 1)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 2, col + 1),
                                null
                        )
                );
            } else if (board.squareOccupied(row + 2, col + 1) && board.getPiece(new ChessPosition(row + 2, col + 1)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 2, col + 1),
                                null
                        )
                );
            }
        }

        //Check right up
        if (board.inBounds(row + 1, col + 2)) {
            if (!board.squareOccupied(row + 1, col + 2)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 1, col + 2),
                                null
                        )
                );
            } else if (board.squareOccupied(row + 1, col + 2) && board.getPiece(new ChessPosition(row + 1, col + 2)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 1, col + 2),
                                null
                        )
                );
            }
        }

        // Check right down
        if (board.inBounds(row - 1, col + 2)) {
            if (!board.squareOccupied(row - 1, col + 2)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 1, col + 2),
                                null
                        )
                );
            } else if (board.squareOccupied(row - 1, col + 2) && board.getPiece(new ChessPosition(row - 1, col + 2)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 1, col + 2),
                                null
                        )
                );
            }
        }

        // Check down left
        if (board.inBounds(row - 2, col - 1)) {
            if (!board.squareOccupied(row - 2, col - 1)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 2, col - 1),
                                null
                        )
                );
            } else if (board.squareOccupied(row - 2, col - 1) && board.getPiece(new ChessPosition(row - 2, col - 1)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 2, col - 1),
                                null
                        )
                );
            }
        }

        //Check down right
        if (board.inBounds(row - 2, col + 1)) {
            if (!board.squareOccupied(row - 2, col + 1)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 2, col + 1),
                                null
                        )
                );
            } else if (board.squareOccupied(row - 2, col + 1) && board.getPiece(new ChessPosition(row - 2, col + 1)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 2, col + 1),
                                null
                        )
                );
            }
        }

        return possibleMoves;
    }

    public Collection<ChessMove> findKingMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int row = myPosition.getRow();
        int col = myPosition.getColumn();

        // Check upper left
        if (board.inBounds(row + 1, col - 1)) {
            if (!board.squareOccupied(row + 1, col - 1)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 1, col - 1),
                                null
                        )
                );
            } else if (board.squareOccupied(row + 1, col - 1) && board.getPiece(new ChessPosition(row + 1, col - 1)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 1, col - 1),
                                null
                        )
                );
            }
        }

        // Check upper
        if (board.inBounds(row + 1, col)) {
            if (!board.squareOccupied(row + 1, col)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 1, col),
                                null
                        )
                );
            } else if (board.squareOccupied(row + 1, col) && board.getPiece(new ChessPosition(row + 1, col)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 1, col),
                                null
                        )
                );
            }
        }

        // Check upper right
        if (board.inBounds(row + 1, col + 1)) {
            if (!board.squareOccupied(row + 1, col + 1)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 1, col + 1),
                                null
                        )
                );
            } else if (board.squareOccupied(row + 1, col + 1) && board.getPiece(new ChessPosition(row + 1, col + 1)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row + 1, col + 1),
                                null
                        )
                );
            }
        }

        // Check left
        if (board.inBounds(row, col - 1)) {
            if (!board.squareOccupied(row, col - 1)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row, col - 1),
                                null
                        )
                );
            } else if (board.squareOccupied(row, col - 1) && board.getPiece(new ChessPosition(row, col - 1)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row, col - 1),
                                null
                        )
                );
            }
        }

        // Check right
        if (board.inBounds(row, col + 1)) {
            if (!board.squareOccupied(row, col + 1)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row, col + 1),
                                null
                        )
                );
            } else if (board.squareOccupied(row, col + 1) && board.getPiece(new ChessPosition(row, col + 1)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row, col + 1),
                                null
                        )
                );
            }
        }

        // Check down left
        if (board.inBounds(row - 1, col - 1)) {
            if (!board.squareOccupied(row - 1, col - 1)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 1, col - 1),
                                null
                        )
                );
            } else if (board.squareOccupied(row - 1, col - 1) && board.getPiece(new ChessPosition(row - 1, col - 1)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 1, col - 1),
                                null
                        )
                );
            }
        }

        //Check down
        if (board.inBounds(row - 1, col)) {
            if (!board.squareOccupied(row - 1, col)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 1, col),
                                null
                        )
                );
            } else if (board.squareOccupied(row - 1, col) && board.getPiece(new ChessPosition(row - 1, col)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 1, col),
                                null
                        )
                );
            }
        }

        //Check down right
        if (board.inBounds(row - 1, col + 1)) {
            if (!board.squareOccupied(row - 1, col + 1)) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 1, col + 1),
                                null
                        )
                );
            } else if (board.squareOccupied(row - 1, col + 1) && board.getPiece(new ChessPosition(row - 1, col + 1)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row - 1, col + 1),
                                null
                        )
                );
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
