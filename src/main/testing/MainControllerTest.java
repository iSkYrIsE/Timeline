package Testing;

import Controllers.PopupController;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.stage.Window;
import main.Main;
import org.junit.Before;
import org.junit.Test;

import static org.testfx.api.FxAssert.*;
import static org.testfx.util.NodeQueryUtils.hasText;
import static org.testfx.util.NodeQueryUtils.isVisible;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.base.ParentMatchers;
import org.testfx.service.query.PointQuery;

import java.io.IOException;

public class MainControllerTest extends ApplicationTest {




    @Before
    public void setup() throws Exception {
        FxToolkit.registerPrimaryStage();
        FxToolkit.setupApplication(Main.class);
    }

    @Test
    public void adminPanel() {
       login();
       clickOn("#view_edit_toggle");
       clickOn("#timelines_list");
       press(KeyCode.DOWN);
       clickOn("#admin_panel_button");
        verifyThat("#accountview", ParentMatchers.hasChild());

    }

    @Test
    public void addPanel() {
        login();
        clickOn("#view_edit_toggle");
        clickOn("#timelines_list");
        press(KeyCode.DOWN);
        clickOn("#add_event_button");
        verifyThat("#accountview", ParentMatchers.hasChild());

    }



    @Test
    public void editPanel() {
        login();
        clickOn("#view_edit_toggle");
        clickOn("#timelines_list");
        press(KeyCode.DOWN);
        clickOn("#edit_timeline_button");
        verifyThat("#accountview", ParentMatchers.hasChild());

    }


    @Test
    public void login() {
        verifyThat(lookup("#add_timeline_btn"), hasText("+"));
        clickOn("#loginButton");
        clickOn("#UserNameTextField");
        type(KeyCode.A,KeyCode.S,KeyCode.D);
        clickOn("#UserNamePasswordField");
        type(KeyCode.A,KeyCode.S,KeyCode.D);
        clickOn("#LogInButton");
    }

    @Test
    public void listVisualization() {
        clickOn("#timelines_list");
        verifyThat("#timeline_scroll_pane", ParentMatchers.hasChild());
    }

    @Test
    public void edit_no_login() {
        //button exists
        verifyThat(lookup("#view_edit_toggle"), Node::isVisible);
        //click
        clickOn("#view_edit_toggle");
        //transition
        verifyThat(lookup("#accountview"), Node::isVisible);
        //error message exists
        verifyThat(lookup("#accountview"), ParentMatchers.hasChild());
    }

    @Test
    public void make_account() {
        //given
        clickOn("#loginButton");
        clickOn("#createAccountUsername");

        //WHEN non-existing credentials are given
        type(KeyCode.A,KeyCode.DIGIT2);
        clickOn("#createAccountFullName");
        type(KeyCode.A,KeyCode.DIGIT2);
        clickOn("#createAccountPassword");
        type(KeyCode.A,KeyCode.DIGIT2);
        clickOn("#createAccountPasswordHint");
        type(KeyCode.A,KeyCode.DIGIT2);
        clickOn("#buttonCreateAccount");
    }

    @Test
    public void edit_login() {
        login();
        //button exists
        verifyThat(lookup("#view_edit_toggle"), Node::isVisible);
        //click
        clickOn("#view_edit_toggle");

    }

    @Test
    public void add_timeline() {
        login();
        clickOn("#view_edit_toggle");
        clickOn("#add_timeline_btn");
        clickOn("#timelineName");
        type(KeyCode.A, 3);
        clickOn("#timelineDescription");
        type(KeyCode.A, 3);
        clickOn("#timeUnitBox");
        clickOn("Centuries");
        clickOn("#startDatePicker");
        clickOn("2");
        clickOn("#endDatePicker");
        clickOn("3");
        clickOn("#createButton");

    }

    @Test
    public void should_contain_list() throws Exception {
        verifyThat(lookup("#timelines_list"), NodeMatchers.isNotNull());

    }






}