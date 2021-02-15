package Testing;

import Controllers.AdminPanelPopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

public class EventCreationPopupControllerTest extends ApplicationTest {

    AdminPanelPopupController ecpc;


    @Before
    public void setup() throws Exception, IOException {
        FxToolkit.registerPrimaryStage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/EventCreationPopup.fxml"));
        this.ecpc = loader.getController();
        Scene test = new Scene(loader.load());
        FxToolkit.setupStage(stage -> {
            stage.setScene(test);
        });
        FxToolkit.showStage();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
    }


    @Test
    public void eventName() {
        clickOn("#eventName").type(KeyCode.H, KeyCode.I);
        verifyThat(lookup("#eventName"), hasText("hi"));
    }
    @Test
    public void eventDesc() {
        clickOn("#eventDesc").type(KeyCode.H, KeyCode.I);
        verifyThat(lookup("#eventDesc"), hasText("hi"));
    }

    @Test
    public void startingDate() {
        //lookup better method
        clickOn("#startingDate");
    }

    @Test
    public void cancelButton() {
        clickOn("#cancelButton");
        //fix missing ids
    }

}