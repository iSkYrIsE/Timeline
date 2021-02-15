package Testing;

import Controllers.AccountViewController;
import Controllers.AdminPanelPopupController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

public class AdminPanelPopupControllerTest extends ApplicationTest {
    AdminPanelPopupController appc;

    @Before
    public void setup() throws Exception, IOException {
        FxToolkit.registerPrimaryStage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AdminPanelPopup.fxml"));
        this.appc = loader.getController();
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
    public void usernameAdmin() {
        clickOn("#adminPanelUsername").type(KeyCode.H, KeyCode.I);
        verifyThat(lookup("#adminPanelUsername"), hasText("hi"));
    }
    @Test
    public void fullNameAdmin() {
        clickOn("#adminPanelFullName").type(KeyCode.H, KeyCode.I);
        verifyThat(lookup("#adminPanelFullName"), hasText("hi"));
    }

    @Test
    public void passwordAdmin() {
        clickOn("#adminPanelPassword").type(KeyCode.H, KeyCode.I);
        verifyThat(lookup("#adminPanelPassword"), hasText("hi"));
    }
    @Test
    public void passwordHintAdmin() {
        clickOn("#adminPanelPasswordHint").type(KeyCode.H, KeyCode.I);
        verifyThat(lookup("#adminPanelPasswordHint"), hasText("hi"));
    }

    @Test
    public void checkboxAdmin() {
        //what
        clickOn("#checkBox_isAdmin");
        assertEquals(true, appc.getCheckBox_isAdmin().isSelected());
       // verifyThat(lookup("#checkBox_isAdmin"), Node::isPressed);
    }


    @Test
    public void testEditAdmin() {
        clickOn("#admin_listView");

        type(KeyCode.DOWN);
        clickOn("#adminPanelUsername");
        usernameAdmin();
        clickOn("#adminPanelPassword");
        passwordAdmin();
        clickOn("#adminPanelPasswordHint");
        passwordHintAdmin();
        clickOn("#adminPanelFullName");
        fullNameAdmin();
        clickOn("#buttonCreateAccount");
    }


}