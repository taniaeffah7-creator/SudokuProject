/*
 * Sudoku.java
 * CS231 Project 4
 *
 * A Sudoku solver. It fills in the empty Cells of a Board one at a time and
 * uses a stack to remember the order it filled them in, so that when it gets
 * stuck it can undo its most recent choice and try something else.
 */

import java.util.Random;

public class Sudoku {

    /** solve() gives up after this many steps. */
    public static final int MAX_STEPS = 100000;

    private Board board;
    private LandscapeDisplay ld;   // the window that shows the board (Task 7)
    private int delay;             // milliseconds to pause between steps; 0 means no display
    private int steps;             // how many steps the last call to solve() took
    private boolean useBestCell = false;   // Exploration: if true, solve() uses findBestCell()
    //private static Random gen = new Random();

    /**
     * Creates a Sudoku game whose board is read from the given file.
     */
    public Sudoku(String filename, int delay) {
        // TODO (Task 4): Create the board from the file and store delay.
        this.board = new Board(filename);
        this.delay = delay;
        // TODO (Task 7): If delay is greater than 0, create a new LandscapeDisplay for the board.
        if(delay >  0){
            this.ld = new LandscapeDisplay(this.board);
        }
    }

    /**
     * Creates a Sudoku game whose board has numLocked random starting values.
     */
    public Sudoku(int numLocked, int delay) {
        // TODO (Task 4): Create a random board with numLocked locked Cells and store delay.
        this.board = new Board(numLocked);
        this.delay = delay;


        // TODO (Task 7): If delay is greater than 0, create a new LandscapeDisplay for the board.
        if(delay >  0){
            this.ld = new LandscapeDisplay(this.board);
    }
}

    /** Returns this game's board. */
    public Board getBoard() {
        return board;
    }

    /** Returns how many steps the last call to solve() took. */
    public int getSteps() {
        return steps;
    }

    /** Exploration: chooses whether solve() uses findBestCell() instead of findNextCell(). */
    public void setUseBestCell(boolean useBestCell) {
        this.useBestCell = useBestCell;
    }

    /**
     * Returns the next valid value for the given Cell that it has not tried yet,
     * or 0 if there is none. This method does not change the Cell.
     */
    public int findNextValue(Cell cell) {
        // TODO (Task 4)
        for (int i = cell.getValue() + 1; i <= 9; i++) {
                if(board.validValue(cell.getRow(), cell.getCol(), i)){
                    return i;
                }
            }
        return 0;
        }

    /**
     * Finds the first empty Cell, going row by row. If that Cell has a valid
     * value, sets the Cell to it and returns the Cell. If it has no valid value,
     * returns null.
     */
    public Cell findNextCell() {
        // TODO (Task 5)
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                // get the cell
                Cell current = board.get(r, c);
                // if its empty
                if(current.getValue() == 0){
                    int nextVal = findNextValue(current);
                    if(nextVal > 0){
                        current.setValue(nextVal);
                        return current;
                    }
                    if(nextVal == 0){
                        return null;
                    }  
                }
            }
        }
        return null;
    }

    /**
     * Exploration: finds the empty Cell with the fewest valid values. If that
     * Cell has a valid value, sets the Cell to it and returns the Cell.
     * If it has no valid value, returns null.
     */
    public Cell findBestCell() {
        // TODO (Exploration, Required Element 1)
        Cell bestCell = null;
        int minOpt = 10; 
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                // get the current cell
                Cell current = board.get(r, c);
                // check for emptiness
                if(current.getValue() == 0){
                    int currentOpt = 0;
                    for(int val = 1; val <= 9; val++){
                        if(board.validValue(r, c, val) == true){
                            currentOpt++;
                        }
                    }
                    // fewer options
                    if(currentOpt < minOpt){
                        minOpt = currentOpt;
                        bestCell = current;
            
                    }
                }
            } // end col loop
        } // end row loop


        // if no empty cell was found, or best cell has 0 options
        // return null
        if(bestCell == null || minOpt == 0){
            return null;
        }

        // find the next val, set it on the bestcell and return
        int b = findNextValue(bestCell);
        bestCell.setValue(b);
        return bestCell;
        }


    /**
     * Tries to solve the board. Returns true if it fills every empty Cell,
     * or false if the board has no solution or the solver takes more than
     * MAX_STEPS steps. Follow the algorithm on the project page.
     */
    public boolean solve() {
        // TODO (Task 6)
        // Remember: when you pop a Cell that has no values left, set it back to 0.
        Stack<Cell> stack = new LinkedList<Cell>();
        int numEmpty = 81 - board.numLocked();
        steps = 0;

        while(true){
            if(stack.size() == numEmpty){
                board.setFinished(true);
                return true;
            }
        
        //while(stack.size() < numEmpty){
            steps++;
            if(steps > MAX_STEPS){
                 board.setFinished(true);
                return false;
            }
            pause();


            Cell next;
            if (useBestCell) {
                next = findBestCell();
            } else {
                next = findNextCell();
            }

            while(next == null && stack.size() > 0){
                steps++;
                if(steps > MAX_STEPS){
                    board.setFinished(true);
                    return false;
                }
                Cell popCell = stack.pop();
                int value = findNextValue(popCell);
                if(value == 0){
                    popCell.setValue(0);
                } else {
                popCell.setValue(value);
                next = popCell;
            }
        }
            if(next == null){
                board.setFinished(true); // u stuff, so mark finished 
                return false;
            }
            stack.push(next);
            
        }
        
    }

    /**
     * If there is a display, waits delay milliseconds and redraws the board.
     * Call this at the start of every step of solve(). (Already written.)
     */
    private void pause() {
        if (delay > 0) {
            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // the pause was interrupted; just keep going
            }
            if (ld != null) {
                ld.repaint();
            }
        }
    }

    // ------------------------------------------------------------------
    // Your tests for Project 4. Run with:  java Sudoku
    // Each line prints the answer it should get ("expected") and what your code produced ("got").
    // Only call the test methods for tasks you have started.
    // ------------------------------------------------------------------

    public static void main(String[] args) {
        testTask1();
        //testTask2();   // remove the // when you start Task 2
        //testTask3();
        //testTask4();
        //testTask5();
        //testTask6A();  // Task 6, Stage A
        //testTask6B();  // Task 6, Stage B
        //testBestCell();
    }


    private static void testTask1() {
        Board board = new Board("board1.txt");
        System.out.println("numLocked():         expected 10,    got " + board.numLocked());
        System.out.println("validValue(0, 0, 3): expected false, got " + board.validValue(0, 0, 3) + "   (row 0 has a 3)");
        System.out.println("validValue(0, 0, 4): expected true,  got " + board.validValue(0, 0, 4));

        // TODO (Task 1): Write a test where the only conflict is in the same 3x3 block.
        System.out.println("validValue(1, 1, 1): expected true, got " + board.validValue(1, 1, 1) + "   (row 1 has a 1)");
        Sudoku game = new Sudoku("board1.txt", 10);
        game.solve();
    }

    private static void testTask2() {
        System.out.println("solved.txt: expected true,  got " + new Board("solved.txt").validSolution());
        System.out.println("board1.txt: expected false, got " + new Board("board1.txt").validSolution());

        // TODO (Task 2): Read solved.txt, change one value with set, and test validSolution again.
    }

    private static void testTask3() {
        Board board = new Board(20);
        System.out.println(board);
        System.out.println("numLocked(): expected 20, got " + board.numLocked());

        // TODO (Task 3): Check that every locked Cell in the board holds a valid value.
    }

    private static void testTask4() {
        Sudoku game = new Sudoku(0, 0);          // an empty board, no display
        Cell cell = game.getBoard().get(0, 0);
        System.out.println("findNextValue (value 0): expected 1, got " + game.findNextValue(cell));
        cell.setValue(4);
        System.out.println("findNextValue (value 4): expected 5, got " + game.findNextValue(cell));

        // TODO (Task 4): What should findNextValue return when the Cell holds 9? Test it.
    }

    private static void testTask5() {
        Sudoku game = new Sudoku(0, 0);
        Cell first = game.findNextCell();
        System.out.println("first cell: expected row 0, col 0, value 1, got row " + first.getRow()
                + ", col " + first.getCol() + ", value " + first.getValue());

        // TODO (Task 5): Call findNextCell a second time. Which Cell and value do you expect?
    }

    private static void testTask6A() {
        Sudoku easy = new Sudoku("easy.txt", 0);   // needs no backtracking
        System.out.println("easy.txt solve():         expected true, got " + easy.solve());
        System.out.println("easy.txt steps:           expected 63,   got " + easy.getSteps());
        System.out.println("easy.txt validSolution(): expected true, got " + easy.getBoard().validSolution());

        Sudoku hard = new Sudoku("board1.txt", 0);  // needs backtracking
        System.out.println("board1.txt solve():       " + hard.solve()
                + "   (false in Stage A, true after Stage B)");
    }

    private static void testTask6B() {
        Sudoku game = new Sudoku("board1.txt", 0);
        System.out.println(game.getBoard());
        System.out.println("solve():         expected true, got " + game.solve());
        System.out.println(game.getBoard());
        System.out.println("validSolution(): expected true, got " + game.getBoard().validSolution());
        System.out.println("steps: " + game.getSteps());

        // TODO (Task 6): Solve board2.txt and check the result the same way.
    }

    private static void testBestCell() {
        Sudoku game = new Sudoku("board2.txt", 0);
        game.setUseBestCell(true);
        System.out.println("solve() with findBestCell: expected true, got " + game.solve());
        System.out.println("steps: " + game.getSteps());

        // TODO (Exploration): Solve board2.txt again with findNextCell and compare the steps.
        // Run 1: Row by Row
        // Sudoku rowByRow = new Sudoku("board1.txt", 0);
        // rowByRow.solve();
        // System.out.println("findNextCell steps: " + rowByRow.getSteps());

        // // Run 2: Best Cell
        // Sudoku best = new Sudoku("board1.txt", 0);
        // best.setUseBestCell(true);
        // best.solve();
        // System.out.println("findBestCell steps: " + best.getSteps());



        Sudoku rowByRow = new Sudoku("board2.txt", 0);
        rowByRow.solve();
        System.out.println("board2 findNextCell steps: " + rowByRow.getSteps());

        Sudoku best = new Sudoku("board2.txt", 0);
        best.setUseBestCell(true);
        best.solve();
        System.out.println("board2 findBestCell steps: " + best.getSteps());

    }



}
