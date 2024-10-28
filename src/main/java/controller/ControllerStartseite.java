package controller;

import formatierungstool.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ControllerStartseite implements Initializable{
    public AnchorPane anchorPane;
    @FXML
    public void buttonFreitextAction(ActionEvent event) {
        try {


            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Freitext_Aufgabe_FX.fxml"));
            Parent root = loader.load();
            Main.stage.setScene(new Scene(root));
            Main.stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonMultipleAction(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Multiple_Choice_Aufgabe_FX.fxml"));
            Parent root = loader.load();
            Main.stage.setScene(new Scene(root));
            Main.stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void buttonProgrammBeendenAction(ActionEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
