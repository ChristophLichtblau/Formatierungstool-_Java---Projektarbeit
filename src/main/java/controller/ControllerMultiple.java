package controller;

import formatierungstool.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerMultiple implements Initializable {
    @FXML
    TextArea multipleAntwort;
    @FXML
    TextArea multipleFrage;
    @FXML
    TextField multipleNameNeuesDokument;
    @FXML
    TextField multipleAnzahlCheckbox;
    @FXML
    Label multipleDokumentAusgewaehlt;
    @FXML
    ComboBox<String> multipleVorhandeneDokumente;
    @FXML
    TextField multipleNameDerAufgabe;
    @FXML
    TextField multipleErreichbarePunkte;
    private String textToAdd = null;
    private XWPFDocument document;
    private String neuesDokument;
    ObservableList<String> menuItemsListe = FXCollections.observableArrayList();
    private MenuItem menuItem;
    private List<String> dokumentenname = new ArrayList<>();
    private String name;
    private String punkte;
    private String[] textMitUmbruch = null;
    private String[] checkboxenMitUmbruch = null;
    private String checkboxToAdd = null;



    public void buttonProgrammBeendenMultipleAction(ActionEvent event){
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }


    public void multipleZurueckZurStartseiteAction(ActionEvent event){
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Startseite_FX.fxml"));
            Parent root = loader.load();
            Main.stage.setScene(new Scene(root));
            Main.stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void multipleCheckboxEinfuegenAction(ActionEvent event){
        // Setze den einfügenden Text via Unicode auf Checkbox
        String text ="\u2610" + " ";
        StringBuilder builder = new StringBuilder();
        // auslesen, wie viele Checkboxen eingefügt werden sollen
        String anzahlCheckbox = multipleAnzahlCheckbox.getText();
        // einfügen eines Zeilenumbruchs nach jeder eingefügten Checkbox
        if(anzahlCheckbox.matches("\\d+")) {
            int anzahlCheckboxInt = Integer.parseInt(anzahlCheckbox);
            for(int i = 0; i < anzahlCheckboxInt; i++){
                StringBuilder append = builder.append(text).append("\n");
            }
            multipleAntwort.appendText(builder.toString());
        }else{
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Achtung Fehler!");
            alert.setHeaderText(null);
            alert.setContentText("Bei der Anzahl der Checkboxen dürfen nur ganze Zahlen eingegeben werden!");
            alert.showAndWait();
        }


    }
    public void multipleDokumentErstellenAction(ActionEvent event){
        dokumentErstellen(multipleFrage, multipleAntwort, multipleNameNeuesDokument, multipleVorhandeneDokumente);
    }
    public void multipleZuDokumentHinzufuegenAction(ActionEvent event){
        textHinzufuegen();
    }
    public void menuItemErstellen() {
        // Den Pfad zum Ordner "Dokumente" angeben
        File folder = new File("src\\main\\resources\\formatierungstool\\vorhandeneDokumente\\");
        // Überprüfen, ob der Ordner existiert und ob er ein Verzeichnis ist
        if (folder.exists() && folder.isDirectory()) {
            // Alle Dateien im Ordner abrufen
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    // Überprüfen, ob es sich um eine .txt-Datei handelt
                    if (file.getName().endsWith(".docx")) {
                        // Für jede .docx-Datei im Ordner ein MenuItem mit dem Dateinamen erstellen und hinzufügen
                        menuItem = new MenuItem(file.getName());
                        String docItem = menuItem.getText();
                        dokumentenname.add(docItem);
                    }
                }
            }
        }
    }
    public void textHinzufuegen(){
        // Auslesen des Textes aus dem TextField freitextName, wenn es nicht leer ist
        if (!multipleNameDerAufgabe.getText().isEmpty()) {
            name = multipleNameDerAufgabe.getText() + " ";
        }else{name = "";}
        if (!multipleErreichbarePunkte.getText().isEmpty()) {
            // Es wird der Text nur gespeichert, wenn ausschliesslich ganze Zahlen eingetragen sind
            if (multipleErreichbarePunkte.getText().matches("^[0-9_ ]*$")) {
                punkte = "(" + multipleErreichbarePunkte.getText() + " Punkte" + ")";
            } else {
                // erstellen eines Alerts, wenn bei der Eingabe der Punkte NICHT nur ganze Zahlen eingegeben werden, sondern andere Zeichen
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Achtung Fehler!");
                alert.setHeaderText(null);
                alert.setContentText("Die Eingabe der Punkte muss aus ganzen Zahlen bestehen oder frei gelassen werden! ");
                alert.showAndWait();
            }
        }else{punkte = "";}
        // Nach dem Auslesen des Textes:
        //  speichern des Textes aus der TextArea in einem String
        textToAdd = multipleFrage.getText();
        try {
            // Laden des vorhandenen Word-Dokuments
            FileInputStream fis = new FileInputStream("src\\main\\resources\\formatierungstool\\vorhandeneDokumente\\" + multipleDokumentAusgewaehlt.getText());
            document = new XWPFDocument(fis);
            // Hinzufügen des Textes aus dem String zum Word-Dokument
            XWPFParagraph paragraphAufgabe = document.createParagraph();
            XWPFParagraph paragraphText = document.createParagraph();
            // Abgleich, ob es Zeilenumbrüche in der TextArea gibt
            if (textToAdd.contains("\n")) {
                // Der String wird bei Umbrüchen geteilt und in einem String Array abgespeichert
                textMitUmbruch = textToAdd.split("\n");
            } else {
                if (textMitUmbruch == null) {
                    textMitUmbruch = new String[]{textToAdd};
                } else if (textMitUmbruch.length == 0) {
                    textMitUmbruch[0] = textToAdd;
                }
            }
            // erstellen eines Runs für den Absatz aufgabenname und Punkte
            XWPFRun runAufgabe = paragraphAufgabe.createRun();
            // einfügen des Textes der Aufgabe und Anzahl der Punkte
            runAufgabe.setText(name + " " + punkte);
            // einstellen, dass der text linksbündig ist
            paragraphAufgabe.setAlignment(ParagraphAlignment.LEFT);
            // Fette darstellung der Zeile
            runAufgabe.setBold(true);
            for (String zeile : textMitUmbruch) {
                // Es wird ein neuer Run erstellt und dann der Text der aktuellen Zeile zu diesem Run hinzugefügt. Anschließend wird mit run.addCarriageReturn() ein manueller Zeilenumbruch zum Run hinzugefügt.
                XWPFRun run = paragraphText.createRun();
                run.setText(zeile);
                run.addCarriageReturn();
            }
            checkboxToAdd = multipleAntwort.getText();
            // Abgleich, ob es Zeilenumbrüche in der TextArea gibt
            if (checkboxToAdd.contains("\n")) {
                // Der String wird bei Umbrüchen geteilt und in einem String Array abgespeichert
                checkboxenMitUmbruch = checkboxToAdd.split("\n");
            } else {
                if (checkboxenMitUmbruch == null) {
                    checkboxenMitUmbruch = new String[]{textToAdd};
                } else if (checkboxenMitUmbruch.length == 0) {
                    checkboxenMitUmbruch[0] = checkboxToAdd;
                }
            }
            // Iteration via For-Each über String Array
            for (String zeile : checkboxenMitUmbruch) {
                // Es wird ein neuer Run erstellt und dann der Text der aktuellen Zeile zu diesem Run hinzugefügt. Anschließend wird mit run.addCarriageReturn() ein manueller Zeilenumbruch zum Run hinzugefügt.
                XWPFRun run = paragraphText.createRun();
                run.setText(zeile);
                run.addCarriageReturn();
            }
            // Speichern des Word-Dokuments unter einem neuen Namen
            FileOutputStream outStream = new FileOutputStream(new File("src\\main\\resources\\formatierungstool\\vorhandeneDokumente\\" + multipleDokumentAusgewaehlt.getText()));
            document.write(outStream);
            // Schließen des Streams
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void dokumentErstellen(TextArea textAreaFrage, TextArea textAreaAntwort, TextField textField, ComboBox<String> comboBox){
        textToAdd = textAreaFrage.getText() + textAreaAntwort.getText();
        try {
            // Laden des vorhandenen Word-Dokuments
            FileInputStream fis = new FileInputStream("src\\main\\resources\\formatierungstool\\wordLayout\\vorlage.docx");
            document = new XWPFDocument(fis);
            File vergleicheDatei = new File("src\\main\\resources\\formatierungstool\\vorhandeneDokumente\\" + textField.getText() + ".docx");

            if (!textField.getText().isEmpty() && textField.getText().matches("^[a-zA-Z0-9_ ]*$") && !vergleicheDatei.exists()) {
                // Speichern des Word-Dokuments unter einem neuen Namen
                neuesDokument = textField.getText();
                FileOutputStream outStream = new FileOutputStream(new File("src\\main\\resources\\formatierungstool\\vorhandeneDokumente\\" + neuesDokument + ".docx"));
                document.write(outStream);
                // Schließen des Streams
                outStream.close();
            } else {
                // erstellen eines Alerts, wenn Datei existiert, das Namensfeld für den Dateinamen leer ist oder eine Datei mit dem selben Namen bereits existiert
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Achtung Fehler!");
                alert.setHeaderText(null);
                alert.setContentText("Dokument konnte nicht gespeichert werden!\nBitte überprüfe, dass der Dokumentenname noch nicht vergeben ist und keine Sonderzeichen im Namen enthalten sind. ");
                alert.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        MenuItem menuItem = new MenuItem(textField.getText() + ".docx");
        // menuItem bekommt onActionMethode die eigenst erstellt wurde
        menuItem.setOnAction(this::handleMenuItemSelection);
        // dokument wird der Liste der MenuItems hinzugefügt
        menuItemsListe.add(textField.getText() + ".docx");
        // ComboBox bekommt Items mit dem Namen der Dokumente
        comboBox.getItems().add(textField.getText() + ".docx");
        // die MenuItems können selektiert werden
        comboBox.setOnAction(this::handleComboBoxSelection);
    }
    private void handleMenuItemSelection(ActionEvent event) {
        // selectiertes menuItem auslesen
        MenuItem selectedMenuItem = (MenuItem) event.getSource();
        // String nach des ausgewählten MenuItems speichern
        String selectedDocumentName = selectedMenuItem.getText();
        // Label bekommt Text des ausgewählten MenuItems
        multipleDokumentAusgewaehlt.setText(selectedDocumentName);
    }
    public void handleComboBoxSelection(ActionEvent event) {
        String selectedItem = multipleVorhandeneDokumente.getValue();

        // Setze den Text des Labels auf das ausgewählte Menüelement.
        multipleDokumentAusgewaehlt.setText(selectedItem);
    }
    private void ausgewaehltesDokumentAnzeigen(){
        // Iterieren durch Liste dokumentenname(String)
        for (String dokument : dokumentenname) {
            // erstellen eines MenuItems
            MenuItem menuItem = new MenuItem(dokument);
            // menuItem bekommt onActionMethode die eigenst erstellt wurde
            menuItem.setOnAction(this::handleMenuItemSelection);
            // dokument wird der Liste der MenuItems hinzugefügt
            menuItemsListe.add(dokument);
            // ComboBox bekommt Items mit den namen der Dokumente
            multipleVorhandeneDokumente.getItems().add(dokument);
            // die MenuItems konnen selectiert werden
            multipleVorhandeneDokumente.setOnAction(this::handleComboBoxSelection);

        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        menuItemErstellen();
        ausgewaehltesDokumentAnzeigen();
    }





    // Zeichensatz für Checkbox "\u2610"

}
