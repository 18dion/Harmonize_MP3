package application;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("mp3.fxml"));
        mp3Controller controller = new mp3Controller();
        loader.setController(controller);
        Parent root = loader.load();
        
        // Create a scene with the loaded FXML content
        Scene scene = new Scene(root);
        
        // Set the scene to the stage
        primaryStage.setScene(scene);
        
        // Set the title of the stage
        primaryStage.setTitle("Harmonize MP3");
        
        // Show the stage
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

