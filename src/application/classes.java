// Controller for the main menu screen

package application;
/*Program Name: classes.java
 * Author: Nate Mondero
 * Last Update: 11/30/2024
 * Purpose: This file contais classes that will be used in my final Sudoku project
 * 
 * Requirements:
 *  Abstract class: BoardElement
 *  Inherited class: Square extends BoardElement, SubGrid extends BoardElement
 */

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;
import java.util.Collections;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;
import javafx.beans.property.SimpleIntegerProperty;
 
// Base abstract class: Describes an element that will appear on the Sudoku board
abstract class BoardElement implements Cloneable {
    // Static member variable determining length of certain BoardElement instances
    final public static int ROW_LENGTH = 9; // Length of the board as well as max value that can be put into a Square
    final public static int GRID_LENGTH = 3;
     
    // Member variables: The row and column locations of the board element (This has different meanings in different children classes)
    protected int row; 
    protected int col;
 
    // Parameterized constructor
    protected BoardElement(int row, int col) throws Exception {
        this.set_row(row);
        this.set_col(col);
    }
 
    // Setter methods
    public void set_row(int row) throws Exception{
        if (row < 0 || row >= ROW_LENGTH) { // Exception for invalid input
            throw new Exception("Exception: " + row + " is outside of the 0 - 8 index range for rows");
        } 
        else this.row = row;
    }
    public void set_col(int col) throws Exception{
        if (col < 0 || col >= ROW_LENGTH) { // Exception for invalid input
            throw new Exception("Exception: " + col + " is outside of the 0 - 8 index range for columns");
        } else this.col = col;
    }
 
    // Getter methods
    public int get_col() {return col;}
    public int get_row() {return row;}
    //public boolean get_is_valid() {return is_valid;}
 
    // Abstract method to determine the validity of the BoardElement instance
    public abstract boolean is_valid();
 
}
 
// Square class: Describes individual squares on the Sudoku Board
class Square extends BoardElement implements Comparable<Square> {
    // Member variables
    private int value = 0; // Value that the square contains. Defaults to 0 (Squares with values of 0 will not display)
    private boolean valid_in_col = false;
    private boolean valid_in_row = false;
    private boolean valid_in_grid = false;
    private boolean is_prefilled = false; // Boolean flag determining whether or not the Square is prefilled or editable (Starting squares are not editable)
 
    // Default constructor
    public Square(int row, int col) throws Exception {
        super(row, col);
    }
    // Parameterized constructor
    public Square(int row, int col, int value) throws Exception {
        this(row, col);
        set_value(value);
    }
 
    // Setter methods
    public void set_value(int val) throws Exception {
        // Check to see if the value is editable
        /*if (!is_prefilled) {
            throw new Exception("Exception: Cannot change the value of this square (row " + row + " col: + " + col);
        }*/
        // Set value equal to val parameter. Reset val to 0 if not valid (this allows entry of invalid numbers, but they just reset the square).
        // NOTE: Default value of 0 is also not valid, so the square should display as blank and should invalidate the grid containing it
        if (val >= 1 && val <= 9) this.value = val;
        else this.value = 0;
         
        // Set boolean validity flags back to true (THEY SHOULD BE IMMEDIATELY RESET AFTER THIS METHOD IF DUPLICATE VALUES IN ROW/COL/GRID ARE DETECTED)
        this.valid_in_col = true;
        this.valid_in_grid = true;
        this.valid_in_row = true;
        System.out.println("Set square at " + row + " " + col + " to " + val);
    }
    public void set_to_prefilled() {this.is_prefilled = true;}
    public void set_valid_in_row(boolean is_valid) {this.valid_in_row = is_valid;}
    public void set_valid_in_col(boolean is_valid) {this.valid_in_col = is_valid;}
    public void set_valid_in_grid(boolean is_valid) {this.valid_in_grid = is_valid;}
 
    // Accessor methods
    public int get_value() {return this.value;}
    public boolean get_is_prefilled() {return this.is_prefilled;}
 
    // Override is_valid method. returns whether the current value is valid (between 1 and 9 inclusive)
    @Override
    public boolean is_valid() {return valid_in_col && valid_in_row && valid_in_grid && value > 0 && value <= ROW_LENGTH;}
 
    @Override
    public int compareTo(Square other) {
        if (this.value > other.get_value()) return 1;
        if (this.value < other.get_value()) return -1;
        else return 0;
    }

    @Override
    public boolean equals(Object obj) {return this == obj;}
}
 
/*SubGrid class: Describes an individual 3x3 subgrid of the Sudoku Board
* - row and col represents the top left square in the grid
* - The grid is implemented as a 3x3 array of squares

class SubGrid extends BoardElement {
    // Member variable
    private Square[][] grid = new Square[GRID_LENGTH][GRID_LENGTH]; // Create a new 2-D array with 3 rows and columns
 
    // Default constructor: Takes the top left coordinate (row, col) of the SubGrid and the list of squares to insert
    public SubGrid(int row, int col, Square[] grid_list) throws Exception {
        super(row, col); // Call super constructor
        for (Square sq : grid_list) { // Assign grid values according to the row/column
            grid[sq.row % GRID_LENGTH][sq.col % GRID_LENGTH] = sq; // Making sure to convert from 9x9 to 3x3 with the moudlus
        }
    }
 
    // Accessor function
    public Square get_square_at(int r, int c) {return grid[r % 3][c % 3];}
    public int get_value_at(int r, int c) {return grid[r % 3][c % 3].get_value();}
 
    // Setter function
    public void set_value_at(int r, int c, int val) throws Exception {grid[r % 3][c % 3].set_value(val);} // Making sure to convert from 9x9 to 3x3 with the modulus
 
    // Override is_valid method. Returns true if the grid's squares contain only valid, non-duplicate values
    @Override
    public boolean is_valid() {
        // Check if each square in the grid is valid
        for (int r = 0; r < GRID_LENGTH; r++) {
            for (int c = 0; c < GRID_LENGTH; c++) {
                if (!grid[r][c].is_valid()) return false;
            }
        } 
        return true; // Return true if each square was valid
    }
}*/
 
/*GameBoard: Contains board elements, the turn stack and move count, and manages the game state*/
class GameBoard {
    // Static Variables
    final public static int NUM_SQUARES = 81;
     
    // Member Variables
    //private SubGrid[][] subgrids = new SubGrid[BoardElement.GRID_LENGTH][BoardElement.GRID_LENGTH];
    private Stack<Move> move_stack = new Stack<>();
    private final SimpleIntegerProperty move_count = new SimpleIntegerProperty(0);
 
    private Map<Integer, ArrayList<Square>> squares_in_subgrid = new HashMap<>(9); // Map grid number to list of squares in grid at i (0 - 9, starting in the top left, sweeping left to right down each row)
    private Map<Integer, ArrayList<Square>> squares_in_row = new HashMap<>(9); // Map row index to list of squares in the row at i
    private Map<Integer, ArrayList<Square>> squares_in_col = new HashMap<>(9); // Map col index to list of squares in the col at i
     
    // Constructor: Takes in amount of pre-filled squares and creates a GameBoard with that many pre-filled squares in random places
    // NOTE: THE BOARD GENERATION HAS A CHANCE TO CRASH CORRELATING TO THE SIZE OF NUM_PREFILLED. I CHEATED AROUND THIS BY TRYING TO MAKE A NEW BOARD UNTIL IT WORKS
    public GameBoard(int num_prefilled) throws Exception {

        initialize_maps();
        System.out.println("Entering GameBoard constructor... about to make list of prefilled square indexes");
        // Set up order of prefilled squares
        ArrayList<Boolean> is_prefilled = new ArrayList<>(); // Boolean array that represents which indexes are prefilled (and uneditable!)
         
        for (int i = 0; i < NUM_SQUARES; i++) {
            if (i < num_prefilled) { // Only put in as many prefilled as specified
                is_prefilled.addLast(true);
            } 
             
            else is_prefilled.addLast(false); // Else, make it not prefilled
        } 
        Collections.shuffle(is_prefilled); // Shuffle the order of prefilled squares

        for (int i = 0; i < NUM_SQUARES; i++) {
            if (is_prefilled.get(i)) System.out.println(i + " is prefilled");
        }
 
        // Initialize Sqaures in the current subgrid
        // ArrayList<ArrayList<Square>> subgrid_squares = new ArrayList<>(9);
         
        // Iterate through each row. Insert default squares if is_prefilled at that location. Insert prefilled square with valid random value if is_prefilled at that location
        for (int r = 0; r < 9; r++) {        
            for (int c = 0; c < 9; c++) {
                 
                int square_81index = (9 * r) + c; // The index that the current square will be on (used to compare to is_prefilled which is size-81 array)
                int subgrid_index = 3 * (r / 3) + (c / 3); //WILD integer division here. r / 3 and c / 3 represent the row and column that the subgrid index is at. The subgrid index goes up by 3 * (r / 3) (Think about it. Draw out a 9x9 grid and a 3x3 grid)
                Square new_sq;
 
                // If current index IS prefilled
                if (is_prefilled.get(square_81index)) { // If the square at the index is prefilled, we must create it with the given value and set to prefilled to true
                    
                    Set<Integer> taken_values = new HashSet<>();
                    //Set<Square> visited = new HashSet<>();

                    if (!squares_in_row.get(r).isEmpty()) {
                        for (Square sq : squares_in_row.get(r)) {
                            int value = sq.get_value();
                            if (!taken_values.contains(value) && value != 0) {
                                taken_values.add(value);     
                            }
                            //visited.add(sq);  
                        }
                    }
                    
                    if (!squares_in_col.get(c).isEmpty()) {
                        for (Square sq : squares_in_col.get(c)) {
                            int value = sq.get_value();
                            if (!taken_values.contains(value) && value != 0) {
                                taken_values.add(value);     
                            }
                            //visited.add(sq);  
                        }
                    }

                    if (!squares_in_subgrid.get(subgrid_index).isEmpty()) {
                        for (Square sq : squares_in_subgrid.get(subgrid_index)) {
                            int value = sq.get_value();
                            if (!taken_values.contains(value) && value != 0) {
                                taken_values.add(value);     
                            }
                            //visited.add(sq);  
                        }
                    }
                    
                    
                    int valid_num = random_num_excluding(taken_values); // Get a random number that has not been taken yet

                    // Create the a valid random val and use it to create a prefilled square
                    new_sq = new Square(r, c, valid_num);
                    new_sq.set_to_prefilled();
                    System.out.println("Pre-Filled square created at Row " + r + " Col " + c + " Value: " + valid_num);
                    //subgrid_squares.get(subgrid_index).addLast(new_sq);
                }
                
                // If current index IS NOT prefilled
                else {
                    new_sq = new Square(r, c); 
                    System.out.println("Default square created at Row " + r + " Col " + c);
                }     
                
                // Add the new square (whether prefilled or not) to the square maps     
                squares_in_subgrid.get(subgrid_index).addLast(new_sq);
                squares_in_col.get(c).addLast(new_sq);
                squares_in_row.get(r).addLast(new_sq);
            }                     
        }

        System.out.println("Done Initializing gameboard");

        /*After assigning every square with proper values, create the subgrids
        System.out.println("Done assigning square values... attempting assign subgrids");
        int r = 0, c = 0;
        for (int i = 0; i < 9; i++) {
            subgrids[r][c] = new SubGrid(r, c, (Square[]) squares_in_subgrid.get(i).toArray());
            System.out.println("Row " + r + " Col " + c + " worked");
            if (++c > 2) {c = 0; r++;}
        }*/
    }
 
    // Parameter Constructor
    public GameBoard(/*SubGrid[][] subgrids,*/ Stack<Move> move_stack, int move_count, Map<Integer, ArrayList<Square>> squares_in_row, Map<Integer, ArrayList<Square>> squares_in_col, Map<Integer, ArrayList<Square>> squares_in_subgrid) {
        //this.subgrids = subgrids;
        this.move_stack = move_stack;
        set_move_count(move_count);
        this.squares_in_col = squares_in_col;
        this.squares_in_row = squares_in_row;
        this.squares_in_subgrid = squares_in_subgrid;
    }

    private void initialize_maps() {
        for (int i = 0; i < 9; i++) {
            squares_in_row.put(i, new ArrayList<Square>());
            squares_in_col.put(i, new ArrayList<Square>());
            squares_in_subgrid.put(i, new ArrayList<Square>());
        }
    }

    private void set_move_count(int count) {
        move_count.set(count);
    }

    public void increment_move_count() {
        int previous = move_count.get();
        set_move_count(++previous);
    }

    public void decrement_move_count() {
        int previous = move_count.get();
        set_move_count(--previous);
    }
 
    // Setter functions
    public void set_value_at(int row, int col, int val, boolean is_undo_move) throws Exception {
        //SubGrid target_subgrid = subgrids[row / 3][col / 3];
        ArrayList<Square> target_subgrid = squares_in_subgrid.get((3 * (row / 3)) + (col / 3));
        
        //Square target_square = target_subgrid.get_square_at(row, col);
        Square target_square = target_subgrid.get(3 * (row % 3) + (col % 3));
        
        int previous_value = target_square.get_value();
        System.out.println("Previous value: " + previous_value);
         
        // If the set value indicates the square is being reset or put to a different value, we need to revalidate certain squares
        if (val < 1 || val > 9 || (previous_value != 0 && val != previous_value)) {
            target_square.set_value(val); // Reset the target square

            Square first_matching_square = null; // This stores the first square matching the previous value of the reset square. If more than one match is found in a row/col/grid, then they are all invalid in row/col/grid and still invalid. If there was only one matching square, then it needs to be returned to valid in row/col/grid
            // SEARCH ROW for squares that need to be reset to valid in row
            for (Square sq : squares_in_row.get(row)) { // For squares in the specified row...
                System.out.println("Square at row " + sq.get_row() + " col " + sq.get_col() + " value " + sq.get_value());
                if (sq.get_value() == previous_value && sq != target_square) {
                    if (first_matching_square == null) {first_matching_square = sq; System.out.println("First match found");} // If the squares value is equal to the previous value of the reset square and we havent found a previous match, set the first matching square to sq
                    else {first_matching_square = null; System.out.println("2nd match found, breaking from loop");break;}
                }
            }
            if (first_matching_square != null) {first_matching_square.set_valid_in_row(true);}
 
            first_matching_square = null; // Reset to null for searching the col
            // SEARCH COL for squares that need to be reset to valid in row
            for (Square sq : squares_in_col.get(col)) { // For squares in the specified row...
                if (sq.get_value() == previous_value && sq != target_square) {
                    if (first_matching_square == null) {first_matching_square = sq;} // If the squares value is equal to the previous value of the reset square and we havent found a previous match, set the first matching square to sq
                    else {first_matching_square = null; break;}
                }
            }
            if (first_matching_square != null) {first_matching_square.set_valid_in_col(true);}
 
            first_matching_square = null; // Reset to null for searching the col
            // SEARCH GRID for squares that need to be reset to valid in row
            for (Square sq : target_subgrid) { // For squares in the specified row...
                if (sq.get_value() == previous_value && sq != target_square) {
                    if (first_matching_square == null) {first_matching_square = sq;} // If the squares value is equal to the previous value of the reset square and we havent found a previous match, set the first matching square to sq
                    else {first_matching_square = null; break;}
                }
            }
            if (first_matching_square != null) {first_matching_square.set_valid_in_grid(true);}
        }
 
        // Else, the value to set to is a valid 1 - 9 value
        //if (previous_value == 0) {
            target_square.set_value(val); // Set the new value
            if (val != 0) increment_move_count(); // Note we only increment move count if the set-to-value is valid. If the square resets due to invalid value, it doesnt count as a move
 
            // If any matches are found in the row, invalidate the target square and the duplicate square in the same row
            for (Square current_square : squares_in_row.get(row)) {
                if (current_square != target_square && current_square.compareTo(target_square) == 0) {
                    current_square.set_valid_in_row(false);
                    target_square.set_valid_in_row(false);
                    break;
                }
            }
 
            // If any matches are found in the col, invalidate the target square and the duplicate square in the same col
            for (Square current_square : squares_in_col.get(col)) {
                if (current_square != target_square && current_square.compareTo(target_square) == 0) {
                    current_square.set_valid_in_col(false);
                    target_square.set_valid_in_col(false);
                    break;
                }
            }
 
            // If any matches are found in the grid, invalidate the target square and the duplicate square in the same grid
            for (Square current_square : target_subgrid) {
                if (current_square != target_square && current_square.compareTo(target_square) == 0) {
                    current_square.set_valid_in_grid(false);
                    target_square.set_valid_in_grid(false);
                    break;
                }
            }
        //}
        
        if (!is_undo_move) {
            //Push the move that was just made onto the stack. Note, only moves that set a valid value count as a move to count towards move count, but it will still go on the move_stack
            move_stack.push(new Move(target_square, previous_value)); 
            System.out.println("Pushed new move to the stack");
        }

        

    }

    public boolean check_for_win() {
        for (int row = 0; row < 9; row++) {
            for (Square sq : squares_in_row.get(row)) {
                if (!sq.is_valid()) {
                    return false;
                }
            }
        }
        return true;
    }
     
    // Undoes the last move made
    public int undo_move() throws Exception {
        if (!move_stack.isEmpty()){
            Move last_move = move_stack.pop();
            last_move.reverse_move();
            return last_move.get_prev_val();
        }
        else return -1;
    }

    public Move pop_move() {
        if (!move_stack.isEmpty()) {
            return move_stack.pop();
        } else return null;
    }
 
    // Accessor functions
    public int get_move_count() {return move_count.get();}
    public ArrayList<Square> get_squares_in_row(int row) {return squares_in_row.get(row);}
    public int[] get_values_in_row(int row) {
        int[] vals = new int[9];
        for (int i = 0; i < 9; i++) {
            vals[i] = squares_in_row.get(row).get(i).get_value();
        }
        return vals;
    }

    public Square get_square_at(int row, int col) {
        return this.squares_in_row.get(row).get(col);
    }

    public SimpleIntegerProperty move_count_property() {return move_count;}

    public void print_to_console() {
        int current_board_row = 0; // Rows of the board containing numbers

        for (int output_row = 1; output_row <= 15; output_row++) { // 9 board rows, 6 border rows
            int mod5 = output_row % 5;
            
            if (mod5 == 1) System.out.print(" _____   _____   _____ ");
            
            else if (mod5 >= 2 && mod5 <= 4) {
                
                for (int index_in_row = 0; index_in_row < 9; index_in_row++) {
                    Integer current_value = squares_in_row.get(current_board_row).get(index_in_row).get_value();
                    String value_str = (current_value != 0 ? current_value.toString() : ".");
                    int mod3 = index_in_row % 3;
                    if (mod3 == 0) System.out.print("|" + value_str + " ");
                    else if (mod3 == 1) System.out.print(value_str + " ");
                    else if (mod3 == 2) System.out.print(value_str + "| ");
                }
                current_board_row++;
            }

            else if (mod5 == 0) System.out.print(" `````   `````   ````` ");

            System.out.print('\n');
        }

        System.out.println("Rows");
        for (int i = 0; i < 9; i++) {
            System.out.print(i + ": ");
            for (int j = 0; j < 9; j++) {
                int value = squares_in_row.get(i).get(j).get_value();
                if (value == 0) {System.out.print(" ");}
                else {System.out.print(value + " ");}            
            }
            System.out.print('\n');
        }

        System.out.println("Cols");
        for (int i = 0; i < 9; i++) {
            System.out.print(i + ": ");
            for (int j = 0; j < 9; j++) {
                int value = squares_in_col.get(i).get(j).get_value();
                if (value == 0) {System.out.print(" ");}
                else {System.out.print(value + " ");}            
            }
            System.out.print('\n');
        }

        System.out.println("Grids");
        for (int i = 0; i < 9; i++) {
            System.out.print(i + ": ");
            for (int j = 0; j < 9; j++) {
                int value = squares_in_subgrid.get(i).get(j).get_value();
                if (value == 0) {System.out.print(" ");}
                else {System.out.print(value + " ");}
            }
            System.out.print('\n');
        }
    }
 
    // Return the first Square in the given list containing the target_value. Used when placing or deleting a number in a Square to 
    // Inserting Square: Get row, col, and grid of the inserted square -> use this method to get the first instance of any other squares in the row/col/grid -> allow the input but make matching squares invalid
    // Resetting Square: Get row, col, and grid of reset square -> count occurrences of the value that was reset in row/col/grid -> if count == 1 after the reset, then that remaining square should change to valid in row/col/grid 
 
    private int random_num_excluding(Set<Integer> taken_values) {
        ArrayList<Integer> valid_values = new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9));
        for (Integer number : taken_values) {
            valid_values.remove(number);
            
            if(valid_values.isEmpty()) {System.out.println("Valid Values now empty"); System.out.println("Taken Values: " + taken_values.toString());}
        }
        Random rng = new Random();
        int selected_index = rng.nextInt(valid_values.size());
        return valid_values.get(selected_index);
    }
}
 
class Move {
    // Member variables
    private Square square; // The Square that the move happened on
    private int prev_value; // The value before the move
 
    // Constructor
    public Move(Square square, int prev_value) {
        set_square(square);
        set_prev_value(prev_value);
    }
 
    // Setter functions
    private void set_square(Square square) {this.square = square;}
    private void set_prev_value(int val) {this.prev_value = val;}
    public void reverse_move() throws Exception {square.set_value(prev_value);} // Reverts the value of the square referenced (POP FROM THE STACK IN GameBoard AFTER THIS METHOD!!!)
 
    // Accessor functions
    public Square get_square() {return square;}
    public int get_prev_val() {return prev_value;}
}