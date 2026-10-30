/*
 * Lab4Tester.java
 * CS231 Lab 4
 *
 * How to run:
 *     javac *.java
 *     java Lab4Tester        runs Task 1, 2, 3, ... and stops at the first task that needs work
 *     java Lab4Tester 3      runs only Task 3
 */
import java.io.*;
import java.util.concurrent.*;

public class Lab4Tester {

    private static final int NUM_TASKS = 5;
    private static final long TIMEOUT_MS = 5000;
    private static final String TEST_FILE = "lab4_tester_board.txt";

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
            if (cls.startsWith("Cell") || cls.startsWith("Board") || cls.startsWith("LinkedList"))
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
    //  Task 1: LinkedList implements Stack
    // ------------------------------------------------------------------

    @SuppressWarnings("unchecked")
    private static void task1() {
        header(1, "LinkedList implements Stack<T>");

        boolean isStack = expect("new LinkedList<Integer>() is a Stack", true,
                "Add Stack<T> to the implements list in your LinkedList class header.",
                () -> (Object) new LinkedList<Integer>() instanceof Stack);
        if (!isStack) return;

        expect("size() of a new stack", 0,
                "A new list should be empty.",
                () -> ((Stack<Integer>) (Object) new LinkedList<Integer>()).size());

        expect("peek() on an empty stack", null,
                "When there is nothing on the stack, peek() returns null.",
                () -> ((Stack<Integer>) (Object) new LinkedList<Integer>()).peek());

        expect("pop() on an empty stack", null,
                "When there is nothing on the stack, pop() returns null.",
                () -> ((Stack<Integer>) (Object) new LinkedList<Integer>()).pop());

        expect("push 1, 2, 3, then size()", 3,
                "Each push adds one item. Does push update the size?",
                () -> {
                    Stack<Integer> s = (Stack<Integer>) (Object) new LinkedList<Integer>();
                    s.push(1); s.push(2); s.push(3);
                    return s.size();
                });

        expect("push 1, 2, 3, then peek()", 3,
                "The top of the stack is the item pushed most recently.",
                () -> {
                    Stack<Integer> s = (Stack<Integer>) (Object) new LinkedList<Integer>();
                    s.push(1); s.push(2); s.push(3);
                    return s.peek();
                });

        expect("push 1, 2, 3, peek(), then size()", 3,
                "peek() looks at the top item without removing it.",
                () -> {
                    Stack<Integer> s = (Stack<Integer>) (Object) new LinkedList<Integer>();
                    s.push(1); s.push(2); s.push(3);
                    s.peek();
                    return s.size();
                });

        expect("push 1, 2, 3, then pop() three times", "3 2 1",
                "pop() returns items in the reverse of the order they were pushed (last in, first out).",
                () -> {
                    Stack<Integer> s = (Stack<Integer>) (Object) new LinkedList<Integer>();
                    s.push(1); s.push(2); s.push(3);
                    return s.pop() + " " + s.pop() + " " + s.pop();
                });

        expect("push 1, 2, 3, pop() three times, then size()", 0,
                "Does pop() update the size?",
                () -> {
                    Stack<Integer> s = (Stack<Integer>) (Object) new LinkedList<Integer>();
                    s.push(1); s.push(2); s.push(3);
                    s.pop(); s.pop(); s.pop();
                    return s.size();
                });

        expect("empty the stack, push 7, then peek()", 7,
                "After the last item is popped, the stack must still work.",
                () -> {
                    Stack<Integer> s = (Stack<Integer>) (Object) new LinkedList<Integer>();
                    s.push(1); s.pop();
                    s.push(7);
                    return s.peek();
                });

        expect("push and pop 1,000,000 items", "last pop = 0, size = 0",
                "push and pop should each take the same small number of steps no matter how big the stack is. "
                        + "Which end of your list can you reach without a loop?",
                () -> {
                    Stack<Integer> s = (Stack<Integer>) (Object) new LinkedList<Integer>();
                    for (int i = 0; i < 1000000; i++) s.push(i);
                    Integer last = null;
                    for (int i = 0; i < 1000000; i++) last = s.pop();
                    return "last pop = " + last + ", size = " + s.size();
                });
    }

    // ------------------------------------------------------------------
    //  Task 2: Cell
    // ------------------------------------------------------------------

    private static void task2() {
        header(2, "The Cell class");

        expect("new Cell(2, 5, 7): row, col, value", "2 5 7",
                "The 3-parameter constructor stores row, col, and value.",
                () -> { Cell c = new Cell(2, 5, 7); return c.getRow() + " " + c.getCol() + " " + c.getValue(); });

        expect("new Cell(2, 5, 7).isLocked()", false,
                "The 3-parameter constructor makes an unlocked Cell.",
                () -> new Cell(2, 5, 7).isLocked());

        expect("new Cell(8, 0, 4, true): row, col, value, locked", "8 0 4 true",
                "The 4-parameter constructor stores all four fields.",
                () -> { Cell c = new Cell(8, 0, 4, true); return c.getRow() + " " + c.getCol() + " " + c.getValue() + " " + c.isLocked(); });

        expect("setValue(9), then getValue()", 9,
                "setValue changes the value field.",
                () -> { Cell c = new Cell(0, 0, 0); c.setValue(9); return c.getValue(); });

        expect("setValue(9), setValue(0), then getValue()", 0,
                "0 means empty; the solver will set Cells back to 0.",
                () -> { Cell c = new Cell(0, 0, 0); c.setValue(9); c.setValue(0); return c.getValue(); });

        expect("setLocked(true), then setLocked(false)", "true false",
                "setLocked changes the locked field.",
                () -> { Cell c = new Cell(0, 0, 0); c.setLocked(true); boolean a = c.isLocked(); c.setLocked(false); return a + " " + c.isLocked(); });

        expect("two Cells keep separate values", "3 6",
                "Each Cell needs its own fields. Did you declare a field static?",
                () -> { Cell a = new Cell(1, 1, 3); Cell b = new Cell(2, 2, 6); return a.getValue() + " " + b.getValue(); });

        expect("new Cell(0, 0, 5).toString()", "5",
                "toString returns just the value, with no spaces or other text.",
                () -> new Cell(0, 0, 5).toString());
    }

    // ------------------------------------------------------------------
    //  Task 3: Board constructor, get, set
    // ------------------------------------------------------------------

    private static void task3() {
        header(3, "The constructors, get(), and set()");

        boolean built = expect("new Board() fills every location with a Cell", 81,
                "The constructor must create the 2D array AND put a new Cell in every location.",
                () -> {
                    Board b = new Board();
                    int count = 0;
                    for (int r = 0; r < 9; r++)
                        for (int c = 0; c < 9; c++)
                            if (b.get(r, c) != null) count++;
                    return count;
                });
        if (!built) return;

        expect("new Board(): Cells with value 0 that are unlocked", 81,
                "Every Cell starts with value 0 and unlocked.",
                () -> {
                    Board b = new Board();
                    int count = 0;
                    for (int r = 0; r < 9; r++)
                        for (int c = 0; c < 9; c++)
                            if (b.get(r, c).getValue() == 0 && !b.get(r, c).isLocked()) count++;
                    return count;
                });

        expect("new Board(): get(3, 7) knows its row and col", "3 7",
                "Pass the row and column to each new Cell. Check the order of row and col.",
                () -> { Cell c = new Board().get(3, 7); return c.getRow() + " " + c.getCol(); });

        expect("get(1, 1) returns the same Cell every time", true,
                "get should return the Cell stored in the array, not a new Cell.",
                () -> { Board b = new Board(); return b.get(1, 1) == b.get(1, 1); });

        expect("set(4, 6, 8): get(4, 6).getValue() and get(6, 4).getValue()", "8 0",
                "set changes only the Cell at that row and col. Check the order of row and col.",
                () -> { Board b = new Board(); b.set(4, 6, 8); return b.get(4, 6).getValue() + " " + b.get(6, 4).getValue(); });

        expect("set(2, 3, 5, true): value and locked", "5 true",
                "The 4-parameter set changes both the value and the locked status.",
                () -> { Board b = new Board(); b.set(2, 3, 5, true); return b.get(2, 3).getValue() + " " + b.get(2, 3).isLocked(); });

        try {
            writeTestFile();
        } catch (IOException e) {
            System.out.println("  The tester could not write " + TEST_FILE + " in this folder.");
            taskPassed = false;
            return;
        }
        expect("new Board(filename) creates every Cell", true,
                "Board(String filename) must first call this() to build the empty board, then call read(filename).",
                () -> {
                    Board b = new Board(TEST_FILE);
                    for (int r = 0; r < 9; r++)
                        for (int c = 0; c < 9; c++)
                            if (b.get(r, c) == null) return false;
                    return true;
                });
        new File(TEST_FILE).delete();
    }

    // ------------------------------------------------------------------
    //  Task 4: read
    // ------------------------------------------------------------------

    // A test board with a different value pattern in every row, including the last.
    private static final int[][] TEST_BOARD = {
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

    private static void writeTestFile() throws IOException {
        PrintWriter pw = new PrintWriter(new FileWriter(TEST_FILE));
        for (int r = 0; r < 9; r++) {
            StringBuilder sb = new StringBuilder();
            for (int c = 0; c < 9; c++) {
                if (c > 0) sb.append(r == 4 ? "  " : " ");   // one row uses double spaces
                sb.append(TEST_BOARD[r][c]);
            }
            pw.println(sb);
        }
        pw.println();   // trailing blank line, as many text editors add
        pw.close();
    }

    private static void task4() {
        header(4, "read(String filename)");
        try {
            writeTestFile();
        } catch (IOException e) {
            System.out.println("  The tester could not write " + TEST_FILE + " in this folder.");
            taskPassed = false;
            return;
        }

        expect("read(\"" + TEST_FILE + "\") returns", true,
                "read should return true when the file opens.",
                () -> new Board().read(TEST_FILE));

        expect("read(\"no_such_file.txt\") returns", false,
                "read should return false when the file cannot be opened (this part is already written).",
                () -> new Board().read("no_such_file.txt"));

        expect("row 0 after reading", "5 3 0 0 7 0 0 0 0",
                "Use Integer.parseInt to turn each token into an int, and set the Cell in the current row.",
                () -> rowValues(new Board(TEST_FILE), 0));

        expect("row 8 (the last row) after reading", "0 0 0 0 8 0 0 7 9",
                "Does your row counter move forward once per line of the file?",
                () -> rowValues(new Board(TEST_FILE), 8));

        expect("all 81 values after reading", 81,
                "Every Cell should match the file. Check rows and columns are not swapped.",
                () -> {
                    Board b = new Board(TEST_FILE);
                    int count = 0;
                    for (int r = 0; r < 9; r++)
                        for (int c = 0; c < 9; c++)
                            if (b.get(r, c).getValue() == TEST_BOARD[r][c]) count++;
                    return count;
                });

        expect("Cells with non-zero values are locked, zeros are not", 81,
                "Lock a Cell only when its value from the file is not 0.",
                () -> {
                    Board b = new Board(TEST_FILE);
                    int count = 0;
                    for (int r = 0; r < 9; r++)
                        for (int c = 0; c < 9; c++)
                            if (b.get(r, c).isLocked() == (TEST_BOARD[r][c] != 0)) count++;
                    return count;
                });

        new File(TEST_FILE).delete();
    }

    private static String rowValues(Board b, int r) {
        StringBuilder sb = new StringBuilder();
        for (int c = 0; c < 9; c++) {
            if (c > 0) sb.append(" ");
            sb.append(b.get(r, c).getValue());
        }
        return sb.toString();
    }

    // ------------------------------------------------------------------
    //  Task 5: toString
    // ------------------------------------------------------------------

    /** The exact String toString() must return for the given values. */
    private static String expectedString(int[][] v) {
        StringBuilder sb = new StringBuilder();
        for (int r = 0; r < 9; r++) {
            if (r == 3 || r == 6) sb.append("------+-------+------\n");
            for (int c = 0; c < 9; c++) {
                if (c == 3 || c == 6) sb.append(" | ");
                else if (c > 0) sb.append(" ");
                sb.append(v[r][c]);
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /** A message printed without surrounding quotes. */
    private static final class Plain {
        private final String text;
        Plain(String text) { this.text = text; }
        public boolean equals(Object o) { return o instanceof Plain && ((Plain) o).text.equals(text); }
        public int hashCode() { return text.hashCode(); }
        public String toString() { return text; }
    }

    private static final Plain MATCH = new Plain("matches exactly");

    /** Describes the first difference between got and expected, or returns MATCH. */
    private static Plain compareBoards(String got, String expected) {
        if (got == null) return new Plain("toString() returned null");
        if (got.equals(expected)) return MATCH;
        String[] g = got.split("\n", -1);
        String[] e = expected.split("\n", -1);
        for (int i = 0; i < Math.min(g.length, e.length); i++) {
            if (!g[i].equals(e[i]))
                return new Plain("line " + (i + 1) + " is \"" + g[i] + "\" but should be \"" + e[i] + "\"");
        }
        if (!got.endsWith("\n"))
            return new Plain("the last line should end with \"\\n\"");
        return new Plain("has " + (g.length - 1) + " lines but should have " + (e.length - 1));
    }

    private static void task5() {
        header(5, "toString()");

        expect("new Board().toString()", MATCH,
                "Values are separated by one space, blocks by \" | \", no space at the end of a line, "
                        + "and the line ------+-------+------ after rows 3 and 6.",
                () -> compareBoards(new Board().toString(), expectedString(new int[9][9])));

        expect("toString() of a board with different values in every row", MATCH,
                "Loop over rows on the outside and columns on the inside, and print each Cell's value.",
                () -> {
                    Board b = new Board();
                    for (int r = 0; r < 9; r++)
                        for (int c = 0; c < 9; c++)
                            b.set(r, c, TEST_BOARD[r][c]);
                    return compareBoards(b.toString(), expectedString(TEST_BOARD));
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
            if (t < 1 || t > NUM_TASKS) {
                System.out.println("Usage: java Lab4Tester [task number 1-" + NUM_TASKS + "]");
                return;
            }
            boolean ok = runTask(t);
            System.out.println();
            System.out.println(ok ? "Task " + t + " complete." : "Task " + t + " needs more work.");
            return;
        }

        for (int t = 1; t <= NUM_TASKS; t++) {
            if (!runTask(t)) {
                System.out.println();
                System.out.println("Task " + t + " needs more work. Fix the checks above, then run the tester again.");
                return;
            }
        }
        System.out.println();
        System.out.println("============================================================");
        System.out.println(" LAB CHECKPOINT REACHED!  All " + NUM_TASKS + " tasks pass.");
        System.out.println(" Show this output to a TA or instructor.");
        System.out.println("============================================================");
    }
}
