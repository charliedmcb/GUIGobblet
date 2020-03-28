package gobblet.game_state;

import java.util.HashSet;
import java.util.Map;

public class AlphaBetaMinimax {

    private final Board board;

    private static AlphaBetaMinimax alphaBetaMinimaxInstance;

    private AlphaBetaMinimax() {
        board = Board.getInstance();
    }

    public static AlphaBetaMinimax getInstance()
    {
        if (alphaBetaMinimaxInstance == null)
            alphaBetaMinimaxInstance = new AlphaBetaMinimax();

        return alphaBetaMinimaxInstance;
    }

    public void makeMove() {
        Map<Integer, HashSet<Integer>> moves = board.getMoves(0);
        double min = Double.MAX_VALUE;
        int orLoc = 0;
        int nLoc = 0;
        for (int startLoc: moves.keySet()) {
            for (int newLoc: moves.get(startLoc)) {
                if (startLoc < 0) {
                    board.placePiece(((startLoc + 1) * -1) / 3, ((startLoc + 1) * -1) % 3,
                            newLoc / 4, newLoc % 4);
                } else {
                    board.movePiece(startLoc / 4, startLoc % 4,
                            newLoc / 4, newLoc % 4);
                }
                double score = tryMoves(1, 6);
                if (score < min) {
                    min = score;
                    orLoc = startLoc;
                    nLoc = newLoc;
                }
                if (startLoc < 0) {
                    board.revertPlacePiece(newLoc / 4, newLoc % 4,
                            ((startLoc + 1) * -1) / 3, ((startLoc + 1) * -1) % 3);
                } else {
                    board.movePiece(newLoc / 4, newLoc % 4,
                            startLoc / 4, startLoc % 4);
                }
            }
        }
        if (orLoc < 0) {
            board.placePiece(((orLoc + 1) * -1) / 3, ((orLoc + 1) * -1) % 3,
                    nLoc / 4, nLoc % 4);
        } else {
            board.movePiece(orLoc / 4, orLoc % 4,
                    nLoc / 4, nLoc % 4);
        }
    }

    private double tryMoves(int player, int height) {
        if (height == 0) {
            return board.evaluate();
        }
        Map<Integer, HashSet<Integer>> moves = board.getMoves(player);
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        for (int startLoc: moves.keySet()) {
            for (int newLoc: moves.get(startLoc)) {
                if (startLoc < 0) {
                    board.placePiece(((startLoc + 1) * -1) / 3, ((startLoc + 1) * -1) % 3,
                            newLoc / 4, newLoc % 4);
                } else {
                    board.movePiece(startLoc / 4, startLoc % 4,
                            newLoc / 4, newLoc % 4);
                }
                double score = tryMoves((player + 1)%2, height - 1);
                min = score < min ? score : min;
                max = score > max ? score : max;
                if (startLoc < 0) {
                    board.revertPlacePiece(newLoc / 4, newLoc % 4,
                            ((startLoc + 1) * -1) / 3, ((startLoc + 1) * -1) % 3);
                } else {
                    board.movePiece(newLoc / 4, newLoc % 4,
                            startLoc / 4, startLoc % 4);
                }
            }
        }
        if (player == 0) {
            return min;
        } else {
            return max;
        }
    }

}
