/*Program Name: Sudoku.java
 * Author: Nate Mondero
 * Last Update: 12/13/2024
 * Purpose: This program utilizes JavaFX with Screenbuilder to implement a simple GUI-based
 *          desktop Sudoku game.
 * 
 * 
 * WARNING: My original algorithm to generate the game board is not guaranteed to generate 
 *          solvable initial board states. The chance rises with a higher number of starting
 *          squares prefilled.
 * WARNING FIX: The board generation algorithm needs to be changed to a recursive backtracking
 *              that guarantees that any board generated can be solved.
 */

package application;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;

public class Sudoku extends Application {

    @Override
    public void start(Stage primary_stage) throws Exception {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(("mainmenu.fxml")));
            Scene scene = new Scene(root);
            primary_stage.setScene(scene);
            primary_stage.show();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args){
        launch(args);
    }
}
