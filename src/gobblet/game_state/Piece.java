package gobblet.game_state;

class Piece {

    private final int player;
    private final int size;
    private Piece coveredPiece;

    Piece(int player, int size, Piece coveredPiece) {
        this.player = player;
        this.size = size;
        this.coveredPiece = coveredPiece;
    }

    int getPlayer() {
        return player;
    }

    int getSize() {
        return size;
    }

    Piece getCoveredPiece() {
        return coveredPiece;
    }

    void setCoveredPiece(Piece coveredPiece) {
        this.coveredPiece = coveredPiece;
    }

    public String toString() {
        return "(" + (player == 0 ? "W" : "B") + ", " + (size + 1) + ")";
    }

}