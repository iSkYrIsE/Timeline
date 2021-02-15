package Controllers;

import Models.Timeline;
import Models.User;
import Utils.GeneralUtils;
import Utils.GraphicUtils;
import Utils.ToastType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.Main;


import javax.swing.plaf.synth.SynthUI;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

public class TimelinePopupController extends PopupController implements Initializable {

   // User user = null;
    Timeline timeline = null;
    //Stage stage = null;

    @FXML
    private VBox createNewTimeline;

    @FXML
    private Label WindowTitle;

    @FXML
    private TextField timelineName;

    @FXML
    private TextArea timelineDescription;

    @FXML
    private Label timeUnitLabel;

    @FXML
    private ComboBox timeUnitBox;

    @FXML
    private Label timeFrameLabel;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private Button cancelButton;

    @FXML
    private Button createButton;

    @FXML
    private Button deleteButton;

    @FXML
    public void saveTimeline() {
        String error = errMsg();
        System.out.println("CREATE TIMELINE: "+error);
        if(!error.equals("")) {
            System.out.println("ERROR!!");
            showError(error);
        } else {
            System.out.println("no errors");
            try {
                String title = timelineName.getText();
                System.out.println("70");
                String description = timelineDescription.getText();
                System.out.println("72");
                User owner = getUser();
                System.out.println("74");
                ZoneId defaultZoneId = ZoneId.systemDefault();
                System.out.println("76");
                String timeUnit = timeUnitBox.getSelectionModel().getSelectedItem().toString();
                System.out.println("78");
                Date startDate = java.util.Date.from(startDatePicker.getValue().atStartOfDay(defaultZoneId).toInstant());
                System.out.println("80");
                Date endDate = java.util.Date.from(endDatePicker.getValue().atStartOfDay(defaultZoneId).toInstant());
                System.out.println("82");


                if (this.timeline == null) {
                    //Timeline newTimeline
                    timeline = new Timeline(title, description, timeUnit, owner, startDate, endDate);
                    //newTimeline.save();
                    timeline.save();
                } else {
                    this.timeline.editTimeline(title, description, timeUnit, owner, startDate, endDate);
                    timeline.update();
                }
                //timeline = null;
                parent.addTimelineToList(timeline);
                //closePopup();
                close(true);
            } catch (RuntimeException ex) {
                System.out.println(ex.getMessage());
                System.out.println(ex.toString());
                // GraphicUtils.makeToast(stage,ex.getMessage(), ToastType.WARNING);
            } catch (Exception e) {
                System.out.println("exception : " + e.getMessage());
            }
        }
    }



    public void editTimeline(Timeline timeline) {
        WindowTitle.setText("Edit Timeline");
        createButton.setText("Save");
        deleteButton.setVisible(true);

        this.timeline = timeline;

        timelineName.setText(timeline.getTitle());
        timelineDescription.setText(timeline.getDescription());
        timeUnitBox.getSelectionModel().select(timeline.getTimeUnit());

        if(timeline.getStartDate()!=null) {
            LocalDate startDate = timeline.getStartDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            startDatePicker.setValue(startDate);
        }


       /* Instant instant2 = timeline.getEndDate().toInstant();
        ZoneId defaultZoneId2 = ZoneId.systemDefault();
        localDate =  instant2.atZone(defaultZoneId2).toLocalDate();
        endDatePicker.setValue(localDate);*/

        if(timeline.getEndDate()!=null) {
            LocalDate endDate = timeline.getEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            endDatePicker.setValue(endDate);
        }


    }


    @FXML
    public void deleteTimeline() {
        parent.removeFromTimelineList(timeline);
        timeline.delete();
        //closePopup();
        close(true);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        //endDate cant be before start date now
        Callback<DatePicker, DateCell> callB = new Callback<>() {
            @Override
            public DateCell call(final DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        setDisable(empty || item.compareTo(startDatePicker.getValue()) < 0);
                    }
                };
            }
        };
        endDatePicker.setDayCellFactory(callB);
        /*timelineName.textProperty().addListener((ob,oV,nV)->setDirty(true));
        timelineDescription.textProperty().addListener((ob,oV,nV)->setDirty(true));
        timeUnitBox.getSelectionModel().selectedItemProperty().addListener((ob,oV,nV)->setDirty(true));
        startDatePicker.valueProperty().addListener((ob,oV,nV)->setDirty(true));
        endDatePicker.valueProperty().addListener((ob,oV,nV)->setDirty(true));*/
    }

    ///////////////////////////////// GETTERS SETTERS
    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }
    /////////////////////////////////

    ///////////////////////////////// UTILS

    public void closePopup() {
        close();
    }

    private String errMsg() {
        String msg = "";
        if (timelineName.getText().isEmpty()) {
            timelineName.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
            msg+="Timeline name";
        } else {
            timelineName.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
        }
        if(timeUnitBox.getSelectionModel().isEmpty()) {
            timeUnitBox.setStyle("-fx-border-color: #ff0000 ; -fx-border-width: 1px ;");
            if(!msg.equals("")) {
                msg+=", ";
            }
            msg+="time unit";
        } else {
            timeUnitBox.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
        }
        if(startDatePicker.getValue()==null) {
            startDatePicker.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
            if(!msg.equals("")) {
                msg+=", ";
            }
            msg+="start date";
        }else {
            startDatePicker.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
        }
        if(endDatePicker.getValue()==null) {
            endDatePicker.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
            if(!msg.equals("")) {
                msg+=", ";
            }
            msg+="end date";
        }else {
            endDatePicker.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
        }
        if(!msg.equals("")) {
            return msg.substring(0, 1).toUpperCase() + msg.substring(1)+" cannot be empty";
        } else {
            return "";
        }
    }
    public boolean validInput() {
      /* if(timelineNewName.getText().isEmpty())
           throw new RuntimeException("Timeline must has a name(happy now????)");
       if(timeUnit.getValue()==null)
           throw new RuntimeException("Time unit can not be empty");
       else if(startDate.getValue()==null)
           throw new RuntimeException("Start date can not be empty");
       else if(endDate.getValue().isBefore(startDate.getValue()))
           throw new RuntimeException("End date should be after start date ");*/
        return !(timeUnitBox.getSelectionModel().isEmpty()||
                startDatePicker.getValue()==null || endDatePicker.getValue()==null);
        //  else
        //    return true;
    }

    /////////////////////////////////

    @Override
    void enterPress() {
        //What to do on ENTER key press
        saveTimeline();
    }

    @Override
    void escPress() {
        //What to do on ESCAPE key press
        close();
    }
}
