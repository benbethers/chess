package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    public TeamColor currentTurn;
    public ChessBoard board;

    public ChessGame() {
        currentTurn = TeamColor.WHITE;
        board = new ChessBoard();
        board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        // Get piece in question
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        // Retrieve all a list of all moves and a basic list to add all valid moves
        Collection<ChessMove> moves = piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        // For each move
        for (ChessMove move : moves) {
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());

            // Temporary move
            board.removePiece(move.getStartPosition());
            board.removePiece(move.getEndPosition());
            // If move includes a piece promotion, make that promotion as a test, otherwise make the normal test move
            if (move.getPromotionPiece() != null) {
                board.addPiece(move.getEndPosition(), new ChessPiece(piece.getTeamColor(), move.getPromotionPiece()));
            } else {
                board.addPiece(move.getEndPosition(), piece);
            }

            // Take isolated variable for whether the king is still in check
            boolean inCheck = isInCheck(piece.getTeamColor());

            // Undo temporary move
            board.removePiece(move.getEndPosition());
            board.addPiece(move.getStartPosition(), piece);

            if (capturedPiece != null) {
                board.addPiece(move.getEndPosition(), capturedPiece);
            }

            if (!inCheck) {
                validMoves.add(move);
            }
        }
        return validMoves;
        // return board.getPiece(startPosition).pieceMoves(board, startPosition);
    }

    /**
     * Gets all valid moves for a selected team
     *
     * @param teamColor The team color to return the moves of
     * @return Set of valid moves for all pieces of the selected team
     */
    public Collection<ChessMove> validTeamMoves(TeamColor teamColor) {
        Collection validTeamMoves = new ArrayList<>();
        // For every piece find all valid moves that team can make
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (board.getPiece(new ChessPosition(i, j)) != null) {
                    if (board.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor) {
                        validTeamMoves.addAll(this.validMoves(new ChessPosition(i, j)));
                    }
                }
            }
        }
        return validTeamMoves;
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        // Check if the piece to be moved exists
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("This is an invalid move");
        }
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());
        Collection validMoves = this.validMoves(move.getStartPosition());

        // Check if the proposed move is in the valid moves list
        if (!validMoves.contains(move) || movingPiece.getTeamColor() != this.currentTurn) {
            throw new InvalidMoveException("This is an invalid move");
        }

        // Remove the piece from the starting position
        board.removePiece(move.getStartPosition());
        if (board.getPiece(move.getEndPosition()) != null) {
            board.removePiece(move.getEndPosition());
        }

        // Check if the move includes piece promotion and perform move
        if (move.getPromotionPiece() != null) {
            board.addPiece(move.getEndPosition(), new ChessPiece(movingPiece.getTeamColor(), move.getPromotionPiece()));
        } else {
            board.addPiece(move.getEndPosition(), movingPiece);
        }

        // Change turn after move has been made
        if (currentTurn == TeamColor.WHITE) {
            this.setTeamTurn(TeamColor.BLACK);
        } else {
            this.setTeamTurn(TeamColor.WHITE);
        }
    }

    // Find the position of the king of a team
    public ChessPosition findKing(TeamColor teamColor) {
        // Check every piece to see if it is the team color's king
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                if (board.getPiece(new ChessPosition(i, j)) != null) {
                    if (board.getPiece(new ChessPosition(i, j)).getTeamColor() == teamColor && board.getPiece(new ChessPosition(i, j)).getPieceType() == ChessPiece.PieceType.KING) {
                        return new ChessPosition(i, j);
                    }
                }
            }
        }
        return null;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        // Find the opposing team
        TeamColor opposingTeam;
        if (teamColor == TeamColor.BLACK) {
            opposingTeam = TeamColor.WHITE;
        } else {
            opposingTeam = TeamColor.BLACK;
        }

        // Store king position
        ChessPosition kingPosition = this.findKing(teamColor);

        // For each piece
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                // Store piece and position
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);

                // Check every move to see if it includes something that puts the king under direct danger
                if (piece != null && piece.getTeamColor() == opposingTeam) {
                    for (ChessMove move : piece.pieceMoves(board, position)) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        // Return false if not in check
        if (!this.isInCheck(teamColor)) {
            return false;
        }

        // Check the effects of every move and see if it removes the king from check
        for (ChessMove move : this.validTeamMoves(teamColor)) {
            ChessPiece movingPiece = board.getPiece(move.getStartPosition());
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());

            // Temporarily make move
            board.removePiece(move.getStartPosition());
            board.removePiece(move.getEndPosition());
            board.addPiece(move.getEndPosition(), movingPiece);

            boolean stillInCheck = this.isInCheck(teamColor);

            // Undo temporary move
            board.removePiece(move.getEndPosition());
            board.addPiece(move.getStartPosition(), movingPiece);

            // Add captured piece back if it exists
            if (capturedPiece != null) {
                board.addPiece(move.getEndPosition(), capturedPiece);
            }

            // This move saves the king
            if (!stillInCheck) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return this.validTeamMoves(teamColor).isEmpty() && !this.isInCheck(teamColor);
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        throw new RuntimeException("Not implemented");
    }
}
