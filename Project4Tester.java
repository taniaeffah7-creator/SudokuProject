/*
 * Project4Tester.java
 * CS231 Project 4
 *
 * How to run:
 *     javac *.java
 *     java Project4Tester        runs Task 1, 2, 3, ... and stops at the first task that needs work
 *     java Project4Tester 4      runs only Task 4
 *     java Project4Tester 8      runs only the findBestCell checks (Exploration)
 */
import java.util.concurrent.*;

public class Project4Tester {

    private static final long TIMEOUT_MS = 10000;
    private static boolean taskPassed;
    private static ExecutorService runner = newRunner();

    // ------------------------------------------------------------------
    //  Reporting helpers
    // ------------------------------------------------------------------

    private interface Check {
        Object run() throws Exception;
    }

    private static ExecutorService newRunner() {
        return Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r);
            t.setDaemon(true);
            return t;
        });
    }

    /** Runs one check and compares its result to expected. Returns true on PASS. */
    private static boolean expect(String desc, Object expected, String hint, Check check) {
        Future<Object> future = runner.submit(check::run);
        Object got;
        try {
            got = future.get(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        } catch (TimeoutException e) {
            future.cancel(true);
            runner.shutdownNow();
            runner = newRunner();
            System.out.println("  [TIMEOUT] " + desc);
            System.out.println("           took longer than " + (TIMEOUT_MS / 1000) + " seconds");
            System.out.println("           hint:     " + hint);
            taskPassed = false;
            return false;
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            System.out.println("  [CRASH] " + desc);
            System.out.println("           " + cause.getClass().getSimpleName()
                    + (cause.getMessage() != null ? ": " + cause.getMessage() : ""));
            String where = locate(cause);
            if (where != null)
                System.out.println("           at " + where);
            System.out.println("           hint:     " + hint);
            taskPassed = false;
            return false;
        } catch (InterruptedException e) {
            taskPassed = false;
            return false;
        }

        boolean ok = (expected == null) ? got == null : expected.equals(got);
        if (ok) {
            System.out.println("  [PASS] " + desc + "  ->  " + show(got));
        } else {
            System.out.println("  [FAIL] " + desc);
            System.out.println("           expected: " + show(expected));
            System.out.println("           got:      " + show(got));
            System.out.println("           hint:     " + hint);
            taskPassed = false;
        }
        return ok;
    }

    private static String show(Object o) {
        if (o == null) return "null";
        if (o instanceof String) return "\"" + o + "\"";
        return o.toString();
    }

    /** Finds the first stack frame inside the student's own classes. */
    private static String locate(Throwable t) {
        for (StackTraceElement el : t.getStackTrace()) {
            String cls = el.getClassName();
            if (cls.startsWith("Cell") || cls.startsWith("Board") || cls.startsWith("LinkedList")
                    || cls.startsWith("Sudoku"))
                return el.getClassName() + "." + el.getMethodName()
                        + " (" + el.getFileName() + " line " + el.getLineNumber() + ")";
        }
        return null;
    }

    private static void header(int task, String title) {
        System.out.println();
        System.out.println("------------------------------------------------------------");
        System.out.println(" Task " + task + " - " + title);
        System.out.println("------------------------------------------------------------");
    }

    // ------------------------------------------------------------------
    //  Test boards
    // ------------------------------------------------------------------

    // A puzzle with 30 starting values and one solution.
    private static final int[][] PUZZLE = {
        {5, 3, 0, 0, 7, 0, 0, 0, 0},
        {6, 0, 0, 1, 9, 5, 0, 0, 0},
        {0, 9, 8, 0, 0, 0, 0, 6, 0},
        {8, 0, 0, 0, 6, 0, 0, 0, 3},
        {4, 0, 0, 8, 0, 3, 0, 0, 1},
        {7, 0, 0, 0, 2, 0, 0, 0, 6},
        {0, 6, 0, 0, 0, 0, 2, 8, 0},
        {0, 0, 0, 4, 1, 9, 0, 0, 5},
        {0, 0, 0, 0, 8, 0, 0, 7, 9}
    };

    // A completed, valid board.
    private static final int[][] SOLVED = {
        {1, 9, 8, 7, 6, 5, 4, 3, 2},
        {7, 6, 5, 4, 3, 2, 1, 8, 9},
        {4, 3, 2, 1, 9, 8, 6, 5, 7},
        {9, 8, 1, 5, 4, 6, 2, 7, 3},
        {2, 7, 6, 9, 1, 3, 8, 4, 5},
        {5, 4, 3, 8, 2, 7, 9, 1, 6},
        {6, 1, 7, 3, 8, 9, 5, 2, 4},
        {3, 2, 4, 6, 5, 1, 7, 9, 8},
        {8, 5, 9, 2, 7, 4, 3, 6, 1}
    };

    // A board the row-by-row solver finishes without backtracking (the same as easy.txt).
    private static final int[][] EASY = {
        {0, 0, 0, 0, 6, 0, 0, 3, 2},
        {7, 6, 0, 4, 0, 0, 0, 0, 0},
        {4, 0, 0, 1, 0, 0, 0, 0, 0},
        {9, 8, 0, 0, 0, 0, 0, 0, 3},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 4, 3, 0, 2, 0, 0, 0, 0},
        {0, 0, 0, 3, 0, 0, 5, 0, 4},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 5, 0, 0, 0, 0, 0, 0, 0}
    };

    // A solvable board that the row-by-row solver cannot finish within MAX_STEPS.
    private static final int[][] SLOW = {
        {0, 0, 0, 9, 0, 0, 0, 3, 0},
        {0, 0, 7, 8, 0, 0, 0, 0, 0},
        {0, 0, 0, 0, 0, 2, 0, 0, 0},
        {1, 5, 0, 0, 6, 0, 8, 9, 0},
        {0, 0, 0, 0, 0, 0, 0, 0, 0},
        {0, 9, 0, 0, 0, 0, 4, 0, 0},
        {0, 0, 9, 0, 0, 0, 0, 0, 0},
        {0, 0, 2, 0, 0, 0, 1, 0, 0},
        {0, 0, 4, 0, 0, 0, 0, 0, 7}
    };

    /** Builds a Board from an array; non-zero values are locked. */
    private static Board boardOf(int[][] v) {
        Board b = new Board();
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (v[r][c] != 0) b.set(r, c, v[r][c], true);
        return b;
    }

    /** Makes a Sudoku game (no display) whose board holds the given values. */
    private static Sudoku gameOf(int[][] v) {
        Sudoku game = new Sudoku(0, 0);
        Board b = game.getBoard();
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (v[r][c] != 0) b.set(r, c, v[r][c], true);
        return game;
    }

    /** Returns true if every starting value in v is still in place on b. */
    private static boolean startingValuesKept(Board b, int[][] v) {
        for (int r = 0; r < 9; r++)
            for (int c = 0; c < 9; c++)
                if (v[r][c] != 0 && b.get(r, c).getValue() != v[r][c]) return false;
        return true;
    }

    // ------------------------------------------------------------------
    //  Task 1: numLocked and validValue
    // ------------------------------------------------------------------

    private static void task1() {
        header(1, "numLocked() and validValue()");

        expect("numLocked() on a new Board()", 0,
                "Count the Cells whose isLocked() is true.",
                () -> new Board().numLocked());

        expect("numLocked() on a puzzle with 30 starting values", 30,
                "Loop over every row and every column.",
                () -> boardOf(PUZZLE).numLocked());

        expect("validValue(0, 0, 5) on an empty board", true,
                "With no other values on the board, any value from 1 to 9 is valid.",
                () -> new Board().validValue(0, 0, 5));

        expect("validValue(0, 0, 0) and validValue(0, 0, 10)", "false false",
                "Only 1 to 9 are valid values. Check the range first.",
                () -> { Board b = new Board(); return b.validValue(0, 0, 0) + " " + b.validValue(0, 0, 10); });

        expect("a 5 at (0, 8): validValue(0, 0, 5)", false,
                "Check every Cell in the same row.",
                () -> { Board b = new Board(); b.set(0, 8, 5); return b.validValue(0, 0, 5); });

        expect("a 5 at (8, 0): validValue(0, 0, 5)", false,
                "Check every Cell in the same column.",
                () -> { Board b = new Board(); b.set(8, 0, 5); return b.validValue(0, 0, 5); });

        expect("a 5 at (1, 1): validValue(0, 0, 5)", false,
                "Check every Cell in the same 3x3 block, not just the same row and column.",
                () -> { Board b = new Board(); b.set(1, 1, 5); return b.validValue(0, 0, 5); });

        expect("a 9 at (4, 5): validValue(3, 3, 9) and validValue(3, 6, 9)", "false true",
                "Find the block's top-left corner with integer division: (row / 3) * 3 and (col / 3) * 3.",
                () -> { Board b = new Board(); b.set(4, 5, 9); return b.validValue(3, 3, 9) + " " + b.validValue(3, 6, 9); });

        expect("a 5 at (4, 4) only: validValue(0, 0, 5)", true,
                "A value in a different row, column, and block is not a conflict.",
                () -> { Board b = new Board(); b.set(4, 4, 5); return b.validValue(0, 0, 5); });

        expect("a 7 at (3, 3): validValue(3, 3, 7)", true,
                "The Cell at (row, col) itself does not count. Skip it when you check its row, column, and block.",
                () -> { Board b = new Board(); b.set(3, 3, 7); return b.validValue(3, 3, 7); });
    }

    // ------------------------------------------------------------------
    //  Task 2: validSolution
    // ------------------------------------------------------------------

    private static void task2() {
        header(2, "validSolution()");

        expect("validSolution() on a completed, valid board", true,
                "Every Cell is between 1 and 9 and valid where it is. Did validValue count the Cell itself?",
                () -> boardOf(SOLVED).validSolution());

        expect("validSolution() on an empty board", false,
                "A board with empty Cells (value 0) is not solved.",
                () -> new Board().validSolution());

        expect("validSolution() on a completed board with one Cell set to 0", false,
                "Check every Cell, including the last one.",
                () -> { Board b = boardOf(SOLVED); b.set(8, 8, 0, false); return b.validSolution(); });

        expect("validSolution() after swapping two values in row 0", false,
                "Swapping two values keeps the row valid but breaks their columns. Check every Cell with validValue.",
                () -> { Board b = boardOf(SOLVED); b.set(0, 0, 9, true); b.set(0, 1, 1, true); return b.validSolution(); });
    }

    // ------------------------------------------------------------------
    //  Task 3: Board(int numFixed)
    // ------------------------------------------------------------------

    private static void task3() {
        header(3, "Board(int numFixed)");

        expect("new Board(0).numLocked()", 0,
                "With numFixed = 0, the board stays empty.",
                () -> new Board(0).numLocked());

        expect("new Board(20).numLocked()", 20,
                "Keep going until exactly numFixed Cells are locked. Skip a Cell that is already locked.",
                () -> new Board(20).numLocked());

        expect("every locked Cell in 20 boards of 25 holds a valid value", true,
                "Only place a value when validValue says it is allowed there.",
                () -> {
                    for (int k = 0; k < 20; k++) {
                        Board b = new Board(25);
                        for (int r = 0; r < 9; r++)
                            for (int c = 0; c < 9; c++)
                                if (b.get(r, c).isLocked() && !b.validValue(r, c, b.get(r, c).getValue()))
                                    return false;
                    }
                    return true;
                });

        expect("unlocked Cells in a new Board(20) are empty", true,
                "Cells you did not lock should keep the value 0.",
                () -> {
                    Board b = new Board(20);
                    for (int r = 0; r < 9; r++)
                        for (int c = 0; c < 9; c++)
                            if (!b.get(r, c).isLocked() && b.get(r, c).getValue() != 0) return false;
                    return true;
                });

        expect("the value 9 appears in 20 boards of 25", true,
                "Random values must range from 1 to 9. rand.nextInt(9) gives 0 to 8; what do you add?",
                () -> {
                    for (int k = 0; k < 20; k++) {
                        Board b = new Board(25);
                        for (int r = 0; r < 9; r++)
                            for (int c = 0; c < 9; c++)
                                if (b.get(r, c).getValue() == 9) return true;
                    }
                    return false;
                });

        expect("two Board(30)s are different", true,
                "Use a Random object to choose the row, column, and value.",
                () -> new Board(30).toString().equals(new Board(30).toString()) == false);
    }

    // ------------------------------------------------------------------
    //  Task 4: Sudoku constructors and findNextValue
    // ------------------------------------------------------------------

    private static void task4() {
        header(4, "The Sudoku constructors and findNextValue()");

        boolean built = expect("new Sudoku(0, 0).getBoard() is a Board with 0 locked Cells", 0,
                "The constructor Sudoku(int numLocked, int delay) must create the board.",
                () -> new Sudoku(0, 0).getBoard().numLocked());
        if (!built) return;

        expect("new Sudoku(25, 0).getBoard().numLocked()", 25,
                "Pass numLocked to the Board constructor.",
                () -> new Sudoku(25, 0).getBoard().numLocked());

        expect("empty board, Cell (0, 0) holds 0: findNextValue", 1,
                "A Cell holding 0 has not tried any value yet.",
                () -> { Sudoku g = new Sudoku(0, 0); return g.findNextValue(g.getBoard().get(0, 0)); });

        expect("empty board, Cell (0, 0) holds 4: findNextValue", 5,
                "The Cell has already tried its current value (and every value before it). Where should the search start?",
                () -> { Sudoku g = new Sudoku(0, 0); Cell c = g.getBoard().get(0, 0); c.setValue(4); return g.findNextValue(c); });

        expect("empty board, Cell (0, 0) holds 9: findNextValue", 0,
                "Return 0 when there are no values left to try.",
                () -> { Sudoku g = new Sudoku(0, 0); Cell c = g.getBoard().get(0, 0); c.setValue(9); return g.findNextValue(c); });

        expect("1, 2, 3 in row 0, Cell (0, 0) holds 0: findNextValue", 4,
                "Use the Board's validValue to skip values that are not allowed.",
                () -> {
                    Sudoku g = new Sudoku(0, 0); Board b = g.getBoard();
                    b.set(0, 3, 1, true); b.set(0, 4, 2, true); b.set(0, 5, 3, true);
                    return g.findNextValue(b.get(0, 0));
                });

        expect("1 to 8 in row 0 and a 9 below: findNextValue for Cell (0, 0)", 0,
                "Return 0 when no value from the current one up to 9 is valid.",
                () -> {
                    Sudoku g = new Sudoku(0, 0); Board b = g.getBoard();
                    for (int c = 1; c <= 8; c++) b.set(0, c, c, true);
                    b.set(5, 0, 9, true);
                    return g.findNextValue(b.get(0, 0));
                });

        expect("findNextValue does not change the Cell", 4,
                "findNextValue only returns a value. The caller decides whether to set it.",
                () -> { Sudoku g = new Sudoku(0, 0); Cell c = g.getBoard().get(0, 0); c.setValue(4); g.findNextValue(c); return c.getValue(); });
    }

    // ------------------------------------------------------------------
    //  Task 5: findNextCell
    // ------------------------------------------------------------------

    private static String describe(Cell c) {
        if (c == null) return "null";
        return "row " + c.getRow() + ", col " + c.getCol() + ", value " + c.getValue();
    }

    private static void task5() {
        header(5, "findNextCell()");

        expect("empty board: findNextCell()", "row 0, col 0, value 1",
                "Go row by row, column by column, to the first Cell whose value is 0. Set it to its next value.",
                () -> describe(new Sudoku(0, 0).findNextCell()));

        expect("empty board: findNextCell() twice", "row 0, col 1, value 2",
                "After the first call, Cell (0, 0) holds 1, so it is no longer empty.",
                () -> { Sudoku g = new Sudoku(0, 0); g.findNextCell(); return describe(g.findNextCell()); });

        expect("Cells (0, 0) and (0, 1) locked: findNextCell()", "row 0, col 2, value 1",
                "Skip Cells that already hold a value.",
                () -> {
                    Sudoku g = new Sudoku(0, 0); Board b = g.getBoard();
                    b.set(0, 0, 5, true); b.set(0, 1, 6, true);
                    return describe(g.findNextCell());
                });

        expect("first empty Cell has no valid value: findNextCell()", "null",
                "If the first empty Cell has no valid value, return null. Do not move on to another Cell.",
                () -> {
                    Sudoku g = new Sudoku(0, 0); Board b = g.getBoard();
                    for (int c = 1; c <= 8; c++) b.set(0, c, c, true);
                    b.set(5, 0, 9, true);
                    return describe(g.findNextCell());
                });
    }

    // ------------------------------------------------------------------
    //  Task 6: solve
    // ------------------------------------------------------------------

    private static void task6() {
        header(6, "solve()");

        expect("Stage A: solve() a board that needs no backtracking", "solved: true, valid: true",
                "Follow the Stage A algorithm: find the next Cell, push it, and repeat until the stack holds numEmpty Cells.",
                () -> { Sudoku g = gameOf(EASY); boolean s = g.solve(); return "solved: " + s + ", valid: " + g.getBoard().validSolution(); });

        expect("solve() a board with no solution", false,
                "When the stack is empty and there is still no next Cell, every option has been tried: return false.",
                () -> {
                    Sudoku g = new Sudoku(0, 0); Board b = g.getBoard();
                    for (int c = 1; c <= 8; c++) b.set(0, c, c, true);
                    b.set(5, 0, 9, true);
                    return g.solve();
                });

        expect("after backing up past a Cell, the Cell is cleared to 0", "solved: false, Cell (0, 0) holds 0",
                "When a popped Cell has no values left (findNextValue returns 0), set its value back to 0 before popping the next Cell.",
                () -> {
                    // Row 0 needs 1 and 2 in Cells (0, 0) and (0, 1), but column 1 already has 1 and 2.
                    // The solver tries 1 and then 2 in Cell (0, 0), backs up past it, and must clear it.
                    Sudoku g = new Sudoku(0, 0); Board b = g.getBoard();
                    for (int c = 2; c <= 8; c++) b.set(0, c, c + 1, true);
                    b.set(5, 1, 1, true);
                    b.set(6, 1, 2, true);
                    boolean s = g.solve();
                    return "solved: " + s + ", Cell (0, 0) holds " + b.get(0, 0).getValue();
                });

        expect("solve() an empty board", "solved: true, valid: true",
                "Follow the algorithm on the project page. Push each Cell that findNextCell returns.",
                () -> { Sudoku g = new Sudoku(0, 0); boolean s = g.solve(); return "solved: " + s + ", valid: " + g.getBoard().validSolution(); });

        expect("solve() a puzzle with 30 starting values", "solved: true, valid: true",
                "When findNextCell returns null, pop Cells and give each its next value until one has a value that works. A popped Cell with no values left must be set back to 0.",
                () -> { Sudoku g = gameOf(PUZZLE); boolean s = g.solve(); return "solved: " + s + ", valid: " + g.getBoard().validSolution(); });

        expect("solve() keeps the starting values", true,
                "Never change a locked Cell. Only Cells returned by findNextCell go on the stack.",
                () -> { Sudoku g = gameOf(PUZZLE); g.solve(); return startingValuesKept(g.getBoard(), PUZZLE); });

        expect("solve() counts steps", true,
                "Add 1 to steps at the start of every step of the loop.",
                () -> { Sudoku g = new Sudoku(0, 0); g.solve(); return g.getSteps() >= 81; });

        expect("solve() gives up after MAX_STEPS steps on a slow board", "solved: false, gave up: true",
                "At the start of each step, if steps is more than MAX_STEPS, return false.",
                () -> { Sudoku g = gameOf(SLOW); boolean s = g.solve(); return "solved: " + s + ", gave up: " + (g.getSteps() > Sudoku.MAX_STEPS); });
    }

    // ------------------------------------------------------------------
    //  Task 8: findBestCell (Exploration, Required Element 1)
    // ------------------------------------------------------------------

    private static void task8() {
        header(8, "findBestCell() (Exploration, Required Element 1)");

        expect("Cell (8, 8) has only one valid value: findBestCell()", "row 8, col 8, value 9",
                "Count the valid values for every empty Cell and choose the Cell with the fewest.",
                () -> {
                    Sudoku g = new Sudoku(0, 0); Board b = g.getBoard();
                    for (int c = 0; c <= 7; c++) b.set(8, c, c + 1, true);
                    return describe(g.findBestCell());
                });

        expect("an empty Cell has no valid value: findBestCell()", "null",
                "If the Cell with the fewest valid values has none, the board is stuck: return null.",
                () -> {
                    Sudoku g = new Sudoku(0, 0); Board b = g.getBoard();
                    for (int c = 1; c <= 8; c++) b.set(4, c, c, true);
                    b.set(0, 0, 9, true);
                    return describe(g.findBestCell());
                });

        expect("solve() with findBestCell on the slow board", "solved: true, valid: true",
                "solve() should call findBestCell instead of findNextCell when useBestCell is true.",
                () -> {
                    Sudoku g = gameOf(SLOW); g.setUseBestCell(true);
                    boolean s = g.solve();
                    return "solved: " + s + ", valid: " + g.getBoard().validSolution();
                });
    }

    // ------------------------------------------------------------------
    //  Main
    // ------------------------------------------------------------------

    private static boolean runTask(int t) {
        taskPassed = true;
        switch (t) {
            case 1: task1(); break;
            case 2: task2(); break;
            case 3: task3(); break;
            case 4: task4(); break;
            case 5: task5(); break;
            case 6: task6(); break;
            case 7:
                header(7, "The display");
                System.out.println("  The tester does not check the display. Run java Sudoku with a delay");
                System.out.println("  greater than 0 and watch the board fill in.");
                break;
            case 8: task8(); break;
        }
        return taskPassed;
    }

    public static void main(String[] args) {
        if (args.length > 0) {
            int t;
            try {
                t = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                t = -1;
            }
            if (t < 1 || t > 8) {
                System.out.println("Usage: java Project4Tester [task number 1-8]");
                return;
            }
            boolean ok = runTask(t);
            System.out.println();
            System.out.println(ok ? "Task " + t + " complete." : "Task " + t + " needs more work.");
            return;
        }

        for (int t = 1; t <= 8; t++) {
            if (!runTask(t)) {
                System.out.println();
                System.out.println("Task " + t + " needs more work. Fix the checks above, then run the tester again.");
                return;
            }
        }
        System.out.println();
        System.out.println("============================================================");
        System.out.println(" All tasks pass, including findBestCell.");
        System.out.println(" Now run your experiments and write your report.");
        System.out.println("============================================================");
    }
}
