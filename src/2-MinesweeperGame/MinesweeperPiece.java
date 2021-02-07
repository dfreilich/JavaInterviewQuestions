package interview3;


public class MinesweeperPiece implements Piece {
    public static String EMPTY = "empty";
    public static String BOMB = "bomb";

    private boolean exposed;
    private String value;

    public MinesweeperPiece(String value) {
        this.value = value;
    }

    public MinesweeperPiece(int value) {
        this.value = "" + value;
    }

    public boolean isExposed() {
        return exposed;
    }

    public void setExposed() { exposed = true; }

    public String getValue(){
        return value;
    }

    public boolean isBomb() {
        return value == BOMB;
    }
}
