package formatierungstool;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class Main extends Application {

    public static Stage stage;
    // Methode, um Programm auf 2. Bildschirm zu starten
    public static void setScreen(Stage stage){
        for (Screen screen : Screen.getScreens()) {
            System.out.println("Screen: " + screen.getBounds());
        }

        // Wähle den zweiten Bildschirm aus (Index 1)
        Screen secondScreen = Screen.getScreens().get(1);

        stage.setX(secondScreen.getVisualBounds().getMinX()); // Setze x-Koordinate basierend auf dem zweiten Bildschirm
        stage.setY(secondScreen.getVisualBounds().getMinY()); // Setze y-Koordinate basierend auf dem zweiten Bildschirm*/
    }
    @Override
    public void start(Stage stage) throws IOException {
        Main.stage =stage;
        setScreen(stage);
        // Laden der FXML - Startseite
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("Startseite_FX.fxml")));
        // Setzen der FXML auf die Scene
        Scene scene = new Scene(root);
        // Scene wird der Stage hinzugefügt
        stage.setScene(scene);
        // setzen des öffnenden Fensters auf Vollbild
        stage.setFullScreen(true);
        // anzeigen des Fensters
        stage.show();

    }
    public static void main(String[] args) throws IOException {
        // starten des Programms
        launch();
    }
}