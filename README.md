# Sudoku Project
This project is a foundational implementation of a Sudoku game board built in Java. It was developed to practice core Computer Science concepts, focusing on Object-Oriented Programming (OOP) and building custom data structures from scratch. 

The application parses text files to populate a 9x9 grid, using 2D arrays to manage the board state and encapsulation to lock initial puzzle values so players can't alter them during gameplay. 

# Key features
- 2D array management: Implemented a 9x9 grid using a 2D array of Cell objects. This validates and manages row/column logic

- File I/O Parsing: Reads configurations from external .txt files. Uses BufferedReader and String.split() to parse space-separated integers and handles empty spaces (represented by 0).

- Object-Oriented Design: Used encapsulation to protect the state of individual cell objects (value, row, column, and locked status).

- Includes custom-built LinkedList and Stack implementations written from scratch to understand memory management and pointer manipulation.

- Formatted Output: Overrides toString() to display the board in a readable format with visual dividers (| and -) between 3x3 sub-grids


# Technologies Used
Java
Concepts: Object-Oriented Programming(OOP), 2D Arrays, File I/O, Data Structures(Linked List, Stack)
Tools: Git, GitHub


# How to run
1. Clone this repository to your local machine:
   git clone https://github.com/taniaeffah7/SudokuProject.git

2. Navigate into the project directory and compile all Java files: javac *.java

3. Run the board tester:
   java Lab4Tester


# Acknowledgements & Notes
- The initial skeleton files and testing framework(Lab4Tester.java) were provided by the CS231 (Data Structures) course.
- The core logic within Board.java, Cell.java, file parsing, and the custom data structure implementations were written entirely by me as part of my coursework.



