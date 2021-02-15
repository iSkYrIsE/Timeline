package Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.text.Text;
import javafx.stage.Window;

public class ConfirmationMessageController {

    private Window ownerWindow;

    //TO MODIFY THE ACTIVE STATE OF THE CONFIRMATION inside the popup
    //this avoid the confirmation popup being show multiple times
    PopupController pc;
    public void setPC(PopupController pc) {
        this.pc = pc;
    }
    //////////////

    @FXML
    private Text ConfirmationTitle;

    @FXML
    private Text OptionalId;

    @FXML
    private Button ButtonNo;

    @FXML
    private Button ButtonYes;

    @FXML
    protected void close() {
        //you hide the confirmation popup
        ButtonNo.getScene().getWindow().hide();
        //you hide the popup with the form
        ownerWindow.getScene().getWindow().hide();
    }


    @FXML
    protected void doNotClose() {
        //you hide the confirmation popup
        ButtonYes.getScene().getWindow().hide();
        if(pc!=null) {
            //we tell the popupcontrolelr the confirmation popup is no longer active
            pc.setConfirmationPopupActive(false);
            ownerWindow.requestFocus();
        }
    }

    public void setText(String title, String desc) {
        //set Messages
        ConfirmationTitle.setText(title);
        OptionalId.setText(desc);
    }
    public Window getParent() {
        return ownerWindow;
    }

    public void setParent(Window parent) {
        this.ownerWindow = parent;
    }

    public Text getConfirmationTitle() {
        return ConfirmationTitle;
    }

    public void setConfirmationTitle(Text confirmationTitle) {
        ConfirmationTitle = confirmationTitle;
    }

    public Text getOptionalId() {
        return OptionalId;
    }

    public void setOptionalId(Text optionalId) {
        OptionalId = optionalId;
    }
}
