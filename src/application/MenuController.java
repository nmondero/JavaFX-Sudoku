package application;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.io.IOException;
import javafx.scene.Node;

public class MenuController {

    @FXML
    private Slider start_amount_slider; // Slider that adjusts the start value

    @FXML
    private Label start_count_label; // Label that displays the starting square count

    @FXML
    private Button start_game_btn; // Button that starts a game

    // Add a listener to the slider's value to update the label indicating it's value to the user
    @FXML
    void initialize() {
        start_amount_slider.valueProperty().addListener((new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> oberservable, Number oldValue, Number newValue) {
                start_count_label.setText("Starting Squares: " + newValue.intValue());
            }
        }));
        start_amount_slider.setValue(15); // Set start amount to default value of 15
    }

    // Handle clicking start game
    @FXML
    void on_start_game_clicked(MouseEvent event) throws Exception, IOException {
        GameBoard gameboard; // Create a gameboard variable
        boolean success = false;
        
        while (!success) { // Keep trying to make a new gameboard until it works (THIS IS NOT GUARANTEED TO EVEN GENERATE A SOLVABLE GAME BOARD!!!!! NEEDS A RECURSIVE BACKTRACKING SOLUTION IN GameBoard CONSTRUCTOR!!!)
            try {
                gameboard = new GameBoard((int) start_amount_slider.getValue()); // Construct a new gameboard using the start amount slider value
                gameboard.print_to_console(); 
                success = true; // Signal that we can break the while loop after completing the rest of the tasks (we finally successfully created a GameBoard object)
                
                Node node = (Node) event.getSource();
                Stage stage = (Stage) node.getScene().getWindow();
                stage.close();

                // Load the game screen FXML
                FXMLLoader loader = new FXMLLoader(getClass().getResource("gamescreen.fxml"));
                Parent root = loader.load();
                GameController controller = loader.getController(); // Load a GameController in
                stage.setUserData(gameboard); // Attach the gameboard to the stage so we can extract it in another scene
                stage.setOnShown(windowEvent -> controller.onStageReady(stage)); // Add event listener
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("Exception: " + e.getMessage());
            }
        }
    }
}

