package stages;

import core.Session;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

/**
 * Class representing a popup stage/window in the application
 */
public class PopupStage {
    public PopupStage(Stage popup, String viewName) throws IOException {
        // Loads the required scene
        Parent root;
        root = FXMLLoader.load(getClass().getResource("/views/"+viewName));

        // Sets window (stage) to default size
        popup.setScene(new Scene(root, 700, 600));

        // Sets modality of the stage (can't work with other windows if popup is open
        popup.initModality(Modality.APPLICATION_MODAL);

        // Sets application window name
        popup.setTitle("Organised.");

        // Makes scene non resizable and removes toolbar
        popup.setResizable(false);
        popup.initStyle(StageStyle.UNDECORATED);

        // Adds the application logo
        popup.getIcons().add(new Image("/images/icon.png"));

        // Focuses away from the fields for prompt text to be visible
        root.requestFocus();

        // Saves the stage of the popup, to use it for dragging
        Session.setPopupStage(popup);

        // Sets the coordinates of the stage to the middle of the main stage
        popup.setX(Session.getMainStage().getX()+Session.getMainStage().getWidth()/4);
        popup.setY(Session.getMainStage().getY()+Session.getMainStage().getHeight()/6);

        // Shows the window
        popup.show();
    }
}
