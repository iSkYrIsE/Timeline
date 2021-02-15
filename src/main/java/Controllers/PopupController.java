package Controllers;

import Models.User;
import Utils.GraphicUtils;
import Utils.ToastType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import main.Main;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public abstract class PopupController {
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    //THIS IS A SUPER CLASS FOR THE FORMS
    //responsible to create a focus listener (when then this window is out of focus)
    //see if the content is dirty or not
    //trigger confirmation window
    Stage stage = null;
    boolean dirty = false;
    boolean confirmationPopupActive = false;
    //To not show the confirmation popup or closing the popup when selecting files we lockFocus
    boolean lockFocus = false;
    boolean focused = true;
    //Most of our forms needs the current login user
    User user = null;
    //Parent root; // this is to automatically setup listners in TextFields and others

    String confirmationTitle = "TITLE NOT DEFINED", confirmationText = "Text not defined. Use: setConfirmationTitle(String title) and setConfirmationText(String text)";

    //IN ORDER TO CALL METHODS ON THE MAIN CONTROLLER WE NEED A REFERENCE TO IT!
    MainController parent;

    //if forceClose is true, we don't check if the form has unsaved data
    public void close(boolean forceClose) {
        //setLockFocus(true);
        System.out.println("closing: "+this.getClass());
        if(forceClose) {
            stage.close();
        } else {
            // if is dirty, let's ask the user first
            if(dirty) {
                showConfirmation();
            } else {
                stage.close();
            }
        }
    }

    public void close() {
        close(false);
    }

    private void setFocused(boolean focused) {
        this.focused = focused;
    }

    //!!!THIS METHOD SHOULD BE CALLED AFTER SETTING THE OBJECT BEING EDITED!!!
    public void setUp(Stage stage, Parent root) {
        this.stage=stage;

        //create listeners for dirtiness
        //TODO: make it compatible with the other input forms
        if(root!=null) {
            ArrayList<Node> inputs = getFormInputs((Pane) root, new ArrayList<Node>());
            for (Node input : inputs) {
                if (input instanceof PasswordField) {
                    ((PasswordField) input).textProperty().addListener((obs, oldText, newText) -> {
                        setDirty(true);
                        checkPasswordStrength(newText);
                    });
                } else if (input instanceof TextInputControl) {
                    ((TextInputControl) input).textProperty().addListener((obs, oldText, newText) -> {
                        setDirty(true);
                    });
                } else if (input instanceof CheckBox) {
                    ((CheckBox) input).selectedProperty().addListener((obs, oldText, newText) -> {
                        setDirty(true);
                    });
                } else if (input instanceof DatePicker) {
                    ((DatePicker) input).valueProperty().addListener((obs, oldText, newText) -> {
                        setDirty(true);
                    });
                } else if (input instanceof Spinner) {
                    ((Spinner) input).valueProperty().addListener((obs, oldText, newText) -> {
                        setDirty(true);
                    });
                } else if (input instanceof ComboBox) {
                    ((ComboBox) input).getSelectionModel().selectedItemProperty().addListener((obs, oldText, newText) -> {
                        setDirty(true);
                    });
                }
            }
        }

        if(stage!=null) {
            //stage.requestFocus();
            //stage.setAlwaysOnTop(true);
            stage.focusedProperty().addListener(new ChangeListener<Boolean>() {
                @Override
                public void changed(ObservableValue<? extends Boolean> observable, Boolean unfocused, Boolean focused) {
                    setFocused(focused);
                    if (focused == false && lockFocus == false) {
                        close();
                    }
                }
            });

            stage.getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    if(focused) { //only trigger this if we are focused
                        switch (event.getCode()) {
                            case ENTER:
								enterPress();
                                break;
                            case ESCAPE:
                                escPress();
                                break;
                        }
                    }
                }
            });
        }
    }

    void checkPasswordStrength(String password) {
        //Override in the Account view controller
    }

    abstract void enterPress();
    abstract void escPress();

    private void showConfirmation() {
        if(confirmationPopupActive == false) {
            confirmationPopupActive = true;
            GraphicUtils.makeConfirmation(stage,confirmationTitle,confirmationText, this);
        }
    }

    void showError(String errorMessage) {
        setLockFocus(true);
        GraphicUtils.makeToast(stage, errorMessage, ToastType.ERROR);
        setLockFocus(false);
    }

    void showSuccess(String successMessage) {
        setLockFocus(true);
        GraphicUtils.makeToast(stage, successMessage, ToastType.SUCCESS);
        setLockFocus(false);
    }

    void showWarning(String warningMessage) {
        setLockFocus(true);
        GraphicUtils.makeToast(stage, warningMessage, ToastType.SUCCESS);
        setLockFocus(false);
    }

    ///////////////////////////////// GETTERS SETTERS
    public void setParentController(MainController parent) {
        this.parent = parent;
    }

    protected void setDirty(boolean value) {
        this.dirty = value;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setConfirmationPopupActive(boolean active) {
        this.confirmationPopupActive = active;
    }

    public void setConfirmationTitle(String title) {
        this.confirmationTitle = title;
    }

    public void setConfirmationText(String text) {
        this.confirmationText = text;
    }

    public void setLockFocus(boolean lockFocus) {
        this.lockFocus = lockFocus;
    }
    /////////////////////////////////


    ///UTILS


    public void resetMain() {
        //ARE YOU SURE YOU WANT TO DO IT LIKE THIS? WITHOUT A PROPER PROGRAME STATE????
        try {
            //you should always catch them so it doesn't crash the program ;)
            FXMLLoader f = new FXMLLoader(getClass().getResource("/Views/Main.fxml"));
            Parent mainScreen = f.load();
            //MainController mc = f.getController();
            Scene mainScene = new Scene(mainScreen);
            Main.getPrimaryStage().setScene(mainScene);
        }catch (Exception e) {
            System.err.println("error reseting main");
            System.err.println(e);
        }

    }

    //TODO: make it compatible with the other input forms
    private static <T extends Pane> ArrayList<Node> getFormInputs(T parent, ArrayList<Node> formInputs) {
        for (Node node : parent.getChildren()) {
            // Nodes - You can add more.
            if (node instanceof TextField) {
                formInputs.add(node);
            }else if (node instanceof PasswordField) {
                formInputs.add(node);
            }
            else if (node instanceof TextArea) {
                formInputs.add(node);
            }
            else if (node instanceof CheckBox) {
                formInputs.add(node);
            }
            else if (node instanceof DatePicker) {
                formInputs.add(node);
            }
            else if (node instanceof Spinner) {
                formInputs.add(node);
            }
            else if (node instanceof ComboBox) {
                formInputs.add(node);
            }
            // Recursive.
            if (node instanceof Pane) {
                getFormInputs((Pane) node, formInputs);
            }
        }
        return formInputs;
    }
}
