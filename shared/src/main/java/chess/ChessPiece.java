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
        int verticalProgression = 0;
        int horizonalProgression = 0;

        switch (type) {
            case PAWN:
                try {
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
                    if (board.squareOccupied(myPosition.getRow() + verticalProgression, myPosition.getColumn() + 1)) {
                        possibleMoves.add(
                                new ChessMove(
                                        myPosition,
                                        new ChessPosition(myPosition.getRow() + verticalProgression, myPosition.getColumn() + 1),
                                        this.type
                                )
                        );
                    }
                    // Possible pawn capture left
                    if (board.squareOccupied(myPosition.getRow() + verticalProgression, (myPosition.getColumn()) - 1)) {
                        possibleMoves.add(
                                new ChessMove(
                                        myPosition,
                                        new ChessPosition(myPosition.getRow() + verticalProgression, myPosition.getColumn() - 1),
                                        this.type
                                )
                        );
                    }
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
                    } else if (
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
                } catch (Exception e){
                    break;
                }
                break;
            case ROOK:
                break;
            case BISHOP:
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
}
