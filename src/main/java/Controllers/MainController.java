package Controllers;

import Models.Event;
import Models.Timeline;
import Models.User;



import Utils.GeneralUtils;
import Utils.GraphicUtils;

import Utils.ToastType;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextFlow;

import javafx.stage.*;
import main.Main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable  {

    //constants
    public static final double WINDOW_WIDTH = 1000.0;
    public static final double WINDOW_HEIGHT = 650.0;

    private User currentUser = null;
    private Timeline selectedTimeline = null;
    //private List<Timeline> timelineList;
    ObservableList<Timeline> timelineList = FXCollections.observableArrayList();;
    boolean editMode = false;
    boolean dropDownVisible = false;

    @FXML
    private StackPane main;

    @FXML
    private Button loginButton;

    @FXML
    private Button add_timeline_btn;
    
    /*@FXML
    private StackPane adminPanel_toggle;
    
    @FXML
    private Button admin_panel_button;*/
    
    @FXML
    private StackPane AvatarBox;

    @FXML
    private Label AvatarLetter;

    @FXML
    private ListView<Timeline> timelines_list;

    @FXML
    private VBox DropDownUserMenu;

    @FXML
    private Button EditAccountButton;

    @FXML
    private Button LogOutButton;

    @FXML
    private AnchorPane timeline_page_box;

    @FXML
    private Label timeline_name;

    @FXML
    private Button edit_timeline_button;

    @FXML
    private Button add_event_button;

    @FXML
    private ScrollPane timeline_scroll_pane;

    @FXML
    private TextFlow timeline_description;

    @FXML
    private Label timeline_author;

    @FXML
    private StackPane view_edit_toggle;

    @FXML
    private Pane switch_head;

    @FXML
    private Label switch_edit_text;

    @FXML
    private Label switch_view_text;

    @FXML
    private Region opaqueRegion;

    @FXML
    private StackPane timelineVisualizer;

    @FXML
    private TimelineController timelineVisualizerController;

    @FXML
    private Circle UserImageCircle;

    @FXML
    private Label timeLabelRight;

    @FXML
    private Label timeLabelLeft;

    @FXML
    private Button AdminPanelButton;

    @FXML
    public void showTimelistDebug() {
        System.out.println(timelines_list.getSelectionModel().getSelectedItem());
       //TimelineVisualizerController.generateTimeline(timelines_list.getSelectionModel().getSelectedItem());
    }

    @FXML
    protected void addTimeline() {
        showPopupForm(FormType.TIMELINE, null);
    }

    @FXML
    protected void toggleViewEditMode() {
        if (currentUser == null) {
            GraphicUtils.makeToast(Main.getPrimaryStage(), "Please log in first", ToastType.INFO);
            showLogin();
        } else {
           toggleEditButton();
        }
    }

    @FXML
    protected void showLogin() {
        try {
            FXMLLoader f = new FXMLLoader(getClass().getResource("/Views/AccountView.fxml"));
            Stage loginFormStage = new Stage();
            Parent formView = f.load();
            Scene loginScene = new Scene(formView);
            loginFormStage.setScene(loginScene);
            loginFormStage.initOwner(Main.getPrimaryStage());
            PopupController pc = f.getController(); //PopupController is a super class common to all forms
            pc.setUp(loginFormStage,formView); //ad key press listener
            Main.getPrimaryStage().setScene(loginScene);
        }catch (Exception e) {
            System.err.println("Problem creating login page");
            System.err.println(e);
        }
    }

    @FXML
    protected void showDropDownMenu() {
        dropDownVisible = !dropDownVisible;
        DropDownUserMenu.setVisible(dropDownVisible);
    }

    @FXML
    protected void editTimeline() {
        showPopupForm(FormType.TIMELINE, timelines_list.getSelectionModel().getSelectedItem());
    }
   
    @FXML
    protected void showAdminPanel() throws IOException {
    	if(currentUser == null)
    		GraphicUtils.makeToast(Main.getPrimaryStage(), "Log in first", ToastType.ERROR);
        else if (!getCurrentUser().isAdmin())
            GraphicUtils.makeToast(Main.getPrimaryStage(), "You must be an admin", ToastType.ERROR);
    	else
    		showPopupForm(FormType.ADMINPANEL, null);
    }

    @FXML
    void createEvent() {
        showPopupForm(FormType.EVENT, null);
    }

    @FXML
    protected void DropDownEditAccount() {
        try {
            FXMLLoader f = new FXMLLoader((getClass().getResource("/Views/AccountView.fxml")));
            Parent login = f.load();
            AccountViewController acv = f.getController();
            Scene loginScene = new Scene(login);
            acv.setUser(getCurrentUser());
            acv.showRegistrationForm();
            acv.setUp(null,login); //create listeners
            Main.getPrimaryStage().setScene(loginScene);
        }catch (Exception e) {
            System.err.println("Problem creating edit account view");
            System.err.println(e);
        }
    }

    @FXML
    protected void DropDownLogout() {
        setCurrentUser(null);
        checkLoggedUser();
        showDropDownMenu();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        populateTimelist(true);
        //logged users cant really function, we pass it right after load :(
        checkLoggedUser();
        //create listner to check selection changes in the timeline list
        timelines_list.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Timeline> ov, Timeline old_val, Timeline new_val) -> {
            Timeline selectedItem = timelines_list.getSelectionModel().getSelectedItem();
            openTimeline(selectedItem);
        });
        //hide timeline page on start
        timeline_page_box.setVisible(false);
        //hide add button on start
        add_timeline_btn.setVisible(false);
        //create scroll listener
        timeline_scroll_pane.hvalueProperty().addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            timelineVisualizerController.timelineScroll(newValue.doubleValue());
        });

        //pass width of the viewport to the controller
        //update viewport width onDemand
        timeline_scroll_pane.viewportBoundsProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("VIEWPORT WIDTH: "+newVal.getWidth());
            timelineVisualizerController.setViewportWidth(newVal.getWidth());
        });

        edit_timeline_button.setVisible(false);
        add_event_button.setVisible(false);
    }

    ///////////////////////////////// GETTERS SETTERS
    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }
    /////////////////////////////////

    ///////////////////////////////// UTILS
    public void openTimeline(Timeline timeline) {
        System.out.println("Open timeline");
        timeline_scroll_pane.setHvalue(0.0);
        timeline_author.setText("by "+ GeneralUtils.getFirstLastName(timeline.getCreatedBy().getFullname()));
        timeline_description.getChildren().clear();
        Text timelineDescription = new Text(timeline.getDescription());
        timelineDescription.setFill(Color.web("#707070"));
        timeline_description.getChildren().add(timelineDescription);
        timeline_name.setText(timeline.getTitle());
        timeline_page_box.setVisible(true);
        //Open timeline inside the controller
        timelineVisualizerController.setParentController(this);
        timelineVisualizerController.setTimeline(timeline); //generateTimeline(timeline);
        selectedTimeline = timeline;
    }

    //NEW METHOD TO CREATE A FORM POPUP, if it's to create a new object "objectToEdit" should be null
    void showPopupForm(FormType formType, Object objectToEdit) {
        //Check if the user is logged in first
        if (getCurrentUser() == null) {
            GraphicUtils.makeToast(Main.getPrimaryStage(), "You must be logged in", ToastType.ERROR);
            showLogin();
        }
        else {
            System.out.println("Creating popup");
            try {
                String fxmlResourcePath = "";
                if (formType == FormType.ACCOUNT) {
                    fxmlResourcePath = "/Views/AccountView.fxml";
                } else if (formType == FormType.EVENT) {
                    fxmlResourcePath = "/Views/EventCreationPopup.fxml";
                } else if (formType == FormType.TIMELINE) {
                    fxmlResourcePath = "/Views/TimelinePopup.fxml";
                } else if(formType == FormType.ADMINPANEL) {
                	fxmlResourcePath = "/Views/AdminPanelPopup.fxml";
                }
                FXMLLoader f = new FXMLLoader(getClass().getResource(fxmlResourcePath));
                Stage popup = new Stage();
                Parent formView = f.load();
                popup.setScene(new Scene(formView));
                popup.initOwner(Main.getPrimaryStage());
                popup.initModality(Modality.NONE);
                popup.initStyle(StageStyle.TRANSPARENT);

                PopupController pc = f.getController(); //PopupController is a super class common to all forms
                pc.setUser(getCurrentUser());
                pc.setParentController(this); //so we can call methods from the main without erasing it's state;
                //Set up confirmation messages
                pc.setConfirmationTitle("Are you sure you want to leave?");
                if (formType == FormType.ACCOUNT) {
                    pc.setConfirmationText("The information not saved in your account will be deleted.");
                } else if (formType == FormType.EVENT) {
                    pc.setConfirmationText("The information not saved in the event will be deleted.");
                } else if (formType == FormType.TIMELINE) {
                    pc.setConfirmationText("The information not saved in the timeline will be deleted.");
                } else if (formType == FormType.ADMINPANEL) {
                	pc.setConfirmationText("The information not saved in the admin panel will be deleted.");
                }

                //add object being edited before setUp
                if (objectToEdit != null) {
                    if (pc instanceof AccountViewController) {
                        ((AccountViewController) pc).setUser(getCurrentUser());
                    } else if (pc instanceof EventCreationPopupController && objectToEdit instanceof Event) {
                        ((EventCreationPopupController) pc).editEvent((Event) objectToEdit);
                    } else if (pc instanceof TimelinePopupController && objectToEdit instanceof Timeline) {
                        ((TimelinePopupController) pc).editTimeline((Timeline) objectToEdit);
                    }
                }
                if(formType==FormType.EVENT && selectedTimeline!=null) {
                    ((EventCreationPopupController) pc).setTimeline(selectedTimeline);
                }
                //setUp method create the needed listeners for the form to work: unfocus, setDirtiness on change... etc
                pc.setUp(popup, formView);

                //cool beans opaque function for added style bonus points
                turnOpaque();

                //wait for stuff to happen
                popup.showAndWait();

                //for the case it is canceled
                turnNotOpaque();

            } catch (Exception e) {
                System.err.println("Exception while creating popup form");
                System.err.println(e);
            }
        }
    }

    enum FormType {
        TIMELINE,
        EVENT,
        ACCOUNT,
        ADMINPANEL
    }

    void checkLoggedUser() {
        if (currentUser!=null) {
            //timeline_page_box.setVisible(true);
            AvatarBox.setVisible(true);
            loginButton.setVisible(false);
            if (currentUser.getImage()!=null && currentUser.getImage().equals("") == false){
                AvatarLetter.setVisible(false);
                Image userImage;
                if(currentUser.getImage().startsWith("file")) {
                    userImage = new Image(currentUser.getImage());
                } else {
                    File userImageFile = new File(currentUser.getImage());
                    userImage = new Image(userImageFile.toURI().toString());
                }
                GraphicUtils.setAvatarImage(userImage, UserImageCircle);
            }else {
                AvatarLetter.setText(String.valueOf(currentUser.getFullname().charAt(0)).toUpperCase());
                UserImageCircle.setVisible(false);
            }
            //check if it is admin
            if(currentUser.isAdmin()==false) {
                //remove admin button if it's not admin
                AdminPanelButton.setVisible(false);
                AdminPanelButton.setManaged(false);
            } else {
                AdminPanelButton.setVisible(true);
                AdminPanelButton.setManaged(true);
            }

        }else {
            timeline_page_box.setVisible(false);
            AvatarBox.setVisible(false);
            loginButton.setVisible(true);
            //turn off edit mode
            if(editMode) {
                toggleEditButton();
            }
        }
    }

    //// TIMELINE LIST RELATED METHODS
    public void populateTimelist(boolean coldStart) {
        //timelineList = FXCollections.observableArrayList(Timeline.loadAll());
        //ObservableList<Timeline> items = FXCollections.observableArrayList(timelineList);
        //.stream().collect(Collectors.toList())
        if(coldStart) {
            GraphicUtils.generateTimeList(timelines_list);
            timelineList.addAll(Timeline.loadAll());
            timelines_list.setItems(timelineList); //.setItems(items);

        } else {
            //timelines_list.refresh();
        }
    }
    //to be called by the create/edit timeline
    public void addTimelineToList(Timeline timeline) {
        boolean found = false;
        if(!timelineList.contains(timeline)) {
            timelineList.add(timeline);
        } else {
            //TODO: Improve this (force update)
            //Create a proper observable that notify changes inside the object
            timelineList.set(timelineList.indexOf(timeline),timeline);
        }
        //open timeline after being edited or created
        openTimeline(timeline);
    }

    public void removeFromTimelineList(Timeline timeline) {
        timelineList.remove(timeline);
    }
    //END TIMELINE LIST RELATED METHODS

    void toggleEditButton() {
        editMode = !editMode;
        if (editMode) {
            edit_timeline_button.setVisible(true);
            add_event_button.setVisible(true);
            add_timeline_btn.setVisible(true);
            view_edit_toggle.setMargin(switch_head, new Insets(0, 46, 0, 0));
            switch_edit_text.setStyle("-fx-text-fill: #FFFFFF");
            switch_view_text.setStyle("-fx-text-fill: #888888");
        } else {
            add_timeline_btn.setVisible(false);
            edit_timeline_button.setVisible(false);
            add_event_button.setVisible(false);
            view_edit_toggle.setMargin(switch_head, new Insets(0, 2, 0, 0));
            switch_edit_text.setStyle("-fx-text-fill: #888888");
            switch_view_text.setStyle("-fx-text-fill: #FFFFFF");
        }
        timelineVisualizerController.setEditMode(editMode);
    }

    public void turnNotOpaque() {
        opaqueRegion.setVisible(false);
    }

    public void turnOpaque() {
        opaqueRegion.setStyle("-fx-background-color: #00000044;");
        opaqueRegion.setVisible(true);
        //TRY TO CORRECT BUG THAT AFTER CLICKING ONCE OUTSIDE THE POPUP FORM
        //IT DOESNT SHOW THE CONFIRMATION POPUP AGAIN
        opaqueRegion.setOnMouseClicked((new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                //System.out.println("Main click (and focus request)");
                Main.getPrimaryStage().requestFocus();
            }
        }));
    }

    public void setTimeLabelRight(String text) {
        timeLabelRight.setText(text);
    }

    public void setTimeLabelLeft(String text) {
        timeLabelLeft.setText(text);
    }
}
