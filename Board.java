/*
 * Board.java
 * CS231 Lab 4
 *
 * A Board is a 9x9 grid of Cells. In the lab you will build it, read a
 * puzzle from a file, and print it. In Project 4 you will add the methods
 * that check whether values are valid and whether the board is solved.
 */
import java.util.Random;
import java.awt.Color;
import java.awt.Graphics;
import java.io.*;
//import java.util.Arrays;

public class Board {

    public static final int SIZE = 9;

    // TODO (Task 3): Declare a private field that is a 2D array of Cells.
    private Cell [][] field;


    /**
     * Creates a SIZE x SIZE board. Every location holds a new, unlocked Cell
     * with value 0 that knows its own row and column.
     */
    public Board() {
        // TODO (Task 3)
        this.field = new Cell[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                this.field[r][c] = new Cell(r,c,0);
        }
            }
    }

    /**
     * Creates a board, then fills it from the given file.
     */
    public Board(String filename) {
        // TODO (Task 3): Before this board can be filled from a file, it needs
        // the same empty grid of Cells that Board() builds.
        //
        // You could copy the code from Board() into this constructor line for
        // line. Programmers try hard to avoid that. If the same code lives in
        // two places, then every fix or change has to be made twice, and
        // sooner or later one copy gets forgotten. The rule even has a name:
        // DRY, which stands for "Don't Repeat Yourself."
        //
        // So, don't repeat yourself:
        //   1. Call the first constructor with this(). Java lets one
        //      constructor call another this way, as long as the call is the
        //      very first line.
        //   2. Call the read method with filename, which fills in the board
        //      from the file. (You will write read in Task 4.)
        this();
        read(filename);



    }

    /** Returns the Cell at (row, col). */
    public Cell get(int row, int col) {
        // TODO (Task 3)
        return field[row][col];
    }

    /** Sets the value of the Cell at (row, col). */
    public void set(int row, int col, int value) {
        // TODO (Task 3)
        this.field[row][col].setValue(value);
        
        
    }

    /** Sets both the value and the locked status of the Cell at (row, col). */
    public void set(int row, int col, int value, boolean locked) {
        // TODO (Task 3)
       Cell c = field[row][col];
       c.setValue(value);
       c.setLocked(locked);
    }

    /**
     * Reads a board from a file. Each line of the file is one row of the
     * board: nine numbers separated by spaces, where 0 means an empty Cell.
     * Non-zero values become locked Cells.
     *
     * Returns true if the file was read, or false if it could not be opened.
     */
    public boolean read(String filename) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(filename));

            // TODO (Task 4): You will need a variable here to keep track of
            //                which row of the board the current line belongs to.
            int rowTracker = 0;

            String line = br.readLine();
            while (line != null) {
                line = line.trim();
                if (!line.isEmpty()) {                  // skip blank lines
                    String[] tokens = line.split("\\s+");

                    // Uncomment these two lines to see what each line becomes:
                    //System.out.println("line:   \"" + line + "\"");
                    //System.out.println("tokens: " + Arrays.toString(tokens) + "  length = " + tokens.length);

                    // TODO (Task 4): Use the tokens to set the Cells in the current row.
                    for( int i = 0; i < tokens.length; i++){
                        int val = Integer.parseInt(tokens[i]);
                        boolean isLocked = (val != 0);
                        set(rowTracker, i, val, isLocked);
                       
                    }

                    rowTracker++;

                }

                line = br.readLine();
            }
            br.close();
            return true;
        } catch (FileNotFoundException ex) {
            System.out.println("Board.read(): unable to open file " + filename);
        } catch (IOException ex) {
            System.out.println("Board.read(): error reading file " + filename);
        }
        return false;
    }

    /**
     * Returns a multi-line String showing every value on the board, with
     * lines between the 3x3 blocks. It must match the format on the lab page
     * exactly, for example:
     *
     *     0 0 0 | 3 0 1 | 0 0 0
     *     0 0 0 | 0 0 0 | 0 0 0
     *     9 0 0 | 0 0 0 | 0 0 0
     *     ------+-------+------
     *     ...
     */
    public String toString() {
    String result = "";
    for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            result += field[r][c].getValue();
            // 2. Add the vertical dividers and spacing
            if (c == 2 || c == 5) {
                result += " | "; // Divider after the 3rd and 6th columns
            } else if (c < 8) {
                result += " ";   // Normal space between numbers
            }
        }
        
        // 3. Move to the next line
        result += "\n";
        
        // 4. Add the horizontal dividers after the 3rd and 6th rows
        if (r == 2 || r == 5) {
            result += "------+-------+------\n";
        }
    }
    
    return result;
}



    // ===================== Project 4 =====================
    private static Random gen = new Random();
    private boolean finished;   // true once the solver has finished (Task 7)

    /**
     * Creates a board with numFixed randomly placed, locked, valid values.
     */
    public Board(int numFixed) {
        // TODO (Task 3)
        this();   // start from an empty board (DRY again!)
        while(numLocked() < numFixed){
            int randomR = gen.nextInt(9);
            int randomC = gen.nextInt(9);
            int randomV = gen.nextInt(9)+ 1;
            if(validValue(randomR, randomC, randomV) == true){
                set(randomR, randomC, randomV, true);
            }
        }
    }

    /** Returns the number of locked Cells on the board. */
    public int numLocked() {
        // TODO (Task 1)
        int counter = 0;
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if(field[r][c].isLocked()){
                    counter++;
                }
            
            }
        }
        return counter;

    }

    /**
     * Returns true if value could go in the Cell at (row, col) without repeating
     * a value in the same row, column, or 3x3 block. The Cell at (row, col)
     * itself does not count.
     */
    public boolean validValue(int row, int col, int value) {
        // TODO (Task 1)
        // checking the row
        // reject invalid numbers
        if (value < 1 || value > 9) {
            return false;
        }
        for(int i = 0; i < 9; i++){
            //if the cell in the row has the same value its invalid
            if( i != col && field[row][i].getValue() == value){
                return false;
            }
        }
        // the col
            for(int i = 0; i < 9; i++){
            // if the cell in this col has the same value, its also invalid 
                if(i != row && field[i][col].getValue()== value){
                    return false;
            }
        }

        int startRow = row - (row % 3);
        int startCol = col - (col % 3);
        for (int r = startRow; r < startRow + 3; r++) {
            for (int c = startCol; c < startCol + 3; c++) {
                if((r != row || c != col) && field[r][c].getValue() == value){
                    return false;
                }
            }
        }
            
        return true;

        }

 
    /** Returns true if every Cell holds a value from 1 to 9 that is valid where it is. */
    public boolean validSolution() {
        // TODO (Task 2)
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if( field[r][c].getValue() == 0){
                    return false;
                }
                if(!validValue(r, c, field[r][c].getValue())){
                    return false;
                }
            }
                
            }
        return true;
    }

    /** Records whether the solver has finished. */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    /** Draws the board. LandscapeDisplay calls this method. */
    public void draw(Graphics g, int scale) {
        // TODO (Task 7)
            for (int r = 0; r < SIZE; r++) {
        for (int c = 0; c < SIZE; c++) {
            get(r, c).draw(g, c * scale + 5, r * scale + 20, scale);
        }
    }
    g.setColor(Color.BLACK);
    for (int i = 0; i <= SIZE; i += 3) {
        g.drawLine(i * scale, 0, i * scale, SIZE * scale);
        g.drawLine(0, i * scale, SIZE * scale, i * scale);
    }
    if (finished) {
        if (validSolution()) {
            g.setColor(new Color(0, 127, 0));
            g.drawString("Hurray!", scale * 3 + 5, scale * 10 + 10);
        } else {
            g.setColor(new Color(127, 0, 0));
            g.drawString("No solution!", scale * 3 + 5, scale * 10 + 10);
        }
    }
}

     
   // Your tests for the Board class. Run with:  java Board
    // Each line prints the answer it should get ("expected") and what your code produced ("got").
    // Only call the test methods for tasks you have started.
    public static void main(String[] args) {
        //testTask3();
        //testTask4();   // remove the // when you start Task 4
        //testTask5();   // remove the // when you start Task 5
    }

    private static void testTask3() {
        Board board = new Board();
        System.out.println("get(0, 0).getValue(): expected 0, got " + board.get(0, 0).getValue());
        System.out.println("get(3, 7).getRow():   expected 3, got " + board.get(3, 7).getRow());
        board.set(4, 6, 8);
        System.out.println("get(4, 6).getValue(): expected 8, got " + board.get(4, 6).getValue());

        Board fromFile = new Board("board1.txt");
        System.out.println("Board(\"board1.txt\") has a Cell at (8, 8): expected true, got " + (fromFile.get(8, 8) != null));

        // TODO (Task 3): Write a test of set(row, col, value, locked).
        board.set(2,1,3,false);
        System.out.println("  isLocked(): expected false, got " + board.get(2, 1).isLocked());



    }

    private static void testTask4() {
        Board board = new Board("board1.txt");
        System.out.println("get(0, 3).getValue(): expected 3,    got " + board.get(0, 3).getValue());
        System.out.println("get(0, 3).isLocked(): expected true, got " + board.get(0, 3).isLocked());

        Board solved = new Board("solved.txt");
        System.out.println("get(8, 0).getValue(): expected 8,    got " + solved.get(8, 0).getValue());

        // TODO (Task 4): Pick a number in the LAST column of solved.txt and test it.
        solved.set(5, 3, 2);
        System.out.println("get(5, 3).getValue(): expected 2, got " + solved.get(5, 3).getValue());
    }

    private static void testTask5() {
        Board board = new Board("solved.txt");
        System.out.println(board);   // compare with solved.txt and with the board on the lab page

        // TODO (Task 5): Print a Board made from board1.txt and compare it with the file.
        Board board1 = new Board("board1.txt");
        System.out.println(board1);
        System.out.println();
    

    }
}
