package interview3;

public interface Board<T extends Piece > {
    void change(int x, int y) throws Exception;
    void print();

    boolean hasWon();
}
