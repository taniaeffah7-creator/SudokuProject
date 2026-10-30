/*
 * Cell.java
 * CS231 Lab 4
 *
 * A Cell is one square of a Sudoku board. It knows its own row and column,
 * its current value (0 means empty, otherwise 1-9), and whether it is locked.
 * A locked Cell holds one of the puzzle's starting values and must never be
 * changed by the solver.
 */
public class Cell {

    // TODO (Task 2): Declare four private fields:
    //   the row index, the column index, the value, and whether the Cell is locked.
    private int rowIndex;
    private int columnIndex;
    private int value;
    private boolean lockedCell;



    /**
     * Creates an unlocked Cell at (row, col) holding the given value.
     */
    public Cell(int row, int col, int value) {
        // TODO (Task 2)
       this.rowIndex = row;
       this.columnIndex= col;
       this.value = value;
    }

    /**
     * Creates a Cell at (row, col) holding the given value, locked or unlocked.
     */
    public Cell(int row, int col, int value, boolean locked) {
        // TODO (Task 2)
        this.rowIndex = row;
        this.columnIndex = col;
        this.value = value;
        this.lockedCell = locked;
    }

    /** Returns this Cell's row index. */
    public int getRow() {
        // TODO (Task 2)
        return this.rowIndex;
    }

    /** Returns this Cell's column index. */
    public int getCol() {
        // TODO (Task 2)
        return this.columnIndex;
    }

    /** Returns this Cell's value (0 means empty). */
    public int getValue() {
        // TODO (Task 2)
        return this.value;
    }

    /** Sets this Cell's value. */
    public void setValue(int newValue) {
        // TODO (Task 2)
        this.value = newValue;
    }

    /** Returns true if this Cell is locked. */
    public boolean isLocked() {
        // TODO (Task 2)
        return lockedCell;
    }

    /** Sets whether this Cell is locked. */
    public void setLocked(boolean lock) {
        // TODO (Task 2)
        this.lockedCell = lock;
    }

    /** Returns this Cell's value as a String, for example "5". */
    public String toString() {
        // TODO (Task 2)
        return this.value + "";
    }

    // Your tests for the Cell class. Run with:  java Cell
    // Each line prints the answer it should get ("expected") and what your code produced ("got").
    public static void main(String[] args) {
        // Cell a = new Cell(2, 5, 7);
        // System.out.println("getRow():   expected 2,     got " + a.getRow());
        // System.out.println("getValue(): expected 7,     got " + a.getValue());
        // System.out.println("isLocked(): expected false, got " + a.isLocked());
        // System.out.println("toString(): expected 7,     got " + a);

        // TODO (Task 2): Write at least two tests of your own. Ideas:
        //   - Make a Cell with the 4-parameter constructor. Is it locked?
        //   - Call setValue or setLocked, then print the result.

        Cell c = new Cell(6,7,8, true);
        System.out.println("getRow():   expected 6,     got " + c.getRow());
        System.out.println("getCol():   expected 7,     got " + c.getCol());
        System.out.println("getValue(): expected 8,     got " + c.getValue());
        System.out.println("isLocked(): expected true, got " + c.isLocked());
        System.out.println("toString(): expected 8,     got " + c);

        
        Cell b = new Cell(600,71,80, false);
        System.out.println("getRow():   expected 600,     got " + b.getRow());
        System.out.println("getCol():   expected 71,     got " + b.getCol());
        System.out.println("getValue(): expected 800,     got " + b.getValue());
        System.out.println("isLocked(): expected false, got " + b.isLocked());
        System.out.println("toString(): expected 80,     got " + b);


    }
}
