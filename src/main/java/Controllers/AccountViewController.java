package Controllers;

import Models.User;
import Utils.FileController;
import Utils.GraphicUtils;
import Utils.ToastType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;


import static Utils.DatabaseController.usernameTaken;

public class AccountViewController extends PopupController implements Initializable {
    //private Parent parent;
    private User user;
    //private File selectedFile;
    private String selectedFilePath = "";
    @FXML
    private AnchorPane accountview;

    @FXML
    private StackPane LoginBox;

    @FXML
    private TextField UserNameTextField;

    @FXML
    private PasswordField UserNamePasswordField;

    @FXML
    private Button LogInButton;

    @FXML
    private Button RegisterButton;

    @FXML
    private Button goBackButton;

    @FXML
    private StackPane createAccountStackPane;

    @FXML
    private VBox createAccountVBox;

    @FXML
    private Label createAccountLabel;

    @FXML
    private TextField createAccountUsername;

    @FXML
    private TextField createAccountFullName;

    @FXML
    private PasswordField createAccountPassword;

    @FXML
    private TextField createAccountPasswordHint;

    @FXML
    private HBox createAccountHBox;

    @FXML
    private Button selectFileButton;

    @FXML
    private Label fileNotSelectedLabel;

    @FXML
    private Button buttonCreateAccount;

    @FXML
    private Circle imageCreateAccount;
    
    @FXML
    private ProgressBar createAccountPasswordStrengthBar;

    //@FXML
    public void showRegistrationForm() {
        createAccountStackPane.setVisible(true);
        LoginBox.setVisible(false);
        createAccountLabel.setText("Edit Account");
        buttonCreateAccount.setText("Save Account");
        createAccountUsername.setVisible(false);
        createAccountStackPane.setPrefWidth(1000);
    }

    @FXML
    public void goBack() {
        backToMain(user);
    }

    @FXML
    public void createAccount() {
        try {
            String username = createAccountUsername.getText();
            String fullName = createAccountFullName.getText();
            String pwd = createAccountPassword.getText();
            String pwdHint = createAccountPasswordHint.getText();

            boolean error = false;
            if(createAccountUsername.getText().isEmpty() || createAccountFullName.getText().isEmpty() || createAccountPassword.getText().isEmpty()) {
                error = true;
                GraphicUtils.makeToast(Main.getPrimaryStage(), "Fill out all fields", ToastType.ERROR);
            }

            //check if username is taken (only when we are creating an account)
            if(error == false && this.user == null && usernameTaken(username)) {
                error = true;
                GraphicUtils.makeToast(Main.getPrimaryStage(), "The username is already taken", ToastType.ERROR);
            }

            if(error == false && passwordStrengthChecker(createAccountPassword.getText()).equals("weak")) {
                error = true;
                GraphicUtils.makeToast(Main.getPrimaryStage(), "The password is too weak", ToastType.ERROR);
            }

            if (this.user == null && error==false) {
                this.user = new User(username, MD5Hash(pwd), pwdHint, fullName, selectedFilePath, true);
                this.user.save();
                backToMain(this.user);
            } else if (this.user != null && error==false) {
                this.user.editUser(username, MD5Hash(pwd), pwdHint, fullName, selectedFilePath, false);
                this.user.update();
                backToMain(this.user);
            }
        }catch (Exception e) {
            System.err.println("Problem creating account");
            System.err.println(e.getMessage());
        }
    }

    @Override
    void checkPasswordStrength(String password){
        passwordStrengthChecker(password);
    }

    public String passwordStrengthChecker(String password) {
        int uppercase = 0;
        int lowercase = 0;
        int specialcharacters = 0;
        int digits = 0;
        String strength = "ok";
        for (int i = 0; i < password.length(); i++) {
            if (Character.isUpperCase(password.charAt(i)))
                uppercase++;
            else if (Character.isLowerCase(password.charAt(i)))
                lowercase++;
            else if (Character.isDigit(password.charAt(i)))
                digits++;
            else if(password.charAt(i)=='.' || password.charAt(i)=='#' || password.charAt(i)=='!' ||
            		password.charAt(i)=='*' || password.charAt(i)=='@' || password.charAt(i)=='%' ||
            		password.charAt(i)=='^' || password.charAt(i)=='&') {
            	specialcharacters++;
            }
        }
        if (password.length() >= 5 && password.length()<8 && lowercase >=1) {
        	createAccountPasswordStrengthBar.setProgress(0.5);
			createAccountPasswordStrengthBar.setStyle("-fx-accent: yellow;");
            strength="medium";
        }

		if (password.length() >= 8 && uppercase >= 1 && lowercase >= 1 && digits >= 1 && specialcharacters >= 1) {
        	createAccountPasswordStrengthBar.setProgress(0.9);
	    	createAccountPasswordStrengthBar.setStyle("-fx-accent: green;");
            strength="ok";
		}
        
		if(password.length() <= 5) {
			createAccountPasswordStrengthBar.setProgress(0.2);
    		createAccountPasswordStrengthBar.setStyle("-fx-accent: #ff0000;");
            strength="weak";
		}
		return strength;
    }
    
    public static String MD5Hash(String password) throws NoSuchAlgorithmException {
    	MessageDigest md = MessageDigest.getInstance("MD5");
    	byte[] messageDigest = md.digest(password.getBytes());
    	BigInteger no = new BigInteger(1, messageDigest);
    	String hash = no.toString(16);
        while (hash.length() < 32) {
            hash = "0" + hash;
        }
        return hash;
    }
   
    //@FXML
    public void setUser(User user) {
    	//showRegistrationForm();
    	this.user = user;
    	createAccountUsername.setText(user.getUsername());
        createAccountFullName.setText(user.getFullname());
        //We should leave the password empty.. if not we will hash it again when saved
        //createAccountPassword.setText(user.getPassword());
        //passwordStrengthChecker(createAccountPassword.getText());
        createAccountPasswordHint.setText(user.getPasswordHint());
        if(user.getImage()!=null && user.getImage().equals("") == false) {
            selectedFilePath = user.getImage();
            setUserImage(selectedFilePath);
            fileNotSelectedLabel.setText(FileController.extractFileName(user.getImage()));
        }
        
        //this.user.update();
        //backToMain(user);
    }

    @FXML
    public void loginPress() {
        try {
            String userName = UserNameTextField.getText();

            if (userName.length() == 0 || UserNamePasswordField.getText().length() == 0) {
                GraphicUtils.makeToast(Main.getPrimaryStage(), "Please enter the username or password", ToastType.ERROR);
            } else {
                String password = MD5Hash(UserNamePasswordField.getText());
                User user = User.login(userName, password);
                if (user == null) {
                    String pwdHint=User.getPasswordHint(userName);
                    if(pwdHint==null){
                        GraphicUtils.makeToast(Main.getPrimaryStage(),"The user was not found ", ToastType.ERROR);

                    }else{
                        if(!pwdHint.isEmpty()){
                            pwdHint="\nHint: "+pwdHint;
                        }
                        GraphicUtils.makeToast(Main.getPrimaryStage(),"The password is wrong" +pwdHint, ToastType.ERROR);
                    }
                } else {
                    backToMain(user);
                }
            }
        } catch (Exception e) {
            System.err.println("LOGIN ERROR");
            System.err.println(e.getMessage());
        }
    }


    //TODO: never throw FileNotFoundException to the GUI process...
    //you should always catch them so it doesn't crash the program ;)
    @FXML
    public void fileChooser() {
        String imageUrl = FileController.imageChooser();
        if(imageUrl!=null) {
            selectedFilePath = imageUrl;
            //imageViewCreateAccount.setImage(image);
            setUserImage(imageUrl);
            fileNotSelectedLabel.setText(FileController.extractFileName(imageUrl));
        }
/*
        try {
        this.selectedFile = FileController.fileChooser();
        
        if (selectedFile != null) {
            try {
                String imageUrl = selectedFile.toURI().toURL().toExternalForm();
                Image image = new Image(imageUrl);
                imageViewCreateAccount.setImage(image);
            } catch (MalformedURLException ex) {
                throw new IllegalStateException(ex);
            }
            fileNotSelectedLabel.setText(selectedFile.getName());
        }
        }catch (Exception e) {
            System.err.println("Error selecting file");
            System.err.println(e.getMessage());
        }*/
    }
    ///////////////////////////////// GETTERS SETTERS

    void setUserImage(String imagePath) {
        Image image;
        if(imagePath.startsWith("file")) {
            image = new Image(imagePath);
        } else {
            File userImageFile = new File(imagePath);
            image = new Image(userImageFile.toURI().toString());
        }
        GraphicUtils.setAvatarImage(image, imageCreateAccount);
    }

    /////////////////////////////////

    ///////////////////////////////// UTILS
    void backToMain(User user) {
        //TODO: never throw IOException to the GUI process...
        //you should always catch them so it doesn't crash the program ;)
        try {
            FXMLLoader f = new FXMLLoader(getClass().getResource("/Views/Main.fxml"));
            Parent mainScreen = f.load();
            MainController mc = f.getController();
            Scene mainScene = new Scene(mainScreen);
            mc.setCurrentUser(user);
            mc.checkLoggedUser();
            Main.getPrimaryStage().setScene(mainScene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    void enterPress() {
        //What to do on ENTER key press
        //distinguish which form the user is using
        if(createAccountFullName.isFocused() || selectFileButton.isFocused() || createAccountUsername.isFocused() || createAccountPassword.isFocused() || createAccountPasswordHint.isFocused() || buttonCreateAccount.isFocused()) {
            createAccount();
        } else {
            loginPress();
        }
    }

    @Override
    void escPress() {
        //What to do on ESCAPE key press
        backToMain(user);
    }

    /////////////////////////////////
}
