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
import org.apache.poi.xwpf.usermodel.*;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ControllerFreitext implements Initializable {
    @FXML
    private TextArea freitextFrageTextArea;
    @FXML
    private TextField freitextNeuesDokumentTextField;
    @FXML
    private ComboBox<String> freitextComboBox;
    @FXML
    private TextField freitextPunkte;
    @FXML
    private TextField freitextName;
    @FXML
    private TextField freitextZeilen;
    @FXML
    private Label freitextLabelDokumentAusgewaehlt;
    private String[] textMitUmbruch = null;
    private String textToAdd = null;
    private String neuesDokument;
    private String name;
    private String punkte;
    private XWPFDocument document;
    private int anzahlLeerzeilenInt;
    private MenuItem menuItem;
    private static List<String> dokumentenname = new ArrayList<>();
    ObservableList<String> menuItemsListe = FXCollections.observableArrayList();

    static public List<String> getDokumentenname() {
        return dokumentenname;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public void freitextDokumentErstellenAction(ActionEvent event) {
        // es wird ein neues leeres docx Dokument mit dem im Textfield eingegebenen Namen und vorab erstellten Layout erstellt
        dokumentErstellen(freitextFrageTextArea, freitextNeuesDokumentTextField, freitextComboBox);
    }

    public void textHinzufuegen() {
        // Auslesen des Textes aus dem TextField freitextName, wenn es nicht leer ist
        if (!freitextName.getText().isEmpty()) {
            // wenn im TextField etwas steht, wird der Text im String "name" gespeichert. Dies wird die Überschrift der Aufgabe
            name = freitextName.getText() + " ";
        }else{name = "";}
        //  wenn das TextField der Punkteeingabe nicht leer ist, dann werden diese im String "punkte" gespeichert
        if (!freitextPunkte.getText().isEmpty()) {
            // Es wird der Text nur gespeichert, wenn ausschliesslich ganze Zahlen eingetragen sind
            if (freitextPunkte.getText().matches("^[0-9_ ]*$")) {
                punkte = "(" + freitextPunkte.getText() + " Punkte" + ")";
            } else {
                // erstellen eines Alerts, wenn bei der Eingabe der Punkte NICHT nur ganze Zahlen eingegeben werden, sondern andere Zeichen
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Achtung Fehler!");
                alert.setHeaderText(null);
                alert.setContentText("Die Eingabe der Punkte muss aus ganzen Zahlen bestehen oder frei gelassen werden! ");
                alert.showAndWait();
            }
        }else{
            punkte = "";
        }

        //  speichern des Textes aus der TextArea im String "textToAdd"
        textToAdd = freitextFrageTextArea.getText();
        try {
            // Laden des vorhandenen Word-Dokuments
            FileInputStream fis = new FileInputStream("src\\main\\resources\\formatierungstool\\vorhandeneDokumente\\" + freitextLabelDokumentAusgewaehlt.getText());
            document = new XWPFDocument(fis);
            // erstellen neuer Absätze für Aufgabenname und Punkte in einer Zeile und den Aufgabentext
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
            // einstellen, das der text linksbündig ist
            paragraphAufgabe.setAlignment(ParagraphAlignment.LEFT);
            // Fette darstellung der Zeile
            runAufgabe.setBold(true);


            // Iteration via For-Each über String Array
            for (String zeile : textMitUmbruch) {
                // Es wird ein neuer Run erstellt und dann der Text der aktuellen Zeile zu diesem Run hinzugefügt. Anschließend wird mit run.addCarriageReturn() ein manueller Zeilenumbruch zum Run hinzugefügt.
                XWPFRun run = paragraphText.createRun();
                run.setText(zeile);
                run.addCarriageReturn();
            }
            // wenn im TextField für die Zeilenanzahl nur ganze Zahlen stehen und dieser nicht leer ist, wird die Anzahl ausgelesen und anschließend in einen Integer geparst
            if (freitextZeilen.getText().matches("^[0-9_ ]*$") && !freitextZeilen.getText().isEmpty()) {
                String anzahlLeerzeilenString = freitextZeilen.getText();
                anzahlLeerzeilenInt = Integer.parseInt(anzahlLeerzeilenString);
                // Ansonsten folgt ein Alert mit einer Fehlermeldung
            } else if (freitextZeilen.getText().isEmpty()) {
                // erstellen eines Alerts, wenn bei der Eingabe der Punkte NICHT nur ganze Zahlen eingegeben werden, sondern andere Zeichen
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Achtung Fehler!");
                alert.setHeaderText(null);
                alert.setContentText("Keine Zeilenanzahl für Antworten eingetragen! ");
                alert.showAndWait();
            } else {
                // erstellen eines Alerts, wenn bei der Eingabe der Punkte NICHT nur ganze Zahlen eingegeben werden, sondern andere Zeichen
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Achtung Fehler!");
                alert.setHeaderText(null);
                alert.setContentText("Die Eingabe der Zeilen muss aus ganzen Zahlen bestehen! ");
                alert.showAndWait();
            }
            // Methode zum Einfügen der Antwortzeilen aufrufen
            zeilenEinfuegen();
            // Speichern des Word-Dokuments unter einem neuen Namen
            FileOutputStream outStream = new FileOutputStream(new File("src\\main\\resources\\formatierungstool\\vorhandeneDokumente\\" + freitextLabelDokumentAusgewaehlt.getText()));
            document.write(outStream);
            // Schließen des Streams
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void buttonZurueckZurStartseiteFreitextAction(ActionEvent event) {
        // bei click, wird die aktuelle stage geschlossen
        /*Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();*/

        try { // und anschließend eine neue STage erstellt und die FXML der Startseite geladen
            /*FXMLLoader loader = new FXMLLoader(Main.class.getResource("Startseite_FX.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setFullScreen(true);
            newStage.show();*/
            FXMLLoader loader = new FXMLLoader(Main.class.getResource("Startseite_FX.fxml"));
            Parent root = loader.load();
            Main.stage.setScene(new Scene(root));
            Main.stage.setFullScreen(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void buttonProgrammBeendenFreitextAction(ActionEvent event) {
        // bei click wird die aktuelle Stage geschlossen
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public void menuItemErstellen() {
        // Den Pfad zum Ordner "Dokumente" angeben
        File folder = new File("src\\main\\resources\\formatierungstool\\vorhandeneDokumente");
        // Überprüfen, ob der Ordner existiert und ob er ein Verzeichnis ist
        if (folder.exists() && folder.isDirectory()) {
            // Alle Dateien im Ordner abrufen
            File[] files = folder.listFiles();
            // wenn Dateienn vorhanden sind
            if (files != null) {
                // wird durch das Array iteriert
                for (File file : files) {
                    // Überprüft, ob es sich um eine docx-Datei handelt
                    if (file.getName().endsWith(".docx")) {
                        // Für jede .docx-Datei im Ordner ein MenuItem mit dem Dateinamen erstellen und hinzufügen
                        menuItem = new MenuItem(file.getName());
                        // der Text des menuItems in einem String gespeichert
                        String docItem = menuItem.getText();
                        // der String in einer Array Liste gespeichert
                        dokumentenname.add(docItem);
                    }
                }
            }
        }
    }

    private void handleMenuItemSelection(ActionEvent event) {
        // selectiertes menuItem auslesen
        MenuItem selectedMenuItem = (MenuItem) event.getSource();
        // String nach des ausgewählten MenuItems speichern
        String selectedDocumentName = selectedMenuItem.getText();
        // Label bekommt Text des ausgewählten MenuItems
        freitextLabelDokumentAusgewaehlt.setText(selectedDocumentName);
    }

    private void ausgewaehltesDokumentAnzeigen() {
        // Iterieren durch Liste dokumentenname(String)
        for (String dokument : dokumentenname) {
            // erstellen eines MenuItems
            MenuItem menuItem = new MenuItem(dokument);
            // menuItem bekommt onActionMethode die eigenst erstellt wurde
            menuItem.setOnAction(this::handleMenuItemSelection);
            // dokument wird der Liste der MenuItems hinzugefügt
            menuItemsListe.add(dokument);
            // ComboBox bekommt Items mit den namen der Dokumente
            freitextComboBox.getItems().add(dokument);
            // die MenuItems konnen selectiert werden
            freitextComboBox.setOnAction(this::handleComboBoxSelection);

        }
    }

    public void handleComboBoxSelection(ActionEvent event) {
        // der Name des ausgewählten MenuItems, wird in einem String gespeichert
        String selectedItem = freitextComboBox.getValue();

        // Setze den Text des Labels auf das ausgewählte Menüelement.
        freitextLabelDokumentAusgewaehlt.setText(selectedItem);
    }

    public void zeilenEinfuegen() {
        for (int i = 0; i < anzahlLeerzeilenInt; i++) {
            // Es wird eine Tabelle erstellt
            XWPFTable table = document.createTable(1, 1);
            // Festlegen der Breite der Tabelle auf die Breite des gesamten Dokuments
            CTTblWidth width = table.getCTTbl().addNewTblPr().addNewTblW();
            width.setType(STTblWidth.PCT);
            // Prozentwert für die Breite (hier: 5000 bedeutet 100%)
            width.setW(BigInteger.valueOf(5000));
            // Entfernen des linken Rahmens der Tabelle
            CTTblPr tblPr = table.getCTTbl().getTblPr();
            if (tblPr == null) tblPr = table.getCTTbl().addNewTblPr();
            CTBorder borderL = CTBorder.Factory.newInstance();
            borderL.setVal(STBorder.NONE);
            if (tblPr.isSetTblBorders()) {
                tblPr.getTblBorders().unsetLeft();
            }
            // Entfernen des rechten Rahmens der Tabelle
            if (tblPr.isSetTblBorders()) {
                tblPr.getTblBorders().unsetRight();
            }
        }
        // erstellen eines neues Absatzes im ausgewählten Dokument
        XWPFParagraph paragraph = document.createParagraph();
        // paragraph wird ein neuer run hinzugefügt
        XWPFRun run = paragraph.createRun();
        // Manueller Absatz wird hinzugefügt
        run.addCarriageReturn();
    }

    public void dokumentErstellen(TextArea textArea, TextField textField, ComboBox<String> comboBox) {
        // der Inhalt der TextArea wir in einem String gespeichert
        textToAdd = textArea.getText();
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
        // ComboBox bekommt Items mit den namen der Dokumente
        comboBox.getItems().add(textField.getText() + ".docx");
        // die MenuItems konnen selectiert werden
        comboBox.setOnAction(this::handleComboBoxSelection);
    }

    public void freitextFrageHinzufuegenAction() {
        textHinzufuegen();
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Methoden, die beim Aufrufen der Seite ausgeführt werden
        menuItemErstellen();
        ausgewaehltesDokumentAnzeigen();// Erhalte alle verfügbaren Bildschirme


    }

}
