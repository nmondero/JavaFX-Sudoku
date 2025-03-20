package application;

// Test class

public class classes_test {
    public static void main(String[] args){
        GameBoard board = null;

        boolean success = false;
        int initialization_error_count = 0;
        while (!success) {
            initialization_error_count++;
            try {
                board = new GameBoard(50);
                success = true;
                board.print_to_console();
                String error_message = "Could not add value 5 to row 0 col 0";
                try {
                    System.out.println(); System.out.print("Attempting to add value 5 to row 0 col 0...");
                    board.set_value_at(0, 0, 5, false); System.out.println(" Success: value 5 added to row 0 col 0");
                    System.out.println("Is Square valid after move: " + board.get_squares_in_row(0).get(0).is_valid());
                    board.print_to_console();
                    System.out.println(); System.out.println("Attempting to undo previous move (Reset row 0 col 0 to value 0)");
                    error_message = "Could not undo previous move";
                    board.undo_move();
                    System.out.println("Successfully undid move!"); System.out.println("Is Square valid after move: " + board.get_squares_in_row(0).get(0).is_valid()); board.print_to_console();
                } catch (Exception e) {System.out.println(" Unsuccessful: " + error_message);}
            } catch (Exception e) {System.out.println("Initialization Error " + initialization_error_count + " in a row");}
        }
    }

    





}