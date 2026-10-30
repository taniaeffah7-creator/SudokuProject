/*
 * file name:      LinkedList.java
 * author:         Tania Effah
 * date:           10/21/2026
 * purpose:        A generic singly-linked list that supports a for-each loop.
 *
 * CS231 Lab 3 - Linked Lists
 *
 * Work through the tasks IN ORDER. Each task matches a task on the Lab 3
 * page and a task in LinkedListTester.
 *
 *   javac LinkedList.java LinkedListTester.java
 *   java LinkedListTester          (runs tasks in order, stops at the first one that needs work)
 *   java LinkedListTester 4        (runs only Task 4)
 *
 * Before you run the tester, experiment with your method in main() at the
 * bottom of this file. Predict what should happen, run it, and compare.
 *
 * RULE: Do not import anything other than java.util.Iterator.
 */

import java.util.Iterator;

/*
 * The header below already says "implements Iterable<T>". That is a promise
 * to Java that this class will have an iterator() method (Task 9). Because of
 * that promise, other code - like the tester - can write
 *
 *     for (Integer value : myList) { ... }
 */
public class LinkedList<T> implements Iterable<T>, Stack<T> {

    // ======================================================================
    // TASK 1 - The Node class, the fields, and the constructor
    // ======================================================================

    /**
     * A Node is one "box" in the chain. It holds one item and a reference to
     * the next Node (or null if it is the last Node).
     *
     *     [ data | next ] ---> [ data | next ] ---> [ data | null ]
     *
     * Node is private: code outside LinkedList never sees Nodes, only items.
     * Because Node is written inside LinkedList<T>, it can use the type T.
     */
    private class Node {
        private Node next;

        // TODO (Task 1): declare a private field of type T that holds this
        //                Node's item. You choose the name (for example, data).
        private T data; 


        /**
         * Creates a Node holding item, with no next Node yet.
         */
        public Node(T item) {
            // TODO (Task 1): store item in your data field and set next to null.
            this.data = item;
            this.next = null;
        }

        /** Returns the item stored in this Node. */
        public T getData() {
            // TODO (Task 1)

            return data;
        }

        /** Makes this Node point to n. */
        public void setNext(Node n) {
            // TODO (Task 1)
            this.next = n;
        }

        /** Returns the Node that comes after this one (or null). */
        public Node getNext() {
            // TODO (Task 1)
            return this.next;
        }
    }

    // The LinkedList itself only needs two fields:
    private Node head; // the first Node in the list, or null when the list is empty
    private int size;  // how many items are in the list
    //private Node tail;

    /**
     * Creates an empty list.
     */
    public LinkedList() {
        // TODO (Task 1): an empty list has no head Node and zero items.
        this.head = null;
        this.size = 0;
    //this.tail = null;
        
    }

    /**
     * Returns the number of items in the list.
     */
    public int size() {
        // TODO (Task 1)
        // int count = 0;
        // while (head != null) {
        //     count++;
        //     head = head.next;
        // }

        // return count;
        return size;
    }


    // a mehtod that add the items at the top of the stack
    public void push(T item){
        add(item);
    }

    // removes and returns  the top item

    public T pop(){
        if(isEmpty()){
            return null;
        }
        T data = head.data;
        head = head.next;
        size--;
        return data;
    }

    public T peek(){
        if(head == null){
            return null;
        }
        return this.head.data;
    }


    /**
     * Returns true if the list holds no items, false otherwise.
     */
    public boolean isEmpty() {
        // TODO (Task 1)
        // if(head == null){
        //     return true;
        // }
        return size == 0;
    }

    // ======================================================================
    // TASK 2 - add(item) at the front, and toString()
    // ======================================================================

    /**
     * Inserts item at the BEGINNING of the list.
     *
     *   before:  head --> [B] --> [C] --> null
     *   add(A):  head --> [A] --> [B] --> [C] --> null
     *
     * Hint: make a new Node, point it at the current head, then move head.
     *       Does the order of those two steps matter? Draw it!
     *       Does your code still work when the list is empty?
     */
    public void add(T item) {
        // TODO (Task 2)
        Node myNewNode = new Node(item);
        // if(this.head == null){
        //     this.head =  myNewNode;
        //     this.size++;
        //     return;
        // }
        myNewNode.setNext(head);
        head = myNewNode;
        this.size++;

    }

    /**
     * Returns a String showing the items from head to tail, in this exact format:
     *
     *     empty list        -->  "[]"
     *     one item          -->  "[7]"
     *     several items     -->  "[4, 3, 2, 1, 0]"
     *
     * Hint: start a "current" reference at head and move it with
     *       current = current.getNext() until it becomes null.
     *       Only put ", " BETWEEN items, not after the last one.
     */
    public String toString() {
        // TODO (Task 2)
        String result = "[" ;
        Node current = this.head;
        while(current != null){
            result += current.data;
            if(current.next != null){
                // current = current.getNext();
                result+= ", ";
            }
            current = current.getNext();
        }
        result+= "]";
        return result;
    }

    // ======================================================================
    // TASK 3 - get(index)
    // ======================================================================

    /**
     * Returns the item at position index (0 is the head).
     * You may assume 0 <= index < size().
     *
     * Hint: a linked list has no [ ] indexing. To reach position index,
     *       start at head and step forward index times.
     */
    public T get(int index) {
        // TODO (Task 3)
        // Node temp = new Node; 
        Node current = head;
        for(int i = 0; i < index; i++){
            current = current.getNext();
        }
        return current.getData();
    }

    // ======================================================================
    // TASK 4 - add(index, item)
    // ======================================================================

    /**
     * Inserts item so that it ends up at position index.
     * You may assume 0 <= index <= size(). (index == size() means "at the end".)
     *
     *   list: [A, C]     add(1, B)   -->   [A, B, C]
     *
     *   head --> [A] --> [C] --> null
     *             ^
     *             the Node BEFORE the insertion point (position index - 1)
     *
     * Hint: index 0 is a special case - which method already does that?
     *       Otherwise, walk to the Node at position index - 1, then splice
     *       the new Node in after it. Don't forget size.
     */
    public void add(int index, T item) {
        // TODO (Task 4)
        if(index == 0){
            add(item);
            return;
            
        }
        Node temp = head;
        for(int i = 0; i < index -1; i++){
            temp = temp.next;
        }
        Node nN = new Node(item);
        nN.next = temp.next;
        temp.next = nN;
        
        size++;

    }

    // ======================================================================
    // TASK 5 - contains(o)
    // ======================================================================

    /**
     * Returns true if some item in the list satisfies item.equals(o).
     *
     * Hint: use .equals(), not ==. With objects, == asks "is this the very
     *       same object in memory?", which is not the question here.
     */
    public boolean contains(Object o) {
        // TODO (Task 5)
        Node current = head;

        while( current != null){
            if(( o == null && current.data == null) ||
             (o!= null && o.equals(current.data))){
                return true;
                }
                current = current.next;
            }
        return false;
    }

    // ======================================================================
    // TASK 6 - remove() and clear()            *** LAB CHECKPOINT after this task ***
    // ======================================================================

    /**
     * Removes the first item of the list and returns it.
     * If the list is empty, return null.
     */
    public T remove() {
        // TODO (Task 6)
        if(head == null){
        return null;
    }
        T remove = head.data;
        head = head.next;

        size--;

        return remove;
    }
    /**
     * Empties the list.
     *
     * Hint: you do not need a loop. What do the fields of an empty list look like?
     */
    public void clear() {
        // TODO (Task 6)
        head = null;
        size = 0;
    }

    // ======================================================================
    // TASK 7 - remove(index)
    // ======================================================================

    /**
     * Removes the item at position index and returns it.
     * You may assume 0 <= index < size().
     *
     *   list: [A, B, C]    remove(1) returns B    list becomes [A, C]
     *
     * Hint: just like add(index, item), you need the Node BEFORE the one
     *       you are removing so you can make it skip over the removed Node.
     */
    public T remove(int index) {
        // TODO (Task 7)
        if(index ==0){
            T data = head.data;
            head = head.next;
            size--;
            return data;
        }
        
        Node newZ = head;
        for(int i = 0; i< index-1; i++ ){
            newZ = newZ.next;
        }
        Node target = newZ.next;
        T data = target.data;
        newZ.next = target.next;
        target.next = null;
        size--;
        return data;
    }

    // ======================================================================
    // TASK 8 - equals(o)
    // ======================================================================

    /**
     * Returns true if o is a LinkedList holding equal items in the same order.
     *
     * The parameter is an Object (not a LinkedList) because this method
     * overrides the equals method that every Java Object has. Start with:
     *
     *     if (!(o instanceof LinkedList)) {
     *         return false;
     *     }
     *     LinkedList<?> other = (LinkedList<?>) o;
     *
     * Then compare sizes, and compare items one pair at a time.
     * This method must not change either list.
     */
    public boolean equals(Object o) {
        // TODO (Task 8)
    if (!(o instanceof LinkedList)) {
        return false;
    }
     LinkedList<?> other = (LinkedList<?>) o;

    if(this.size() != other.size()){
        return false;
    }

    Iterator <T> mine = this.iterator();
    Iterator <?> theirs = other.iterator();

    while(mine.hasNext()){
        T a = mine.next();
        Object b = theirs.next();
        if (a == null ? b != null : !a.equals(b)) {
            return false;
        }
    }
    return true;   
    }
    // for(int i = 0; i < size(); i++){

    // }

    // ======================================================================
    // TASK 9 - The Iterator
    // ======================================================================

    /**
     * An LLIterator walks through the list one Node at a time. It remembers
     * the Node whose item will be handed out by the NEXT call to next().
     */
    private class LLIterator implements Iterator<T> {

        // TODO (Task 9): declare one field of type Node.
        private Node current;

        /** Creates an iterator that starts at the given head Node. */
        public LLIterator(Node head) {
            // TODO (Task 9)
            
            this.current = head;
           

        }

        /** Returns true if there are still items to hand out. */
        public boolean hasNext() {
            // TODO (Task 9)
            return current != null;
        }

        /** Returns the current item and moves forward one Node. */
        public T next() {
            // TODO (Task 9)
            T data = current.data;
            current = current.next;
            return data;
        }

        /** Optional for an Iterator. Leave this empty. */
        public void remove() {
        }
    }

    /**
     * Returns a new LLIterator that starts at the head of this list.
     * This is the method that "implements Iterable<T>" promised.
     * Note: this method belongs to LinkedList, NOT to LLIterator.
     */
    public Iterator<T> iterator() {
        // TODO (Task 9): return a new LLIterator, starting at this list's head.
        return new LLIterator(head);
    }

    // ======================================================================
    // PROJECT 3 - Task 1   (do NOT start these during lab)
    // These stubs are here so the file compiles. You will finish them when
    // you start Project 3.
    // ======================================================================

    /**
     * Inserts item at the END of the list.
     *   [A, B]   addLast(C)   -->   [A, B, C]
     */
    public void addLast(T item) {
        // TODO (Project 3, Task 1)
        Node newNode = new Node(item);
        if(head == null){
            head = newNode;
        } else{
            Node current = head;
            while(current.next != null){
                current = current.next;
            }
            current.next = newNode;
        }
        size++;
    }

    /**
     * Returns the position of the first item satisfying item.equals(o),
     * or -1 if there is no such item.
     *   [A, B, C, B]   indexOf(B) --> 1      indexOf(Z) --> -1
     */
    public int indexOf(Object o) {
        // TODO (Project 3, Task 1)
        Node current = head;
        int index = 0;
        while (current != null) {
            if (current.data.equals(o)) {
                return index;
            }
            current = current.next;
            index++;
        }
        return -1;
    }

    // ======================================================================
    // Your testing space. Experiment here BEFORE running LinkedListTester.
    //   javac LinkedList.java
    //   java LinkedList
    // ======================================================================
    public static void main(String[] args) {
        // LinkedList<Integer> list = new LinkedList<Integer>();
        // System.out.println("New list:  " + list + "   size = " + list.size());

        // // Example experiment for Task 2 - predict the output before running!
        // list.add(1);
        // list.add(2);
        // list.add(3);
        // System.out.println("After add(1), add(2), add(3): " + list);


        Stack<Integer> stack = new LinkedList<Integer>();
        //stack.push(1);
        //stack.push(2);
        stack.push(10); // last
        stack.push(20);
        stack.push(1);
        stack.push(2);// top

        System.out.println("peek(): expected 2, got " + stack.peek());
        // stack.pop(); // removing 2
        System.out.println("pop():  expected 2, got " + stack.pop());
        // System.out.println("peek(): expected 2, got " + stack.peek());
        // System.out.println("pop():  expected 1, got " + stack.pop());
        System.out.println("size(): expected 3, got " + stack.size());
        // //System.out.println("pop():  expected 20, got " + stack.pop());
        // //System.out.println("size(): expected 4, got " + stack.size());

       
    }
}
