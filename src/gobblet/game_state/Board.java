package gobblet.game_state;

import java.util.*;

public class Board {

    private Piece[][] boardPieces;
    private Piece[][] playerPieces;

    private ArrayList<HashMap<Integer, HashSet<Integer>>> moves = new ArrayList<HashMap<Integer, HashSet<Integer>>>();

    private static Board boardInstance;

    private Board() {
        boardPieces = new Piece[4][4];
        playerPieces = new Piece[2][3];

        for (int player = 0; player < 2; player++) {
            moves.add(new HashMap<Integer, HashSet<Integer>>());
            for (int stack = 0; stack < 3; stack++) {
                Piece topPiece = new Piece(player, 0, null);
                for (int size = 1; size < 4; size++) {
                    topPiece = new Piece(player, size, topPiece);
                }
                playerPieces[player][stack] = topPiece;
                HashSet<Integer> setOfMoves = new HashSet<Integer>();
                for (int location = 0; location < 16; location++) {
                    setOfMoves.add(location);
                }
                moves.get(player).put(-1 * (player * 3 + stack + 1), setOfMoves);
            }
        }
    }

    public static Board getInstance()
    {
        if (boardInstance == null)
            boardInstance = new Board();

        return boardInstance;
    }

    public void movePiece(int xOr, int yOr, int xNew, int yNew) {
        Piece piece = boardPieces[xOr][yOr];
        boardPieces[xOr][yOr] = piece.getCoveredPiece();
        piece.setCoveredPiece(boardPieces[xNew][yNew]);
        boardPieces[xNew][yNew] = piece;

        HashSet<Integer> possibleMoves = moves.get(convertPieceToPlayer(piece)).get(4*xOr+yOr);
        possibleMoves.remove(4*xNew + yNew);
        possibleMoves.add(4*xOr+yOr);
        moves.get(convertPieceToPlayer(piece)).put(4*xNew + yNew, possibleMoves);

        if (convertPieceToPlayer(piece.getCoveredPiece()) == (convertPieceToPlayer(piece) + 1) % 2) {
            moves.get((convertPieceToPlayer(piece) + 1) % 2).remove(4*xNew + yNew);
        }

        possibleMoves = new HashSet<Integer>();
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (convertPieceToSize(boardPieces[x][y]) < convertPieceToSize(boardPieces[xOr][yOr])) {
                    possibleMoves.add(4*x+y);
                }
            }
        }
        moves.get(convertPieceToPlayer(piece)).remove(4*xOr+yOr);
        if (possibleMoves.size() != 0) {
            moves.get(convertPieceToPlayer(boardPieces[xOr][yOr])).put(4*xOr+yOr, possibleMoves);
        }

    }

    public void placePiece(int player, int stack, int xNew, int yNew) {
        Piece piece = playerPieces[player][stack];
        playerPieces[player][stack] = piece.getCoveredPiece();
        piece.setCoveredPiece(boardPieces[xNew][yNew]);
        boardPieces[xNew][yNew] = piece;

        HashSet<Integer> possibleMoves = moves.get(player).get(-1*(player*3 + stack + 1));
        possibleMoves.remove(4*xNew + yNew);
        moves.get(player).put(4*xNew + yNew, possibleMoves);

        if (convertPieceToPlayer(piece.getCoveredPiece()) == (player + 1) % 2) {
            moves.get((player + 1) % 2).remove(4*xNew + yNew);
        }

        possibleMoves = new HashSet<Integer>();
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (convertPieceToSize(boardPieces[x][y]) < convertPieceToSize(playerPieces[player][stack])) {
                    possibleMoves.add(4*x+y);
                }
            }
        }
        if (possibleMoves.size() == 0) {
            moves.get(player).remove(-1*(player*3 + stack + 1));
        } else {
            moves.get(player).put(-1*(player*3 + stack + 1), possibleMoves);
        }
    }

    public void revertPlacePiece(int xOr, int yOr, int player, int stack) {
        Piece piece = boardPieces[xOr][yOr];
        boardPieces[xOr][yOr] = piece.getCoveredPiece();
        piece.setCoveredPiece(playerPieces[player][stack]);
        playerPieces[player][stack] = piece;

        HashSet<Integer> possibleMoves = moves.get(player).get(4*xOr+yOr);
        possibleMoves.add(4*xOr+yOr);
        moves.get(player).put(-1*(3*player+stack + 1), possibleMoves);

        possibleMoves = new HashSet<Integer>();
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                if (convertPieceToSize(boardPieces[x][y]) < convertPieceToSize(boardPieces[xOr][yOr])) {
                    possibleMoves.add(4*x+y);
                }
            }
        }
        moves.get(player).remove(4*xOr+yOr);
        if (possibleMoves.size() != 0) {
            moves.get(getPlayerForBoardLocation(xOr, yOr)).put(4*xOr+yOr, possibleMoves);
        }
    }

    private int checkVictory(int x, int y, int dx, int dy) {
        if (boardPieces[x][y] == null) {
            return -1;
        }
        if ((x == 3 && dx == 1) || (y == 3 && dy == 1)) {
            return boardPieces[x][y].getPlayer();
        }
        if (boardPieces[x+dx][y+dy] == null) {
            return -1;
        }
        if (boardPieces[x][y].getPlayer() != boardPieces[x+dx][y+dy].getPlayer()) {
            return -1;
        }
        return checkVictory(x+dx, y+dy, dx, dy);
    }

    public int victory() {
        boolean playerOneWin = false;
        boolean playerTwoWin = false;
        for (int i = 0; i < 4; i++) {
            int vic = checkVictory(i, 0, 0, 1);
            if (vic == 0) {
                playerOneWin = true;
            } else if (vic == 1) {
                playerTwoWin = true;
            }
        }
        for (int i = 0; i < 4; i++) {
            int vic = checkVictory(0, i, 1, 0);
            if (vic == 0) {
                playerOneWin = true;
            } else if (vic == 1) {
                playerTwoWin = true;
            }
        }
        int vic = checkVictory(0, 0, 1, 1);
        if (vic == 0) {
            playerOneWin = true;
        } else if (vic == 1) {
            playerTwoWin = true;
        }
        vic = checkVictory(3, 0, -1, 1);
        if (vic == 0) {
            playerOneWin = true;
        } else if (vic == 1) {
            playerTwoWin = true;
        }
        if (playerOneWin && playerTwoWin) {
            return 2;
        } else if (playerOneWin) {
            return 0;
        } else if (playerTwoWin) {
            return 1;
        }
        return -1;
    }

    private String convertPieceToText(Piece piece) {
        if (piece == null) {
            return "<EMPTY>";
        }
        return piece.toString();
    }

    public String getTextForBoardLocation(int x, int y) {
        return convertPieceToText(boardPieces[x][y]);
    }

    public String getTextForPlayerStack(int player, int stack) {
        return convertPieceToText(playerPieces[player][stack]);
    }

    private int convertPieceToPlayer(Piece piece) {
        if (piece == null) {
            return -1;
        }
        return piece.getPlayer();
    }

    public int getPlayerForBoardLocation(int x, int y) {
        return convertPieceToPlayer(boardPieces[x][y]);
    }

    private int convertPieceToSize(Piece piece) {
        if (piece == null) {
            return -1;
        }
        return piece.getSize();
    }

    public int getSizeForBoardLocation(int x, int y) {
        return convertPieceToSize(boardPieces[x][y]);
    }

    public int getSizeForPlayerStack(int player, int stack) {
        return convertPieceToSize(playerPieces[player][stack]);
    }

    public double evaluate() {
        double score = 0;
        for (int x = 0; x < 4; x++) {
            for (int y = 0; y < 4; y++) {
                Piece piece = boardPieces[x][y];
                double factor = 1;
                while (piece != null) {
                    score += factor * (convertPieceToPlayer(piece) == 0 ? -1 : 1) * convertPieceToSize(piece);
                    factor = factor / 2;
                    piece = piece.getCoveredPiece();
                }
            }
        }
        return score;
    }

    public HashMap<Integer, HashSet<Integer>> getMoves(int player) {
        return moves.get(player);
    }

}