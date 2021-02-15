package Controllers;

import Models.Event;
import Models.Timeline;
import Utils.FileController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.converter.LocalTimeStringConverter;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.spec.ECField;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.chrono.ChronoLocalDate;
import java.time.chrono.Chronology;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class EventCreationPopupController extends PopupController implements Initializable {

    Timeline timeline;
    Event event = new Event();
    boolean editEvent = false;

    private File imageFile;
    private SpinnerValueFactory<LocalTime> startValue;
    private SpinnerValueFactory<LocalTime> endValue;

    @FXML
    private ImageView eventImage;

    @FXML
    private Button selectImageButton;

    @FXML
    private TextField eventName;

    @FXML
    private DatePicker startingDate;

    @FXML
    private DatePicker endingDate;

    @FXML
    private Spinner startTimeSpinner;

    @FXML
    private Spinner endTimeSpinner;

    @FXML
    private TextArea eventDesc;

    @FXML
    private Button deletButton;

    @FXML
    private Button createButton;

    @FXML
    private Button cancelButton;

    @FXML
    public void saveEvent() {
        if (!editEvent) {
            event.setTimeline(this.timeline);
            event.setCreatedBy(getUser());
        }
        String error = errMsg();
        System.out.println("CREATE TIMELINE: " + error);
        if (!error.equals("")) {
            System.out.println("ERROR!!");
            showError(error);
        } else {
            System.out.println("no errors");
            try {
                event.setName(eventName.getText());
                System.out.println("65");
                event.setDescription(eventDesc.getText());
                System.out.println("67");
                event.setTimeline(timeline);
                System.out.println("69");
                event.setStartDate(startValue.getValue().atDate(startingDate.getValue()));
                System.out.println("71");
                //only if chosen by user
                if (endingDate.getValue() != null) {
                    System.out.println("hi");
                    event.setEndDate(endingDate.getValue().atStartOfDay());
                }
                System.out.println("73");
                if (imageFile != null) {
                    System.out.println("image");
                    event.setImage(imageFile.getName());
                }
                System.out.println("77");
                event.setStart(startValue.getValue());
                //only if chosen by user
                if (endValue.getValue() != null)
                    event.setEnd(endValue.getValue());
                //what if the user create an event doesnt happen at current time???
                if(editEvent) {
                    event.update();
                } else {
                    event.save();
                }
                System.out.println("80");
                System.out.println("82");
                parent.openTimeline(timeline);//Reload timeline to show event
                super.close(true);
                System.out.println("84");
            } catch (Exception e) {
                //e.printStackTrace();
                errMsg();
            }
        }
    }

    //a button should be made in  Event.fxml (edit button) and set action on (e-> this.eventController.editEvent);

    public void editEvent(Event event) {
        editEvent = true;
        this.event = event;
        try {
    createButton.setText("Save");
    deletButton.setVisible(true);
    eventName.setText(event.getName());
    if (!event.getDescription().isEmpty())
        eventDesc.setText(event.getDescription());
    if (event.getStartDate() != null)
        startingDate.setValue(event.getStartDate().toLocalDate());
    if (event.getEndDate() != null)
        endingDate.setValue(event.getEndDate().toLocalDate());
    // if(event.getImage()!=null)
    //   eventImage.setImage(new Image(event.getImage()));
    //  startTimeSpinner.setValueFactory(event.getStart().getvalueFactory());
    //endTimeSpinner.setValueFactory(event.getEndTime());

       /* createButton.setOnAction(e -> {
            event.setName(eventName.getText());
            event.setDescription(eventDesc.getText());
            event.setStartDate(startingDate.getValue().atStartOfDay());
            event.setEndDate(endingDate.getValue().atStartOfDay());
            event.setImage(imageFile.getName());

            event.update();
            closePopup();
        });*/

      /*  cancelButton.setOnAction(e -> {
            closePopup();
        });*/

        }catch(NullPointerException e){
    System.out.println(e.getMessage());

        }
    }

  //  @FXML
  /* public void setEvent(Event e) {
        this.event = e;
        // gotta fix this shit first this.timeline = e.
    }*/

    @FXML
    public void fileChooser() {
        setLockFocus(true); //so we don't trigger the onFocus "event"
        String imageUrl = FileController.imageChooser();
        if(imageUrl!=null) {
            Image image = new Image(imageUrl);
            eventImage.setImage(image);
            event.setImage(imageUrl);
            setDirty(true);
        }
        setLockFocus(false); //we restore the previous state of the LockFocus
        /*try {
            try {
                File selectedFile = FileController.fileChooser();
                if (selectedFile != null) {
                    String imageUrl = selectedFile.toURI().toString();
                    Image image = new Image(imageUrl);
                    if (editEvent && event.getImage().equals(imageUrl) == false && imageUrl.length() > 0) {
                        //if the image is not the same as the old image we mark the form as dirty
                        setDirty(true);
                        event.setImage(imageUrl);
                        eventImage.setImage(image);
                    } else if (!editEvent && imageUrl.length() > 0) {
                        //if we add an image to a new event, we also make it dirty
                        setDirty(true);
                        event.setImage(imageUrl);
                        eventImage.setImage(image);
                    }
                }
            }catch (Exception e) {
                System.err.println("Error selecting file");
                System.err.println(e.getMessage());
            }


            /*FileChooser filePicChooser = new FileChooser();
            filePicChooser.setTitle("Select  Image");
            filePicChooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter("PNG files", "*.png")
                    , new FileChooser.ExtensionFilter("JPG files", "*.jpg")
            );
            imageFile = filePicChooser.showOpenDialog(new Stage());
            //this.imageFile = selectedFile.getName();
            //we need to check in the image selected in null or not
            //when the user don't select anything (when he presses cancel) the returned value is null! ;)
            if (imageFile != null) {
                //we need the path to see if this image is the same as the old image in the event (when editing an event)
                String newImagePath = imageFile.toURI().toString();
                //We set the image on the ImageView
                eventImage.setImage(new Image(newImagePath));
                if (event != null && event.getImage().equals(newImagePath) == false && newImagePath.length() > 0) {
                    //if the image is not the same as the old image we mark the form as dirty
                    setDirty(true);
                } else if (event == null && newImagePath.length() > 0) {
                    //if we add an image to a new event, we also make it dirty
                    setDirty(true);
                }
            }*/
        /*}catch (Exception e) {
            System.err.println("Error selecting file");
            System.err.println(e.getMessage());
        }*/

    }

    ///////////////////////////////// GETTERS SETTERS

    public Timeline getTimeline() {
        return timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
       if(timeline.getStartDate()!=null) {
            //startDate can not be before start date of timeline
            startingDate.setDayCellFactory(new Callback<>() {
                @Override
                public DateCell call(final DatePicker param) {
                    return new DateCell() {
                        @Override
                        public void updateItem(LocalDate item, boolean empty) {
                            super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                            final LocalDate timelineStartDate = timeline.getStartDate().toInstant()
                                    .atZone(ZoneId.systemDefault())
                                    .toLocalDate();
                            setDisable(empty || item.compareTo(timelineStartDate) < 0 );
                        }
                    };
                }
            });
        }
    }

    /////////////////////////////////

    ///////////////////////////////// UTILS
    public void closePopup() {
        super.close();
    }

    @FXML
    public void deleteEvent() {
        event.delete();
        //close popup
        close(true);
        //refresh timeline
        parent.openTimeline(timeline);
    }

    private String errMsg() {//it should be like this???
        String msg = "";
        if (eventName.getText().isEmpty()) {
            eventName.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
            msg += "Event name";
        } else {
            eventName.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
        }
        if (startingDate.getValue() == null) {
            startingDate.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
            if (!msg.equals(""))
                msg += ", ";
            msg += "Starting date";
        } else {
            startingDate.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
        }
        //  else if (endingDate.getValue() == null){
        //         endingDate.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
        // }
        if (startTimeSpinner.getValue() == null) {
            startTimeSpinner.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
            if (!msg.equals("")) {
                msg += ", ";
            }
            msg += "start time";
        } else {
            startTimeSpinner.setStyle("-fx-border-color: transparent ; -fx-border-width: 1px ;");
        }
        if (!msg.equals("")) {
            return msg.substring(0, 1).toUpperCase() + msg.substring(1) + " cannot be empty";
        } else
            return "";
        //  else if (event.getEnd() == null ) {
        //      endTimeSpinner.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
        //  }

//        else if (event.getStart() == null || event.getStart().getHour() <= 0 || event.getStart().getHour() > 24 || event.getStart().getMinute() >= 60 || event.getStart().getMinute() < 0) {
//            startTimeSpinner.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
//        else if (event.getEnd() == null || event.getEnd().getHour() <= 0 || event.getEnd().getHour() > 24 || event.getEnd().getMinute() >= 60 || event.getEnd().getMinute() < 0) {
//        endTimeSpinner.setStyle("-fx-border-color: red ; -fx-border-width: 1px ;");
    }

    private SpinnerValueFactory spinnerValue() {
        SpinnerValueFactory value = new SpinnerValueFactory<LocalTime>() {

            {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                setConverter(new LocalTimeStringConverter(formatter, null));
            }

            @Override
            public void decrement(int steps) {
                if (getValue() == null)
                    setValue(LocalTime.now());
                else {
                    LocalTime time = (LocalTime) getValue();
                    setValue(time.minusMinutes(steps));
                }
            }

            @Override
            public void increment(int steps) {
                if (this.getValue() == null)
                    setValue(LocalTime.now());
                else {
                    LocalTime time = (LocalTime) getValue();
                    setValue(time.plusMinutes(steps));
                }
            }
        };
        LocalTime time = LocalTime.now();
        value.setValue(time);
        return value;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.startValue = spinnerValue();
        //  startValue.setValue(time.get());
        startTimeSpinner.setValueFactory(startValue);
        //  startTimeSpinner.setEditable(true);

        endValue = spinnerValue();
        endTimeSpinner.setValueFactory(endValue);
        //  endTimeSpinner.setEditable(true);

        //endDate can not be before start date now
        Callback<DatePicker, DateCell> callB = new Callback<>() {
            @Override
            public DateCell call(final DatePicker param) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty); //To change body of generated methods, choose Tools | Templates.
                        setDisable(empty || (startingDate.getValue() != null && item.compareTo(startingDate.getValue()) < 0));
                    }
                };
            }
        };
        endingDate.setDayCellFactory(callB);
    }

    @Override
    void enterPress() {
        //What to do on ENTER key press
        saveEvent();
    }

    @Override
    void escPress() {
        //What to do on ESCAPE key press
        close();
    }
}
