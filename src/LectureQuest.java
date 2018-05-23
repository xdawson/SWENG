import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import java.util.ArrayList;
import javafx.scene.layout.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.io.File;


public class LectureQuest extends Application {

  private Colors programDefaultColor = new Colors("#000000", "#000000");
  private TextStyle programDefaultStyle = new TextStyle("Arial", 20, false, false, false);

  private Defaults programDefault = new Defaults(programDefaultStyle, programDefaultColor);

  private Presentation presentation;

  public static void main(String[] args) { launch(args); }

  @Override
  public void start(Stage primaryStage) {

    primaryStage.setTitle("Lecture Quest Alpha");
    //primaryStage.getIcons().add(new Image(this.getClass().getResource("../resources/LQ_logo_2_32.png").toExternalForm()));
    //TODO Make sure this does what it's supposed to
    primaryStage.getIcons().add(new Image("file:../resources/LQ_logo_2_32.png"));

    File questXml = openFile(primaryStage);

    if(questXml == null) {
      System.out.println("null or invalid file chosen. Exiting.");
      stop();
    }
    else {
      XMLParserNew xmlReader = new XMLParserNew(questXml, programDefault);
      presentation = xmlReader.getPresentation();

      //TODO Make sure this does what it's supposed to
      //Font.loadFont(this.getClass().getResource("../resources/fonts/BebasNeue-Regular.ttf").toExternalForm(), 20);
      Font.loadFont(this.getClass().getResourceAsStream("../resources/fonts/BebasNeue-Regular.ttf"), 20);

      Scene scene = new Scene(this.presentation.pane, presentation.getWidth(), presentation.getHeight());

      
      //scene.getStylesheets().add(getClass().getResource("../resources/presentationStyle.css").toExternalForm());
//    scene.getStylesheets().add("https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.css");
      //TODO Make sure this does what it's supposed to
      File f = new File("presentationStyle.css");
      scene.getStylesheets().clear();
      scene.getStylesheets().add("file:///" + f.getAbsolutePath().replace("\\", "/"));

      scene.setOnKeyPressed((keyEvent) -> {
        switch(keyEvent.getCode()) {
          case ESCAPE:
            stop();
            break;
          case RIGHT:
            this.presentation.moveNextSlide();
            break;
          case LEFT:
            // TODO: go to previous slide
            this.presentation.moveBackSlide();
            break;
        }
      });

      primaryStage.setScene(scene);
      primaryStage.show();
    }
  }

  private File openFile (Stage stage) {
    FileChooser fileChooser = new FileChooser();
    fileChooser.setTitle("Open Image");
    fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Quest", "*.4l"),
            new FileChooser.ExtensionFilter("PWS", "*.pws"),
            new FileChooser.ExtensionFilter("All", "*.*")
    );
    return fileChooser.showOpenDialog(stage);
  }

  public void setSlide(int newLevel, int newQuestion) {
    this.levelNum = newLevel + 1;
    this.qNum = (newQuestion + 1);
    this.presentation.setText("Level: " + Integer.toString(levelNum) + " Question: " + Integer.toString(qNum));
    System.out.println("Level: " + Integer.toString(levelNum) + " Question: " + Integer.toString(qNum));
    presentation.moveSlide(CombineMenuID(levelNum, qNum));
  }

  public String CombineMenuID(int newLevel, int newQuestion){
    String newMenuID = (newLevel + "/" + newQuestion + "/" + 1);
    return newMenuID;
  }

  @Override
  public void stop() { Platform.exit(); }
}
