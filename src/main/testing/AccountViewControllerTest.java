package Testing;

import Controllers.AccountViewController;
import Controllers.MainController;
import Models.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import main.Main;
import org.junit.Before;
import org.junit.Test;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.matcher.base.NodeMatchers;
import org.testfx.matcher.base.ParentMatchers;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.util.NodeQueryUtils.hasText;

public class AccountViewControllerTest extends ApplicationTest {

    AccountViewController avc;

    @Before public void setup() throws Exception, IOException {
        FxToolkit.registerPrimaryStage();
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Views/AccountView.fxml"));
        this.avc = loader.getController();
        Scene login = new Scene(loader.load());
        FxToolkit.setupStage(stage -> {
            stage.setScene(login);
        });
        FxToolkit.showStage();
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.show();
    }

    @Test
    public void goback() {
        clickOn("#goBackButton");
        verifyThat(lookup("#main"), Node::isVisible);
    }


    @Test
    public void usernameLogin() {
        clickOn("#UserNameTextField").type(KeyCode.H, KeyCode.I);
        verifyThat(lookup("#UserNameTextField"), hasText("hi"));
    }
    @Test
    public void passwordLogin() {
        clickOn("#UserNamePasswordField").type(KeyCode.H, KeyCode.I);
        verifyThat(lookup("#UserNamePasswordField"), hasText("hi"));
    }

    @Test
    public void createUsername() {
        clickOn("#createAccountUsername").type(KeyCode.H, KeyCode.I);
        verifyThat(lookup("#createAccountUsername"), hasText("hi"));
    }
    @Test
    public void createFullName() {
        clickOn("#createAccountFullName").type(KeyCode.H, KeyCode.I);
        verifyThat(lookup("#createAccountFullName"), hasText("hi"));
    }

    @Test
    public void createPassword() {
        clickOn("#createAccountPassword").type(KeyCode.H, KeyCode.I);
        verifyThat(lookup("#createAccountPassword"), hasText("hi"));
    }

    @Test
    public void createPasswordHint() {
        clickOn("#createAccountPasswordHint").type(KeyCode.H, KeyCode.I);
        verifyThat(lookup("#createAccountPasswordHint"), hasText("hi"));
    }

    //testing the filechooser


    @Test
    public void fileChooser() {
        //further test for document?
        clickOn("#selectFileButton");
        type(KeyCode.DOWN);
        type(KeyCode.ENTER);
        verifyThat("#accountview", ParentMatchers.hasChild());
    }


    @Test
    public void failedLogin() {
        //GIVEN a failed login attempt on login screen
        clickOn("#UserNameTextField");

        //WHEN non-existing credentials are given
        type(KeyCode.A,KeyCode.DIGIT2);
        clickOn("#UserNamePasswordField");
        type(KeyCode.A,KeyCode.DIGIT2);
        clickOn("#LogInButton");

        //THEN spawn error message child
        verifyThat(lookup("#accountview"), ParentMatchers.hasChild());
    }



    @Test
    public void succesfulLogin() {
        //GIVEN a succesful login attempt on login screen
        clickOn("#UserNameTextField");

        //WHEN existing credentials are given
        type(KeyCode.A,KeyCode.S, KeyCode.D);
        clickOn("#UserNamePasswordField");
        type(KeyCode.A,KeyCode.S, KeyCode.D);
        clickOn("#LogInButton");

        //THEN move to main, with current user credentials
        verifyThat(lookup("#main"), Node::isVisible);
    }


    @Test
    public void makeAccount() {
        clickOn("#createAccountUsername");

        //WHEN non-existing credentials are given
        type(KeyCode.A,KeyCode.A);
        clickOn("#createAccountFullName");
        type(KeyCode.A,KeyCode.A);
        clickOn("#createAccountPassword");
        type(KeyCode.A,KeyCode.A);
        clickOn("#createAccountPasswordHint");
        type(KeyCode.A,KeyCode.A);
        clickOn("#buttonCreateAccount");


    }

    @Test
    public void makeAccount_sameNickName() {
        //not working, async calls break it
        makeAccount();
        makeAccount();
        verifyThat(lookup("#accountview"), ParentMatchers.hasChild());
    }


    @Test
    public void makeAccount_wrongdetails() {
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


/*
    @Test
    public void passUser() {
        User u = new User("username", "password", "passwordHint", "fullName", "image", false);
        System.out.println(avc.getUser());

        avc.setUser(u);
        System.out.println(avc.getUser());
        interact(() -> {
            try {
                avc.loginPress();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        verifyThat(lookup("#main"), Node::isVisible);

    }
*/
}