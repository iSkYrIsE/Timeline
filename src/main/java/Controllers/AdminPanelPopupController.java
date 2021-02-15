package Controllers;

import java.security.NoSuchAlgorithmException;

import Models.User;
import Utils.DatabaseController;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AdminPanelPopupController extends PopupController implements Initializable {

	@FXML
    private Button adminPanel_loadButton;

    @FXML
    private Label adminPanelLabel;

    @FXML
    private TextField adminPanelUsername;

    @FXML
    private TextField adminPanelFullName;

    @FXML
    private TextField adminPanelPassword;

    @FXML
    private TextField adminPanelPasswordHint;

    @FXML
    private Button buttonCreateAccount;

    @FXML
    private Button buttonDeleteAccount1;
    
    @FXML
    private CheckBox checkBox_isAdmin;

    @FXML
    private ListView<User> admin_listView;

    public void selectUser(User user) {
		adminPanelUsername.setText(user.getUsername());
		adminPanelFullName.setText(user.getFullname());
		adminPanelPassword.setText(user.getPassword());
		adminPanelPasswordHint.setText(user.getPasswordHint());
		checkBox_isAdmin.setSelected(user.isAdmin());
	}

	public void showEntries() {
		ObservableList<User> items = FXCollections.observableArrayList(DatabaseController.getAllUsers());
		
		admin_listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<User>() {
			@Override
			public void changed(ObservableValue<? extends User> observable, User oldValue, User newValue) {
				selectUser(newValue);
			}
		});

		/*admin_listView.setCellFactory(param -> new ListCell<User>() {
			@Override
			protected void updateItem(User item, boolean empty) {
				super.updateItem(item, empty);
				if (empty || item == null || item.getUsername() == null) {
					setText(null);
				} else {
					adminPanelUsername.setText(item.getUsername());
					adminPanelFullName.setText(item.getFullname());
					adminPanelPassword.setText(item.getPassword());
					adminPanelPasswordHint.setText(item.getPasswordHint());
					checkBox_isAdmin.setSelected(item.isAdmin());
				}
			}
		});*/
		admin_listView.setCellFactory(param -> new ListCell<User>() {
		    @Override
		    protected void updateItem(User item, boolean empty) {
		        super.updateItem(item, empty);
		        if (empty || item == null || item.getUsername() == null) {
		            setText(null);
		        } else {
		            setText(item.getUsername());
		        }
		    }
		});
		admin_listView.setItems(items);
	}
	
    public void deleteAccount() {
    	User banana = admin_listView.getSelectionModel().getSelectedItem();
    	banana.delete();
    }

    public void editAccount() throws NoSuchAlgorithmException{
    	User banana = admin_listView.getSelectionModel().getSelectedItem();
    	banana.editUser(adminPanelUsername.getText(), AccountViewController.MD5Hash(adminPanelPassword.getText()), adminPanelPasswordHint.getText(), adminPanelFullName.getText(),this.user.getImage() , isAdmin());
        banana.update();
    }
    
    public boolean isAdmin() {
    	if (checkBox_isAdmin.isSelected() == true)
    		return true;
    	else
    		return false;
    }
	
	@Override
	void enterPress() {
		//What to do on ENTER key press
	}

	@Override
	void escPress() {
		//What to do on ESCAPE key press
	}

	public CheckBox getCheckBox_isAdmin() {
		return checkBox_isAdmin;
	}

	public void setCheckBox_isAdmin(CheckBox checkBox_isAdmin) {
		this.checkBox_isAdmin = checkBox_isAdmin;
	}

	/**
	 * Called to initialize a controller after its root element has been
	 * completely processed.
	 *
	 * @param location  The location used to resolve relative paths for the root object, or
	 *                  {@code null} if the location is not known.
	 * @param resources The resources used to localize the root object, or {@code null} if
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		showEntries();
	}
}
