package chess;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Manages a chess game, including turns, legal moves, check, checkmate,
 * and stalemate detection.
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
     * Returns the team whose turn it is.
     *
     * @return the current team
     */
    public TeamColor getTeamTurn() {
        return currentTurn;
    }

    /**
     * Sets the team whose turn it is.
     *
     * @param team the team to move next
     */
    public void setTeamTurn(TeamColor team) {
        currentTurn = team;
    }

    /** Represents the two teams in a chess game. */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Finds the legal moves for the piece at a position.
     * A move is excluded if it leaves that piece's king in check.
     *
     * @param startPosition the position of the piece
     * @return legal moves, or null if the position is empty
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = board.getPiece(startPosition);

        if (piece == null) {
            return null;
        }

        Collection<ChessMove> candidates =
                piece.pieceMoves(board, startPosition);
        Collection<ChessMove> validMoves = new ArrayList<>();

        for (ChessMove move : candidates) {
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());

            // Apply the move temporarily to evaluate king safety.
            applyTemporaryMove(move, piece);

            boolean leavesKingInCheck = isInCheck(piece.getTeamColor());

            // Restore the original board before considering the next move.
            restoreTemporaryMove(move, piece, capturedPiece);

            if (!leavesKingInCheck) {
                validMoves.add(move);
            }
        }

        return validMoves;
    }

    /**
     * Returns every legal move available to the specified team.
     *
     * @param teamColor the team whose moves are requested
     * @return all legal moves for that team
     */
    public Collection<ChessMove> validTeamMoves(TeamColor teamColor) {
        Collection<ChessMove> moves = new ArrayList<>();

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece == null || piece.getTeamColor() != teamColor) {
                    continue;
                }

                moves.addAll(validMoves(position));
            }
        }

        return moves;
    }

    /**
     * Executes a legal move and switches the active team.
     *
     * @param move the move to execute
     * @throws InvalidMoveException if the move is illegal or belongs to
     *                               the wrong team
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece movingPiece = board.getPiece(move.getStartPosition());

        if (movingPiece == null) {
            throw new InvalidMoveException("This is an invalid move");
        }

        Collection<ChessMove> moves = validMoves(move.getStartPosition());

        if (movingPiece.getTeamColor() != currentTurn
                || !moves.contains(move)) {
            throw new InvalidMoveException("This is an invalid move");
        }

        board.removePiece(move.getStartPosition());
        board.removePiece(move.getEndPosition());
        board.addPiece(
                move.getEndPosition(),
                getMovedPiece(movingPiece, move)
        );

        currentTurn = currentTurn == TeamColor.WHITE
                ? TeamColor.BLACK
                : TeamColor.WHITE;
    }

    /**
     * Returns the promoted piece when a pawn promotes, or the original
     * piece when no promotion occurs.
     */
    private ChessPiece getMovedPiece(ChessPiece piece, ChessMove move) {
        if (move.getPromotionPiece() == null) {
            return piece;
        }

        return new ChessPiece(
                piece.getTeamColor(),
                move.getPromotionPiece()
        );
    }

    /**
     * Applies a move temporarily for legal-move evaluation.
     */
    private void applyTemporaryMove(ChessMove move, ChessPiece piece) {
        board.removePiece(move.getStartPosition());
        board.removePiece(move.getEndPosition());
        board.addPiece(
                move.getEndPosition(),
                getMovedPiece(piece, move)
        );
    }

    /**
     * Restores the board after a temporary move evaluation.
     */
    private void restoreTemporaryMove(
            ChessMove move,
            ChessPiece piece,
            ChessPiece capturedPiece) {
        board.removePiece(move.getEndPosition());
        board.addPiece(move.getStartPosition(), piece);

        if (capturedPiece != null) {
            board.addPiece(move.getEndPosition(), capturedPiece);
        }
    }

    /**
     * Finds the king belonging to a team.
     *
     * @param teamColor the team whose king is sought
     * @return the king's position, or null if no king is found
     */
    public ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece == null) {
                    continue;
                }

                if (piece.getTeamColor() == teamColor
                        && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }

        return null;
    }

    /**
     * Determines whether the specified team's king is under attack.
     *
     * @param teamColor the team to check
     * @return true if the king is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        TeamColor opposingTeam = teamColor == TeamColor.WHITE
                ? TeamColor.BLACK
                : TeamColor.WHITE;

        ChessPosition kingPosition = findKing(teamColor);

        if (kingPosition == null) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(position);

                if (piece == null || piece.getTeamColor() != opposingTeam) {
                    continue;
                }

                for (ChessMove move : piece.pieceMoves(board, position)) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines whether a team is in checkmate: its king is in check
     * and no legal move can remove the threat.
     *
     * @param teamColor the team to evaluate
     * @return true if the team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        return validTeamMoves(teamColor).isEmpty();
    }

    /**
     * Determines whether a team is in stalemate: it has no legal moves
     * but its king is not in check.
     *
     * @param teamColor the team to evaluate
     * @return true if the team is in stalemate
     */
    public boolean isInStalemate(TeamColor teamColor) {
        return validTeamMoves(teamColor).isEmpty()
                && !isInCheck(teamColor);
    }

    /**
     * Replaces the current chessboard.
     *
     * @param board the board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Returns the current chessboard.
     *
     * @return the current board
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object object) {
        if (object instanceof ChessGame other) {
            return board.equals(other.getBoard())
                    && currentTurn == other.getTeamTurn();
        }

        return false;
    }

    @Override
    public int hashCode() {
        return 31 * board.hashCode() + currentTurn.hashCode();
    }
}