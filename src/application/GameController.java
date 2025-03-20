// Controller for the window that plays the game

package application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.scene.paint.Color;
import javafx.beans.value.ObservableValue;

import java.io.IOException;

import javafx.beans.value.ChangeListener;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;

public class GameController {

    final int INACTIVE = -1; // Const int to determine inactivity

    GameBoard gameboard; // Object that keeps track of game logic and

    // Ints that keep track of which square on the board UI is currently selected
    int active_row;
    int active_col;

    @FXML
    private GridPane board_ui; // The UI element representing the game board

    @FXML
    private Label move_count_label; // UI element showing current moves

    @FXML
    private Button undo_button; // UI button for undo-ing the last move

    @FXML
    private Label win_label;

    @FXML
    private Button return_to_main_btn;

    // When we start this program, intialize active row and col to INACTIVE
    @FXML
    public void initialize() {
        active_row = INACTIVE;
        active_col = INACTIVE;
    }

    // When the stage is ready, we get the gameboard from the stage data and update the gridpane
    @FXML
    public void onStageReady(Stage stage) {
        gameboard = (GameBoard) stage.getUserData();
        gameboard.move_count_property().addListener((new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> oberservable, Number oldValue, Number newValue) {
                move_count_label.setText("Move Count: " + gameboard.get_move_count());
            }
        }));
        updateGridPane();
    }

    // Visually update the gridpane that represents the game board
    @FXML
    private void updateGridPane() {
        if (gameboard == null) return; // If there is no grid pane (somehow), then return
        board_ui.getChildren().clear(); // Clear the gridpane first
        
        // Iterate through each square in the grid
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                Square current_square = gameboard.get_square_at(row, col); // Get the current Square object
                int value = current_square.get_value(); // Get the value at the current Square
                Label cellLabel = new Label(value == 0 ? "" : String.valueOf(value)); // Create a Label for the cell to display the Square's value (WE ARE USING THIS LABEL TO DISPLAY THE NUMBER IN THE SQUARE)
                cellLabel.setMouseTransparent(true); // Set the label to mouse-transparent so we can click the grid behind the label
    
                // Styling for positioning and alignment
                String style = "-fx-font-size: 18; -fx-alignment: center; ";
    
                // Set the background color for different conditions
                if (row == active_row && col == active_col) {
                    style += "-fx-background-color: LightGray; "; // Selected Square (via mouse click)
                } else if (current_square.get_is_prefilled()) {
                    style += "-fx-background-color: DarkGray; "; // Prefilled Square
                } else {
                    style += "-fx-background-color: White; "; // Default background
                }

                if (gameboard.get_square_at(row, col).is_valid()) { // Valid value in Square
                    cellLabel.setTextFill(Color.BLACK);
                } else { // Invalid value in Square
                    cellLabel.setTextFill(Color.RED); 
                }
    
                // Border styling for all cells
                style += "-fx-border-color: black; -fx-border-width: 1px; "; // Uniform borders
    
                // Apply the style to the label
                cellLabel.setStyle(style);
                cellLabel.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                GridPane.setFillWidth(cellLabel, true);
                GridPane.setFillHeight(cellLabel, true);
    
                // Add the cell to the board UI
                board_ui.add(cellLabel, col, row);
            }
        }
    }
    

    // When we click the grid
    @FXML
    void on_grid_clicked(MouseEvent event) {
        // Get the x and y coordinate of the click on the grid 
        double x = event.getX();
        double y = event.getY();

        // Get the dimensions of the grid
        double grid_width = board_ui.getWidth();
        double grid_height = board_ui.getHeight();
        
        // Get the row and col that was clicked
        int click_row = (int) (y / (grid_height / 9));
        int click_col = (int) (x / (grid_width / 9));

        // If the Square clicked is prefilled, reset the active Square (Deselect it so it is unshaded)
        if (gameboard.get_square_at(click_row, click_col).get_is_prefilled()) {reset_active();}

        // Else if the Square clicked was already active, reset the active Square (deselect it so it is unshaded)
        else if (active_row == click_row && active_col == click_col) {reset_active();}

        // Else set the active Square coordinates to the square that was clicked
        else {
            active_row = click_row;
            active_col = click_col;
            System.out.println("Clicked at row " + click_row + " col: " + click_col + " Value: " + gameboard.get_square_at(click_row, click_col).get_value());
        }

        updateGridPane(); // Update the grid UI
    }

    // When we click out of the grid, we reset the active Square (deselect it)
    @FXML
    void on_click_out_of_grid(MouseEvent event) {
        // Get the coordinates of the mouse click
        double x = event.getX();
        double y = event.getY();

        // Get the width and height of the GridPane
        double gridWidth = board_ui.getWidth();
        double gridHeight = board_ui.getHeight();
        int left_edge = 500 - ((int) gridWidth / 2);
        int right_edge = 500 + ((int) gridWidth / 2) + 25;
        int top_edge = 310 - ((int) gridHeight / 2);
        int bottom_edge = 310 + ((int) gridHeight / 2);

        // Check if the click is inside the bounds of the GridPane, we didn't click outside of the grid
        if (x <= right_edge && x >= left_edge && y >= top_edge && y <= bottom_edge) {
            System.out.println("Clicked out of grid");
        } else { // Else we did click outside the grid. Reset the active square then update the UI
            reset_active();
            updateGridPane();
        }
    }

    // Handle keyboard entry for putting values in squares
    @FXML
    void on_keyboard_entry(KeyEvent event) throws Exception {
        System.out.println("Keyboard attempt: active row " + active_row + " active col: " + active_col);

        // If there is no selected square, then do nothing
        if (active_row == INACTIVE || active_col == INACTIVE) {System.out.println("Nothing Selected for input"); return;}

        // Get the key pressed and check if it is a digit 1 - 9
        String entry = event.getText();
        if (entry.matches("[1-9]")) { // If it is a digit 1 - 9, we set the value at the active (selected) square to the number entered
            System.out.println("Got valid entry: " + entry);
            int entry_number = Integer.parseInt(entry);
            gameboard.set_value_at(active_row, active_col, entry_number, false);
        }
        else {gameboard.set_value_at(active_row, active_col, 0, false);} // If the entry was not valid, then reset the square
        if (gameboard.check_for_win()) {
            return_to_main_btn.setVisible(true);
            win_label.setVisible(true);
        }
        updateGridPane(); // Update the UI
    }


    // Reset the coordinates pointing to the active square. Effectively deselects any selected square on the game board UI
    private void reset_active() {
        System.out.println("Reset active called");
        active_row = active_col = INACTIVE;
    }

    // Handle user clicking the Undo Move button
    @FXML
    void on_click_undo(ActionEvent event) throws Exception {
        System.out.println("Undo Button Clicked");
        Move last_move = gameboard.pop_move();
        if (last_move != null) {
            int prev_val = last_move.get_prev_val();
            int row = last_move.get_square().get_row();
            int col = last_move.get_square().get_col();
            gameboard.set_value_at(row, col, prev_val, true);
            gameboard.increment_move_count();
        }

        updateGridPane();
        board_ui.requestFocus();
    }

    @FXML
    void on_return_to_main(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mainmenu.fxml"));
        Parent root = loader.load();
        Node node = (Node) event.getSource();
        Stage stage = (Stage) node.getScene().getWindow();
        stage.close();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        
    }
    
}
