package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
     * The various different chess piece options
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
        Collection<ChessMove> possibleMoves = new ArrayList<>();

        switch (type) {
            case PAWN:
                try {
                    possibleMoves = this.findPawnMoves(board, myPosition);
                } catch (Exception e){
                    break;
                }
                break;
            case ROOK:
                try {
                    possibleMoves = this.findRookMoves(board, myPosition);
                } catch (Exception e){
                    break;
                }
                break;
            case BISHOP:
                try {
                    possibleMoves = this.findBishopMoves(board, myPosition);
                } catch (Exception e){
                    break;
                }
                break;
            case KNIGHT:
                break;
            case QUEEN:
                break;
            case KING:
                break;
        }

        return possibleMoves;
    }

    public Collection<ChessMove> findBishopMoves(ChessBoard board, ChessPosition myPosition) throws Exception {
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
                        this.type
                    )
                );
            } else if (board.squareOccupied(i, j) && board.getPiece(new ChessPosition(i, j)).pieceColor != pieceColor) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(i, j),
                        this.type
                    )
                );
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
                        this.type
                    )
                );
            } else if (board.squareOccupied(i, j) && board.getPiece(new ChessPosition(i, j)).pieceColor != pieceColor) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(i, j),
                        this.type
                    )
                );
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
                        this.type
                    )
                );
            } else if (board.squareOccupied(j, i) && board.getPiece(new ChessPosition(j, i)).pieceColor != pieceColor) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(j, i),
                        this.type
                    )
                );
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
                        this.type
                    )
                );
            } else if (board.squareOccupied(j, i) && board.getPiece(new ChessPosition(j, i)).pieceColor != pieceColor) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(j, i),
                        this.type
                    )
                );
            } else {
                break;
            }
            j++;
        }

        return possibleMoves;
    }

    public Collection<ChessMove> findRookMoves(ChessBoard board, ChessPosition myPosition) throws Exception {
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
                        this.type
                    )
                );
            } else if (board.squareOccupied(i, col) && board.getPiece(new ChessPosition(i, col)).pieceColor != pieceColor) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(i, col),
                        this.type
                    )
                );
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
                        this.type
                    )
                );
            } else if (board.squareOccupied(i, col) && board.getPiece(new ChessPosition(i, col)).pieceColor != pieceColor) {
                possibleMoves.add(
                    new ChessMove(
                        myPosition,
                        new ChessPosition(i, col),
                        this.type
                    )
                );
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
                        this.type
                    )
                );
            } else if (board.squareOccupied(row, i) && board.getPiece(new ChessPosition(row, i)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row, i),
                                this.type
                        )
                );
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
                        this.type
                    )
                );
            } else if (board.squareOccupied(row, i) && board.getPiece(new ChessPosition(row, i)).pieceColor != pieceColor) {
                possibleMoves.add(
                        new ChessMove(
                                myPosition,
                                new ChessPosition(row, i),
                                this.type
                        )
                );
            } else {
                break;
            }
        }

        return possibleMoves;
    }

    public Collection<ChessMove> findPawnMoves(ChessBoard board, ChessPosition myPosition) throws Exception {
        Collection<ChessMove> possibleMoves = new ArrayList<>();
        int verticalProgression = 0;

        //Determine if pawn is moving backward or forward
        if (this.pieceColor == ChessGame.TeamColor.WHITE) {
            verticalProgression = 1;
        } else if (this.pieceColor == ChessGame.TeamColor.BLACK){
            verticalProgression = -1;
        }
        // Regular pawn advance
        if (!board.squareOccupied(myPosition.getRow() + verticalProgression, myPosition.getColumn())) {
            possibleMoves.add(
                new ChessMove(
                    myPosition,
                    new ChessPosition(myPosition.getRow() + verticalProgression, myPosition.getColumn()),
                    this.type
                )
            );
        }
        // Possible pawn capture right
        if (
            board.squareOccupied(myPosition.getRow() + verticalProgression, (myPosition.getColumn()) + 1)
            && board.getPiece(new ChessPosition(myPosition.getRow() + verticalProgression, myPosition.getColumn() + 1)).pieceColor != this.pieceColor
        ) {
            possibleMoves.add(
                new ChessMove(
                    myPosition,
                    new ChessPosition(myPosition.getRow() + verticalProgression, myPosition.getColumn() + 1),
                    this.type
                )
            );
        }
        // Possible pawn capture left
        if (
            board.squareOccupied(myPosition.getRow() + verticalProgression, (myPosition.getColumn()) - 1)
            && board.getPiece(new ChessPosition(myPosition.getRow() + verticalProgression, myPosition.getColumn() - 1)).pieceColor != this.pieceColor
        ) {
            possibleMoves.add(
                new ChessMove(
                    myPosition,
                    new ChessPosition(myPosition.getRow() + verticalProgression, myPosition.getColumn() - 1),
                    this.type
                )
            );
        }
        //Starting double white move
        if (
            this.pieceColor == ChessGame.TeamColor.WHITE
            && myPosition.getRow() == 2
            && !board.squareOccupied(myPosition.getRow() + 1, (myPosition.getColumn()))
            && !board.squareOccupied(myPosition.getRow() + 2, (myPosition.getColumn()))
        ) {
            possibleMoves.add(
                new ChessMove(
                    myPosition,
                    new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn()),
                    this.type
                )
            );
        }
        // Starting double black move
        else if (
            this.pieceColor == ChessGame.TeamColor.BLACK
            && myPosition.getRow() == 7
            && !board.squareOccupied(myPosition.getRow() - 1, (myPosition.getColumn()))
            && !board.squareOccupied(myPosition.getRow() - 2, (myPosition.getColumn()))
        ) {
            possibleMoves.add(
                new ChessMove(
                    myPosition,
                    new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn()),
                    this.type
                )
            );
        }

        return possibleMoves;
    }
}
